package com.example.chatalk.Utills;

public class Friends {
    private String profileImage;
    private String username;


    private String email;
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Friends(){}

    public Friends(String profileImage, String username) {
        this.profileImage = profileImage;
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
