package com.android.sportic;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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


public class EventFragment extends Fragment {

    ListView List_view;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> List_of_groups = new ArrayList<>();

    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    CollectionReference Event;
    String userID;

    EditText search;
    ArrayList<String> Search;
    ArrayList<String> MyEvent = new ArrayList<>();
    Button ButtonSearch;

    View groupfragmentview;

    public EventFragment() {
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
        groupfragmentview = inflater.inflate(R.layout.fragment_event, container, false);
        initializeField();

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
                    MyEvent.add(doc.getId());
                }
            }
        });


        RetrieveAndDisplayGroups();

        ButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(search.getText().toString().trim()))
                {
                    Search.set(0,"");
                }
                else{
                    Search.set(0,search.getText().toString().trim());
                }


                RetrieveAndDisplayGroups();
            }
        });




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
                    String test = Search.get(0);
                    String data = ((QueryDocumentSnapshot)iterator.next()).getId();
                    if((Search.contains(data) || Search.get(0) == "") && !MyEvent.contains(data))
                    {
                        set.add(data);
                    }
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

        Search = new ArrayList<>();
        Search.add("");

        search = (EditText) groupfragmentview.findViewById(R.id.TextSearchEvent);
        ButtonSearch = (Button) groupfragmentview.findViewById(R.id.ButtonSearchEvent);

        List_view = (ListView) groupfragmentview.findViewById(R.id.list_view_search);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,List_of_groups);
        List_view.setAdapter(arrayAdapter);


    }
}