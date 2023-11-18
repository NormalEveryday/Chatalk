package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatalk.Utills.Friends;
import com.example.chatalk.Utills.Users;
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
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FindFriendActivity extends AppCompatActivity {
    FirebaseRecyclerOptions<Users> options;
    FirebaseRecyclerAdapter<Users,FindFriendViewHolder> adapter;

    FirebaseRecyclerOptions<Users> requestOption;
    FirebaseRecyclerAdapter<Users,FindFriendViewHolder> adapterOption;
    FirebaseUser mUser ;
    FirebaseAuth mAuth;
    Toolbar toolbar;
    RecyclerView recyclerView,requestRecyclerView;
    DatabaseReference friendRef;

    TextView titleRequest;
    Boolean check = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Find Friends");

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        requestRecyclerView = findViewById(R.id.requestRecycler);
        titleRequest = findViewById(R.id.titleRequest);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LoadRequest();
        LoadUser("");

    }

    public void LoadRequest(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String key = dataSnapshot.getKey().toString();
                        for(DataSnapshot mySnapshot : dataSnapshot.getChildren()){
                            String myKey = mySnapshot.getKey();

                            Query query = ref;
                            requestOption = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query,Users.class).build();
                            adapterOption = new FirebaseRecyclerAdapter<Users, FindFriendViewHolder>(requestOption) {
                                @Override
                                protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Users model) {
                                    if(!mUser.getUid().equals(getRef(position).getKey().toString())
                                            && key.equals(getRef(position).getKey())
                                            && myKey.equals(mUser.getUid())){

                                        Picasso.get().load(model.getProfileImage()).into(holder.profileImage);
                                        holder.username.setText(model.getUsername());
                                        holder.email.setText(model.getEmail());

                                    }else {
                                        holder.itemView.setVisibility(View.GONE);
                                        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                                    }

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(FindFriendActivity.this, ProfileFriendsActivity.class);
                                            intent.putExtra("userKey",getRef(position).getKey().toString());
                                            startActivity(intent);
                                        }
                                    });
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(FindFriendActivity.this, ViewFriendActivity.class);
                                            intent.putExtra("userID",getRef(position).getKey().toString());
                                            startActivity(intent);
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_find_friend,parent,false);
                                    return new FindFriendViewHolder(view);
                                }
                            };

                            adapterOption.startListening();
                            requestRecyclerView.setAdapter(adapterOption);

                        }
//
                    }
                }else {
                    Log.d("DEBUG","Dont have any request!");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void LoadUser(String s) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = ref.orderByChild("username").startAt(s).endAt(s+"\uf8ff");

        friendRef.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    List<String> friendUID = new ArrayList<>();
                    for(DataSnapshot friendDataSnapshot: snapshot.getChildren()){
                        friendUID.add(friendDataSnapshot.getKey());
                    }

                    options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query,Users.class).build();
                    adapter = new FirebaseRecyclerAdapter<Users, FindFriendViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Users model) {

                            if(!mUser.getUid().equals(getRef(position).getKey().toString())
                                    && !friendUID.contains(getRef(position).getKey())
                                    && !model.getEmail().equals("")){

                                Picasso.get().load(model.getProfileImage()).into(holder.profileImage);
                                holder.username.setText(model.getUsername());
                                holder.email.setText(model.getEmail());
                            }else {
                                holder.itemView.setVisibility(View.GONE);
                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                            }

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(FindFriendActivity.this, ProfileFriendsActivity.class);
                                    intent.putExtra("userKey",getRef(position).getKey().toString());
                                    startActivity(intent);
                                }
                            });
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(FindFriendActivity.this, ViewFriendActivity.class);
                                    intent.putExtra("userID",getRef(position).getKey().toString());
                                    startActivity(intent);
                                }
                            });

                        }
                        @NonNull
                        @Override
                        public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_find_friend,parent,false);
                            return new FindFriendViewHolder(view);
                        }
                    };

                }else {
                    options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query,Users.class).build();
                    adapter = new FirebaseRecyclerAdapter<Users, FindFriendViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Users model) {

                            if(!mUser.getUid().equals(getRef(position).getKey().toString())
                                    && !model.getEmail().equals("")){

                                Picasso.get().load(model.getProfileImage()).into(holder.profileImage);
                                holder.username.setText(model.getUsername());
                                holder.email.setText(model.getEmail());
                            }else {
                                holder.itemView.setVisibility(View.GONE);
                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                            }

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(FindFriendActivity.this, ProfileFriendsActivity.class);
                                    intent.putExtra("userKey",getRef(position).getKey().toString());
                                    startActivity(intent);
                                }
                            });
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(FindFriendActivity.this, ViewFriendActivity.class);
                                    intent.putExtra("userID",getRef(position).getKey().toString());
                                    startActivity(intent);
                                }
                            });

                        }
                        @NonNull
                        @Override
                        public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_find_friend,parent,false);
                            return new FindFriendViewHolder(view);
                        }
                    };
                }
                adapter.startListening();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                LoadUser(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}