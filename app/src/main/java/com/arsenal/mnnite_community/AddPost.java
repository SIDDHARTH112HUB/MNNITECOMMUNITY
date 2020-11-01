package com.arsenal.mnnite_community;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class AddPost extends AppCompatActivity {
    private EditText title,description;
    private ImageView postImg;
    private ProgressBar progressBar;
    private Button post;

    private StorageReference storageReference;

    FirebaseUser currentUser ;
    String userId;

    private Uri postImageUri;

    static int PReqCode = 1 ;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    private Toolbar postToolbar;

    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        title=findViewById(R.id.postTitle);
        description=findViewById(R.id.postDescription);
        postImg=findViewById(R.id.postImg);
        progressBar=findViewById(R.id.postProgressBar);
        post=findViewById(R.id.postAdd);
        storageReference=FirebaseStorage.getInstance().getReference();

        postToolbar=(Toolbar)findViewById(R.id.post_toolbar);
        setSupportActionBar(postToolbar);

        getSupportActionBar().setTitle("Add new post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        userID=mAuth.getCurrentUser().getUid();

        postImg=findViewById(R.id.postImg);
        postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(AddPost.this);



            }
        });
        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri=result.getUri();
                postImg.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                post.setVisibility(View.INVISIBLE);
                String postTitle=title.getText().toString();
                String postDescription=description.getText().toString();
                if (TextUtils.isEmpty(postTitle)){
                    title.setError("field required");
                }else  if (TextUtils.isEmpty(postDescription)){
                    description.setError("field required");
                }else{


                    String randomName = UUID.randomUUID().toString();
                    StorageReference filePath = storageReference.child("Post_images").child(randomName+".jpg");

                    filePath.putFile(postImageUri).addOnSuccessListener(taskSnapshot -> {

                        filePath.getDownloadUrl().addOnSuccessListener(uri -> {

                            Map<String, String> postMap = new HashMap<>();
                            postMap.put("Description,", postDescription);
                            postMap.put("image", uri.toString());
                            postMap.put("Title", postTitle);
                            postMap.put("post_time", new Date().toString());
                            postMap.put("Current_user_id", userID);
                            fStore.collection("Posts").add(postMap).addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(AddPost.this, "Post added successfully", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(() -> {
                                    startActivity(new Intent(AddPost.this,Dashboard.class));
                                    finish();
                                }, 300);
                            }).addOnFailureListener(e -> showMessage(e.getMessage()));


                        });
                        filePath.getDownloadUrl().addOnFailureListener(e -> {
                            String error = e.getMessage();
                            showMessage("Error : " + e);
                        });

                    }).addOnFailureListener(e -> showMessage(e.getMessage()));

                }


            }
        });

    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}