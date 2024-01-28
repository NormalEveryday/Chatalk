package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.example.chatalk.Utills.BaseActivity;
import com.example.chatalk.Utills.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    Toolbar toolbar;

    RecyclerView recyclerView;

    FirebaseRecyclerAdapter<Friends,FriendViewHolder> adapter;
    FirebaseRecyclerOptions<Friends> options;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Friends");
        toolbar.setTitle("Friends");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        CheckFriendExist();


        LoadFriend("");

    }

//    private void CheckFriendExist() {
//        List<String> UserUID = new ArrayList<>();
//        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
//        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
//                        UserUID.add(dataSnapshot.getKey());
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        mRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
//                    if(!UserUID.contains(dataSnapshot.getKey())){
//
//                        dataSnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                                if(error == null){
//
//                                }else {
//                                    Log.e("FirebaseError", "Error removing data: " + error.getMessage());
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
private void CheckFriendExist() {
    DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
    mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                List<String> UserUID = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserUID.add(dataSnapshot.getKey());
                }

                // Now that UserUID is populated, check and remove invalid friends
                mRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                            if (!UserUID.contains(friendSnapshot.getKey())) {
                                friendSnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error != null) {
                                            Log.e("FirebaseError", "Error removing data: " + error.getMessage());
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Error reading data: " + error.getMessage());
                    }
                });
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("FirebaseError", "Error reading data: " + error.getMessage());
        }
    });
}


    public void  LoadFriend(String s){
        Query query = mRef.child(mUser.getUid()).orderByChild("ptime");
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();
        adapter = new FirebaseRecyclerAdapter<Friends, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Friends model) {
                holder.username.setText(model.getUsername());
                holder.email.setText(model.getEmail());
                Picasso.get().load(model.getProfileImage()).into(holder.profileImage);


                DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
                UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            if(dataSnapshot.child("email").getValue().equals(model.getEmail())){
                                if(!dataSnapshot.child("status").getValue().equals("online")){
                                    holder.status.setText("offline");
                                }else {
                                    holder.status.setText(dataSnapshot.child("status").getValue().toString());
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //get user to chat
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(FriendActivity.this, ViewFriendActivity.class);
                        intent.putExtra("userID",getRef(position).getKey().toString());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_friend,parent,false);

                return new FriendViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }


}