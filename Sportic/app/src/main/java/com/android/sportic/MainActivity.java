package com.android.sportic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userID;
    TextView print;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        TextView print = (TextView) findViewById(R.id.Print);

        userID = fauth.getCurrentUser().getUid();

        //read in DATABASE
        DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d("DocumentSnapshot","Error:"+error.getMessage());
                }
                else {
                    print.setText("Welcome " + value.getString("fullname"));
                }
            }
        });

    }

    public void Event(View view){
        startActivity(new Intent(getApplicationContext(),Event.class));
    }

    public void CreateEvent(View view){
        startActivity(new Intent(getApplicationContext(),CreateActivity.class));
    }

    public void SearchEvent(View view){
        startActivity(new Intent(getApplicationContext(),SearchActivity.class));
    }
    public void Friends(View view){
        startActivity(new Intent(getApplicationContext(),Friends.class));
    }

    public void logout (View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish(); 
    }

    public void profile (View view){
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
    }

}