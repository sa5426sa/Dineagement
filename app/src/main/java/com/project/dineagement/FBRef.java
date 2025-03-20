package com.project.dineagement;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FBRef {
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();

    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refUsers = FBDB.getReference("users");
    public static DatabaseReference refTasks, refCompletedTasks, refDeletedTasks;

    public static String uid;

    public static void getUser(@NonNull FirebaseUser user) {
        uid = user.getUid();
        refTasks = FBDB.getReference("tasks").child(uid);
        refCompletedTasks = FBDB.getReference("tasks_done").child(uid);
        refDeletedTasks = FBDB.getReference("tasks_bin").child(uid);
    }
}
