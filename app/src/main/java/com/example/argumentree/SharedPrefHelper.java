package com.example.argumentree;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.argumentree.models.User;
import com.google.gson.Gson;

public class SharedPrefHelper {
    public static final String TAG = "SharedPrefHelper";

    public static User getUser(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SP_CURRENT_USER, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPref.getString(Constants.SP_CURRENT_USER, null);
        if (json == null){
            Log.e(TAG, "Shared pref user is null");
        }
        return gson.fromJson(json, User.class);
    }

    public static void putUserIn(Context context, User user){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SP_CURRENT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(Constants.SP_CURRENT_USER, json);

        editor.apply();
    }

    public static boolean hasUserIn(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SP_CURRENT_USER, Context.MODE_PRIVATE);
        String json = sharedPref.getString(Constants.SP_CURRENT_USER, null);
        return (json != null);
    }
}
