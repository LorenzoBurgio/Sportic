package com.android.sportic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity{

    FirebaseAuth fauth;
    ImageView imageView;
    String userID;
    Button OpenCamera;
    TextView FirstName;
    TextView LastName;
    TextView FullName;
    TextView Location;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setContentView(R.layout.activity_edit_profile);
        setContentView(R.layout.activity_main);
        fauth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.image_view);
        OpenCamera = findViewById(R.id.camera);
        FirstName = findViewById(R.id.firstName);
        FullName = findViewById(R.id.fullName);
        LastName = findViewById(R.id.lastName);
        Location = findViewById(R.id.location);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = fauth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("users").document(userID);

        //set profile with Database.

        if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ProfileActivity.this,
                    new String[]
                            {
                                    Manifest.permission.CAMERA
                            },100);
        }


    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode,data);

        if(requestCode == 100)
        {
            android.graphics.Bitmap captureImage = (android.graphics.Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(captureImage);

            //Update Image in dataBase.
        }
    }*/
}