package com.example.chatalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ProfileFriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friends);
        String UserUID = getIntent().getStringExtra("userKey");

    }
}