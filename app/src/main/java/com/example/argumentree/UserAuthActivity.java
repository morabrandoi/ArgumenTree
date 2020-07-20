package com.example.argumentree;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.argumentree.fragments.LoginFragment;
import com.example.argumentree.fragments.SignUpFragment;
import com.example.argumentree.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

public class UserAuthActivity extends AppCompatActivity {
    public static final String TAG = "UserAuthActivity";
    public FirebaseAuth auth;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);

        auth = FirebaseAuth.getInstance();

        Fragment signUpFrag = new SignUpFragment();
        fragmentManager.beginTransaction().replace(R.id.flContainerUserAuth, signUpFrag).commit();
    }

//    public void storeUserInSharedPrefs(User user){
//        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(user);
//        editor.putString(Constants.KEY_SP_CURRENT_USER, json);
//
//        editor.apply();
//    }
}