package com.picassos.mint.console.android.models;

public class Members {
    private int user_id;
    private String username;
    private String email;
    private String verified;

    public Members(int user_id, String username, String email, String verified) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.verified = verified;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }
}
