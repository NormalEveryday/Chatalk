package com.example.chatalk;


//import static com.example.mediaapp.MyHolder.recyclerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.sax.EndElementListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatalk.Utills.Comment;
import com.example.chatalk.Utills.Posts;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.StructuredQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {
    FirebaseRecyclerAdapter<Posts,MyHolder> adapter;
    //    FirebaseRecyclerOptions<Posts> options;
    DatabaseReference mRef,PostRef,LikeRef,CommentRef;

    FirebaseStorage storage;
    StorageReference StoreRef;

    CircleImageView image_profile,image_comment,getImage_comment;

    TextView usernameHead,usernameComment,timeAgo,postDesc;

    String username,profileImageUrl;
    ImageView postImage, likeImage,commentImage, sendComment;
    EditText inputComment;


    TextView likeCounter,commentsCounter;
    FirebaseUser mUser;
    FirebaseAuth mAuth;


    FirebaseRecyclerOptions<Comment> commentOption;
    FirebaseRecyclerAdapter<Comment,CommentViewHolder> commentAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        image_profile = findViewById(R.id.profileImagePost);
        usernameHead = findViewById(R.id.profileUsernamePost);
        timeAgo = findViewById(R.id.timeAgo);
        postDesc = findViewById(R.id.postDesc);
        postImage = findViewById(R.id.postImage);
        likeImage = findViewById(R.id.likeImage);
        image_comment = findViewById(R.id.avatar);
        commentImage = findViewById(R.id.commentsImage);
        inputComment = findViewById(R.id.inputComment);
        sendComment = findViewById(R.id.sendComment);
        MyHolder.recyclerView = findViewById(R.id.recyclerView2);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        StoreRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        likeCounter = findViewById(R.id.likeCounter);
        commentsCounter = findViewById(R.id.commentsCount);
        mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(image_comment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LoadPost();

    }
    public void LoadPost(){
        Picasso.get().load(getIntent().getStringExtra("image_profile")).into(image_profile);
        usernameHead.setText(getIntent().getStringExtra("username"));
        timeAgo.setText(getIntent().getStringExtra("timeAgo"));
        postDesc.setText(getIntent().getStringExtra("postDesc"));
        Picasso.get().load(getIntent().getStringExtra("PostImageUrl")).into(postImage);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddComment();
            }
        });
        DatabaseReference likes = FirebaseDatabase.getInstance().getReference("Likes").child(getIntent().getStringExtra("postKey"));
        likes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalLikes = (int)snapshot.getChildrenCount();
                    likeCounter.setText(totalLikes+"");
                    likeImage.setImageResource(R.drawable.ic_thumb_up_blue);


                }else {
                    likeImage.setImageResource(R.drawable.ic_thumb_up_foreground);
                    likeCounter.setText("0");
//                    likeImage.setImageResource(R.drawable.ic_thumb_up_blue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likes.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            LikeRef.child(getIntent().getStringExtra("postKey")).child(mUser.getUid()).removeValue();
                        }else {
                            LikeRef.child(getIntent().getStringExtra("postKey")).child(mUser.getUid()).setValue("like");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        DatabaseReference comment = FirebaseDatabase.getInstance().getReference("Posts")
                .child(getIntent().getStringExtra("postKey")).child("Comments");

        comment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalComment = (int) snapshot.getChildrenCount();
                    commentsCounter.setText(totalComment+"");
                }else {
                    commentsCounter.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputComment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(CommentActivity.this.INPUT_METHOD_SERVICE);
                imm.showSoftInput(inputComment, InputMethodManager.SHOW_IMPLICIT);

            }
        });


        LoadComment();

    }

    private void LoadComment() {

        MyHolder.recyclerView.setLayoutManager(new LinearLayoutManager(CommentActivity.this));


        Query query = FirebaseDatabase.getInstance().getReference("Posts")
                .child(getIntent().getStringExtra("postKey"))
                .child("Comments").orderByChild("datePost");

        commentOption = new FirebaseRecyclerOptions.Builder<Comment>().setQuery(query,Comment.class).build();
        commentAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(commentOption) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
                Picasso.get().load(model.getProfileImageUrl()).into(holder.profileImage);
                holder.username.setText(model.getUsername());
                holder.comment.setText(model.getComment());
                Log.d("DEBUG","info comment:"+model.getComment()+model.getUsername()+model.getProfileImageUrl());

            }
            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_comment,parent,false);
                return new CommentViewHolder(view);
            }
        };


        commentAdapter.startListening();
        MyHolder.recyclerView.setAdapter(commentAdapter);
        LinearLayoutManager layoutManager = (LinearLayoutManager) MyHolder.recyclerView.getLayoutManager();
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

    }

    private void AddComment() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference User = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        User.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profileImageUrl = snapshot.child("profileImage").getValue(String.class);
                username = snapshot.child("username").getValue(String.class);
                Log.d("DEBUG","text: "+username + profileImageUrl);


                HashMap hashMap = new HashMap();
                String comment = inputComment.getText().toString();
                hashMap.put("comment",comment);
                hashMap.put("ptime", timestamp);
                hashMap.put("username",username);
                hashMap.put("profileImageUrl",profileImageUrl);
                Log.d("DEBUG","text: "+username + profileImageUrl);
                DatabaseReference dataref = FirebaseDatabase.getInstance().getReference("Posts").child(getIntent().getStringExtra("postKey")).child("Comments");
                dataref.child(timestamp).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(CommentActivity.this,"Comment success",Toast.LENGTH_SHORT).show();
                            inputComment.setText("");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CommentActivity.this, "Comment Failed", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}