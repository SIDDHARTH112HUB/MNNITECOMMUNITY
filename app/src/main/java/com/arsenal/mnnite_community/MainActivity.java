package com.arsenal.mnnite_community;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText nameText,emailText,passwordText;
    Button signupBtn , loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        nameText=findViewById(R.id.editName);
        emailText=findViewById(R.id.editEmail);
        passwordText=findViewById(R.id.editPass);
        signupBtn=findViewById(R.id.signupBtn);
        loginBtn=findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent =new Intent(com.arsenal.mnnite_community.MainActivity.this,LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =nameText.getText().toString();
                String email=emailText.getText().toString();
                String password= passwordText.getText().toString();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(name)||TextUtils.isEmpty(password))
                {
                    Toast.makeText(com.arsenal.mnnite_community.MainActivity.this, "Enter Every Details", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<6){
                    Toast.makeText(com.arsenal.mnnite_community.MainActivity.this, "password must be atleast 6 charecter long", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(com.arsenal.mnnite_community.MainActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(com.arsenal.mnnite_community.MainActivity.this,Dashboard.class));
                                    }
                                    else{
                                        Toast.makeText(com.arsenal.mnnite_community.MainActivity.this, "Couldn't create a Account", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}