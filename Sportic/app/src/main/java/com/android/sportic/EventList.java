package com.android.sportic;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EventList extends AppCompatActivity {

    ListView List_view;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> List_of_groups = new ArrayList<>();


    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    CollectionReference Event;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        userID = fauth.getCurrentUser().getUid();


        
        InitializeFields();

        RetrieveAndDisplayEvent();


        List_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String EventName = adapterView.getItemAtPosition(i).toString();
                Intent Event = new Intent(getApplicationContext(),EventPage.class);
                Event.putExtra("EventName",EventName);
                startActivity(Event);
            }
        });

    }



    private void InitializeFields() {
        List_view = (ListView) findViewById(R.id.list_view);

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,List_of_groups);

        List_view.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayEvent() {

        Event = fstore.collection("Event");

        Event.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
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
                    String data = ((QueryDocumentSnapshot)iterator.next()).getId();
                    set.add(data);
                }



                List_of_groups.clear();
                List_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }
        });

    }


}