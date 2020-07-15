package com.example.argumentree.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.argumentree.R;
import com.example.argumentree.UserAuthActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

/**
 * A simple {@link Fragment} subclass.
*/
public class MyProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    private Button btnLogOut;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Signing out user
                FirebaseAuth.getInstance().signOut();

                // Sending user back to log in screen
                Intent intent = new Intent(getContext(), UserAuthActivity.class);
                startActivity(intent);

                // Setting tab to be home timeline
                Fragment fragment = new HomeFragment(); 
                getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);
                bottomNavigationView.setSelectedItemId(R.id.action_home);
            }
        });
    }
}