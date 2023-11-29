package com.example.chatalk.Utills;

public class Chat {
    private String sms,status,userID,timestamp,username, ptime;
    Chat(){}

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Chat(String sms, String status, String userID, String timestamp,String username,String ptime) {
        this.sms = sms;
        this.status = status;
        this.userID = userID;
        this.timestamp = timestamp;
        this.username = username;
        this.ptime = ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public String getPtime() {
        return ptime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
