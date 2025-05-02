package com.project.dineagement.objects;

import com.google.firebase.database.PropertyName;

import org.jetbrains.annotations.PropertyKey;

public class User {
    private String uid, username, image;

    private boolean isManager;

    public User() {}

    public User(String uid, String username) {
        this.uid = uid;
        this.username = username;
        image = "";
        isManager = false;
    }

    public User(String uid, String username, String image) {
        this.uid = uid;
        this.username = username;
        this.image = image;
        isManager = false;
    }

    public User(String uid, String username, boolean isManager) {
        this.uid = uid;
        this.username = username;
        image = "";
        this.isManager = isManager;
    }

    public User(String uid, String username, String image, boolean isManager) {
        this.uid = uid;
        this.username = username;
        this.image = image;
        this.isManager = isManager;
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

    @PropertyName("isManager")
    public boolean isManager() {
        return isManager;
    }

    @PropertyName("isManager")
    public void setManager(boolean manager) {
        isManager = manager;
    }
}
