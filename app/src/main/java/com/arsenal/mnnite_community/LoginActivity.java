package com.arsenal.mnnite_community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText emailText, passText;
    Button loginbtn, sugnUpbtn;
    TextView forgotPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth= FirebaseAuth.getInstance();
        emailText=findViewById(R.id.emailEditText);
        passText= findViewById(R.id.passwordEditText);
        loginbtn=findViewById(R.id.loginButton);
        forgotPass = findViewById(R.id.forgotPasswordTextView);
        sugnUpbtn =findViewById(R.id.btnSignup);

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(emailText.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                showMessage("Email Sent");
                            }
                        });
            }
        });
        sugnUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent =new Intent(LoginActivity.this,MainActivity.class);
                startActivity(signUpIntent);
                finish();
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailText.getText().toString();
                String password= passText.getText().toString();
                if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
                    showMessage("Enter every detail");
                }
                else {
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        showMessage("Log In Successful");
                                        Intent dashboardIntent =new Intent(LoginActivity.this,Dashboard.class);
                                        startActivity(dashboardIntent);
                                        finish();
                                    }
                                    else{
                                        showMessage("Please enter correct details");
                                    }
                                }

                            });
                }
            }
        });



    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}