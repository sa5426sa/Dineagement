package com.project.dineagement;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;

public class Utilities {
    @NonNull
    public static String format2Display(@NonNull String str) {
        return str.substring(4, 6) + "/" + str.substring(6, 8) + "/" + str.substring(0, 4);
    }

    @NonNull
    public static String display2Format(@NonNull String str){
        return str.substring(6, 10)+str.substring(0, 2)+str.substring(3, 5);
    }
}
