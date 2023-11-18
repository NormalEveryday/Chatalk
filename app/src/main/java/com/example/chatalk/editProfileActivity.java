package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chatalk.Utills.BaseActivity;
import com.example.chatalk.Utills.Posts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class editProfileActivity extends AppCompatActivity {
    EditText username, description;

    FirebaseAuth mAuth;

    FirebaseUser mUser;

    CircleImageView profileImage;
    Button save;

    Uri imageuri, postImageUri;

    StorageReference StorageRef, forPostRef;

    ProgressDialog progressDialog;

    private static final int REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        username = findViewById(R.id.username);
        description = findViewById(R.id.description);

        profileImage = findViewById(R.id.userImage);
        save = findViewById(R.id.save);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        StorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username.setText(snapshot.child("username").getValue().toString());
                    Log.d("DEBUG", "imageurl: " + imageuri);
                    description.setText(snapshot.child("description").getValue().toString());
                    Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(profileImage);
                    Query query = FirebaseDatabase.getInstance().getReference("Posts")
                            .orderByChild("uid").equalTo(mUser.getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                String postID = dataSnapshot.getKey();
                                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(postID);
                                postRef.child("username").setValue(username.getText().toString());
                                // Cần sửa lại
//                                Query query2 = FirebaseDatabase.getInstance().getReference().child("Comments").child(postID)
//                                        .orderByChild("uid").equalTo(mUser.getUid());
//                                query2.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
//                                            String commentId = dataSnapshot2.getKey();
//                                            DatabaseReference comRef = dataSnapshot2.getRef().child(commentId);
//                                            comRef.child("username").setValue(username.getText().toString());
//                                        }
//                                    }
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataKeySnapShot :snapshot.getChildren()){
                                String dateKey = dataKeySnapShot.getKey();
                                for(DataSnapshot commentKeySnapShot : dataKeySnapShot.getChildren()){
                                    String commentKey = commentKeySnapShot.getKey();
                                    DataSnapshot commentSnapShot = commentKeySnapShot.child("uid");
                                    if(commentSnapShot.getValue(String.class).equals(mUser.getUid())){
                                        DatabaseReference updateRef = ref.child(dateKey).child(commentKey).child("username");
                                        updateRef.setValue(username.getText().toString());
                                    }


                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageuri != null) {
                    StorageRef.child(mUser.getUid()).putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            StorageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("username", username.getText().toString());
                                    hashMap.put("profileImage", uri.toString());
                                    hashMap.put("description",description.getText().toString());
                                    Log.d("DEBUG", "nameuser: " + username.getText().toString());
                                    Log.d("DEBUG", "image" + uri.toString());
                                    DatabaseReference root = FirebaseDatabase.getInstance().getReference();

                                    root.child("Users").child(mUser.getUid()).updateChildren(hashMap);


                                }
                            });
                        }
                    });

                } else {

                    HashMap hashMap = new HashMap();
                    hashMap.put("username", username.getText().toString());
                    hashMap.put("description",description.getText().toString());
                    Log.d("DEBUG", "name user: " + username.getText().toString());
                    DatabaseReference root = FirebaseDatabase.getInstance().getReference();

                    root.child("Users").child(mUser.getUid()).updateChildren(hashMap);


                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageuri = data.getData();
            profileImage.setImageURI(imageuri);

        }
    }


}