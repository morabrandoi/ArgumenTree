package com.example.argumentree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.argumentree.fragments.HomeFragment;
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

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class OthersProfileActivity extends AppCompatActivity {
    public static final String TAG = "OthersProfileActivity";

    // UI variables
    private Button btnLogOut;
    private ImageView ivOthersProfilePic;
    private TextView tvOthersUsername;
    private TextView tvOthersBio;
    private ToggleButton togBtnLike;
    private TextView tvLikeCount;
    private TextView tvHasContributedTo;
    private RecyclerView rvContributions;

    // Model variables
    private List<Post> othersPosts;
    private PostsAdapter postsAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        // pull user from intent
        Intent intent = getIntent();
        Parcelable wrappedUser = intent.getParcelableExtra("user");
        this.user = Parcels.unwrap(wrappedUser);

        // Pulling all view elements in
        btnLogOut = findViewById(R.id.btnLogOut);
        ivOthersProfilePic = findViewById(R.id.ivOthersProfilePic);
        tvOthersUsername = findViewById(R.id.tvOthersUsername);
        tvOthersBio = findViewById(R.id.tvOthersBio);
        togBtnLike = findViewById(R.id.togBtnLike);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvHasContributedTo = findViewById(R.id.tvHasContributedTo);
        rvContributions = findViewById(R.id.rvContributions);

        // Setting up recycler view
        othersPosts = new ArrayList<Post>();
        postsAdapter = new PostsAdapter(this, othersPosts);
        rvContributions.setLayoutManager(new LinearLayoutManager(this));
        rvContributions.setAdapter(postsAdapter);
        rvContributions.addItemDecoration(new DividerItemDecoration(rvContributions.getContext(), DividerItemDecoration.VERTICAL));
        fillContributionsFromFirestore();

        // Setting UI elements
        tvOthersUsername.setText(user.getUsername());
        tvOthersBio.setText(user.getBio());

        if (this.user.getProfilePic() != null){
            Glide.with(this).load( this.user.getProfilePic() ).into(ivOthersProfilePic);
        }

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

                        // cast incoming post as Question or Response
                        String postType = document.getString(Constants.POST_TYPE);
                        if (postType.equals(Constants.QUESTION)) {
                            Question question = document.toObject(Question.class);
                            question.setDocID(document.getId());
                            othersPosts.add(question);
                        } else if (postType.equals( Constants.RESPONSE )) {
                            Response response = document.toObject(Response.class);
                            response.setDocID(document.getId());
                            othersPosts.add(response);
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