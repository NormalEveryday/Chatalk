package com.example.chatalk;

import static android.Manifest.permission.RECORD_AUDIO;

import android.app.ActivityManager;
import android.app.LocaleConfig;
import android.content.ComponentName;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.chatalk.Utills.BaseActivity;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.android.BuildConfig;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;



    //Chatbot
    private String stringAPIKey = "API_PALM";
    private String stringURLEndPoint = "https://generativelanguage.googleapis.com/v1beta3/models/text-bison-001:generateText?key=" + stringAPIKey;
    private String stringOutput = "";


    private TextToSpeech textToSpeech;

    private SpeechRecognizer speechRecognizer;
    private Intent intent;


    DrawerLayout drawerLayout;
    NavigationView navigationView;

    CircleImageView image_profile, profileImageHeader;
    DatabaseReference UnFollow;

    TextView usernameHeader, emailHeader;

    String imageurl;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef, PostRef, LikeRef, CommentRef;
    StorageReference mStore;
    EditText inputPostDesc;

    ImageView addImagePost, sendImagePost, commentImage;

    private static final int REQUEST_CODE = 101;
    ImageView sendComment;
    EditText inputComment;
    Uri imageuri;

    ProgressDialog progressDialog;
    StorageReference postImageRef;
    public static FirebaseRecyclerAdapter<Posts, MyHolder> adapter;
    public static FirebaseRecyclerOptions<Posts> options;
    RecyclerView recyclerView;

    DatabaseReference Report;


    String usernameView, profileImageUrlView;

    int originalHeight;

//    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor editor;


    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toast.makeText(MainActivity.this, ProfileActivity.State, Toast.LENGTH_SHORT).show();
        Log.d("DEBUG","State :"+ProfileActivity.State);


        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        // Access your API key as a Build Configuration variable
        String apiKey = "AIzaSyAqNmllczUTlKBq7GbXCDe5NpiIR2hVETU";



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
        UnFollow = FirebaseDatabase.getInstance().getReference().child("UnFollow").child(mUser.getUid());
        FirebaseMessaging.getInstance().subscribeToTopic(mUser.getUid());
        Report = FirebaseDatabase.getInstance().getReference().child("Reports");


        //set up for gpt
        ActivityCompat.requestPermissions(this,
                new String[]{RECORD_AUDIO},
                PackageManager.PERMISSION_GRANTED);




        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                if (textToSpeech != null && i == TextToSpeech.SUCCESS) {
                    // Text-to-speech engine initialized successfully
                    textToSpeech.setSpeechRate((float) 0.8);
                } else {
                    // Initialization failed
                    Log.e("TTS", "Initialization failed");
                }
            }
        });


        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int i) {
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                String string = "";
//                textView.setText("");
                if (matches != null) {
                    string = matches.get(0);
//                    editText.setText(string);
                    PaLM(string);
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });
        /// end


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
                startActivityForResult(intent, REQUEST_CODE);
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
        if (item.getItemId() == R.id.home) {
//            startActivity(new Intent(MainActivity.this, MainActivity.class));
//            finish();
            return false;
        } else if (item.getItemId() == R.id.profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        } else if (item.getItemId() == R.id.friendlist) {

            if (ProfileActivity.State == "safemode") {
                Toast.makeText(MainActivity.this, "You don't have permission to do this", Toast.LENGTH_LONG).show();
            } else {

                startActivity(new Intent(MainActivity.this, FriendActivity.class));
            }

        } else if (item.getItemId() == R.id.findfriend) {
            if (ProfileActivity.State == "safemode") {
                Toast.makeText(MainActivity.this, "You don't have permission to do this", Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(MainActivity.this, FindFriendActivity.class));
            }

        } else if (item.getItemId() == R.id.logout) {
            Toast.makeText(MainActivity.this, ProfileActivity.State, Toast.LENGTH_LONG).show();
            if (ProfileActivity.State.equals("safemode")) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).removeValue();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mUser.delete();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //                mAuth.signOut();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
                ProfileActivity.State = "unsafemode";
                ProfileActivity.editor.putString(ProfileActivity.CURRENT_STATE_KEY, "unsafemode");
                ProfileActivity.editor.apply();
