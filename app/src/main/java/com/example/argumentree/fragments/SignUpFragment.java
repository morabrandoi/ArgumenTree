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

    private Button btnSignUp;
    private Button btnLogInInstead;
    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword;

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

        btnSignUp = view.findViewById(R.id.btnSignUp);
        btnLogInInstead = view.findViewById(R.id.btnLogInInstead);
        etEmail = view.findViewById(R.id.etSignUpEmail);
        etUsername = view.findViewById(R.id.etSignUpUsername);
        etPassword = view.findViewById(R.id.etSignUpPassword);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            // TODO: Parse error and update UI accordingly (e.g. weakpassword -> Toast to change password)
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
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

    private void updateUI(FirebaseUser user) {
        if (user == null){ // Auth failed
            etPassword.setText(null);

        // Auth was successful
        } else {
            addUserToFirestore(user.getUid());
            getActivity().finish();
        }
    }

    private void addUserToFirestore(String uid) {

        // TODO: Stretch: Implement that the email and username must be unique
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
        db.collection("users").document(username)
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