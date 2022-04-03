package com.android.sportic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class EditProfile extends AppCompatActivity {
    Button ValidateProfile;
    ImageButton Quit;
    EditText firstname;
    EditText lastname;
    EditText location;
    TextView fullname;
    ImageView imageView;
    android.widget.Button OpenCamera;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    String userID;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    BitmapFactory.Options options = new BitmapFactory.Options();

    StorageReference pathReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imageView = findViewById(R.id.image_view);
        OpenCamera = findViewById(R.id.open_camera);
        ValidateProfile = findViewById(R.id.validateProfile);
        fullname = findViewById(R.id.editfullName);
        firstname = findViewById(R.id.editfirstName);
        lastname = findViewById(R.id.editlastName);
        location = findViewById(R.id.editlocation);
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
                    String Location = value.getString("city");
                    if (user.getPhotoUrl()!=null)
                    {
                        Glide.with(EditProfile.this)
                                .load(user.getPhotoUrl())
                                .into(imageView);
                    }
                    if (firstName==null)
                    {
                        firstname.setText("John");

                    }
                    else
                    {
                        firstname.setText(firstName);
                    }
                    if (lastName==null)
                    {
                        lastname.setText("Doe");

                    }
                    else
                    {
                        lastname.setText(lastName);
                    }
                    if (location==null)
                    {
                        location.setText("city");

                    }
                    else
                    {
                        location.setText(Location);
                    }
                    fullname.setText(firstname.getText().toString() + " " + lastname.getText().toString());
                }
            }
        });
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
                documentReference.update("firstname",firstname.getText().toString());
                documentReference.update("lastname",lastname.getText().toString());
                documentReference.update("fullname",firstname.getText().toString() + " " +lastname.getText().toString());
                documentReference.update("city",location.getText().toString());
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                finish();
            }
        });
        Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
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
                        Toast.makeText(EditProfile.this, "Upload succesfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, "Profile image failed.",Toast.LENGTH_SHORT);
                    }
                });
    }
}