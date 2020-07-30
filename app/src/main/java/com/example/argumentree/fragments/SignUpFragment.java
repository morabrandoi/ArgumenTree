package com.example.argumentree.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.argumentree.R;
import com.example.argumentree.SharedPrefHelper;
import com.example.argumentree.UserAuthActivity;
import com.example.argumentree.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    public static final String TAG = "SignUpFragment";

    // UI elements
    private Button btnSignUp;
    private Button btnLogInInstead;
    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword;

    // Firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db;


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // pulling in UI elements
        btnSignUp = view.findViewById(R.id.btnSignUp);
        btnLogInInstead = view.findViewById(R.id.btnLogInInstead);
        etEmail = view.findViewById(R.id.etSignUpEmail);
        etUsername = view.findViewById(R.id.etSignUpUsername);
        etPassword = view.findViewById(R.id.etSignUpPassword);

        // instantiate firebase objects
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // set on click listeners
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                // TODO: Parse error and update UI accordingly (e.g. weakpassword -> Toast to change password)
                // TODO: Stretch: Implement that the email and username must be unique
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            addUserToFirestore(user.getUid());
                            getActivity().finish();
                        } else {
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            etPassword.setText(null);
                        }

                    }
                });
            }
        });


        btnLogInInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment loginFrag = new LoginFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainerUserAuth, loginFrag).commit();
            }
        });

    }

    // creating a firestore user object
    private void addUserToFirestore(String uid) {
        // Assigning fields of the user object
        String email = etEmail.getText().toString();
        String username = etUsername.getText().toString();
        String profilePic = null;
        String bio = "Hanging in there!";
        String authUserID = uid;
        Date createdAt = new Date();
        int likes = 0;

        // Creating user object from the fields
        final User user = new User(email, username, profilePic, bio, authUserID, createdAt, likes);
        // Grabbing reference to current activity
        final UserAuthActivity parentActivity = (UserAuthActivity) getActivity();

        // Posting user object to firestore and storing user object in shared prefs when successful
        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        // Storing user in shared prefs
                        SharedPrefHelper.putUserIn(parentActivity, user);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding document", e);
                    }
                });
    }


}