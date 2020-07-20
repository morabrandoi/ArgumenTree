package com.example.argumentree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.argumentree.fragments.HomeFragment;
import com.example.argumentree.fragments.NotificationsFragment;
import com.example.argumentree.fragments.MyProfileFragment;
import com.example.argumentree.fragments.SearchFragment;
import com.example.argumentree.fragments.SharedPrefHelper;
import com.example.argumentree.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Ensure if user is signed in then user object is in shared prefs
        ensureUserInSharedPref();
        // Pulling references View elements
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.action_compose) {
                    // Executes if compose Tab was clicked
                    Intent intent = new Intent(MainActivity.this, ComposeQuestionActivity.class);
                    startActivity(intent);
                    return true;
                }


                // Switching tabs as fragments
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_profile:
                        // If not signed in, go to log in page.
                        Log.e(TAG, "GetUID" + FirebaseAuth.getInstance().getCurrentUser());
                        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Intent intent = new Intent(MainActivity.this, UserAuthActivity.class);
                            startActivity(intent);
                            return false;
                        }
                        fragment = new MyProfileFragment();
                        break;
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_notifications:
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

    private void ensureUserInSharedPref() {
        boolean hasUser = SharedPrefHelper.hasUserIn(this);
        if (!hasUser && FirebaseAuth.getInstance().getCurrentUser() != null) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Query query = db.collection("users").whereEqualTo(Constants.KEY_USER_AUTH_USER_ID, currentUserUID);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            // putting User object in shared prefs
                            User user = document.toObject(User.class);
                            SharedPrefHelper.putUserIn(MainActivity.this, user);

                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        }

    }
}