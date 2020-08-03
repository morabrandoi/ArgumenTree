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
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);

        Fragment signUpFrag = new SignUpFragment();
        fragmentManager.beginTransaction().replace(R.id.flContainerUserAuth, signUpFrag).commit();
    }

}