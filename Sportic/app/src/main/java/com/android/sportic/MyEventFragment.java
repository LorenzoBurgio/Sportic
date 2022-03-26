package com.android.sportic;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.Set;

public class MyEventFragment extends Fragment {


    ListView List_view;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> List_of_groups = new ArrayList<>();

    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    CollectionReference Event;
    String userID;

    ArrayList<String> MyEvent;

    View groupfragmentview;

    public MyEventFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupfragmentview = inflater.inflate(R.layout.fragment_my_event, container, false);

        initializeField();
        RetrieveMyEvent();

        RetrieveAndDisplayGroups();



        List_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String EventName = adapterView.getItemAtPosition(i).toString();
                Intent Event = new Intent(getContext(),EventPage.class);
                Event.putExtra("EventName",EventName);
                startActivity(Event);
            }
        });

        return groupfragmentview;
    }

    private void RetrieveMyEvent() {
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
                    String data = ((QueryDocumentSnapshot)iterator.next()).getId();
                    MyEvent.add(data);
                }
            }
        });
    }

    private void RetrieveAndDisplayGroups() {
        fstore.collection("Event").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    if(MyEvent.contains(data))
                        set.add(data);
                }



                List_of_groups.clear();
                List_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initializeField() {
        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = fauth.getCurrentUser().getUid();

        MyEvent = new ArrayList<>();

        List_view = (ListView) groupfragmentview.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,List_of_groups);
        List_view.setAdapter(arrayAdapter);


    }




}