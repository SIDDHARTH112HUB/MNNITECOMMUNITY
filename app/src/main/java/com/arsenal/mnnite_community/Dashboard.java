package com.arsenal.mnnite_community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.arsenal.mnnite_community.models.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Dashboard extends AppCompatActivity {

    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;
    private Button btnLogOut;
    private FloatingActionButton edit;
    Dialog popAddPost;
    ImageView popupPostImage, popupAddBtn;
    TextView popupTitle, popupDescription;
    ProgressBar popupClickProgress;
    private Uri pickedImgUri = null;
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        btnLogOut = findViewById(R.id.btnLogOut);
        edit = findViewById(R.id.fabedit);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intentLogOut = new Intent(Dashboard.this, MainActivity.class);
                startActivity(intentLogOut);
                finish();
            }
        });
        iniPopup();
        setupPopupImageClick();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAddPost.show();
            }
        });

    }

    private void setupPopupImageClick() {
        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here when image clicked we need to open the gallery
                // before we open the gallery we need to check if our app have the access to user files
                // we did this before in register activity I'm just going to copy the code to save time ...

                checkAndRequestForPermission();


            }
        });


    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Dashboard.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(Dashboard.this, "Please accept for required permission", Toast.LENGTH_SHORT).show();

            } else {
                ActivityCompat.requestPermissions(Dashboard.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        } else {
//                 everything goes well : we have permission to access user gallery
            openGallery();
        }


    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            popupPostImage.setImageURI(pickedImgUri);

        }


    }

    private void iniPopup() {

        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.pop_add_post);
        popAddPost.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);


        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                if (!popupTitle.getText().toString().isEmpty()
                        && !popupDescription.getText().toString().isEmpty()
                        && pickedImgUri != null) {


                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("blog_images");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());

                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownlaodLink = uri.toString();
                                    // create post Object
                                    Post post = new Post(popupTitle.getText().toString(),
                                            popupDescription.getText().toString(),
                                            imageDownlaodLink,
                                            currentUser.getUid(),
                                            currentUser.getPhotoUrl().toString());

                                    // Add post to firebase database

                                    addPost(post);



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // something goes wrong uploading picture

                                    showMessage(e.getMessage());
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);



                                }
                            });


                        }
                    });

                }

                else {
                    showMessage("Please verify all input fields and choose Post Image");
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    private void addPost(Post post) {

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        // get post unique ID and upadte post key
        String key = myRef.getKey();
        post.setPostKey(key);

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post Added successfully");
                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });

    }

    private void showMessage(String message) {

        Toast.makeText(Dashboard.this, message, Toast.LENGTH_LONG).show();
    }
}