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
import android.widget.ToggleButton;

import com.example.argumentree.Constants;
import com.example.argumentree.PostsAdapter;
import com.example.argumentree.R;
import com.example.argumentree.SharedPrefHelper;
import com.example.argumentree.UserAuthActivity;
import com.example.argumentree.models.Post;
import com.example.argumentree.models.Question;
import com.example.argumentree.models.Response;
import com.example.argumentree.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyProfileFragment extends Fragment {
    public static final String TAG = "MyProfileFragment";

    // UI variables
    private Button btnLogOut;
    private ImageView ivProfilePageProfilePic;
    private TextView tvProfilePageUsername;
    private TextView tvProfilePageBio;
    private ToggleButton togBtnLike;
    private TextView tvLikeCount;
    private TextView tvHasContributedTo;
    private RecyclerView rvContributions;

    // Model variables
    private List<Post> ownPosts;
    private PostsAdapter postsAdapter;
    private User user;

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
        ownPosts = new ArrayList<Post>();
        postsAdapter = new PostsAdapter(getContext(), ownPosts);
        rvContributions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContributions.setAdapter(postsAdapter);

        // fill view with data
        user = fillUserInfoFromSharedPrefs();
        fillContributionsFromFirestore();

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

    private User fillUserInfoFromSharedPrefs(){
        // Getting User object from shared prefs
        User user = SharedPrefHelper.getUser(getActivity());
        tvProfilePageUsername.setText(user.getUsername());
        tvProfilePageBio.setText(user.getBio());

        return user;
    }

    private void fillContributionsFromFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("posts")
                .orderBy(Constants.KEY_POST_CREATED_AT, Query.Direction.ASCENDING)
                .whereEqualTo(Constants.KEY_QUESTION_AUTHOR, user.getUsername());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "successful pull of posts");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // check which kind it is
                        String postType = document.getString(Constants.KEY_POST_TYPE);
                        if (postType.equals(Constants.KEY_QUESTION_TYPE)){
                            Question question = document.toObject(Question.class);
                            question.setDocID(document.getId());
                            ownPosts.add(question);
                        }
                        else if (postType.equals("response")){
                            Response response = document.toObject(Response.class);
                            response.setDocID(document.getId());
                            ownPosts.add(response);
                        }

                    }
                    postsAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }
}