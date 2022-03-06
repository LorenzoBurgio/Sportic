package com.android.sportic;

import static android.content.ContentValues.TAG;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userID;

    Button create;
    EditText name;
    Spinner levels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = fauth.getCurrentUser().getUid();

        create = (Button) findViewById(R.id.CreateButton);
        name = findViewById(R.id.Name);
        levels = (Spinner) findViewById(R.id.Levels);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levels.setAdapter(adapter);
        levels.setOnItemSelectedListener(this);


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Name = name.getText().toString().trim();

                if (TextUtils.isEmpty(Name)){
                    name.setError("Name is required");
                    return;
                }

                DocumentReference documentReference = fstore.collection("Event").document(Name);
                Map<String,Object> event = new HashMap<>();
                event.put("name",Name);
                documentReference.set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess: Event Created ");
                    }
                });

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}