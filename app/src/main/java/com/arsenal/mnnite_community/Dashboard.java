package com.arsenal.mnnite_community;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static com.arsenal.mnnite_community.R.menu.main_menu;

public class Dashboard extends AppCompatActivity {

    private static final int PReqCode = 2;
    private static final int REQUESCODE = 2;
    private FloatingActionButton edit;
    ImageView popupPostImage, popupAddBtn;
    TextView popupTitle, popupDescription;
    ProgressBar popupClickProgress;
    private Uri pickedImgUri;
    //firebase declarations
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    FirebaseFirestore firebaseFirestore;
    String userId;

    private BottomNavigationView mainbottomNav;

    private Toolbar mainToolbar;

    private homeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private Account_Fragment accountFragment;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mainbottomNav = findViewById(R.id.mainBottomNav);


        //instantiating firebase declarations
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();



        mainToolbar=(Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setTitle("Home");


        homeFragment=new homeFragment();
        notificationFragment=new NotificationFragment();
        accountFragment= new Account_Fragment();

        replaceFragment(homeFragment);
        mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.bottom_action_home:
                        replaceFragment(homeFragment);
                        return true;
                    case R.id.bottom_action_notification:
                        replaceFragment(notificationFragment);
                        return true;
                    case R.id.bottom_action_account:
                        replaceFragment(accountFragment);
                        return true;

                    default:
                        return false;

                }


            }
        });

        edit = findViewById(R.id.fabedit);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this,AddPost.class));

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_logout:
                logout();

                return true;
            case R.id.setting_button   :
                startSetting();

                return  true;

            default:
                return false;



        }


    }

    private void startSetting() {
        Intent intentLogOut = new Intent(Dashboard.this, setup_activity.class);
        startActivity(intentLogOut);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intentLogOut = new Intent(Dashboard.this, LoginActivity.class);
        startActivity(intentLogOut);
        finish();

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

    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();


    }

    private void showMessage(String message) {

        Toast.makeText(Dashboard.this, message, Toast.LENGTH_LONG).show();
    }
}