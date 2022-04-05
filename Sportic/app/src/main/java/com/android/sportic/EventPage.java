package com.android.sportic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventPage extends AppCompatActivity implements OnMapReadyCallback {

    private Button chat;
    private Button EventJoin;
    private Button EventLeave;
    private String EventName;
    private TextView Eventname;
    private TextView EventAdress;
    private TextView EventSport;
    private TextView EventLevel;

    private GoogleMap mMap;
    private final float DEFAULT_ZOOM = 15f;

    private ListView EventParticipant;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> List_of_Participants = new ArrayList<>();

    FirebaseAuth fauth;
    DocumentReference Event;
    FirebaseFirestore fstore;
    String userID;

    ArrayList<String> user_ID;
    ArrayList<String> user_Name;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        initMap();

        user_ID = new ArrayList<>();
        user_Name = new ArrayList<>();

        EventName = getIntent().getExtras().get("EventName").toString();

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        Event = fstore.collection("Event").document(EventName);

        userID = fauth.getCurrentUser().getUid();

        chat = (Button) findViewById(R.id.EventChat);
        EventJoin = (Button) findViewById(R.id.EventJoin);
        EventLeave = (Button) findViewById(R.id.eventLeave);
        EventLeave.setVisibility(View.INVISIBLE);
        Eventname = (TextView) findViewById(R.id.EventName);
        Eventname.setText(EventName);
        EventAdress = (TextView) findViewById(R.id.EventAdress);
        EventSport = (TextView) findViewById(R.id.EventSport);
        EventLevel= (TextView) findViewById(R.id.EventLevel);

        EventParticipant = (ListView) findViewById(R.id.ListParticipant);
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,List_of_Participants);

        EventParticipant.setAdapter(arrayAdapter);

        RetrieveAndDisplayParticipant();

        fstore.collection("users").document(userID).collection("MyEvent").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    QueryDocumentSnapshot doc = (QueryDocumentSnapshot) iterator.next();
                    if(doc.getId().equals(EventName))
                    {
                        EventJoin.setVisibility(View.INVISIBLE);
                        EventLeave.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        Event.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                EventSport.setText(value.getString("sport"));
                EventLevel.setText("Level: " + value.getString("level"));
                String adress = "Adress: " + value.getString("adress");
                EventAdress.setText(adress);

                LatLng latLng = new LatLng(value.getDouble("latitude"),value.getDouble("longitude"));
                MoveCamera(latLng,DEFAULT_ZOOM);

                MarkerOptions options = new MarkerOptions().position(latLng).title(adress);
                Marker marker = mMap.addMarker(options);
            }
        });


        EventJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fstore.collection("users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Map<String,Object> user = new HashMap<>();
                        user.put("pseudo",value.getString("pseudo"));
                        Event.collection("Participants").document(userID).set(user);
                    }
                });

                Map<String,Object> MyEvent = new HashMap<>();
                MyEvent.put("name",EventName);
                fstore.collection("users").document(userID).collection("MyEvent").document(EventName).set(MyEvent);
            }
        });

        EventLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event.collection("Participants").document(userID).delete();
                fstore.collection("users").document(userID).collection("MyEvent").document(EventName).delete();
                EventJoin.setVisibility(View.VISIBLE);
                EventLeave.setVisibility(View.INVISIBLE);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Event = new Intent(getApplicationContext(),ChatActivity.class);
                Event.putExtra("EventName",EventName);
                startActivity(Event);
            }
        });

        EventParticipant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String UserName = adapterView.getItemAtPosition(i).toString();
                int id = Get_Id(UserName,user_Name);
                String ide = user_ID.get(id);
                Log.e("id",ide);
                Intent friends = new Intent(getApplicationContext(),FriendPage.class);
                friends.putExtra("UserName",ide);
                startActivity(friends);
            }
        });
    }

    private void RetrieveAndDisplayParticipant() {
        CollectionReference Participants = Event.collection("Participants");
        Participants.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d("DocumentSnapshot","Error:"+error.getMessage());
                    return;
                }
                Set<String> set = new HashSet<>();
                Iterator iterator = value.getDocuments().iterator();

                while (iterator.hasNext())
                {
                    QueryDocumentSnapshot doc = (QueryDocumentSnapshot) iterator.next();
                    String id = doc.getId();
                    set.add(doc.getString("pseudo"));
                    user_ID.add(id);
                    user_Name.add(doc.getString("pseudo"));

                }



                List_of_Participants.clear();
                List_of_Participants.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void MoveCamera(LatLng latLng, float Zoom){
        Log.d("GetLocationDevice","Moving the camera to lat: " + latLng.latitude+", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Zoom));

    }

    private void initMap(){
        Log.e("intiMap","Map initialisation");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.EventMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    private int Get_Id(String name, List<String> user_Name){
        int i = 0;

        for (;i < user_ID.size();i++){
            if (name == user_Name.get(i))
                return i;
        }

        return i;
    }
}