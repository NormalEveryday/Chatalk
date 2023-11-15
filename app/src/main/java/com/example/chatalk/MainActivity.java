package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatalk.Utills.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity  extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    Toolbar toolbar;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    CircleImageView image_profile,profileImageHeader;

    TextView usernameHeader,emailHeader;

    String imageurl;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef,PostRef,LikeRef,CommentRef;
    StorageReference mStore;
    EditText inputPostDesc;

    ImageView addImagePost,sendImagePost, commentImage;

    private static final int REQUEST_CODE = 101;
    ImageView sendComment;
    EditText inputComment;
    Uri imageuri;

    ProgressDialog progressDialog;
    StorageReference postImageRef;
    public static FirebaseRecyclerAdapter<Posts,MyHolder> adapter;
    public static FirebaseRecyclerOptions<Posts> options;
    RecyclerView recyclerView;


    String usernameView,profileImageUrlView;

//    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor editor;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle("Chatalk");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);


        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        View view = navigationView.inflateHeaderView(R.layout.menu_header);

        navigationView.setNavigationItemSelectedListener(this);
//        profileImageHeader = findViewById(R.id.profile_image_header);
//        usernameHeader = findViewById(R.id.usernameHeader);
        image_profile = findViewById(R.id.profile_image);
        profileImageHeader = view.findViewById(R.id.profile_image_header);
        usernameHeader = view.findViewById(R.id.usernameHeader);
        emailHeader = view.findViewById(R.id.emailHeader);

        inputComment = findViewById(R.id.inputComment);
        sendComment = findViewById(R.id.sendComment);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        commentImage = findViewById(R.id.commentsImage);

        addImagePost = findViewById(R.id.addImagePost);
        sendImagePost = findViewById(R.id.send_post_imageView);
        inputPostDesc = findViewById(R.id.inputAddPost);
        recyclerView = findViewById(R.id.recyclerView);
//        MyHolder.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");



        sendImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPost();
            }
        });

        addImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        progressDialog = new ProgressDialog(this);
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        //like child realtime db
        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        CommentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        postImageRef = FirebaseStorage.getInstance().getReference().child("PostsImages");
        LoadPost();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.home){
            startActivity(new Intent(MainActivity.this,MainActivity.class));
        }else if (item.getItemId() == R.id.profile) {
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        }else if (item.getItemId() == R.id.friendlist) {
            startActivity(new Intent(MainActivity.this, FriendActivity.class));

        }else if (item.getItemId() == R.id.findfriend) {
            startActivity(new Intent(MainActivity.this,FindFriendActivity.class));
        }else if (item.getItemId() == R.id.chat) {
            startActivity(new Intent(MainActivity.this,ChatUserActivity.class));

        }else if (item.getItemId() == R.id.logout) {
            Toast.makeText(MainActivity.this,ProfileActivity.State,Toast.LENGTH_LONG).show();
            if(ProfileActivity.State == "safemode"){
                mUser.delete();
                DatabaseReference mU = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
                mU.removeValue();
                // Chuyển người dùng đến màn hình SplashActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
                ProfileActivity.State = "unsafemode";
                ProfileActivity.editor.putString(ProfileActivity.CURRENT_STATE_KEY, "unsafemode");
                ProfileActivity.editor.apply();
                Toast.makeText(MainActivity.this,ProfileActivity.State,Toast.LENGTH_SHORT).show();
            }
            if(ProfileActivity.State == "unsafemode"){
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this,SplashActivity.class));
            }

            finish();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        if(item.getItemId() == R.id.chat){

            startActivity(new Intent(MainActivity.this,ChatUserActivity.class));
            return true;
        }
        return true;

    }

