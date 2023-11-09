package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatalk.Utills.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    CircleImageView userImage;
    TextView username, des;
    ImageView edit, safemode, unsafemode;

    FirebaseRecyclerAdapter<Posts, MyHolder> adapter;
    FirebaseRecyclerOptions<Posts> options;
    RecyclerView recyclerView;
    DatabaseReference PostRef, LikeRef, CommentRef;
    FirebaseUser mUser;
    StorageReference StorageRef;

    public static String CurrentState = "unsafemode";

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;


    public static final String PREFERENCE_FILE_KEY = "com.example.chatalk.PREFERENCES";
    public static final String CURRENT_STATE_KEY = "CurrentState";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(ProfileActivity.this,CurrentState,Toast.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.username);
        userImage = findViewById(R.id.userImage);
        recyclerView = findViewById(R.id.recyclerView);
        des = findViewById(R.id.desc);
        edit = findViewById(R.id.editprofile);
        safemode = findViewById(R.id.safemode);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, editProfileActivity.class));
            }
        });

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        CurrentState = sharedPreferences.getString(CURRENT_STATE_KEY, "unsafemode");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        //like child realtime db
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        StorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        CommentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        DatabaseReference profile = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        profile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    des.setText(snapshot.child("description").getValue().toString());
                    username.setText(snapshot.child("username").getValue().toString());
                    Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        safemode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CurrentState.equals("unsafemode")){
                    FirebaseAuth.getInstance().signOut();
                    String newEmail = "newuser@example.com";
                    String newPassword = "password123";

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(newEmail, newPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(ProfileActivity.this, "Change to hidden", Toast.LENGTH_SHORT).show();
                                mUser = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());

                                HashMap hashMap = new HashMap();
                                hashMap.put("username", String.valueOf(System.currentTimeMillis()));
                                hashMap.put("email", "");
                                hashMap.put("description", "");
                                hashMap.put("profileImage", "https://firebasestorage.googleapis.com/v0/b/chatalk-75a17.appspot.com/o/ProfileImages%2Fhidden.jpeg?alt=media&token=2d946df3-1c07-4146-a47d-c75fc205a779&_gl=1*le5h5n*_ga*Njc3OTAwOTQwLjE2OTc2NDQxNzA.*_ga_CW55HF8NVT*MTY5OTM4MDkzNy45My4xLjE2OTkzODM0NjguMjkuMC4w");
                                hashMap.put("status", "offline");
                                mRef.setValue(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {

                                        Toast.makeText(ProfileActivity.this, "Log in", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                firebaseAuth.signInWithEmailAndPassword(newEmail, newPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            if (mRef != null) {

                                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);

                                            }
                                            CurrentState = "safemode";
                                            editor.putString(CURRENT_STATE_KEY, "safemode");
                                            editor.apply();
                                            Toast.makeText(ProfileActivity.this,CurrentState,Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {

                                            Toast.makeText(ProfileActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(ProfileActivity.this, "hidden failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                if(CurrentState.equals("safemode")){
                    mUser.delete();
                    DatabaseReference mU = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
                    mU.removeValue();
                    // Chuyển người dùng đến màn hình SplashActivity
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    FirebaseAuth.getInstance().signOut();
                    CurrentState = "unsafemode";
                    editor.putString(CURRENT_STATE_KEY, "unsafemode");
                    editor.apply();
                    Toast.makeText(ProfileActivity.this,CurrentState,Toast.LENGTH_SHORT).show();
                }



            }
        });


        LoadPost();

    }

    private void LoadPost() {
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("uid").equalTo(mUser.getUid());
        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query, Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull Posts model) {
                final String postKey = getRef(position).getKey();
                holder.postDesc.setText(model.getPostDesc());
//                holder.username.setText(model.getUsername());
                holder.timeAgo.setText(model.getDatePost());
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users");
                mRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.username.setText(snapshot.child("username").getValue().toString());
                        Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(holder.userProfileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//                Picasso.get().load(model.getUserProfileImage()).into(holder.userProfileImage);
                Picasso.get().load(model.getPostImageUrl()).into(holder.postImage);
                holder.countLikes(postKey, mUser.getUid(), LikeRef);
                CommentRef = FirebaseDatabase.getInstance().getReference("Comments").child(postKey);
                holder.CountComment(CommentRef);
                holder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LikeRef.child(postKey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    LikeRef.child(postKey).child(mUser.getUid()).removeValue();
                                    //Change color here
                                    holder.likeImage.setImageResource(R.drawable.ic_thumb_up_foreground);
                                    notifyDataSetChanged();
                                } else {
                                    LikeRef.child(postKey).child(mUser.getUid()).setValue("like");
                                    //Change color here
                                    holder.likeImage.setImageResource(R.drawable.ic_thumb_up_blue);
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ProfileActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                holder.commentsImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ProfileActivity.this, CommentActivity.class);
                        intent.putExtra("username", model.getUsername());
                        intent.putExtra("image_profile", model.getUserProfileImage());
                        intent.putExtra("timeAgo", model.getDatePost());
                        intent.putExtra("postDesc", model.getPostDesc());
                        intent.putExtra("PostImageUrl", model.getPostImageUrl());
                        intent.putExtra("postKey", postKey);
                        intent.putExtra("datePost", model.getDatePost());
                        intent.putExtra("postKey", postKey);
                        startActivity(intent);

                    }
                });


            }

            @NonNull
            @Override
            public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post, parent, false);

                return new MyHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }

}