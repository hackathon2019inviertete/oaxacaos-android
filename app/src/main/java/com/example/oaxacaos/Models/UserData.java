package com.example.oaxacaos.Models;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class UserData {

    public ArrayList<String> files = new ArrayList<>();
    private static final UserData USER_DATA = new UserData();

    public static UserData getInstance() {
        return USER_DATA;
    }

    private UserData() {
    }
}
