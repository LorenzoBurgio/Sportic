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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class MyFriends extends Fragment {


    public MyFriends() {
        // Required empty public constructor
    }

    ListView List_view;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> List_of_groups = new ArrayList<>();

    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userID;


    View groupfragmentview;

    ArrayList<String> user_ID;
    ArrayList<String> user_Name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupfragmentview =  inflater.inflate(R.layout.fragment_my_friends, container, false);

        initializeField();


        RetrieveAndDisplayGroups();

        List_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String UserName = adapterView.getItemAtPosition(i).toString();
                int id = Get_Id(UserName,user_Name);
                String ide = user_ID.get(id);
                Log.e("id",ide);
                Intent friends = new Intent(getContext(),FriendPage.class);
                friends.putExtra("UserName",ide);
                startActivity(friends);
            }
        });

        return groupfragmentview;
    }

    private void RetrieveAndDisplayGroups() {
        fstore.collection("users").document(userID).collection("MyFriends").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d("DocumentSnapshot","Error:"+error.getMessage());
                    return;
                }
                Set<String> set = new HashSet<>();
                Iterator iterator = value.getDocuments().iterator();

                user_ID.clear();
                user_Name.clear();

                while (iterator.hasNext())
                {
                    QueryDocumentSnapshot doc = (QueryDocumentSnapshot) iterator.next();
                    String id = doc.getId();
                    if(doc.getString("invitation") != null) {
                        if (doc.getString("invitation").equals("accepted")) {
                            set.add(doc.getString("pseudo"));
                            user_ID.add(id);
                            user_Name.add(doc.getString("pseudo"));

                        }
                    }
                }

                List_of_groups.clear();
                List_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private int Get_Id(String name, List<String> user_Name){
        int i = 0;

        for (;i < user_ID.size();i++){
            if (name == user_Name.get(i))
                return i;
        }

        return i;
    }

    private void initializeField() {
        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = fauth.getCurrentUser().getUid();


        user_ID = new ArrayList<>();
        user_Name = new ArrayList<>();


        List_view = (ListView) groupfragmentview.findViewById(R.id.list_friends);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,List_of_groups);
        List_view.setAdapter(arrayAdapter);


    }
}