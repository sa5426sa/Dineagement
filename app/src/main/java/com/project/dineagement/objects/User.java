package com.project.dineagement.objects;

public class User {
    private String uid, username, image;

    public User() {
    }

    public User(String uid, String username) {
        this.uid = uid;
        this.username = username;
        image = "";
    }

    public User(String uid, String username, String image) {
        this.uid = uid;
        this.username = username;
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