//                Toast.makeText(MainActivity.this, ProfileActivity.State, Toast.LENGTH_SHORT).show();
                Log.d("DEBUG","State :"+ProfileActivity.State);
            }
            if (ProfileActivity.State.equals("unsafemode")) {
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        if (item.getItemId() == R.id.chat) {
            if (ProfileActivity.State == "safemode") {
                Toast.makeText(MainActivity.this, "You don't have permission to do this", Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(MainActivity.this, ChatUserActivity.class));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
            }

            return true;
        }
        if (item.getItemId() == R.id.assistance) {
//            startActivity(new Intent(MainActivity.this,ChatgptActivity.class));
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
//                return true;
            }
            stringOutput = "";
            speechRecognizer.startListening(intent);

        }
        return true;

    }

    private void PaLM(String input) {


//        JSONObject jsonObject = new JSONObject();
//        JSONObject jsonObjectText = new JSONObject();
//        try {
//            jsonObjectText.put("text", input);
//            jsonObject.put("prompt", jsonObjectText);
//
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
//                stringURLEndPoint,
//                jsonObject,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            String stringOutput = response.getJSONArray("candidates")
//                                    .getJSONObject(0)
//                                    .getString("output");
//
////                            textView.setText(stringOutput);
//                            //say it
//                            textToSpeech.speak(stringOutput, TextToSpeech.QUEUE_FLUSH, null, null);
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                textView.setText("Error");
//                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> mapHeader = new HashMap<>();
//                mapHeader.put("Content-Type", "application/json");
//                return mapHeader;
//            }
//        };
//        int intTimeoutPeriod = 60000; //60 seconds
//        RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//
//        jsonObjectRequest.setRetryPolicy(retryPolicy);
//        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);


        MenuItem item = menu.findItem(R.id.assistance);

        Glide.with(this).asGif().load(R.drawable.box).into(new CustomTarget<GifDrawable>() {
            @Override
            public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                item.setIcon(resource);
                resource.start();
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });



        return true;
    }


    private void LoadPost() {
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        Query query = FirebaseDatabase.getInstance().getReference().child("Posts").orderByChild("datePost");
        options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query, Posts.class).build();
        adapter = new FirebaseRecyclerAdapter<Posts, MyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull Posts model) {
                final String postKey = getRef(position).getKey();
                holder.postDesc.setText(model.getPostDesc());
                holder.timeAgo.setText(model.getDatePost());
                holder.username.setText(model.getUsername());
                Picasso.get().load(model.getUserProfileImage()).into(holder.userProfileImage);
                Picasso.get().load(model.getPostImageUrl()).into(holder.postImage);

                Report.child(mUser.getUid()).child(model.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            if(dataSnapshot.getKey().equals(model.getUid() + model.getDatePost())){

                                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                params.height = 0;
                                holder.itemView.setLayoutParams(params);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                holder.itemView.findViewById(R.id.report).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        HashMap hashMap = new HashMap();
                        hashMap.put("uid",model.getUid());
                        hashMap.put("datePost",model.getDatePost());
                        hashMap.put("PostImageUrl",model.getPostImageUrl());
                        hashMap.put("postDesc",model.getPostDesc());
                        Report.child(mUser.getUid()).child(model.getUid()).child(model.getUid()+model.getDatePost()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this,"Report success",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        UnFollow.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.child(model.getUid()).child("status").getRef().setValue("unfollow");

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                holder.countLikes(postKey, mUser.getUid(), LikeRef);
                CommentRef = FirebaseDatabase.getInstance().getReference("Comments").child(postKey);

                holder.CountComment(CommentRef);
                holder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LikeRef.child(postKey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    LikeRef.child(postKey).child(mUser.getUid()).removeValue();
                                    //Change color here
                                    holder.likeImage.setImageResource(R.drawable.ic_thumb_up_foreground);
                                    notifyDataSetChanged();
                                } else {
                                    LikeRef.child(postKey).child(mUser.getUid()).setValue("like");
                                    //Change color here
                                    holder.likeImage.setImageResource(R.drawable.ic_thumb_up_blue);
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                holder.commentsImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, CommentActivity.class);
                        intent.putExtra("username", holder.username.getText().toString());
                        intent.putExtra("image_profile", model.getUserProfileImage());
                        intent.putExtra("timeAgo", model.getDatePost());
                        intent.putExtra("postDesc", model.getPostDesc());
                        intent.putExtra("PostImageUrl", model.getPostImageUrl());
                        intent.putExtra("postKey", postKey);
                        intent.putExtra("datePost", model.getDatePost());
                        intent.putExtra("postKey", postKey);
                        intent.putExtra("otheruid",model.getUid());
                        startActivity(intent);

                    }
                });



                if (model.getStatus().equals("visible_off")) {
                    // Hide the post item
                    holder.itemView.setVisibility(View.GONE);

                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = 0;
                    params.width = 0;
                    holder.itemView.setLayoutParams(params);

                } else {
                    // Show the post item
                    holder.itemView.setVisibility(View.VISIBLE);

                    // Reset the layout parameters to their original values
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;  // or your original height
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;   // or your original width
                    holder.itemView.setLayoutParams(params);
                }

            }

            @NonNull
            @Override
            public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post, parent, false);

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
        if (desc.isEmpty() || desc.length() < 3) {
            inputPostDesc.setError("Fill the field");
        } else if (imageuri == null) {
            Toast.makeText(MainActivity.this, "Add image please", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Adding Post");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            final String strDate = formatter.format(date);

            postImageRef.child(mUser.getUid() + strDate).putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        int lastVisibleItemPosition = adapter.getItemCount();
                        recyclerView.smoothScrollToPosition(lastVisibleItemPosition);
                        postImageRef.child(mUser.getUid() + strDate).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashMap = new HashMap();
                                hashMap.put("datePost", strDate);
                                hashMap.put("postImageUrl", uri.toString());
                                hashMap.put("postDesc", desc);
                                hashMap.put("userProfileImage", profileImageUrlView);
                                hashMap.put("username", usernameView);
                                hashMap.put("uid", mUser.getUid());
                                hashMap.put("status","visible");
                                PostRef.child(mUser.getUid() + strDate).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();

                                            Toast.makeText(MainActivity.this, "Post success", Toast.LENGTH_SHORT).show();
                                            addImagePost.setImageResource(R.drawable.ic_add_photo);
                                            inputPostDesc.setText("");


                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Post failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageuri = data.getData();
            addImagePost.setImageURI(imageuri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("status").setValue("online");
        if (mUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Date date = new Date();
            SimpleDateFormat formatter =new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            final String strDate = formatter.format(date);

            DatabaseReference userStatus = FirebaseDatabase.getInstance().getReference().child("Users");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        if(dataSnapshot.getKey().equals(mUser.getUid())){
                            if(!dataSnapshot.child("email").getValue().equals("")){
                                mRef.child(mUser.getUid()).child("status").onDisconnect().setValue("Last Seen: "+strDate);
                                mRef.child(mUser.getUid()).child("status").setValue("online");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        profileImageUrlView = snapshot.child("profileImage").getValue().toString();
                        usernameView = snapshot.child("username").getValue().toString();
                        mRef.child(mUser.getUid()).child("status").setValue("online");

                        if (profileImageUrlView != null) {
                            Picasso.get().load(profileImageUrlView).into(profileImageHeader);
                            Picasso.get().load(profileImageUrlView).into(image_profile);

                        }
                        if (usernameView != null) {
                            usernameHeader.setText(usernameView);
                        }
                        if (emailHeader != null) {
                            emailHeader.setText(snapshot.child("email").getValue().toString());
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Error Header", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }

}
