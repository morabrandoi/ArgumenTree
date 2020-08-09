package com.example.argumentree;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.argumentree.models.User;
import com.google.gson.Gson;

public class SharedPrefHelper {
    public static final String TAG = "SharedPrefHelper";

    public static User getUser(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SP_CURRENT_USER, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPref.getString(Constants.SP_CURRENT_USER, null);
        User currentUser = gson.fromJson(json, User.class);

        if (currentUser == null){
            Log.e(TAG, "Shared pref user is null");
        }

        return currentUser;
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
        Gson gson = new Gson();
        User currentUser = gson.fromJson(json, User.class);

        return (currentUser != null);
    }

    public static void clearUser(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SP_CURRENT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(Constants.SP_CURRENT_USER);
        editor.apply();
    }

    public static void putFirebaseInstanceIDIn(Context context, String id){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SP_FIREBASE_INSTANCE_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.SP_FIREBASE_INSTANCE_ID, id);

        editor.apply();
    }

    public static String getFirebaseInstanceID(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SP_FIREBASE_INSTANCE_ID, Context.MODE_PRIVATE);
        String id = sharedPref.getString(Constants.SP_FIREBASE_INSTANCE_ID, null);
        if (id == null){
            Log.e(TAG, "Shared pref firebaseInstanceID is null");
        }
        return id;
    }

    public static boolean hasFirebaseInstanceIDIn(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SP_FIREBASE_INSTANCE_ID, Context.MODE_PRIVATE);
        String id = sharedPref.getString(Constants.SP_FIREBASE_INSTANCE_ID, null);
        return (id == null);
    }
}
