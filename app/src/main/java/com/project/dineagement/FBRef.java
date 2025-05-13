package com.project.dineagement;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FBRef {
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference refUsers = database.getReference("users");
    public static DatabaseReference refTasks, refAllTasks = database.getReference("tasks");

    public static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public static CollectionReference refImages = firestore.collection("images");
    public static CollectionReference refGallery = firestore.collection("gallery");

    public static String uid;

    public static void getUser(FirebaseUser user) {
        uid = user.getUid();
        refTasks = database.getReference("tasks").child(uid);
    }
}
