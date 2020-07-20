package com.example.argumentree.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.argumentree.Constants;
import com.example.argumentree.models.User;
import com.google.gson.Gson;

public class SharedPrefHelper {
    public static final String TAG = "SharedPrefHelper";

    public static User getUser(Context context){
        Log.i(TAG, "Pull user from shared Pref");
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.KEY_SP_CURRENT_USER, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPref.getString(Constants.KEY_SP_CURRENT_USER, null);
        if (json == null){
            Log.e(TAG, "\n\nSHARED PREF USER IS NULL\n\n");
        }
        return gson.fromJson(json, User.class);
    }

    public static void putUserIn(Context context, User user){
        Log.i(TAG, "Put user in sharedPref");
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.KEY_SP_CURRENT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(Constants.KEY_SP_CURRENT_USER, json);

        editor.apply();
    }

    public static boolean hasUserIn(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.KEY_SP_CURRENT_USER, Context.MODE_PRIVATE);
        String json = sharedPref.getString(Constants.KEY_SP_CURRENT_USER, null);
        Log.i(TAG, "Checked if user in shared pref: " + (json != null));
        Log.i(TAG, "It is: " +json);
        return (json != null);
    }
}
