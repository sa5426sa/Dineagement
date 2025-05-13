package com.project.dineagement.objects;

import com.google.firebase.database.PropertyName;
import com.google.firebase.firestore.Blob;

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

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    @PropertyName("isManager")
    public boolean isManager() {
        return isManager;
    }
}
