package com.example.chatalk;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    public String ptime;
    CircleImageView profileImage;
    TextView username,comment;
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.profile);
        username = itemView.findViewById(R.id.usernameTV);
        comment = itemView.findViewById(R.id.commentTV);



    }
}
