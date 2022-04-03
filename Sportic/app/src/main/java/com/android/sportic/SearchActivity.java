package com.android.sportic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String FINE_Location = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String COURSE_Location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private final float DEFAULT_ZOOM = 15f;

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    List<Marker> markers;

    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userID;
    String Event = null;
    Button seeEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        seeEvent = (Button) findViewById(R.id.ViewEvent);

        markers = new ArrayList<>();

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        userID = fauth.getCurrentUser().getUid();

        getLocationPermition();



        seeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Event != null)
                {
                    Toast.makeText(SearchActivity.this, Event,Toast.LENGTH_SHORT).show();
                    Intent event = new Intent(getApplicationContext(),EventPage.class);
                    event.putExtra("EventName",Event);
                    startActivity(event);
                }
                else{
                    Toast.makeText(SearchActivity.this, "Choose an Event",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void MoveCamera(LatLng latLng, float Zoom){
        Log.d("GetLocationDevice","Moving the camera to lat: " + latLng.latitude+", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Zoom));

    }

    private void GetDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(mLocationPermissionGranted)
        {
            @SuppressLint("MissingPermission") Task<Location> location = mFusedLocationProviderClient.getLastLocation();
            location.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null)
                    {
                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        MoveCamera(latLng,DEFAULT_ZOOM);
                    }
                }
            });
        }
    }

    private void getLocationPermition(){
        String[] permissions = {FINE_Location,COURSE_Location};
        Log.e("getLocationPermition","Map getting location permition");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_Location) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_Location) == PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionGranted = true;
                initMap();

            }
            else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap(){
        Log.e("intiMap","Map initialisation");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.SearchMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        Log.e("onRequestParmission","called.");

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int i =0;i<grantResults.length;i++)
                    {
                        if(grantResults [i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.e("intiMap","Error Permission");
                            return;
                        }
                    }
                    Log.e("intiMap","Permission Good");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }

        }
    }

    private void PrintAllEvent(){

        CollectionReference Event = fstore.collection("Event");

        Event.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d("DocumentSnapshot","Error:"+error.getMessage());
                    return;
                }
                Iterator iterator = value.getDocuments().iterator();

                while (iterator.hasNext())
                {
                    QueryDocumentSnapshot doc = (QueryDocumentSnapshot)iterator.next();

                    Double longitude = doc.getDouble("longitude");
                    Double latitude = doc.getDouble("latitude");
                    Log.e("longitude", String.valueOf(longitude));
                    String Name = doc.getString("name");
                    Log.e("Name",Name);

                    LatLng latLng = new LatLng(latitude,longitude);


                    MarkerOptions options = new MarkerOptions().position(latLng).title(Name);
                    Marker marker = mMap.addMarker(options);
                    markers.add(marker);

                }

            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        PrintAllEvent();
        Toast.makeText(this, "MAP is ready",Toast.LENGTH_SHORT).show();
        Log.e("onMapReady","Map is Ready");

        if (mLocationPermissionGranted)
        {

            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            GetDeviceLocation();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Event = marker.getTitle();
                Log.e("OnMarkerListener",Event);

                return false;
            }
        });
    }
}