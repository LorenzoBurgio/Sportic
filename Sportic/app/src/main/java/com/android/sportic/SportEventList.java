package com.android.sportic;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SportEventList extends AppCompatActivity {

    ListView List_view;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> List_of_groups = new ArrayList<>();


    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    CollectionReference Event;
    String userID;
    String sport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_event_list);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        userID = fauth.getCurrentUser().getUid();

        sport = SportAdapter.getSelectedSport();

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
        List_view = (ListView) findViewById(R.id.sport_list_view);

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, List_of_groups);

        List_view.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayEvent() {

        Event = fstore.collection("Event");

        Event.addSnapshotListener(this, (value, error) -> {
            if(error != null)
            {
                Log.d("DocumentSnapshot","Error:"+error.getMessage());
                return;
            }
            Set<String> set = new HashSet<>();
            assert value != null;

            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                String data = ((QueryDocumentSnapshot) documentSnapshot).getId();
                Log.e(TAG, "event :" + data);
                if (Objects.equals(documentSnapshot.getString("sport"), sport)) {
                    set.add(data);
                }
            }
            Log.e(TAG,"Finish");


            List_of_groups.clear();
            List_of_groups.addAll(set);
            arrayAdapter.notifyDataSetChanged();
        });

    }
}