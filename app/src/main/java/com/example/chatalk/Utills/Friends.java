package com.example.chatalk.Utills;

public class Friends {
    private String profileImage;
    private String username,state;

    private String statusMessage;
    private String email;
    public String getEmail() {
        return email;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Friends(){}

    public Friends(String profileImage, String username) {
        this.profileImage = profileImage;
        this.username = username;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