//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId() == R.id.chat){
//            Toast.makeText(MainActivity.this,"Chat here",Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(MainActivity.this,ChatUserActivity.class));
//            return true;
//        }
//        return true;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem searchIcon = menu.findItem(R.id.search_bar);

        SearchView searchBar = (SearchView)searchIcon.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(searchManager != null){
            searchBar.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            DatabaseReference originalQuery = FirebaseDatabase.getInstance().getReference("Posts");

            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }



            @Override
            public boolean onQueryTextChange(String s) {
                if(s.isEmpty()){

                }
                return true;
            }
        });

        return true;
    }



    private void LoadPost() {
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("datePost");
        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query,Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull Posts model) {
                final String postKey = getRef(position).getKey();
                holder.postDesc.setText(model.getPostDesc());
                holder.timeAgo.setText(model.getDatePost());
                holder.username.setText(model.getUsername());
                Picasso.get().load(model.getUserProfileImage()).into(holder.userProfileImage);
                Picasso.get().load(model.getPostImageUrl()).into(holder.postImage);

//                mRef.child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        holder.username.setText(snapshot.child("username").getValue().toString());
//                        imageurl = snapshot.child("profileImage").getValue().toString();
//                        Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(holder.userProfileImage);
//                        Log.d("DEBUG","user:"+snapshot.child("profileImage").getValue().toString());
//                 ;
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


                holder.countLikes(postKey,mUser.getUid(),LikeRef);
                CommentRef = FirebaseDatabase.getInstance().getReference("Comments").child(postKey);

                holder.CountComment(CommentRef);
                holder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LikeRef.child(postKey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    LikeRef.child(postKey).child(mUser.getUid()).removeValue();
                                    //Change color here
                                    holder.likeImage.setImageResource(R.drawable.ic_thumb_up_foreground);
                                    notifyDataSetChanged();
                                }else {
                                    LikeRef.child(postKey).child(mUser.getUid()).setValue("like");
                                    //Change color here
                                    holder.likeImage.setImageResource(R.drawable.ic_thumb_up_blue);
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                holder.commentsImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this,CommentActivity.class);
                        intent.putExtra("username",holder.username.getText().toString());
                        intent.putExtra("image_profile", model.getUserProfileImage());
                        intent.putExtra("timeAgo",model.getDatePost());
                        intent.putExtra("postDesc",model.getPostDesc());
                        intent.putExtra("PostImageUrl",model.getPostImageUrl());
                        intent.putExtra("postKey",postKey);
                        intent.putExtra("datePost",model.getDatePost());
                        intent.putExtra("postKey",postKey);
                        startActivity(intent);

                    }
                });


            }

            @NonNull
            @Override
            public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post,parent,false);

                return new MyHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
    }


    private void AddPost() {
        String desc = inputPostDesc.getText().toString();
        if(desc.isEmpty() || desc.length()<3){
            inputPostDesc.setError("Fill the field");
        }else if(imageuri==null){
            Toast.makeText(MainActivity.this,"Add image please",Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog.setTitle("Adding Post");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            Date date = new Date();
            SimpleDateFormat formatter =new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            final String strDate = formatter.format(date);

            postImageRef.child(mUser.getUid()+strDate).putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        postImageRef.child(mUser.getUid()+strDate).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashMap = new HashMap();
                                hashMap.put("datePost",strDate);
                                hashMap.put("postImageUrl",uri.toString());
                                hashMap.put("postDesc",desc);
                                hashMap.put("userProfileImage",profileImageUrlView);
                                hashMap.put("username",usernameView);
                                hashMap.put("uid",mUser.getUid());
                                PostRef.child(mUser.getUid()+strDate).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            progressDialog.dismiss();

                                            Toast.makeText(MainActivity.this,"Post success",Toast.LENGTH_SHORT).show();
                                            addImagePost.setImageResource(R.drawable.ic_add_photo);
                                            inputPostDesc.setText("");


                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this,"Post failed",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,"Error: "+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode== RESULT_OK && data != null){
            imageuri = data.getData();
            addImagePost.setImageURI(imageuri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mUser == null){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }else{

            mRef.child(mUser.getUid()).child("status").setValue("online");

            mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        profileImageUrlView = snapshot.child("profileImage").getValue().toString();
                        usernameView = snapshot.child("username").getValue().toString();

                        if(profileImageUrlView!=null){
                            Picasso.get().load(profileImageUrlView).into(profileImageHeader);
                            Picasso.get().load(profileImageUrlView).into(image_profile);

                        }
                        if(usernameView !=null){usernameHeader.setText(usernameView);}
                        if(emailHeader!=null){
                            emailHeader.setText(mUser.getEmail());
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this,"Error Header",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}