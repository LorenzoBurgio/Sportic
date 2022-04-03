package com.android.sportic;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ProfileActivity extends AppCompatActivity{

    FirebaseAuth fauth;
    ImageView imageView;
    String userID;
    Button OpenCamera;
    Button EditProfile;
    ImageButton Quit;
    TextView FirstName;
    TextView LastName;
    TextView FullName;
    TextView Location;
    FirebaseFirestore fstore;
    StorageReference pathReference;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    BitmapFactory.Options options = new BitmapFactory.Options();


    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fauth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.image_view);
        OpenCamera = findViewById(R.id.camera);
        FirstName = findViewById(R.id.firstName);
        FullName = findViewById(R.id.editfullName);
        LastName = findViewById(R.id.lastName);
        Location = findViewById(R.id.location);
        EditProfile = findViewById(R.id.edit_profile);
        Quit = findViewById(R.id.quitPage);


        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = fauth.getCurrentUser().getUid();
        FirebaseUser user = fauth.getCurrentUser();
        pathReference = storageRef.child("images/"+userID+".jpg");
        DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null)
                {
                    String firstName = value.getString("firstname");
                    String lastName = value.getString("lastname");
                    String location = value.getString("city");
                    if (user.getPhotoUrl()!=null)
                    {
                        Glide.with(ProfileActivity.this)
                                .load(user.getPhotoUrl())
                                .into(imageView);
                    }

                    if (firstName==null)
                    {
                        FirstName.setText("John");

                    }
                    else
                    {
                        FirstName.setText(firstName);
                    }
                    if (lastName==null)
                    {
                        LastName.setText("Doe");

                    }
                    else
                    {
                        LastName.setText(lastName);
                    }
                    if (location==null)
                    {
                        Location.setText("city");

                    }
                    else
                    {
                        Location.setText(location);
                    }
                    FullName.setText(FirstName.getText().toString() + " " + LastName.getText().toString());
                }
            }
        });


        if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ProfileActivity.this,
                    new String[]
                            {
                                    Manifest.permission.CAMERA
                            },100);
        }
        if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ProfileActivity.this,
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
        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),EditProfile.class));
                finish();
            }
        });

        Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode,data);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = fauth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("users").document(userID);

        if(requestCode == 100) {
            switch (resultCode){
                case RESULT_OK:
                    Bitmap captureImage = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(captureImage);
                    handleUpload(captureImage);
            }
        }

    }
    private void handleUpload(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);

        userID = fauth.getCurrentUser().getUid();
        pathReference = storageRef.child("images/"+userID+".jpg");

        pathReference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(pathReference);
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("onFailure: ",e.getMessage());
            }
        });
    }
    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("OnSuccces: ",uri.toString());
                        setUserProfileUrl(uri);
                    }
                });
    }
    private void setUserProfileUrl(Uri uri){
        FirebaseUser user = fauth.getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ProfileActivity.this, "Upload succesfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Profile image failed.",Toast.LENGTH_SHORT);
                    }
                });
    }
}