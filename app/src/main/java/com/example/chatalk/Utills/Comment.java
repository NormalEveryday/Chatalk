package com.example.chatalk.Utills;

public class Comment {
    private String username,profileImageUrl,comment,ptime,uid,status;
    public Comment(){}

    public Comment(String username, String profileImageUrl, String comment,String ptime,String uid,String status) {
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.comment = comment;
        this.ptime = ptime;
        this.uid = uid;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
