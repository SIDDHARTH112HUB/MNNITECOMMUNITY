package com.arsenal.mnnite_community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class AddPost extends AppCompatActivity {
    private EditText title,description;
    private ImageView postImg;
    private ProgressBar progressBar;
    private Button post;

    static int PReqCode = 1 ;

    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

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

        mAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        userID=mAuth.getCurrentUser().getUid();

        postImg=findViewById(R.id.postImg);
        postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 22) {

                    checkAndRequestForPermission();


                }
                else
                {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(AddPost.this);
                }





            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postTitle=title.getText().toString();
                String postDescription=description.getText().toString();
                if (TextUtils.isEmpty(postTitle)){
                    title.setError("field required");
                }else  if (TextUtils.isEmpty(postDescription)){
                    description.setError("field required");
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    DocumentReference documentReference=fStore.collection("Posts").document(userID);
                    Map<String,Object> post=new HashMap<>();
                    post.put("Title",postTitle);
                    post.put("Description",postDescription);
                    post.put("image",postImg.toString());
                    documentReference.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(AddPost.this, "post added successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddPost.this,Dashboard.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddPost.this, "post cant be added", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });

    }
    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(AddPost.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddPost.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                showMessage("Please accept for required permission");

            }

            else
            {
                ActivityCompat.requestPermissions(AddPost.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else

        {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(AddPost.this);
        }


    }
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}