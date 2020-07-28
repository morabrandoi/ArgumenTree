package com.example.argumentree.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.argumentree.Constants;
import com.example.argumentree.PostsAdapter;
import com.example.argumentree.R;
import com.example.argumentree.models.Post;
import com.example.argumentree.models.Question;
import com.example.argumentree.models.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";

    // UI variables
    private RecyclerView rvHome;

    // Model variables
    private List<Post> homePosts;
    private PostsAdapter postsAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setting up recycler view
        rvHome = view.findViewById(R.id.rvHome);
        homePosts = new ArrayList<Post>();
        postsAdapter = new PostsAdapter(getContext(), homePosts);
        rvHome.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHome.setAdapter(postsAdapter);
        rvHome.addItemDecoration(new DividerItemDecoration(rvHome.getContext(), DividerItemDecoration.VERTICAL));


        // fill view with data
        fillContributionsFromFirestore();

    }

    private void fillContributionsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("posts")
                .orderBy(Constants.POST_CREATED_AT, Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "successful pull of posts");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // check which kind it is
                        String postType = document.getString(Constants.POST_TYPE);
                        if (postType.equals(Constants.QUESTION)) {
                            Question question = document.toObject(Question.class);
                            question.setDocID(document.getId());
                            homePosts.add(question);
                        } else if (postType.equals("response")) {
                            Response response = document.toObject(Response.class);
                            response.setDocID(document.getId());
                            homePosts.add(response);
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