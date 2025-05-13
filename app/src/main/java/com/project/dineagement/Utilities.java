package com.project.dineagement;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;

public class Utilities {
/**
 * Converts a DateFormat display to a traditional display.
 * @param str The string you want to convert.
 * @return The traditional display format of the string.
 * @see #display2Format(String)
 */
    @NonNull
    public static String format2Display(@NonNull String str) {
        return str.substring(4, 6) + "/" + str.substring(6, 8) + "/" + str.substring(0, 4);
    }

    /**
     * Converts a traditional display to a DateFormat display.
     * @param str The string you want to convert.
     * @return The DateFormat display version of the string.
     * @see #format2Display(String)
     */
    @NonNull
    public static String display2Format(@NonNull String str){
        return str.substring(6, 10)+str.substring(0, 2)+str.substring(3, 5);
    }
}
