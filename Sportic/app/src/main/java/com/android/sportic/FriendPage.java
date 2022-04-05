package com.android.sportic;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FriendPage extends AppCompatActivity {


    ImageView imageView;
    TextView FirstName;
    TextView LastName;
    TextView FullName;
    TextView Location;
    StorageReference pathReference;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    BitmapFactory.Options options = new BitmapFactory.Options();

    Button AddFriends;
    Button DeclineFriends;
    Button DeleteInvit;
    Button DeleteFriends;
    Button AcceptFriends;
    Button chat;
    TextView invitmessage;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_page);
        Invitation = "";

        imageView = findViewById(R.id.image_view_Friends);
        FirstName = findViewById(R.id.firstName_Friends);
        FullName = findViewById(R.id.editfullName_Friends);
        LastName = findViewById(R.id.lastName_Friends);
        Location = findViewById(R.id.location_Friends);


        invitmessage = findViewById(R.id.InvitMessage);
        invitmessage.setVisibility(View.INVISIBLE);
        AddFriends = (Button) findViewById(R.id.InvitButton);
        AcceptFriends = (Button) findViewById(R.id.AcceptButton);
        AcceptFriends.setVisibility(View.INVISIBLE);
        DeleteFriends = (Button) findViewById(R.id.DeleteButton);
        DeleteFriends.setVisibility(View.INVISIBLE);
        DeclineFriends = (Button) findViewById(R.id.declineButton);
        DeclineFriends.setVisibility(View.INVISIBLE);
        DeleteInvit = (Button) findViewById(R.id.deleteInviteButton);
        DeleteInvit.setVisibility(View.INVISIBLE);
        chat = (Button) findViewById(R.id.chatButton);
        chat.setVisibility(View.INVISIBLE);

        listView = (ListView) findViewById(R.id.FriendsEvent);
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,List_of_Event);
        listView.setAdapter(arrayAdapter);

        userID = getIntent().getExtras().get("UserName").toString();

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        FirebaseUser user = fauth.getCurrentUser();
        pathReference = storageRef.child("images/"+userID+".jpg");

        if (user.getPhotoUrl()!=null)
        {
            Glide.with(FriendPage.this)
                    .load(user.getPhotoUrl())
                    .into(imageView);
        }

        Myid = fauth.getCurrentUser().getUid();

        Myfriends = fstore.collection("users").document(Myid).collection("MyFriends");
        Hisfriends = fstore.collection("users").document(userID).collection("MyFriends");

        fstore.collection("users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){

                    FullName.setText(value.getString("pseudo"));
                    HisName = value.getString("pseudo");

                    String firstname = value.getString("firstname");
                    FirstName.setText(firstname);
                    String lastname = value.getString("lastname");
                    LastName.setText(lastname);
                    String location = value.getString("firstname");

                    if(location == null){
                        Location.setText("Location");
                    }
                    else{
                        Location.setText(location);
                    }
                }

            }
        });

        fstore.collection("users").document(Myid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    MyName = value.getString("pseudo");
                }

            }
        });

        fstore.collection("users").document(Myid).collection("MyFriends").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null)
                {
                    Log.e("case","enter");
                    if(value.getString("invitation") == null)
                    {
                        Log.e("case","null");
                        AddFriends.setVisibility(View.VISIBLE);
                        chat.setVisibility(View.INVISIBLE);
                        DeleteFriends.setVisibility(View.INVISIBLE);
                        DeclineFriends.setVisibility(View.INVISIBLE);
                        AcceptFriends.setVisibility(View.INVISIBLE);
                        invitmessage.setVisibility(View.INVISIBLE);
                        DeleteInvit.setVisibility(View.INVISIBLE);
                    }
                    else if(value.getString("invitation").equals("send"))
                    {
                        Log.e("case","send");
                        Invitation = "send";
                        DeleteInvit.setVisibility(View.VISIBLE);
                        DeleteFriends.setVisibility(View.INVISIBLE);
                        invitmessage.setVisibility(View.VISIBLE);
                        AddFriends.setVisibility(View.INVISIBLE);
                        chat.setVisibility(View.INVISIBLE);
                        DeclineFriends.setVisibility(View.INVISIBLE);
                        AcceptFriends.setVisibility(View.INVISIBLE);
                    }
                    else if(value.getString("invitation").equals("pending"))
                    {
                        Log.e("case","pending");
                        Invitation = "pending";
                        AcceptFriends.setVisibility(View.VISIBLE);
                        DeclineFriends.setVisibility(View.VISIBLE);
                        AddFriends.setVisibility(View.INVISIBLE);
                        chat.setVisibility(View.INVISIBLE);
                        DeleteFriends.setVisibility(View.INVISIBLE);
                        invitmessage.setVisibility(View.INVISIBLE);
                        DeleteInvit.setVisibility(View.INVISIBLE);
                    }
                    else if(value.getString("invitation").equals("accepted"))
                    {
                        Log.e("case","accepted");
                        Invitation = "accepted";
                        chat.setVisibility(View.VISIBLE);
                        DeleteFriends.setVisibility(View.VISIBLE);
                        DeclineFriends.setVisibility(View.INVISIBLE);
                        AcceptFriends.setVisibility(View.INVISIBLE);
                        AddFriends.setVisibility(View.INVISIBLE);
                        invitmessage.setVisibility(View.INVISIBLE);
                        DeleteInvit.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chat = new Intent(getApplicationContext(),FriendChat.class);
                chat.putExtra("MyId",Myid);
                chat.putExtra("HisId",userID);
                startActivity(chat);
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

        DeclineFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete invitation in both side
                Myfriends.document(userID).delete();
                Hisfriends.document(Myid).delete();
            }
        });

        DeleteInvit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Myfriends.document(userID).delete();
                Hisfriends.document(Myid).delete();
            }
        });

        AcceptFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set invitation to "accepted"
                Myfriends.document(userID).update("invitation","accepted");
                Hisfriends.document(Myid).update("invitation","accepted");
                Map<String,Object> invit= new HashMap<>();
                invit.put("message",0);
                fstore.collection("FriendMessage").add(invit).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Myfriends.document(userID).update("message",documentReference.getId());
                        Hisfriends.document(Myid).update("message",documentReference.getId());
                    }
                });
            }
        });

        DeleteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete friends document in both side
                Myfriends.document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value != null && value.getString("message") !=null) {
                            fstore.collection("FriendMessage").document(value.getString("message")).delete();
                        }
                    }
                });
                Myfriends.document(userID).delete();
                Hisfriends.document(Myid).delete();
            }
        });

        AddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Invitation = "null";

                Myfriends.document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value != null && Invitation.equals("null")){
                            Map<String,Object> invit= new HashMap<>();
                            invit.put("invitation","send");// i set the invitation to send for say i send him an invit
                            invit.put("pseudo",HisName);
                            Myfriends.document(userID).set(invit);
                        }

                    }
                });



                Hisfriends.document(Myid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value != null && Invitation.equals("null"))
                        {
                            Map<String,Object> invit2= new HashMap<>();
                            invit2.put("invitation","pending");//i set the invit to pending.
                            invit2.put("pseudo",MyName);
                            Hisfriends.document(Myid).set(invit2);
                            Invitation = "send";
                            Log.e("invitation","send");
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