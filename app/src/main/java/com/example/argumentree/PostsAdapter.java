package com.example.argumentree;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argumentree.models.Post;
import com.example.argumentree.models.Question;
import com.example.argumentree.models.Response;
import com.example.argumentree.models.User;
import com.example.argumentree.models.Vote;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{
        private static final String TAG = "PostsAdapter";

        private Context context;
        private List<Post> loadedPosts;

        public PostsAdapter(Context context, List<Post> loadedPosts) {
            this.context = context;
            this.loadedPosts = loadedPosts;
        }

        @NonNull
        @Override
        public PostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
            return new PostsAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
            Post post = loadedPosts.get(position);
            // Bind differently based on whether the post object is a question or a response
            if (post instanceof Response) {
                Response response = (Response) post;
                holder.bindResponse(response);
            } else {
                Question question = (Question) post;
                holder.bindQuestion(question);
            }
        }

        @Override
        public int getItemCount() {
            return loadedPosts.size();
        }

        public void clear() {
            loadedPosts.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<Post> loadedPosts) {
            this.loadedPosts.addAll(loadedPosts);
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            // Question UI
            private TextView tvQuestionBody;
            private TextView tvQuestionDescendants;
            private TextView tvQuestionUsername;
            private ImageView iconQuestionReply;
            private ImageView iconQuestionTreeView;

            // Response UI
            private ConstraintLayout clResponse;
            private TextView tvResponseBrief;
            private TextView tvResponseClaim;
            private TextView tvResponseSource;
            private TextView tvResponseLikes;
            private TextView tvResponseDislikes;
            private TextView tvResponseUsername;
            private ImageView iconResponseReply;
            private ImageView iconResponseTreeView;
            private ImageView iconLike;
            private ImageView iconDislike;

            // Model
            User questionUser;
            User responseUser;
            String voteState = null;
            DocumentReference voteDocument = null;
            Boolean middleOfBatchWrite = false;

            //TODO: fix recycler view recycling
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // Question UI references
                tvQuestionBody = itemView.findViewById(R.id.tvQuestionBody);
                tvQuestionDescendants = itemView.findViewById(R.id.tvQuestionDescendants);
                tvQuestionUsername = itemView.findViewById(R.id.tvQuestionUsername);
                iconQuestionReply = itemView.findViewById(R.id.iconQuestionReply);
                iconQuestionTreeView = itemView.findViewById(R.id.iconQuestionTreeView);

                // Response UI references
                clResponse = itemView.findViewById(R.id.clResponse);
                tvResponseBrief = itemView.findViewById(R.id.tvResponseBrief);
                tvResponseClaim = itemView.findViewById(R.id.tvResponseClaim);
                tvResponseSource = itemView.findViewById(R.id.tvResponseSource);
                tvResponseLikes = itemView.findViewById(R.id.tvResponseLikes);
                tvResponseDislikes = itemView.findViewById(R.id.tvResponseDislikes);
                tvResponseUsername = itemView.findViewById(R.id.tvResponseUsername);
                iconResponseReply = itemView.findViewById(R.id.iconResponseReply);
                iconResponseTreeView = itemView.findViewById(R.id.iconResponseTreeView);
                iconLike = itemView.findViewById(R.id.iconLike);
                iconDislike = itemView.findViewById(R.id.iconDislike);

                clResponse.setVisibility(View.GONE);
            }

            // Read in Vote state on load so each post can have proper liking
            private void assignVoteState(final Response response) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();

                // voteState stays null if user is not signed in
                if (auth.getCurrentUser() == null){
                    Log.i(TAG, "get current user was null");
                    return;
                }


                db.collection( Constants.FB_VOTES )
                        .whereEqualTo( Constants.VOTE_RESPONSE_REF, response.getDocID() )
                        .whereEqualTo( Constants.VOTE_USER_REF, auth.getCurrentUser().getUid() )
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    QuerySnapshot result = task.getResult();
                                     if ( result.isEmpty() ) {
                                         voteState = Constants.VOTE_STATE_UNVOTED;
                                         Log.i(TAG, "result from vote lookup was empty");
                                     }
                                     else if (result.size() > 1){
                                         Log.e(TAG, "multiple votes returned when they should be unique");
                                     }
                                     else{
                                         // Should only execute once
                                         for (DocumentSnapshot doc : result){
                                             Log.i(TAG, "parsing result");
                                             voteDocument = doc.getReference();
                                             if ( doc.getString( Constants.VOTE_DIRECTION ).equals( Constants.VOTE_LIKE ) ){
                                                 voteState = Constants.VOTE_STATE_LIKED;
                                             }
                                             else {
                                                 voteState = Constants.VOTE_STATE_DISLIKED;
                                             }
                                         }
                                     }
                                }
                                else{
                                    Log.e(TAG, "Querying for vote unsuccessful");
                                }
                                updateVoteIcons();
                            }
                        });
            }

            // Binds a question post to the view
            public void bindQuestion(final Question question) {
                // Fill username
                getUserAndFill(question.getAuthorRef(), question);

                // Fill initial
                tvQuestionBody.setText(question.getBody());
                tvQuestionDescendants.setText(Integer.toString(question.getDescendants()));
                if ( question.getDescendants() == 0){
                    iconQuestionTreeView.setVisibility(View.GONE);
                }

                iconQuestionReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ComposeResponseActivity.class);
                        intent.putExtra(Constants.PARENT_TYPE, Constants.QUESTION);
                        Parcelable wrappedQuestion = Parcels.wrap(question);
                        intent.putExtra(Constants.QUESTION, wrappedQuestion);
                        context.startActivity(intent);
                    }
                });

                // Set listeners
                iconQuestionTreeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, TreeActivity.class);

                        Parcelable wrappedResponse = Parcels.wrap(question);
                        intent.putExtra(Constants.QUESTION, wrappedResponse);
                        context.startActivity(intent);
                    }
                });

                // tree view button
                iconResponseTreeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, TreeActivity.class);

                        Parcelable wrappedResponse = Parcels.wrap(question);
                        intent.putExtra(Constants.QUESTION, wrappedResponse);
                        context.startActivity(intent);
                    }
                });
            }

            // Binds a response post to the view
            public void bindResponse(final Response response) {
                // fill username
                getUserAndFill(response.getAuthorRef(), response);
                // Bind associated question and fill its information in
                getResponseQuestionAndFill(response.getQuestionRef());
                // bind vote
                assignVoteState(response);

                clResponse.setVisibility(View.VISIBLE);
                tvResponseBrief.setText( response.getBrief() );
                tvResponseClaim.setText( response.getClaim() );
                tvResponseSource.setText( response.getSource() );
                tvResponseLikes.setText( Integer.toString( response.getLikes() ) );
                tvResponseDislikes.setText( Integer.toString( response.getDislikes() ) );
                // set Listeners
                // response button
                iconResponseReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ComposeResponseActivity.class);

                        intent.putExtra(Constants.PARENT_TYPE, Constants.RESPONSE);
                        Parcelable wrappedResponse = Parcels.wrap(response);
                        intent.putExtra(Constants.RESPONSE, wrappedResponse);
                        context.startActivity(intent);
                    }
                });

                iconLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleVote(response, Constants.VOTE_LIKE);
                    }
                });

                // similar to above
                iconDislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleVote(response, Constants.VOTE_DISLIKE);
                    }
                });

                tvResponseBrief.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        String label = "ArgumentTree Response Post";
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(label, response.getBrief() );
                        clipboard.setPrimaryClip(clip);

                        Snackbar.make(view, "Text copied to clipboard", Snackbar.LENGTH_SHORT).show();

                        return false;
                    }
                });
            }

            // Contains the logic for maintaining vote state as well as pushing updates to firestore
            private void handleVote(final Response response, final String direction) {
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Conditions where handleVote should not be executed
                if (auth.getCurrentUser() == null){
                    Log.e(TAG, "User not signed in so skipped doing anything on click");
                    return;
                }
                if (voteState == null){
                    Log.i(TAG, "VoteState is null");
                    return ;
                }
                if (middleOfBatchWrite){
                    Log.i(TAG, "Clicked in middle of batch write");
                    return ;
                }

                middleOfBatchWrite = true;

                final DocumentReference prevVoteDoc = voteDocument;
                final String prevVoteState = voteState;
                final int prevLikes = Integer.parseInt( tvResponseLikes.getText().toString() );
                final int prevDislikes = Integer.parseInt( tvResponseDislikes.getText().toString() );
                WriteBatch batch = db.batch();

                Log.i(TAG, "voteState before: " + voteState);
                if ( direction.equals( Constants.VOTE_LIKE ) ) {
                    if ( voteState.equals( Constants.VOTE_STATE_UNVOTED ) ) {
                        // create vote doc
                        DocumentReference generatedDocID = db.collection(Constants.FB_VOTES).document();
                        batch.set( generatedDocID, new Vote( response.getDocID(), auth.getCurrentUser().getUid(), direction ) );

                        // increase response post counter by 1
                        DocumentReference responseDocRef = db.collection( Constants.FB_POSTS ).document( response.getDocID() );
                        batch.update( responseDocRef, Constants.RESPONSE_LIKES , FieldValue.increment( 1 ) );

                        // updating vote document and state
                        voteDocument = generatedDocID;
                        voteState = Constants.VOTE_STATE_LIKED;

                        // updating view
                        tvResponseLikes.setText( Integer.toString( prevLikes + 1) );
                    }
                    else if ( voteState.equals( Constants.VOTE_STATE_LIKED ) ) {
                        // delete voteDoc
                        batch.delete( voteDocument );

                        // decrement response like counter by 1
                        DocumentReference responseDocRef = db.collection( Constants.FB_POSTS ).document( response.getDocID() );
                        batch.update( responseDocRef, Constants.RESPONSE_LIKES , FieldValue.increment( -1 ) );

                        // updating views vote document
                        voteDocument = null;
                        voteState = Constants.VOTE_STATE_UNVOTED;

                        // updating view
                        tvResponseLikes.setText( Integer.toString( prevLikes - 1) );
                    }
                    else if ( voteState.equals( Constants.VOTE_STATE_DISLIKED ) ) {
                        // update doc to be dislike
                        batch.update( voteDocument, Constants.VOTE_DIRECTION, Constants.VOTE_DISLIKE );

                        DocumentReference responseDocRef = db.collection( Constants.FB_POSTS ).document( response.getDocID() );
                        // decrement response like by 1
                        batch.update( responseDocRef, Constants.RESPONSE_LIKES , FieldValue.increment( 1) );
                        // increment response dislike by 1
                        batch.update( responseDocRef, Constants.RESPONSE_DISLIKES , FieldValue.increment( -1 ) );

                        voteState = Constants.VOTE_STATE_LIKED;

                        // updating view
                        tvResponseLikes.setText( Integer.toString( prevLikes + 1) );
                        tvResponseDislikes.setText( Integer.toString( prevDislikes - 1) );
                    }
                }
                else if ( direction.equals( Constants.VOTE_DISLIKE ) ) {
                    if ( voteState.equals( Constants.VOTE_STATE_UNVOTED ) ) {
                        // create vote doc
                        DocumentReference generatedDocID = db.collection(Constants.FB_VOTES).document();
                        batch.set( generatedDocID, new Vote( response.getDocID(), auth.getCurrentUser().getUid(), direction ) );

                        // increase dislike counter by 1
                        DocumentReference responseDocRef = db.collection( Constants.FB_POSTS ).document( response.getDocID() );
                        batch.update( responseDocRef, Constants.RESPONSE_DISLIKES , FieldValue.increment( 1 ) );

                        // updating vote document
                        voteDocument = generatedDocID;
                        voteState = Constants.VOTE_STATE_DISLIKED;

                        // updating view
                        tvResponseDislikes.setText( Integer.toString( prevDislikes + 1) );

                    }
                    else if ( voteState.equals( Constants.VOTE_STATE_LIKED ) ) {
                        // update doc to be like instead of dislike
                        batch.update( voteDocument, Constants.VOTE_DIRECTION, Constants.VOTE_LIKE );

                        DocumentReference responseDocRef = db.collection( Constants.FB_POSTS ).document( response.getDocID() );
                        // decrement dislike
                        batch.update( responseDocRef, Constants.RESPONSE_DISLIKES , FieldValue.increment( 1 ) );
                        // increment like
                        batch.update( responseDocRef, Constants.RESPONSE_LIKES , FieldValue.increment( -1 ) );

                        voteState = Constants.VOTE_STATE_DISLIKED;

                        // updating view
                        tvResponseLikes.setText( Integer.toString( prevLikes - 1) );
                        tvResponseDislikes.setText( Integer.toString( prevDislikes + 1) );
                    }
                    else if ( voteState.equals( Constants.VOTE_STATE_DISLIKED ) ) {
                        // delete voteDoc
                        batch.delete( voteDocument );

                        // decrement dislike counter
                        DocumentReference responseDocRef = db.collection( Constants.FB_POSTS ).document( response.getDocID() );
                        batch.update( responseDocRef, Constants.RESPONSE_DISLIKES , FieldValue.increment( -1) );

                        // updating views vote document
                        voteDocument = null;
                        voteState = Constants.VOTE_STATE_UNVOTED;

                        // updating view
                        tvResponseDislikes.setText( Integer.toString( prevDislikes - 1) );
                    }
                }
                updateVoteIcons();
                batch.commit()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG, "Success, voteState is now: " + voteState);
                                middleOfBatchWrite = false;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                middleOfBatchWrite = false;
                                voteDocument = prevVoteDoc;
                                voteState = prevVoteState;
                                updateVoteIcons();
                                // updating view
                                tvResponseLikes.setText( Integer.toString( prevLikes ) );
                                tvResponseDislikes.setText( Integer.toString( prevDislikes ) );
                                Log.e(TAG, "failure in transaction", e);
                            }
                        });
            }

            // helper for handleVote which manages the UI changes related to voting
            private void updateVoteIcons() {
                switch (voteState){
                    case Constants.VOTE_STATE_DISLIKED:
                        iconLike.setImageResource(R.drawable.ic_thumbs_up);
                        iconDislike.setImageResource(R.drawable.ic_thumbs_down_filled);
                        break;
                    case Constants.VOTE_STATE_LIKED:
                        iconLike.setImageResource(R.drawable.ic_thumbs_up_filled);
                        iconDislike.setImageResource(R.drawable.ic_thumbs_down);
                        break;
                    case Constants.VOTE_STATE_UNVOTED:
                        iconLike.setImageResource(R.drawable.ic_thumbs_up);
                        iconDislike.setImageResource(R.drawable.ic_thumbs_down);
                        break;
                    default:
                        break;
                }
            }

            // get question object relating to response object
            private void getResponseQuestionAndFill(String questionRef) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection(Constants.FB_POSTS)
                        .document(questionRef)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Question question = document.toObject(Question.class);
                                    question.setDocID(document.getId());

                                    bindQuestion(question);
                                }
                            }
                        });
            }

            // Queries for user of post and passes user and post to binding function
            private void getUserAndFill(String authorRef, final Post post){
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection(Constants.FB_USERS)
                        .document(authorRef)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    User user = document.toObject(User.class);
                                    fillUserDependantInfo(user, post);
                                }
                            }
                        });
            }

            // binding function for information received from getUserAndFill
            private void fillUserDependantInfo(User user, final Post post){
                if (post instanceof Question){
                    questionUser = user;
                    tvQuestionUsername.setText(user.getUsername());
                    tvQuestionUsername.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, OthersProfileActivity.class);
                            Parcelable wrappedUser = Parcels.wrap(questionUser);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, (View) tvQuestionUsername, "username");
                            intent.putExtra("user", wrappedUser);
                            context.startActivity(intent, options.toBundle());
                        }
                    });
                }
                else{
                    responseUser = user;
                    tvResponseUsername.setText(user.getUsername());
                    tvResponseUsername.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, OthersProfileActivity.class);
                            Parcelable wrappedUser = Parcels.wrap(responseUser);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, (View) tvResponseUsername, "username");
                            intent.putExtra("user", wrappedUser);
                            context.startActivity(intent, options.toBundle());
                        }
                    });

                }
            }
        }
}
