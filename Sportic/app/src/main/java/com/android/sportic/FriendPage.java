package com.android.sportic;

import static android.content.ContentValues.TAG;

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

import com.google.android.gms.tasks.OnSuccessListener;
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

public class FriendPage extends AppCompatActivity {

    TextView friendsName;
    Button AddFriends;
    Button chat;

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> List_of_Event = new ArrayList<>();

    CollectionReference Myfriends;
    CollectionReference Hisfriends;

    String Invitation;

    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userID;
    String Myid;
    String MyName;
    String HisName;
    boolean havefriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_page);
        Invitation = "";


        friendsName = (TextView) findViewById(R.id.Friendsname);
        AddFriends = (Button) findViewById(R.id.InvitButton);
        chat = (Button) findViewById(R.id.chatButton);

        listView = (ListView) findViewById(R.id.FriendsEvent);
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,List_of_Event);
        listView.setAdapter(arrayAdapter);

        userID = getIntent().getExtras().get("UserName").toString();

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        Myid = fauth.getCurrentUser().getUid();

        Myfriends = fstore.collection("users").document(Myid).collection("MyFriends");
        Hisfriends = fstore.collection("users").document(userID).collection("MyFriends");

        fstore.collection("users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    friendsName.setText(value.getString("name"));
                    HisName = value.getString("name");
                }

            }
        });

        fstore.collection("users").document(Myid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    MyName = value.getString("name");
                }

            }
        });


        RetrieveAndDisplayGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String EventName = adapterView.getItemAtPosition(i).toString();
                Intent Event = new Intent(getApplicationContext(),EventPage.class);
                Event.putExtra("EventName",EventName);
                startActivity(Event);
            }
        });

        AddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Myfriends.document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value == null)
                            return;
                        if (value.getString("invitation") == null)//if the value is null, => the user don't send me an invit
                        {
                            Invitation = "null";
                            Log.e("invitation","null");
                            Map<String,Object> invit= new HashMap<>();
                            invit.put("invitation","send");// i set the invitation to send for say i send him an invit
                            invit.put("name",HisName);
                            Myfriends.document(userID).set(invit);

                        }
                        else if(value.getString("invitation").equals("pending"))// if i have pending, that meen he alreandy send me an invit
                        {
                            Invitation = "pending";
                            //accept the invitation
                            Myfriends.document(userID).update("invitation","accepted");// so i accept the invitation
                            Hisfriends.document(Myid).update("invitation","accepted");

                        }
                    }
                });

                Hisfriends.document(Myid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.getString("invitation") == null)// if the value is null => i never send him an invit.
                        {
                            Map<String,Object> invit= new HashMap<>();
                            invit.put("invitation","pending");//i set the invit to pending.
                            invit.put("name",MyName);
                            Hisfriends.document(Myid).set(invit);
                        }
                    }
                });
            }
        });


    }


    private void RetrieveAndDisplayGroups() {
        fstore.collection("users").document(userID).collection("MyEvent").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                    set.add(id);

                }



                List_of_Event.clear();
                List_of_Event.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }
        });
    }
}