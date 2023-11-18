package com.example.chatalk.Utills;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onPause() {
        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        Date date = new Date();
        SimpleDateFormat formatter =new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        final String strDate = formatter.format(date);
        mUserRef.child(mUser.getUid()).child("status").setValue("Last seen: "+strDate);
        super.onPause();

    }

    @Override
    protected void onStop() {
        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        Date date = new Date();
        SimpleDateFormat formatter =new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        final String strDate = formatter.format(date);
        mUserRef.child(mUser.getUid()).child("status").setValue("Last seen: "+strDate);
        super.onStop();
    }

    @Override
    protected void onStart() {
        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserRef.child(mUser.getUid()).child("status").setValue("online");
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        Date date = new Date();
        SimpleDateFormat formatter =new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        final String strDate = formatter.format(date);
        mUserRef.child(mUser.getUid()).child("status").setValue("Last seen: "+strDate);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserRef.child(mUser.getUid()).child("status").setValue("online");
        super.onResume();
    }

//    private void updateStatus(String status) {
//        Date date = new Date();
//        SimpleDateFormat formatter =new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
//        final String strDate = formatter.format(date);
//        mUserRef.child(mUser.getUid()).child("status").setValue("Last seen: "+strDate);
//    }
}
