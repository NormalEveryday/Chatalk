package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
    ImageView editprofile,addFriend, decline;

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String profileImageUrl,uname,description,oemail;
    FirebaseRecyclerAdapter<Posts,MyHolder> adapter;
    FirebaseRecyclerOptions<Posts> options;

    DatabaseReference PostRef,LikeRef,CommentRef,requestRef,friendRef;
    String userID;

    RecyclerView recyclerView;
    String CurrentState = "nothing";

    public static final String PREFERENCE_FILE_KEY = "com.example.chatalk.PREFERENCES";
    public static final String CURRENT_STATE_KEY = "CurrentState";
    public static SharedPreferences.Editor editor;


    String myusername,myprofileImage,mydesc,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_friend);
        username = findViewById(R.id.username);
        desc = findViewById(R.id.desc);
        userImage = findViewById(R.id.userImage);
        addFriend = findViewById(R.id.addFriend);
        recyclerView = findViewById(R.id.recyclerView);
        decline = findViewById(R.id.decline);
        userID = getIntent().getStringExtra("userID");

        //sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        CurrentState = sharedPreferences.getString(CURRENT_STATE_KEY,"nothing");
        editor.apply();



        Log.d("DEBUG","CurrentState: "+CurrentState);
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        //like child realtime db
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        CommentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        mUserRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uname = snapshot.child("username").getValue().toString();
                profileImageUrl = snapshot.child("profileImage").getValue().toString();
                description = snapshot.child("description").getValue().toString();
                oemail = snapshot.child("email").getValue().toString();

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
            }
        });

        LoadPost();
        checkUserExistance(userID);

        LoadMyProfile();
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Unfriend(userID);
            }
        });


    }

    private void LoadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myusername = snapshot.child("username").getValue().toString();
                mydesc = snapshot.child("description").getValue().toString();
                myprofileImage = snapshot.child("profileImage").getValue().toString();
                email = snapshot.child("email").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Unfriend(String userID) {
        if(CurrentState.equals("friend")){
            friendRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        friendRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ViewFriendActivity.this,"Unfriend",Toast.LENGTH_SHORT).show();
                                CurrentState = "nothing";
                                ViewFriendActivity.editor.putString(CURRENT_STATE_KEY,"nothing");
                                editor.apply();
                                addFriend.setImageResource(R.drawable.ic_add_friend_white);
                                decline.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });

            friendRef.child(mUser.getUid()).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){

                        CurrentState = "nothing";
                        ViewFriendActivity.editor.putString(CURRENT_STATE_KEY,"nothing");
                        editor.apply();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        if(CurrentState.equals("he_sent_pending")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","decline");
            hashMap.put("ptime", String.valueOf(System.currentTimeMillis()));
            requestRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this,"Decline Friend",Toast.LENGTH_SHORT).show();
                        CurrentState = "he_sent_decline";
                        ViewFriendActivity.editor.putString(CURRENT_STATE_KEY,"he_sent_decline");
                        editor.apply();
                        decline.setVisibility(View.GONE);
                        addFriend.setVisibility(View.GONE);
                        requestRef.child(userID).child(mUser.getUid()).removeValue();

                    }
                }
            });

        }
    }

    private void checkUserExistance(String userID) {
        friendRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    CurrentState = "friend";
                    ViewFriendActivity.editor.putString(CURRENT_STATE_KEY,"friend");
                    editor.apply();
                    addFriend.setImageResource(R.drawable.ic_chat_white);
                    decline.setImageResource(R.drawable.decline_white);
                    decline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        requestRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").getValue().equals("pending")){
                        CurrentState = "I_sent_pending";
                        ViewFriendActivity.editor.putString(CURRENT_STATE_KEY,"I_sent_pending");
                        editor.apply();
                        addFriend.setImageResource(R.drawable.cancel);
                        decline.setVisibility(View.GONE);
                    }
                }
                if(snapshot.exists()){
                    if(snapshot.child("status").getValue().equals("decline")){
                        CurrentState = "I_sent_decline";
                        ViewFriendActivity.editor.putString(CURRENT_STATE_KEY,"I_sent_decline");
                        editor.apply();
                        addFriend.setImageResource(R.drawable.cancel);
                        decline.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        requestRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").getValue().toString().equals("pending")){
                        CurrentState = "he_sent_pending";
                        ViewFriendActivity.editor.putString(CURRENT_STATE_KEY,"he_sent_pending");
                        editor.apply();
                        addFriend.setImageResource(R.drawable.accept);
                        decline.setImageResource(R.drawable.reject);
//
//                        decline.setVisibility(View.GONE);
//                        addFriend.setVisibility(View.GONE);
                        decline.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(CurrentState.equals("nothing")){
            CurrentState = "nothing";
            ViewFriendActivity.editor.putString(CURRENT_STATE_KEY,"nothing");
            editor.apply();
            addFriend.setImageResource(R.drawable.ic_add_friend_white);
            decline.setVisibility(View.VISIBLE);
        }
    }

    private void PerformAction(String userID) {
        if(CurrentState.equals("nothing")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","pending");
            hashMap.put("ptime", String.valueOf(System.currentTimeMillis()));
            requestRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ViewFriendActivity.this,"Request send!",Toast.LENGTH_SHORT).show();
                        decline.setVisibility(View.GONE);
                        CurrentState = "I_sent_pending";
                        ViewFriendActivity.editor.putString(CURRENT_STATE_KEY,"I_sent_pending");
                        editor.apply();
                        addFriend.setImageResource(R.drawable.cancel);
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
                        ViewFriendActivity.editor.putString(CURRENT_STATE_KEY,"nothing");
                        editor.apply();
                        addFriend.setImageResource(R.drawable.ic_add_friend_white);
                        decline.setVisibility(View.GONE);

                    }
                }
            });
        }
        if(CurrentState.equals("he_sent_pending")){

            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        final HashMap hashMap = new HashMap();
                        hashMap.put("status","friend");
                        hashMap.put("username",uname);
                        hashMap.put("profileImage",profileImageUrl);
                        hashMap.put("email",oemail);




                        final HashMap hashMap1 = new HashMap();
                        hashMap1.put("username",myusername);
                        hashMap1.put("profileImage",myprofileImage);
                        hashMap1.put("status","friend");
                        hashMap1.put("email",email);


                        friendRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    friendRef.child(userID).child(mUser.getUid()).updateChildren(hashMap1).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            Toast.makeText(ViewFriendActivity.this,"Added Friend",Toast.LENGTH_SHORT).show();
                                            CurrentState = "friend";
                                            ViewFriendActivity.editor.putString(CURRENT_STATE_KEY,"friend");
                                            editor.apply();
                                            addFriend.setImageResource(R.drawable.ic_chat_white);
                                            decline.setImageResource(R.drawable.decline_white);
                                            decline.setVisibility(View.VISIBLE);
                                            requestRef.child(userID).child(mUser.getUid()).removeValue();

                                        }
                                    });
                                }
                            }
                        });

                    }
                }
            });
            requestRef.child(mUser.getUid()).child(userID).removeValue();


        }
        if(CurrentState.equals("he_sent_decline")){
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        CurrentState = "nothing";
                        editor.putString(CURRENT_STATE_KEY,"nothing");
                        editor.apply();
                    }
                }
            });
        }
        if(CurrentState.equals("friend")){
            //
            friendRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        CurrentState = "nothing";
                        ViewFriendActivity.editor.putString(CURRENT_STATE_KEY,"nothing");
                        editor.apply();
                    }else {

                        Intent intent = new Intent(ViewFriendActivity.this,ChatActivity.class);
                        intent.putExtra("OtherUserID",userID);
                        startActivity(intent);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            requestRef.child(mUser.getUid()).child(userID).removeValue();

        }
//        if(CurrentState.equals("he_sent_pending")){
//            HashMap hashMap = new HashMap();
//            hashMap.put("status","decline");
//            requestRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
//                @Override
//                public void onComplete(@NonNull Task task) {
//                    if(task.isSuccessful()){
//                        Toast.makeText(ViewFriendActivity.this,"Decline Friend",Toast.LENGTH_SHORT).show();
//                        CurrentState = "he_sent_decline";
//                        decline.setVisibility(View.GONE);
//                        addFriend.setVisibility(View.GONE);
//                    }
//                }
//            });
//
//        }
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

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("CurrentState", CurrentState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        CurrentState = savedInstanceState.getString("nothing");
//    }



}