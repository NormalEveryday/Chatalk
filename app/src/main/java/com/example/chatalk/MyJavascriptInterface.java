package com.example.chatalk;

import android.webkit.JavascriptInterface;

public class MyJavascriptInterface {

    private final VideoCallActivity callActivity;

    public MyJavascriptInterface(VideoCallActivity callActivity) {
        this.callActivity = callActivity;
    }

    @JavascriptInterface
    public void onPeerConnected() {
        callActivity.onPeerConnected();
    }
}
