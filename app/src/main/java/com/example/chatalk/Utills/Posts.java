package com.example.chatalk.Utills;

public class Posts {
    private String datePost,
            postDesc,
            postImageUrl,
            userProfileImage,
            username,
        status,uid;
    public Posts(){}
    public Posts(String datePost, String postDesc, String postImageUrl, String userProfileImage, String username,String status,String uid) {
        this.datePost = datePost;
        this.postDesc = postDesc;
        this.postImageUrl = postImageUrl;
        this.userProfileImage = userProfileImage;
        this.username = username;
        this.status = status;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatePost() {
        return datePost;
    }

    public void setDatePost(String datePost) {
        this.datePost = datePost;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
