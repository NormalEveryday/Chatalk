package com.example.chatalk;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyHolder extends RecyclerView.ViewHolder {
    CircleImageView userProfileImage;
    ImageView postImage, likeImage, commentsImage;
    TextView username, postDesc, timeAgo,
            likeCounter, commentsCounter;

    ImageView sendComment;
    EditText inputComment;

    public static RecyclerView recyclerView;

    String ptime;
    public MyHolder(@NonNull View itemView) {
        super(itemView);
        userProfileImage = itemView.findViewById(R.id.profileImagePost);
        username = itemView.findViewById(R.id.profileUsernamePost);
        postImage = itemView.findViewById(R.id.postImage);
        postDesc = itemView.findViewById(R.id.postDesc);
        timeAgo = itemView.findViewById(R.id.timeAgo);
        likeImage = itemView.findViewById(R.id.likeImage);
        commentsImage = itemView.findViewById(R.id.commentsImage);
        likeCounter = itemView.findViewById(R.id.likeCounter);
        commentsCounter = itemView.findViewById(R.id.commentsCount);

        inputComment = itemView.findViewById(R.id.inputComment);
        sendComment = itemView.findViewById(R.id.sendComment);

        recyclerView = itemView.findViewById(R.id.recyclerView2);



    }

    public CircleImageView getProfileimage() {
        return userProfileImage;
    }

    public void setProfileimage(CircleImageView profileimage) {
        this.userProfileImage = profileimage;
    }

    public ImageView getPostImage() {
        return postImage;
    }

    public void setPostImage(ImageView postImage) {
        this.postImage = postImage;
    }

    public ImageView getLikeImage() {
        return likeImage;
    }

    public void setLikeImage(ImageView likeImage) {
        this.likeImage = likeImage;
    }

    public ImageView getCommentsImage() {
        return commentsImage;
    }

    public void setCommentsImage(ImageView commentsImage) {
        this.commentsImage = commentsImage;
    }

    public TextView getUsername() {
        return username;
    }

    public void setUsername(TextView username) {
        this.username = username;
    }

    public TextView getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(TextView postDesc) {
        this.postDesc = postDesc;
    }

    public TextView getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(TextView timeAgo) {
        this.timeAgo = timeAgo;
    }

    public TextView getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(TextView likeCounter) {
        this.likeCounter = likeCounter;
    }

    public TextView getCommentsCounter() {
        return commentsCounter;
    }

    public void setCommentsCounter(TextView commentsCounter) {
        this.commentsCounter = commentsCounter;
    }

    public void countLikes(String postKey, String uid, DatabaseReference likeRef) {
        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalLikes = (int) snapshot.getChildrenCount();
                    likeCounter.setText(totalLikes+"");

                }else {
                    likeCounter.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(uid).exists()){
                    likeImage.setImageResource(R.drawable.ic_thumb_up_blue);
                }else {
                    likeImage.setImageResource(R.drawable.ic_thumb_up_foreground);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void CountComment(DatabaseReference CommentRef) {
        CommentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalComment =(int) snapshot.getChildrenCount();
                    commentsCounter.setText(totalComment+"");
                }else{
                    commentsCounter.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
