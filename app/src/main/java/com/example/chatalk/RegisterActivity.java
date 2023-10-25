package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatalk.SetupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout inputEmail,inputPassword,inputConfirmPassword;

    private Button btnRegister;
    private TextView alreadyhaveaccount;

    FirebaseAuth firebaseAuth;
    ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.confirmPassword);
        btnRegister = findViewById(R.id.registerbtn);
        alreadyhaveaccount = findViewById(R.id.haveAccount);
        firebaseAuth = FirebaseAuth.getInstance();
        //diaglog
        loadingbar = new ProgressDialog(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registration();
            }
        });

        alreadyhaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void Registration() {
        String email = inputEmail.getEditText().getText().toString();
        String password = inputPassword.getEditText().getText().toString();
        String confirmpassword = inputConfirmPassword.getEditText().getText().toString();
        if(email.isEmpty() || !email.contains("@gmail")){
            showError(inputEmail,"Email is not valid");
        } else if (password.isEmpty() || password.length() < 8) {
            showError(inputPassword,"Pass work must be greater than 8 character");
        } else if (!confirmpassword.equals(password)) {
            showError(inputConfirmPassword,"Password does not match");
        }
        else {
            loadingbar.setTitle("Registration");
            loadingbar.setMessage("Registration is in progress");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        loadingbar.dismiss();
                        Toast.makeText(RegisterActivity.this,"Registration success",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        loadingbar.dismiss();
                        Toast.makeText(RegisterActivity.this,"Registration failed",Toast.LENGTH_SHORT).show();
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