package com.example.argumentree;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.example.argumentree.models.Post;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {
    public static final String TAG = "PostDetailActivity";

    private Post post;

    // single post to focus
    private RecyclerView rvDetails;
    private List<Post> singleFocusPost;
    private PostsAdapter singlePostAdapter;

    // Response Post
    private RecyclerView rvResponses;
    private List<Post> responsePosts;
    private PostsAdapter responseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // pulling in post and responses from intent
        Intent intent = getIntent();
        Parcelable wrappedPost = intent.getParcelableExtra(Constants.FB_POSTS);
        Parcelable wrappedResponses = intent.getParcelableExtra(Constants.ALL_RESPONSES);

        responsePosts = Parcels.unwrap(wrappedResponses);
        post = Parcels.unwrap(wrappedPost);


        // Setting up recycler view for single post
        rvDetails = findViewById(R.id.rvDetails);
        singleFocusPost = new ArrayList<Post>();
        singleFocusPost.add(post);
        singlePostAdapter = new PostsAdapter(this, singleFocusPost);
        rvDetails.setLayoutManager(new LinearLayoutManager(this));
        rvDetails.setAdapter(singlePostAdapter);
        rvDetails.addItemDecoration(new DividerItemDecoration(rvDetails.getContext(), DividerItemDecoration.VERTICAL));

        // setting up recycler view for related posts
        rvResponses = findViewById(R.id.rvResponses);
        responseAdapter = new PostsAdapter(this, responsePosts);
        rvResponses.setLayoutManager(new LinearLayoutManager(this));
        rvResponses.setAdapter(responseAdapter);
        rvResponses.addItemDecoration(new DividerItemDecoration(rvResponses.getContext(), DividerItemDecoration.VERTICAL));
    }
}