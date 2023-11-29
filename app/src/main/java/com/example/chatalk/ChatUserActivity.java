package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatalk.Utills.BaseActivity;
import com.example.chatalk.Utills.Chat;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatUserActivity extends AppCompatActivity {

    Toolbar toolbar;
    public static final int CHAT_ACTIVITY_REQUEST_CODE = 1;
    RecyclerView recyclerView;

    FirebaseRecyclerAdapter<Friends, FriendViewHolder> adapter;
    FirebaseRecyclerOptions<Friends> options;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chats");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        status = findViewById(R.id.mess);
        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        LoadFriend("");


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHAT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // SMS sent successfully, reload friends
                LoadFriend("");
            }
        }
    }

    public void LoadFriend(String s) {
        Query query = mRef.child(mUser.getUid()).orderByChild("ptime");
        //.startAt(s).endAt(s + "\uf8ff")
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();
        adapter = new FirebaseRecyclerAdapter<Friends, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Friends model) {
                holder.username.setText(model.getUsername());
                Picasso.get().load(model.getProfileImage()).into(holder.profileImage);

                DatabaseReference messRef = FirebaseDatabase.getInstance().getReference().child("Message");



                messRef.child(mUser.getUid()).child(getRef(position).getKey()).orderByKey().limitToLast(1)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                        Chat lastMessage = messageSnapshot.getValue(Chat.class);
                                        if (lastMessage != null) {
                                            // Display the last message
                                            holder.mess.setText(lastMessage.getUsername()+": "+lastMessage.getSms());

                                        }
                                    }
                                } else {
                                    // No messages found
                                    holder.mess.setText("No messages");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                //get user to chat
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ChatUserActivity.this, ChatActivity.class);
                        intent.putExtra("OtherUserID", getRef(position).getKey());
                        intent.putExtra("email", model.getEmail());
                        Date date = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                        final String strDate = formatter.format(date);
                        intent.putExtra("seenStatus","seen: "+ strDate);
                        startActivity(intent);

                        finish();
                    }
                });

                holder.itemView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ChatUserActivity.this,"click",Toast.LENGTH_SHORT).show();
                        PopupMenu popupMenu = new PopupMenu(ChatUserActivity.this,view);
                        popupMenu.getMenuInflater().inflate(R.menu.menu_chat, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                if(menuItem.getItemId() == R.id.unseen){
                                    DatabaseReference messRef = FirebaseDatabase.getInstance().getReference().child("Message");
                                    messRef.child(getRef(position).getKey()).child(mUser.getUid()).orderByChild("ptime").limitToLast(1).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                                dataSnapshot.child("status").getRef().setValue("unseen");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                                if(menuItem.getItemId() == R.id.deletMess){
                                    mRef.child(mUser.getUid()).child(getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                snapshot.child("statusMessage").getRef().setValue("hide");
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });

                if(model.getStatusMessage().equals("hide")){
                    holder.itemView.setVisibility(View.GONE);

                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);
                }else {
                    holder.itemView.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    holder.itemView.setLayoutParams(params);
                }


            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_chat, parent, false);
                return new FriendViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChatUserActivity.this,MainActivity.class));
        finish();
    }
}