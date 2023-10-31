package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.service.autofill.SavedDatasetsInfo;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 101;
    CircleImageView profileImage;
    EditText name,desc,email;
    Button save;

    Uri imageuri;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    StorageReference StorageRef;

    ProgressDialog mloadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        profileImage = findViewById(R.id.profile_image);
        name = findViewById(R.id.name);
        desc = findViewById(R.id.desc);
        email = findViewById(R.id.email);
        save = findViewById(R.id.saveInfo);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        StorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        mloadingBar = new ProgressDialog(this);
        email.setText(mUser.getEmail());



        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);

            }
        });

        save.setTextColor(Color.WHITE);
        save.setBackgroundColor(Color.BLACK);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveData();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode== RESULT_OK && data != null){
            imageuri = data.getData();
            profileImage.setImageURI(imageuri);
        }
    }

    public void SaveData(){
        String username = name.getText().toString();
        String emaill = email.getText().toString();
        String mydesc = desc.getText().toString();
        if(username.isEmpty() || username.length()<3){
            showError(name,"username isn't valid !");
        }
        else if(mydesc.isEmpty()){
            showError(desc,"You need to describe about your self");
        } else if (imageuri ==  null) {
            Toast.makeText(SetupActivity.this,"Please select image",Toast.LENGTH_SHORT).show();
        } else {
            mloadingBar.setTitle("Setup profile");
            mloadingBar.setCanceledOnTouchOutside(false);
            mloadingBar.show();


            StorageRef.child(mUser.getUid()).putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        StorageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashMap = new HashMap();
                                hashMap.put("username",username);
                                hashMap.put("email",emaill);
                                hashMap.put("description", mydesc);
                                hashMap.put("profileImage",uri.toString());
                                hashMap.put("status","offline");
                                mRef.child(mUser.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {

                                        Intent intent = new Intent(SetupActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        mloadingBar.dismiss();
                                        Toast.makeText(SetupActivity.this,"Save",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mloadingBar.dismiss();
                                        Toast.makeText(SetupActivity.this,"Save failed "+e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        });
                    }
                }
            });

        }
    }
    private void showError(EditText inputEmail, String email_is_not_valid) {
        inputEmail.setError(email_is_not_valid);
        inputEmail.requestFocus();
    }
}