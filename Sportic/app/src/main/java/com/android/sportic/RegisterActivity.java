package com.android.sportic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText name,email,pwd;
    Button register;
    FirebaseAuth fauth;
    FirebaseFirestore  fstore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.etNameR);
        email = (EditText) findViewById(R.id.etEmailR);
        pwd = (EditText) findViewById(R.id.etPasswordR);
        register = (Button) findViewById(R.id.RegisterR);



        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        if(fauth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = email.getText().toString().trim();
                String password = pwd.getText().toString().trim();
                String Name = name.getText().toString().trim();

                //verify all the value
                if (TextUtils.isEmpty(Email)){
                    email.setError("Email is required.");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    pwd.setError("Password is required.");
                    return;
                }
                if (password.length() < 6 ){
                    pwd.setError("Password must be >= § characters");
                    return;
                }

                //register the user in firebase

                fauth.createUserWithEmailAndPassword(Email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"User Created.",Toast.LENGTH_SHORT).show();
                            userID = fauth.getCurrentUser().getUid();
                            //Write in DATABASE
                            DocumentReference documentReference = fstore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("name",Name);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG,"onSuccess: user profile is created for " + userID );
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
}