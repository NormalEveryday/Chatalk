package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatalk.MainActivity;
import com.example.chatalk.RegisterActivity;
import com.example.chatalk.SetupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout inputPassword,inputEmail;
    Button loginbtn;

    ProgressDialog loadingbar;
    FirebaseAuth firebaseAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    private TextView register,forgetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitivity);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        loginbtn = findViewById(R.id.loginbtn);
        forgetPassword = findViewById(R.id.forgetpassword);
        register = findViewById(R.id.registerbtn);
        loadingbar = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
    }


    private void Login() {
        String email = inputEmail.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();
        if (email.isEmpty() || !email.contains("@gmail")) {
            showError(inputEmail, "Email is not valid");
        } else if (password.isEmpty() || password.length() < 8) {
            showError(inputPassword, "Pass work must be greater than 8 character");
        }else {
            loadingbar.setTitle("Login");
            loadingbar.setMessage("Login is in progress");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();


            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        loadingbar.dismiss();
                        Toast.makeText(LoginActivity.this,"Login success",Toast.LENGTH_SHORT).show();
                        mUser = FirebaseAuth.getInstance().getCurrentUser();
                        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
                        if(mRef!=null){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(LoginActivity.this, SetupActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                        finish();
                    }else{
                        loadingbar.dismiss();
                        Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showError(TextInputLayout inputEmail, String email_is_not_valid) {
        inputEmail.setError(email_is_not_valid);
        inputEmail.requestFocus();
    }
}