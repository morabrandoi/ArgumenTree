package com.example.argumentree;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.argumentree.fragments.LoginFragment;
import com.example.argumentree.fragments.SignUpFragment;
import com.google.firebase.auth.FirebaseAuth;

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
}