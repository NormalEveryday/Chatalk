package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.rpc.context.AttributeContext;

public class ForgetPasswordActivity extends AppCompatActivity {
    EditText email;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        email = findViewById(R.id.Email);
        send = findViewById(R.id.send);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString();
                if(em.isEmpty()){
                    Toast.makeText(ForgetPasswordActivity.this,"Email can't be null",Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.sendPasswordResetEmail(em).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgetPasswordActivity.this,"Check your email",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ForgetPasswordActivity.this,"Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}