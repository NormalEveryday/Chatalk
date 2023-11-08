package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatalk.Utills.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFriendActivity extends AppCompatActivity {
    CircleImageView userImage;
    TextView username,desc;
    ImageView editprofile,addFriend;

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String profileImageUrl,uname,description;
    FirebaseRecyclerAdapter<Posts,MyHolder> adapter;
    FirebaseRecyclerOptions<Posts> options;

    DatabaseReference PostRef,LikeRef,CommentRef,requestRef,friendRef;
    String userID;

    RecyclerView recyclerView;
    String CurrentState = "nothing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend);
        username = findViewById(R.id.username);
        desc = findViewById(R.id.desc);
        userImage = findViewById(R.id.userImage);
        addFriend = findViewById(R.id.addFriend);
        recyclerView = findViewById(R.id.recyclerView);
        userID = getIntent().getStringExtra("userID");

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        //like child realtime db
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        CommentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uname = snapshot.child("username").getValue().toString();
                profileImageUrl = snapshot.child("profileImage").getValue().toString();
                description = snapshot.child("username").getValue().toString();

                username.setText(uname);
                desc.setText(description);
                Picasso.get().load(profileImageUrl).into(userImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PerformAction(userID);
                addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ViewFriendActivity.this,"Decline send!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        LoadPost();

    }

    private void PerformAction(String userID) {
        if(CurrentState.equals("nothing")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","pending");
            requestRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this,"Request send!",Toast.LENGTH_SHORT).show();
                        CurrentState = "I_sent_pending";

                    }
                }
            });

        }
        if(CurrentState.equals("I_sent_pending") || CurrentState.equals("I_sent_decline")){
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this,"Cancelled request send!",Toast.LENGTH_SHORT).show();
                        CurrentState = "nothing";

                    }
                }
            });
        }
        if(CurrentState.equals("he_send_pending")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","friend");
            hashMap.put("username",uname);
            hashMap.put("profileImage",profileImageUrl);
            friendRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        friendRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Toast.makeText(ViewFriendActivity.this,"Added Friend",Toast.LENGTH_SHORT).show();
                                CurrentState = "friend";
                            }
                        });
                    }
                }
            });

        }
        if(CurrentState.equals("friend")){
            //
        }
    }


    private void LoadPost() {
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("uid").equalTo(userID);
        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query,Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull Posts model) {
                final String postKey = getRef(position).getKey();
                holder.postDesc.setText(model.getPostDesc());
//                holder.username.setText(model.getUsername());
                holder.timeAgo.setText(model.getDatePost());
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users");
                mRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
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
                holder.countLikes(postKey,userID,LikeRef);
                CommentRef = FirebaseDatabase.getInstance().getReference("Comments").child(postKey);
                holder.CountComment(CommentRef);
                holder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LikeRef.child(postKey).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    LikeRef.child(postKey).child(userID).removeValue();
                                    //Change color here
                                    holder.likeImage.setImageResource(R.drawable.ic_thumb_up_foreground);
                                    notifyDataSetChanged();
                                }else {
                                    LikeRef.child(postKey).child(userID).setValue("like");
                                    //Change color here
                                    holder.likeImage.setImageResource(R.drawable.ic_thumb_up_blue);
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ViewFriendActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                holder.commentsImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ViewFriendActivity.this,CommentActivity.class);
                        intent.putExtra("username",model.getUsername());
                        intent.putExtra("image_profile",model.getUserProfileImage());
                        intent.putExtra("timeAgo",model.getDatePost());
                        intent.putExtra("postDesc",model.getPostDesc());
                        intent.putExtra("PostImageUrl",model.getPostImageUrl());
                        intent.putExtra("postKey",postKey);
                        intent.putExtra("datePost",model.getDatePost());
                        intent.putExtra("postKey",postKey);
                        startActivity(intent);

                    }
                });


            }

            @NonNull
            @Override
            public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post,parent,false);

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