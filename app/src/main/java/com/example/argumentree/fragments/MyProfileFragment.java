package com.example.argumentree.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import com.example.argumentree.R;
import com.example.argumentree.UserAuthActivity;
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

/**
 * A simple {@link Fragment} subclass.
*/
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

        // fill view with data
        fillUserInfoFromFirestore(view);

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
            }
        });

        //
    }

    private void fillUserInfoFromFirestore(final View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = db.collection("users").whereEqualTo("auth_user_id", currentUserUID);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                        // Filling in User info
                        User user = User.UserFromDocument(document);
                        tvProfilePageUsername.setText(user.username);
                        tvProfilePageBio.setText(user.bio);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}