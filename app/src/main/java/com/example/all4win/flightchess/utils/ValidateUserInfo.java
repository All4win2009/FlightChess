package com.example.all4win.flightchess.utils;

/**
 * Created by AndreBTS on 25/09/2015.
 */
public class ValidateUserInfo {
    public static boolean isEmailValid(String email) {
        //TODO change for your own logic
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isNameValid(String name){
        int len = name.length();
        return (len >= 3 && len <= 10);
    }

    public static boolean isPasswordValid(String password) {
        //TODO change for your own logic
        int len = password.length();
        if (password == null||password.equals(""))return false;
        return len >= 6;
    }
}
