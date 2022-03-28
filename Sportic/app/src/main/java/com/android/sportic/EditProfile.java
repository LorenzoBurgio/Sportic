package com.android.sportic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfile extends AppCompatActivity {
    Button ValidateProfile;
    EditText firstname;
    EditText lastname;
    EditText location;
    ImageView imageView;
    android.widget.Button OpenCamera;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imageView = findViewById(R.id.image_view);
        OpenCamera = findViewById(R.id.open_camera);
        ValidateProfile = findViewById(R.id.validateProfile);
        firstname = findViewById(R.id.editfirstName);
        lastname = findViewById(R.id.editlastName);
        location = findViewById(R.id.editlocation);

        if(ContextCompat.checkSelfPermission(EditProfile.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EditProfile.this,
                    new String[]
                            {
                                    Manifest.permission.CAMERA
                            },100);
        }
        OpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.content.Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);
            }
        });
        ValidateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update profile in database.
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode,data);

        if(requestCode == 100)
        {
            android.graphics.Bitmap captureImage = (android.graphics.Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(captureImage);
        }
    }

}