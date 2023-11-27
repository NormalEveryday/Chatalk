package com.example.chatalk;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.ktx.Firebase;
import java.util.UUID;

public class VideoCallActivity extends AppCompatActivity {

    private String username = "";
    WebView webView;
    private String friendsUsername = "";
    private boolean isPeerConnected = false;

    private boolean isAudio = true;
    private boolean isVideo = true;

    ImageView toggleAudioBtn,toggleVideoBtn,acceptBtn,rejectBtn;
    Button callBtn;
    RelativeLayout callLayout,inputLayout;
    LinearLayout callControlLayout;

    DatabaseReference firebaseRef;
    EditText friendNameEdit;

    TextView incomingCallTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        callBtn = findViewById(R.id.callBtn);
        friendNameEdit = findViewById(R.id.friendNameEdit);
        toggleAudioBtn = findViewById(R.id.toggleAudioBtn);
        toggleVideoBtn = findViewById(R.id.toggleVideoBtn);
        callLayout = findViewById(R.id.callLayout);
        incomingCallTxt = findViewById(R.id.incomingCallTxt);
        acceptBtn = findViewById(R.id.acceptBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        inputLayout = findViewById(R.id.inputLayout);
        webView = findViewById(R.id.webView);
        callControlLayout = findViewById(R.id.callControlLayout);
//        username = getIntent().getStringExtra("username");
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("DEBUG","OtherUserID " + getIntent().getStringExtra("OtherUserID"));
        Log.d("DEBUG","OtherUserID " + friendNameEdit);
        friendNameEdit.setText(getIntent().getStringExtra("OtherUserID"));
        firebaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        if (mUser == null) {
            // User not authenticated, handle accordingly
            Log.d("DEBUG","NULL");
        } else {
            Log.d("DEBUG","NOT NULL");
            // Continue with your code
        }

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsUsername = friendNameEdit.getText().toString();
                sendCallRequest();
            }
        });

        toggleAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAudio = !isAudio;
                callJavascriptFunction("javascript:toggleAudio(\"" + isAudio + "\")");
                toggleAudioBtn.setImageResource(isAudio ? R.drawable.ic_baseline_mic_24 : R.drawable.ic_baseline_mic_off_24);
            }
        });

        toggleVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideo = !isVideo;
                callJavascriptFunction("javascript:toggleVideo(\"" + isVideo + "\")");
                toggleVideoBtn.setImageResource(isVideo ? R.drawable.ic_baseline_videocam_24 : R.drawable.ic_baseline_videocam_off_24);
            }
        });

        setupWebView();
    }

    private void sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", Toast.LENGTH_LONG).show();
            return;
        }

        firebaseRef.child("incoming").setValue(getIntent().getStringExtra("OtherUserID"));
        firebaseRef.child("isAvailable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if ("true".equals(snapshot.getValue().toString())) {
                    listenForConnId();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void listenForConnId() {
        firebaseRef.child(friendsUsername).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null)
                    return;
                switchToControls();
                callJavascriptFunction("javascript:startCall(\"" + snapshot.getValue() + "\")");
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void setupWebView() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new MyJavascriptInterface(VideoCallActivity.this), "Android");

        loadVideoCall();
    }

    private void loadVideoCall() {
        String filePath = "file:android_asset/call.html";
        webView.loadUrl(filePath);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                initializePeer();
            }
        });
    }

    private String uniqueId = "";

    private void initializePeer() {
        uniqueId = getUniqueID();

        callJavascriptFunction("javascript:init(\"" + uniqueId + "\")");
        firebaseRef.child("incoming").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                onCallRequest((String) snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void onCallRequest(String caller) {
        if (caller == null) return;

        callLayout.setVisibility(View.VISIBLE);
        incomingCallTxt.setText(caller + " is calling...");

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseRef.child("connId").setValue(uniqueId);
                firebaseRef.child("isAvailable").setValue(true);

                callLayout.setVisibility(View.GONE);
                switchToControls();
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                firebaseRef.child(username).child("incoming").setValue(null);
                firebaseRef.child("incoming").setValue(null);
                callLayout.setVisibility(View.GONE);
            }
        });
    }

    private void switchToControls() {
        inputLayout.setVisibility(View.GONE);
        callControlLayout.setVisibility(View.VISIBLE);
    }

    private String getUniqueID() {
        return UUID.randomUUID().toString();
    }

    private void callJavascriptFunction(String functionString) {
        webView.post(() -> webView.evaluateJavascript(functionString, null));
    }

    public void onPeerConnected() {

        isPeerConnected = true;
        // You can add any additional logic here when the peer connection is established.
        // For example, you might want to enable certain UI elements or perform other actions.
        Log.d("VideoCallActivity", "Peer connected!");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

//    @Override
//    protected void onDestroy() {
//        firebaseRef.setValue(null);
//        webView.loadUrl("about:blank");
//        super.onDestroy();
//    }


}
