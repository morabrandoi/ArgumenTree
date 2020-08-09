package com.example.argumentree.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.argumentree.Constants;
import com.example.argumentree.PostsAdapter;
import com.example.argumentree.R;
import com.example.argumentree.SharedPrefHelper;
import com.example.argumentree.models.Post;
import com.example.argumentree.models.Question;
import com.example.argumentree.models.Response;
import com.example.argumentree.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyProfileFragment extends Fragment {
    public static final String TAG = "MyProfileFragment";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1000;

    //TODO: Debug And test Sign in and sign out
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

        // Getting references to view elements
        btnLogOut = view.findViewById(R.id.btnLogOut);
        ivProfilePageProfilePic = view.findViewById(R.id.ivProfilePageProfilePic);
        tvProfilePageUsername = view.findViewById(R.id.tvProfilePageUsername);
        tvProfilePageBio = view.findViewById(R.id.tvProfilePageBio);
        togBtnLike = view.findViewById(R.id.togBtnLike);
        tvLikeCount = view.findViewById(R.id.tvLikeCount);
        rvContributions = view.findViewById(R.id.rvContributions);

        // Set up user-dependant information
        user = SharedPrefHelper.getUser(getActivity());
        tvProfilePageUsername.setText(user.getUsername());
        tvProfilePageBio.setText(user.getBio());
        if (user.getProfilePic() != null) {
            Glide.with(this).load(user.getProfilePic()).circleCrop().into(ivProfilePageProfilePic);
        }

        // Setting up recycler view
        ownPosts = new ArrayList<Post>();
        postsAdapter = new PostsAdapter(getContext(), ownPosts);
        rvContributions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContributions.setAdapter(postsAdapter);
        rvContributions.setNestedScrollingEnabled(false);
        rvContributions.addItemDecoration(new DividerItemDecoration(rvContributions.getContext(), DividerItemDecoration.VERTICAL));
        fillContributionsFromFirestore();

        // Setting listeners

        // Logs out the user and sends them to sign in screen
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // remove this firebaseInstanceID from the user array
                removeFirebaseInstanceIDFromUser();

                // Signing out user
                AuthUI.getInstance()
                        .signOut(getActivity())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG, "onSuccess: Signout was successful");
                                Toast.makeText(getActivity(), "Sign out was successful", Toast.LENGTH_SHORT).show();

                                finishSignOut();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "onFailure: Signout was unsuccessful");
                                Toast.makeText(getActivity(), "Sign out was unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        //
        ivProfilePageProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Camera action for result
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });
    }

    private void finishSignOut() {
        // Setting tab to be home timeline
        Fragment fragment = new HomeFragment();
        getFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.action_home);

        // Clear sharedPreferences of logged in user
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.SP_CURRENT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(Constants.SP_CURRENT_USER);
//        editor.putString(Constants.SP_CURRENT_USER, null);

        editor.apply();
    }

    private void removeFirebaseInstanceIDFromUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String firebaseInstanceID = SharedPrefHelper.getFirebaseInstanceID(getContext());

        db.collection(Constants.FB_USERS)
                .document(auth.getCurrentUser().getUid())
                .update(Constants.USER_DEVICE_TOKENS, FieldValue.arrayRemove(firebaseInstanceID))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "onSuccess: firebaseInstanceID was updated and deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: firebaseInstanceID was not updated to user", e);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If return was successful and okay
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ivProfilePageProfilePic.setImageBitmap(bitmap);
            uploadToFirestorage(bitmap);
        }
    }

    private void uploadToFirestorage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("profileImages").child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "onSuccess: " + uri);
                                setFirestoreUserProfilePic(uri);
                                updateSharedPrefUser(uri);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "failure in storage upload: ", e.getCause());
                    }
                });
    }

    private void setFirestoreUserProfilePic(Uri uri) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        db.collection(Constants.FB_USERS)
                .document(user.getUid())
                .update(Constants.USER_PROFILE_PIC, uri.toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "update success!");
                        } else {
                            Log.i(TAG, "update failed");
                        }

                    }
                });
    }

    private void updateSharedPrefUser(Uri uri) {
        user.setProfilePic(uri.toString());
        SharedPrefHelper.putUserIn(getContext(), user);
    }

    private void fillContributionsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(Constants.FB_POSTS)
                .orderBy(Constants.POST_CREATED_AT, Query.Direction.DESCENDING)
                .whereEqualTo(Constants.AUTHOR_REF, user.getAuthUserID());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // split casting of document depending on what kind of post it is
                        String postType = document.getString(Constants.POST_TYPE);
                        if (postType.equals(Constants.QUESTION)) {
                            Question question = document.toObject(Question.class);
                            question.setDocID(document.getId());
                            ownPosts.add(question);
                        } else if (postType.equals(Constants.RESPONSE)) {
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