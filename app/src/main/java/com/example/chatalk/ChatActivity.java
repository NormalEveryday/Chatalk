package com.example.chatalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatalk.Utills.BaseActivity;
import com.example.chatalk.Utills.Chat;
import com.example.chatalk.Utills.EmailAuth;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.datatransport.runtime.scheduling.jobscheduling.SchedulerConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.rpc.context.AttributeContext;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    public static String newestSms;
    Toolbar toolbar;
    RecyclerView recyclerView;

    EditText inputSms;
    ImageView btnSend,emailSend;

    CircleImageView userProfileImageAppbar;

    TextView usernameAppbar,status;
    String OtherUserID;

    DatabaseReference mUserRef,smsRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String OtherUsername, OtherProfileImageLink,OtherUserStatus;

    FirebaseRecyclerOptions<Chat> options;
    FirebaseRecyclerAdapter<Chat,ChatViewHolder> adapter;
    String myProfileImageLink;

    String URL = "https://fcm.googleapis.com/fcm/send";
    RequestQueue requestQueue;

    public static SharedPreferences.Editor editor;
    String otherusename;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        OtherUserID = getIntent().getStringExtra("OtherUserID");

        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        inputSms = findViewById(R.id.inputSms);
        btnSend = findViewById(R.id.btnSend);
        userProfileImageAppbar = findViewById(R.id.userProfileImageAppbar);
        usernameAppbar = findViewById(R.id.usernameAppbar);
        emailSend = findViewById(R.id.emailSend);

        status = findViewById(R.id.status);
        requestQueue = Volley.newRequestQueue(this);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        smsRef = FirebaseDatabase.getInstance().getReference().child("Message");
        LoadOtherUser();

        LoadMyProfile();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendSMS();
            }
        });

        layoutManager.setStackFromEnd(true);

        LoadSMS();
        emailSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMail();
            }
        });


    }

    public void SendMail() {
        try{
            EmailAuth emailAuth = new EmailAuth();
            emailAuth.show(getSupportFragmentManager(),"auth email");
            String senderEmail = mUser.getEmail();
            String receiverEmail = getIntent().getStringExtra("email");
            Log.d("DEBUG","receiver Email : "+ receiverEmail + " and sender : " +senderEmail);
            String passwordSenderEmail=emailAuth.getPass();
            String host = "smtp.gmail.com";

            Properties properties = System.getProperties();
            properties.put("mail.smtp.host",host);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");


            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail,passwordSenderEmail);
                }
            });
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(receiverEmail));
            mimeMessage.setSubject("Subject: Chatalk "+ mUser.getDisplayName());
            if(!inputSms.getText().toString().isEmpty()){
                mimeMessage.setText(inputSms.getText().toString());
            }else {
                Toast.makeText(ChatActivity.this,"message is null",Toast.LENGTH_SHORT).show();
            }

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                        inputSms.setText("");
                    }catch (MessagingException e){
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void LoadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    myProfileImageLink = snapshot.child("profileImage").getValue().toString();
                    otherusename = snapshot.child("username").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void LoadSMS() {
        options = new FirebaseRecyclerOptions.Builder<Chat>().setQuery(smsRef.child(mUser.getUid()).child(OtherUserID),Chat.class).build();
        adapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Chat model) {
                if(model.getUserID().equals(mUser.getUid())){
                    holder.firstUserText.setVisibility(View.GONE);
                    holder.firstUserProfile.setVisibility(View.GONE);
                    holder.secondUserProfile.setVisibility(View.VISIBLE);
                    holder.secondUserText.setVisibility(View.VISIBLE);



                    holder.secondUserText.setText(model.getSms());

                    Picasso.get().load(myProfileImageLink).into(holder.secondUserProfile);

                }else {
                    holder.firstUserText.setVisibility(View.VISIBLE);
                    holder.firstUserProfile.setVisibility(View.VISIBLE);
                    holder.secondUserProfile.setVisibility(View.GONE);
                    holder.secondUserText.setVisibility(View.GONE);



                    holder.firstUserText.setText(model.getSms());

                    Picasso.get().load(OtherProfileImageLink).into(holder.firstUserProfile);
                }
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleview_sms,parent,false);

                return new ChatViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
//        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

    }

    private void SendSMS() {
        String sms = inputSms.getText().toString();
        if(!sms.isEmpty()){
            final HashMap hashMap = new HashMap();
            hashMap.put("sms",sms);
            hashMap.put("status","unseen");
            hashMap.put("userID",mUser.getUid());
            hashMap.put("username",otherusename);
            smsRef.child(OtherUserID).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    smsRef.child(mUser.getUid()).child(OtherUserID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                sendNotification(sms);
                                inputSms.setText("");
                            }

                        }
                    });
                }
            });
        }
    }

    private void sendNotification(String sms) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to","/topics/"+OtherUserID);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("title","Message from "+OtherUsername);
            jsonObject1.put("body",sms);

            jsonObject.put("notification",jsonObject1);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> map = new HashMap<>();
                    map.put("content-type","application/json");
                    map.put("authorization","key=AAAA47A-wM0:APA91bG-e1xjes8qeoq9lnB0_xTpe7LHsIbuHngfT0zo1PCNZJTM2CAaCMkLxbQkpzdqDa2h4VnW3F8ntYyUeaDSfrMd153UXzoUORCYLij9oSTZgjNFMOeOGhKbBopLtRxopp1V6r-M");
                    return map;
                }
            };

            requestQueue.add(request);
        }catch (JSONException e){
            e.printStackTrace();
        }


    }

    private void LoadOtherUser() {
        mUserRef.child(OtherUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                OtherUsername = snapshot.child("username").getValue().toString();

                OtherUserStatus = snapshot.child("status").getValue().toString();
                OtherProfileImageLink = snapshot.child("profileImage").getValue().toString();
                Picasso.get().load(OtherProfileImageLink).into(userProfileImageAppbar);
                usernameAppbar.setText(OtherUsername);
                status.setText(OtherUserStatus);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatActivity.this,ChatUserActivity.class);
        startActivity(intent);
        finish();
    }
}