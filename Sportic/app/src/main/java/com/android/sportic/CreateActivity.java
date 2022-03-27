package com.android.sportic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateActivity extends AppCompatActivity implements OnMapReadyCallback {

    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userID;

    Button create;
    EditText name, adress;
    Spinner levels;
    Spinner SportChoice;
    String level;
    String sport;
    String CompleteAdress;

    List<Marker> markers;

    double longitude;
    double latitude;

    private GoogleMap mMap;
    private final float DEFAULT_ZOOM = 15f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        markers = new ArrayList<>();

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = fauth.getCurrentUser().getUid();

        create = (Button) findViewById(R.id.CreateButton);
        name = findViewById(R.id.Name);
        adress = findViewById(R.id.adress);

        levels = (Spinner) findViewById(R.id.Levels);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levels.setAdapter(adapter);


        SportChoice = (Spinner) findViewById(R.id.SportChoice);
        ArrayAdapter<CharSequence> Sportadapter = ArrayAdapter.createFromResource(this, R.array.Sport, android.R.layout.simple_spinner_item);
        Sportadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SportChoice.setAdapter(Sportadapter);

        initMap();

        adress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH || i == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER)
                {
                    geolocate();
                }
                return false;
            }
        });


        //create An Activity
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Name = name.getText().toString().trim();
                String Adress = adress.getText().toString().trim();


                //Look if all the EditText is not empty
                if (TextUtils.isEmpty(Name)){
                    name.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(Adress)){
                    adress.setError("adress is required");
                    return;
                }


                //WRITE in DATABASE
                DocumentReference documentReference = fstore.collection("Event").document(Name);
                Map<String,Object> event = new HashMap<>();
                event.put("name",Name);
                event.put("adress",Adress);
                event.put("sport",sport);
                event.put("level",level);
                event.put("longitude",longitude);
                event.put("latitude",latitude);
                event.put("adress",CompleteAdress);
                event.put("message",0);


                documentReference.set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess: Event Created ");
                    }
                });

                fstore.collection("users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Map<String,Object> user = new HashMap<>();
                        user.put("pseudo",value.getString("pseudo"));
                        user.put("fullname",value.getString("fullname"));
                        documentReference.collection("Participants").document(userID).set(user);
                    }
                });
                Map<String,Object> MyEvent = new HashMap<>();
                MyEvent.put("name",Name);
                fstore.collection("users").document(userID).collection("MyEvent").document(Name).set(MyEvent);

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

            }
        });



        SportChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                sport = parentView.getItemAtPosition(position).toString();

            }

            public void onNothingSelected(AdapterView<?> parentView)
            {

            }
        });

        levels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                level = parentView.getItemAtPosition(position).toString();

            }

            public void onNothingSelected(AdapterView<?> parentView)
            {

            }
        });

    }

    private void geolocate() {
        String Search = adress.getText().toString();
        Geocoder geocoder = new Geocoder(CreateActivity.this);
         List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(Search,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(list.size() > 0){
            Address adress = list.get(0);
            Log.e("intiMap","find a location" + adress.toString());
            LatLng latLng = new LatLng(adress.getLatitude(),adress.getLongitude());
            MoveCamera(latLng,DEFAULT_ZOOM);

            longitude = adress.getLongitude();
            latitude = adress.getLatitude();
            CompleteAdress = adress.getAddressLine(0);

            if (markers.size() >0)
            {
                markers.get(0).remove();
                markers.remove(0);
            }

            MarkerOptions options = new MarkerOptions().position(latLng);
            Marker marker = mMap.addMarker(options);
            markers.add(marker);



        }
    }

    private void MoveCamera(LatLng latLng, float Zoom){
        Log.d("GetLocationDevice","Moving the camera to lat: " + latLng.latitude+", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Zoom));

    }

    private void initMap(){
        Log.e("intiMap","Map initialisation");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.CreateMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}