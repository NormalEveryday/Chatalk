package com.example.chatalk;


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImage;
    TextView email,username,mess,status;
    public FriendViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.profileImage);
        email = itemView.findViewById(R.id.email);
        username = itemView.findViewById(R.id.userNam);
        mess = itemView.findViewById(R.id.mess);
        status = itemView.findViewById(R.id.State);
    }
}
