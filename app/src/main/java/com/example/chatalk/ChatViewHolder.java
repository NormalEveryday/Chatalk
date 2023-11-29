package com.example.chatalk;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatalk.Utills.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    CircleImageView firstUserProfile,secondUserProfile;
    TextView firstUserText,secondUserText,friendStatus, myStatus;

    public ChatViewHolder(@NonNull View itemView) {

        super(itemView);

        firstUserProfile = itemView.findViewById(R.id.firstUserProfile);
        secondUserProfile = itemView.findViewById(R.id.secondUserProfile);
        firstUserText = itemView.findViewById(R.id.firstUserText);
        secondUserText = itemView.findViewById(R.id.secondUserText);
        friendStatus = itemView.findViewById(R.id.friendStatus);
        myStatus =itemView.findViewById(R.id.myStatus);

    }


}
