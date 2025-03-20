package com.project.dineagement.objects;

public class User {
    private String uid, name, image;

    public User() {
    }

    public User(String name, String uid) {
        this.name = name;
        this.uid = uid;
        image = "";
    }

    public User(String uid, String name, String image) {
        this.uid = uid;
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
