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


public class SearchFriendsFragment extends Fragment {

    ListView List_view;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> List_of_groups = new ArrayList<>();

    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userID;

    EditText search;
    ArrayList<String> Search;
    Button ButtonSearch;

    ArrayList<String> MyPseudo = new ArrayList<>();

    View groupfragmentview;

    ArrayList<String> user_ID;
    ArrayList<String> user_Name;

    public SearchFriendsFragment() {
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
        groupfragmentview = inflater.inflate(R.layout.fragment_search_friends, container, false);
        initializeField();

        fstore.collection("users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    MyPseudo.clear();
                    MyPseudo.add(value.getString("pseudo"));
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
        fstore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                    String test = Search.get(0);
                    QueryDocumentSnapshot doc = (QueryDocumentSnapshot) iterator.next();
                    String name = doc.getString("pseudo");
                    String id = doc.getId();

                    if((Search.contains(name) || Search.get(0) == "") && !MyPseudo.contains(name))
                    {
                        user_ID.add(id);
                        user_Name.add(name);

                        set.add(name);
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

        Search = new ArrayList<>();
        Search.add("");

        user_ID = new ArrayList<>();
        user_Name = new ArrayList<>();

        search = (EditText) groupfragmentview.findViewById(R.id.TextSearchFriends);
        ButtonSearch = (Button) groupfragmentview.findViewById(R.id.ButtonSearchFriends);

        List_view = (ListView) groupfragmentview.findViewById(R.id.list_view_friends);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,List_of_groups);
        List_view.setAdapter(arrayAdapter);


    }
}