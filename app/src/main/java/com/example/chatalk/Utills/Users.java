package com.example.chatalk.Utills;

public class Users {
    private String username,profileImage,status,email;
    public Users(){}

    public Users(String username, String profileImage, String status, String email) {
        this.username = username;
        this.profileImage = profileImage;

        this.status = status;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
