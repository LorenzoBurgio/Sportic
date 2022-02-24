package com.android.sportic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText name;
    private EditText pwd;
    private TextView info;
    private Button login;
    private Button Register;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = (EditText) findViewById(R.id.etEmailR);
        pwd = (EditText) findViewById(R.id.etPasswordR);
        login = (Button) findViewById(R.id.RegisterR);
        Register = (Button) findViewById(R.id.Register);
        fauth = FirebaseAuth.getInstance();



        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Email = name.getText().toString().trim();
                String password = pwd.getText().toString().trim();

                //verify all the value
                if (TextUtils.isEmpty(Email)){
                    name.setError("Email is required.");
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

                //sign in the user

                fauth.signInWithEmailAndPassword(Email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"User Connected.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Error ! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });




            }
        });
    }
}