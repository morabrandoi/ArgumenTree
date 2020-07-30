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
import android.widget.SearchView;
import android.widget.TextView;

import com.example.argumentree.Constants;
import com.example.argumentree.PostsAdapter;
import com.example.argumentree.R;
import com.example.argumentree.models.Post;
import com.example.argumentree.models.Question;
import com.example.argumentree.models.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {
    public static final String TAG = "SearchFragment";

    // UI elements
    private RecyclerView rvSearch;
    private SearchView searchView;

    // Model variables
    private List<Post> searchedPosts;
    private PostsAdapter postsAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // reference all view elements
        rvSearch = view.findViewById(R.id.rvSearch);
        searchView = view.findViewById(R.id.searchView);

        // Setting up recyclerView
        searchedPosts = new ArrayList<Post>();
        postsAdapter = new PostsAdapter(getContext(), searchedPosts);
        rvSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearch.setAdapter(postsAdapter);
        rvSearch.addItemDecoration(new DividerItemDecoration(rvSearch.getContext(), DividerItemDecoration.VERTICAL));

        // Setting up search view
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "On Search click", Snackbar.LENGTH_SHORT).show();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                if ( s.indexOf("/tags:") == 0 ) {
                    fillPostsByTag(s);
                }
                else{
                    fillPostsByString(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
    // TODO: Fill in search by tag as well as regular string match search
    private void fillPostsByTag(String queryString) {
        // splits query string into list of tags
        List<String> tags = new ArrayList<String>(Arrays.asList((queryString.substring(6)).split("\\s*,\\s*")));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(Constants.FB_POSTS).whereArrayContainsAny(Constants.QUESTION_TAGS, tags);
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
                            question.setDocID( document.getId() );
                            searchedPosts.add(question);
                        } else if (postType.equals(Constants.RESPONSE)) {
                            Response response = document.toObject(Response.class);
                            response.setDocID( document.getId() );
                            searchedPosts.add(response);
                        }
                    }
                    postsAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // queries firestore for an exact match on the question body as the string
    private void fillPostsByString(String queryString) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection(Constants.FB_POSTS).whereEqualTo(Constants.QUESTION_BODY, queryString);

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
                            searchedPosts.add(question);
                        } else if (postType.equals(Constants.RESPONSE)) {
                            Response response = document.toObject(Response.class);
                            response.setDocID(document.getId());
                            searchedPosts.add(response);
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