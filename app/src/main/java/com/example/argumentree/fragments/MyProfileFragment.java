package com.example.argumentree.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.argumentree.Constants;
import com.example.argumentree.ProfileAdapter;
import com.example.argumentree.R;
import com.example.argumentree.UserAuthActivity;
import com.example.argumentree.models.Question;
import com.example.argumentree.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MyProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";

    private Button btnLogOut;
    private ImageView ivProfilePageProfilePic;
    private TextView tvProfilePageUsername;
    private TextView tvProfilePageBio;
    private ToggleButton togBtnLike;
    private TextView tvLikeCount;
    private TextView tvHasContributedTo;
    private RecyclerView rvContributions;
    private List<Question> ownQuestions;
    private ProfileAdapter profileAdapter;

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

        // Pulling all view elements in
        btnLogOut = view.findViewById(R.id.btnLogOut);
        ivProfilePageProfilePic = view.findViewById(R.id.ivProfilePageProfilePic);
        tvProfilePageUsername = view.findViewById(R.id.tvProfilePageUsername);
        tvProfilePageBio = view.findViewById(R.id.tvProfilePageBio);
        togBtnLike = view.findViewById(R.id.togBtnLike);
        tvLikeCount = view.findViewById(R.id.tvLikeCount);
        tvHasContributedTo = view.findViewById(R.id.tvHasContributedTo);
        rvContributions = view.findViewById(R.id.rvContributions);

        // Setting up recycler view
        ownQuestions = new ArrayList<Question>();
        profileAdapter = new ProfileAdapter(getContext(), ownQuestions);
        rvContributions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContributions.setAdapter(profileAdapter);


        // fill view with data
        fillUserInfoFromSharedPrefs();

        // Setting listeners

        // Logs out the user and sends them to sign in screen
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

                // Clear sharedPreferences of logged in user
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(Constants.KEY_SP_CURRENT_USER, null);
                editor.apply();
            }
        });

    }

    // TODO: Remove this call and move it to login and sign up. Replace with pull from shared prefs

    private void fillUserInfoFromSharedPrefs(){
        // Getting User object from shared prefs
        User user = SharedPrefHelper.getUser(getActivity());
        tvProfilePageUsername.setText(user.getUsername());
        tvProfilePageBio.setText(user.getBio());
    }

    private void fillContributionsFromFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = db.collection("questions").whereEqualTo(Constants.KEY_USER_AUTH_USER_ID, currentUserUID);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                        Question question = document.toObject(Question.class);
                        ownQuestions.add(question);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}