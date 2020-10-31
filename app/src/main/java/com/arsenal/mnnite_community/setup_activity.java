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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class setup_activity extends AppCompatActivity {

    private CircleImageView setupImage;
    static int PReqCode = 1 ;
    static int REQUESCODE = 1 ;
    private Uri mainImageUri=null;
    private EditText setupname;
    private Button setupButton;
    private String user_id;
    private ProgressBar progressBar;
    private boolean ischanged=false;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_activity);

        Toolbar setupToolbar =findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setting");

        firebaseAuth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore= FirebaseFirestore.getInstance();

        setupname=findViewById(R.id.setupname);
        setupButton=findViewById(R.id.button);
        progressBar=findViewById(R.id.setup_progress);
        currentUser=firebaseAuth.getCurrentUser();
        user_id = firebaseAuth.getCurrentUser().getUid();

        progressBar.setVisibility(View.VISIBLE);


        firebaseFirestore.collection("users").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String name = documentSnapshot.getString("name");
                    String image = documentSnapshot.getString("image");

                    mainImageUri = Uri.parse(image);
                    setupname.setText(name);

                    RequestOptions placeHolder =new RequestOptions();
                    placeHolder.placeholder(R.mipmap.download);
                    Glide.with(setup_activity.this).load(image).into(setupImage);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(e.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
            }
        }

        );


        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = setupname.getText().toString();
                String userEmail = currentUser.getEmail();

                if (ischanged) {
                    if (!TextUtils.isEmpty(username) && mainImageUri != null) {


                        progressBar.setVisibility(View.VISIBLE);

                        StorageReference image_path = storageReference.child("profile_Images").child(user_id + ".jpg");
                        image_path.putFile(mainImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                // image uploaded succesfully
                                // now we can get our image url

                                image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        storeFirestore(uri, username, userEmail, user_id);
                                    }
                                });
                                image_path.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        String error = e.getMessage();
                                        showMessage("Error : " + e);
                                    }
                                });


                            }
                        });
                    }
                }
                else
                {
                    storeFirestore(null,username,userEmail,user_id);
                }
            }
        });


        setupImage=findViewById(R.id.setupImage);
        setupImage.setOnClickListener(new View.OnClickListener() {
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
                            .start(setup_activity.this);
                }





            }
        });

    }

    private void storeFirestore(Uri uri,String username,String userEmail,String user_id) {
        if(uri==null)
        {
            uri=mainImageUri;
        }
        Map<String,String> userMap = new HashMap<>();
        userMap.put("name",username);
        userMap.put("email",userEmail);
        userMap.put("image",uri.toString());

        firebaseFirestore.collection("users")
                .document(user_id)
                .set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        showMessage("user data saved");
                        startActivity(new Intent(setup_activity.this,Dashboard.class));
                        finish();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage(e.getMessage());
                    }
                })
        ;



        // uri contain user image url
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageUri = result.getUri();
                setupImage.setImageURI(mainImageUri);

                ischanged=true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void checkAndRequestForPermission() {


        if (ContextCompat.checkSelfPermission(setup_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(setup_activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                showMessage("Please accept for required permission");

            }

            else
            {
                ActivityCompat.requestPermissions(setup_activity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else

        {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(setup_activity.this);
        }


    }
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}