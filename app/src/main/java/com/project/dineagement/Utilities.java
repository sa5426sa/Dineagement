package com.project.dineagement;

public class Utilities {
    public static String format2Display(String str) {
        return str.substring(4, 6) + "/" + str.substring(6, 8) + "/" + str.substring(0, 4);
    }
    public static String display2Format(String str){
        return str.substring(6, 10)+str.substring(0, 2)+str.substring(3, 5);
    }
}
