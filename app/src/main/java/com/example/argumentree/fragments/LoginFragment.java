package com.example.argumentree.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.argumentree.Constants;
import com.example.argumentree.R;
import com.example.argumentree.SharedPrefHelper;
import com.example.argumentree.UserAuthActivity;
import com.example.argumentree.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    public static final String TAG = "LoginFragment";

    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // pulling in view references
        btnLogin = view.findViewById(R.id.btnLogIn);
        etEmail = view.findViewById(R.id.etLoginEmail);
        etPassword = view.findViewById(R.id.etLoginPassword);

        // Setting listeners on view elements
        //
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Getting user object from firestore and storing it in shared prefs
                            storeFirestoreUser();
                            getActivity().finish();

                        } else {
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });


    }

    // pulls full user information from firestore and stores it in shared prefs
    private void storeFirestoreUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Parent activity
        final UserAuthActivity parentActivity = (UserAuthActivity) getActivity();
        db.collection(Constants.FB_USERS)
                .document(currentUserUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            // putting User object in shared prefs
                            DocumentSnapshot document = task.getResult();
                            User user = document.toObject(User.class);
                            SharedPrefHelper.putUserIn(parentActivity, user);
                        }
                    }
                });
    }

}