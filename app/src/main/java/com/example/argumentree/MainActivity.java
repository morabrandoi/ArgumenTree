package com.example.argumentree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.argumentree.fragments.HomeFragment;
import com.example.argumentree.fragments.NotificationsFragment;
import com.example.argumentree.fragments.MyProfileFragment;
import com.example.argumentree.fragments.SearchFragment;
import com.example.argumentree.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getInstance;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final String TERMS_OF_SERVICE_URL = "https://www.youtube.com/watch?v=5_OKQ7A-5W0";
    public static final String PRIVACY_POLICY = "https://www.youtube.com/watch?v=9KZ-ptvWD6U";
    private static final int RC_SIGN_IN = 1822;
    private static final List<AuthUI.IdpConfig> PROVIDERS = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().setRequireName(false).build(),
            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build());

    // UI variables
    private BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ensure firebaseInstanceID is in sharedPrefs
        ensureFirebaseInstanceIDInSharedPref();

        // Ensure that user is signed in
        syncSharedPrefWithCurrentUser();

        // Pulling references View elements
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_compose) {
                    // Check if user is signed in. If not -> sign in page. If so -> compose page
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        signInUser();

                        return false;
                    } else {
                        Intent intent = new Intent(MainActivity.this, ComposeQuestionActivity.class);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                        startActivity(intent, options.toBundle());
                        return true;
                    }
                }

                // Switching tabs as fragments
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_profile:
                        // If not signed in, go to log in page.
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            signInUser();

                            return false;
                        }
                        fragment = new MyProfileFragment();
                        break;
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_notifications:
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            signInUser();
                            return false;
                        }
                        fragment = new NotificationsFragment();
                        break;
                    case R.id.action_search:
                        fragment = new SearchFragment();
                        break;
                    default:
                        fragment = null;
                }

                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        // Set fragment on main activity
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    // Kick start sign in
    private void signInUser(){
        startActivityForResult(
                getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(PROVIDERS)
                        .setTheme(R.style.AppTheme)
                        .setLogo(R.drawable.ic_logo_with_text)
                        .setTosAndPrivacyPolicyUrls(
                                TERMS_OF_SERVICE_URL,
                                PRIVACY_POLICY
                        )
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                if (response.isNewUser()){

                    // checking to see if automatically added to FirebaseAuth
                    if (FirebaseAuth.getInstance().getCurrentUser() == null){
                        Log.i(TAG, "onActivityResult: USER SHOULD NOT BE NULL");
                        Toast.makeText(this, "ERROR: getCurrentUser is null!", Toast.LENGTH_SHORT).show();
                    }

                    String providerType = response.getProviderType();
                    String email = response.getEmail();
                    String phoneNumber = response.getPhoneNumber();
                    addUserToFirestore(providerType, email, phoneNumber);
                }
                else{
                    getUserAndStoreInSharedPref();

                }


            } else {
                if (response == null){
                    return;
                }

                Log.e(TAG, "onActivityResult: Error Authenticating", response.getError().getCause());

            }
        }
    }

    // if user is not in sharedPref, pull user in and add to shared Pref
    private void syncSharedPrefWithCurrentUser() {
        boolean sharedPrefHasUser = SharedPrefHelper.hasUserIn(this);
        boolean existsCurrentUser = FirebaseAuth.getInstance().getCurrentUser() != null;
        Log.i(TAG, "ensureUserInSharedPref: sharedPrefHasUser? " + sharedPrefHasUser);
        Log.i(TAG, "ensureUserInSharedPref: existsCurrentUser? " + existsCurrentUser);

        if (existsCurrentUser){
            if (sharedPrefHasUser){
                Log.i(TAG, "ensureUserInSharedPref: All good, both currentUser and SharedPref are in sync");
            }
            else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                Log.i(TAG, "ensureUserInSharedPref: CurrentUserID" + currentUserUID);
                db.collection(Constants.FB_USERS)
                        .document(currentUserUID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    // putting User object in shared prefs
                                    DocumentSnapshot document = task.getResult();
                                    User user = document.toObject(User.class);
                                    Log.i(TAG, "onComplete: User " + user.toString());
                                    SharedPrefHelper.putUserIn(MainActivity.this, user);
                                }
                                else{
                                    Log.e(TAG, "onFailure: could not put user in databse", task.getException());
                                }
                            }
                        });
            }
        }
        else{
            if (sharedPrefHasUser){
                SharedPrefHelper.clearUser(this);
                Log.i(TAG, "syncSharedPrefWithCurrentUser: Removed user from sharedPref");
            }
            else {
                Log.i(TAG, "ensureUserInSharedPref: Nothing wrong, user just needs to sign in");
            }
        }
    }

    // pulls user from firestore and stores in sharedPref
    private void getUserAndStoreInSharedPref(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Parent activity
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
                            SharedPrefHelper.putUserIn(MainActivity.this, user);
                            putFCMTokenInFirestore();
                        }
                        else{
                            Log.e(TAG, "onComplete: Failed to get user", task.getException());
                        }
                    }
                });
    }

    // Takes in new user and adds them to firestore
    private void addUserToFirestore(String providerType, String email, String phoneNumber) {
        // Assigning fields of the user object
        String authUserID = FirebaseAuth.getInstance().getUid();
        String bio = "Hanging in there!";
        String profilePic = "https://placeimg.com/360/360/people";
        String username = "changeMe!";
        int likes = 0;

        List<String> deviceTokens = new ArrayList<>();
        deviceTokens.add( SharedPrefHelper.getFirebaseInstanceID(this) );

        Date createdAt = new Date();


        // Creating user object from the fields
        final User user = new User(authUserID, bio, email, phoneNumber, profilePic, providerType, username, likes, deviceTokens, createdAt);

        // Posting user object to firestore and storing user object in shared prefs when successful
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.FB_USERS).document(authUserID)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        // Storing user in shared prefs
                        SharedPrefHelper.putUserIn(MainActivity.this, user);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding document", e);
                    }
                });
    }

    // if the FirebaseInstanceID is not in shared pref, add it
    private void ensureFirebaseInstanceIDInSharedPref() {
        Log.i(TAG, "ensuring firebaseinstanceID is in shared pref");
        boolean hasInstanceID = SharedPrefHelper.hasUserIn(this);

        if (hasInstanceID) {
            Log.i(TAG, "Was in shared pref already. Nice ");
            return;
        }

        Log.i(TAG, "Was not in shared pref already");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        SharedPrefHelper.putFirebaseInstanceIDIn(MainActivity.this, token);

                        // Log and toast
                        String msg = token;
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    // put FirebaseInstanceIDInFirestore for signed in User object
    private void putFCMTokenInFirestore() {
        Log.i(TAG, "putFCMTokenInFirestore: Just testing im here");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String firebaseInstanceID = SharedPrefHelper.getFirebaseInstanceID( this );

        db.collection( Constants.FB_USERS )
                .document( auth.getCurrentUser().getUid() )
                .update( Constants.USER_DEVICE_TOKENS, FieldValue.arrayUnion( firebaseInstanceID ))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "onSuccess: firebaseInstanceID was updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: firebaseInstanceID was not updated to user", e);
                    }
                });
    }
}