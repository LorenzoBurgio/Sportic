package com.android.sportic;

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
import java.util.Map;
import java.util.Set;

public class EventPage extends AppCompatActivity {

    private Button chat;
    private Button EventJoin;
    private String EventName;
    private TextView Eventname;
    private TextView EventAdress;
    private TextView EventSport;
    private TextView EventLevel;

    private ListView EventParticipant;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> List_of_Participants = new ArrayList<>();

    FirebaseAuth fauth;
    DocumentReference Event;
    FirebaseFirestore fstore;
    String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        EventName = getIntent().getExtras().get("EventName").toString();

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        Event = fstore.collection("Event").document(EventName);

        userID = fauth.getCurrentUser().getUid();

        chat = (Button) findViewById(R.id.EventChat);
        EventJoin = (Button) findViewById(R.id.EventJoin);

        Eventname = (TextView) findViewById(R.id.EventName);
        Eventname.setText(EventName);
        EventAdress = (TextView) findViewById(R.id.EventAdress);
        EventSport = (TextView) findViewById(R.id.EventSport);
        EventLevel= (TextView) findViewById(R.id.EventLevel);

        EventParticipant = (ListView) findViewById(R.id.ListParticipant);
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,List_of_Participants);

        EventParticipant.setAdapter(arrayAdapter);

        RetrieveAndDisplayParticipant();

        Event.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                EventSport.setText(value.getString("sport"));
                EventLevel.setText("Level: " + value.getString("level"));
                String adress = "Adress: " + value.getString("adress")+ ", "+ value.getString("city")+  ", "+ value.getString("postalCode");
                EventAdress.setText(adress);
            }
        });






        EventJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fstore.collection("users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Map<String,Object> user = new HashMap<>();
                        user.put("name",value.getString("name"));
                        Event.collection("Participants").document(userID).set(user);
                    }
                });

                Map<String,Object> MyEvent = new HashMap<>();
                MyEvent.put("name",EventName);
                fstore.collection("users").document(userID).collection("MyEvent").document(EventName).set(MyEvent);
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

                    set.add(doc.getString("name"));

                }



                List_of_Participants.clear();
                List_of_Participants.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }
}