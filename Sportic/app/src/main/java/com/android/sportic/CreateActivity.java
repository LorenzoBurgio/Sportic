package com.android.sportic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends AppCompatActivity{

    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userID;

    Button create;
    EditText name, adress, postalCode, city;
    Spinner levels;
    Spinner SportChoice;
    String level;
    String sport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = fauth.getCurrentUser().getUid();

        create = (Button) findViewById(R.id.CreateButton);
        name = findViewById(R.id.Name);
        adress = findViewById(R.id.adress);
        city = findViewById(R.id.City);
        postalCode = findViewById(R.id.Postal_Code);

        levels = (Spinner) findViewById(R.id.Levels);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levels.setAdapter(adapter);


        SportChoice = (Spinner) findViewById(R.id.SportChoice);
        ArrayAdapter<CharSequence> Sportadapter = ArrayAdapter.createFromResource(this, R.array.Sport, android.R.layout.simple_spinner_item);
        Sportadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SportChoice.setAdapter(Sportadapter);


        //create An Activity
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Name = name.getText().toString().trim();
                String Adress = adress.getText().toString().trim();
                String City = city.getText().toString().trim();
                String Postalcode = postalCode.getText().toString().trim();

                //Look if all the EditText is not empty
                if (TextUtils.isEmpty(Name)){
                    name.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(Adress)){
                    adress.setError("adress is required");
                    return;
                }
                if (TextUtils.isEmpty(City)){
                    city.setError("city is required");
                    return;
                }
                if (TextUtils.isEmpty(Postalcode)){
                    postalCode.setError("Postal Code is required");
                    return;
                }

                //WRITE in DATABASE
                DocumentReference documentReference = fstore.collection("Event").document(Name);
                Map<String,Object> event = new HashMap<>();
                event.put("name",Name);
                event.put("adress",Adress);
                event.put("city",City);
                event.put("postalCode",Postalcode);
                event.put("sport",sport);
                event.put("level",level);
                event.put("message",0);


                documentReference.set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess: Event Created ");
                    }
                });

                fstore.collection("users").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Map<String,Object> user = new HashMap<>();
                        user.put("name",value.getString("name"));
                        documentReference.collection("Participants").document(userID).set(user);
                    }
                });
                Map<String,Object> MyEvent = new HashMap<>();
                MyEvent.put("name",Name);
                fstore.collection("users").document(userID).collection("MyEvent").document(Name).set(MyEvent);






                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();



            }
        });



        SportChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                sport = parentView.getItemAtPosition(position).toString();

            }

            public void onNothingSelected(AdapterView<?> parentView)
            {

            }
        });

        levels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                level = parentView.getItemAtPosition(position).toString();

            }

            public void onNothingSelected(AdapterView<?> parentView)
            {

            }
        });

    }

}