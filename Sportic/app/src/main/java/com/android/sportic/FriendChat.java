package com.android.sportic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class FriendChat extends AppCompatActivity {

    private Button sendButton;
    private EditText userMessageInput;
    private ScrollView mscrollView;
    private TextView DisplayTextMessage;
    private String MyId,HisId, currentDate, currentTime;
    private long MessageNum;

    private ArrayList MessageRetrieve;

    FirebaseAuth fauth;
    FirebaseFirestore fstore;

    String userID, userName,MessageLocationId;



    DocumentReference Message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chat);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        MessageLocationId = "null";

        MyId = getIntent().getExtras().get("MyId").toString();
        HisId = getIntent().getExtras().get("HisId").toString();

        MessageRetrieve = new ArrayList();

        fstore.collection("users").document(MyId).collection("MyFriends").document(HisId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    MessageLocationId = value.getString("message");
                    if(MessageLocationId != null) {
                        Log.e("MessageId: ", MessageLocationId);
                        test(MessageLocationId);
                        Start(MessageLocationId);
                    }
                }
            }
        });

        DocumentReference documentReference = fstore.collection("users").document(MyId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d("DocumentSnapshot","Error:"+error.getMessage());
                }
                else {
                    userName = value.getString("pseudo");
                }
            }
        });



        sendButton = (Button) findViewById(R.id.friend_send_message_button);
        userMessageInput = (EditText) findViewById(R.id.friend_input_message);
        mscrollView = (ScrollView) findViewById(R.id.friend_scroll_view);
        DisplayTextMessage = (TextView) findViewById(R.id.friend_text_display);



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message = fstore.collection("FriendMessage").document(MessageLocationId);
                SaveMessageInfoToDataBase();
                userMessageInput.setText("");

                mscrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void test(String id)
    {
        DocumentReference mes = fstore.collection("FriendMessage").document(id);
        mes.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d("DocumentSnapshot","Error:"+error.getMessage());
                    return;
                }
                if (value.get("message") != null)
                    MessageNum = (long) value.get("message"); //retrieve the number of message
            }
        });
    }


    protected void Start(String id) {
        super.onStart();
        Log.e("FirstTime","2");
        DocumentReference mes = fstore.collection("FriendMessage").document(id);
        mes.collection("Messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d("DocumentSnapshot","Error:"+error.getMessage());
                    return;
                }
                if (!value.isEmpty())
                {
                    DisplayMessage(value);
                }
            }
        });

    }

    private void DisplayMessage(QuerySnapshot value) {
        Iterator iterator = value.getDocuments().iterator();

        Log.e("DocumentSnapshot","Message was Display");
        while (iterator.hasNext())
        {
            QueryDocumentSnapshot doc = (QueryDocumentSnapshot) iterator.next();
            Log.e("DocumentSnapshot",doc.getId());
            if (!MessageRetrieve.contains(doc.getId()))
            {
                MessageRetrieve.add(doc.getId());

                String chatName =(String) doc.get("name");
                String chatMessage =(String) doc.get("message");
                String chatDate = (String) doc.get("date");
                String chatTime = (String) doc.get("time");

                DisplayTextMessage.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "     " + chatDate + "\n\n\n");

                mscrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }


        }

    }

    private void SaveMessageInfoToDataBase() {
        String MessageInput = userMessageInput.getText().toString();

        if (TextUtils.isEmpty(MessageInput))
            Toast.makeText(this,"Please Write a message",Toast.LENGTH_SHORT).show();
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String,Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",userName);
            messageInfoMap.put("message",MessageInput);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            CollectionReference collectionReference = Message.collection("Messages");
            collectionReference.document(String.valueOf(MessageNum)).set(messageInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        MessageNum+=1;
                        Message.update("message",MessageNum);
                    }
                }
            });
        }
    }
}