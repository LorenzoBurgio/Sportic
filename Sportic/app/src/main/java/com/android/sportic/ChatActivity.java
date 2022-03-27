package com.android.sportic;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    private Button sendButton;
    private EditText userMessageInput;
    private ScrollView mscrollView;
    private TextView DisplayTextMessage;
    private String EventName, currentDate, currentTime;
    private long MessageNum;

    private ArrayList MessageRetrieve;

    FirebaseAuth fauth;
    FirebaseFirestore fstore;

    String userID, userName;

    DocumentReference Event;
    DocumentReference MessageKeyRef;

    CollectionReference Message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        MessageRetrieve = new ArrayList();

        userID = fauth.getCurrentUser().getUid();

        EventName = getIntent().getExtras().get("EventName").toString();
        Toast.makeText(ChatActivity.this,EventName,Toast.LENGTH_SHORT).show();

        sendButton = (Button) findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id.input_message);
        mscrollView = (ScrollView) findViewById(R.id.my_scroll_view);
        DisplayTextMessage = (TextView) findViewById(R.id.chat_text_display);

        Event = fstore.collection("Event").document(EventName);

        Message = Event.collection("Message");

        Event.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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



        DocumentReference documentReference = fstore.collection("users").document(userID);
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

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 SaveMessageInfoToDataBase();
                 userMessageInput.setText("");

                mscrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        Message.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                String chatName =(String) doc.get("pseudo");
                String chatMessage =(String) doc.get("message");
                String chatDate = (String) doc.get("date");
                String chatTime = (String) doc.get("time");

                DisplayTextMessage.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "     " + chatDate + "\n\n\n");

                mscrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }


        }

    }

    private void SaveMessageInfoToDataBase() {
        String Message = userMessageInput.getText().toString();

        if (TextUtils.isEmpty(Message))
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
            messageInfoMap.put("pseudo",userName);
            messageInfoMap.put("message",Message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            CollectionReference collectionReference = fstore.collection("Event").document(EventName).collection("Message");
            collectionReference.document(String.valueOf(MessageNum)).set(messageInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        MessageNum+=1;
                        Event.update("message",MessageNum);
                    }
                }
            });




        }
    }
}