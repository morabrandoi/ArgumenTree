package com.example.argumentree;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argumentree.models.Post;
import com.example.argumentree.models.Question;
import com.example.argumentree.models.Response;
import com.example.argumentree.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
            private TextView tvResponseInteractions;
            private TextView tvResponseUsername;
            private ImageView iconResponseReply;
            private ImageView iconResponseTreeView;
            private ImageView iconAgree;
            private ImageView iconDisagree;

            // Model
            User questionUser;
            User responseUser;

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
                tvResponseInteractions = itemView.findViewById(R.id.tvResponseInteractions);
                tvResponseUsername = itemView.findViewById(R.id.tvResponseUsername);
                iconResponseReply = itemView.findViewById(R.id.iconResponseReply);
                iconResponseTreeView = itemView.findViewById(R.id.iconResponseTreeView);
                iconAgree = itemView.findViewById(R.id.iconAgree);
                iconDisagree = itemView.findViewById(R.id.iconDisagree);


            }

            public void bindQuestion(final Question question) {
                // Fill username
                getUserAndFill(question.getAuthorRef(), question);

                // Fill initial
                tvQuestionBody.setText(question.getBody());
                tvQuestionDescendants.setText(Integer.toString(question.getDescendants()));

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

            // Necessary because binding the response requires a query for the parent
            public void bindResponse(final Response response) {
                // fill username
                getUserAndFill(response.getAuthorRef(), response);
                // Bind associated question and fill its information in
                getResponseQuestionAndFill(response.getQuestionRef());

                clResponse.setVisibility(View.VISIBLE);
                tvResponseBrief.setText(response.getBrief());
                tvResponseClaim.setText(response.getClaim());
                tvResponseSource.setText(response.getSource());
                tvResponseInteractions.setText(Integer.toString(response.getAgreements() + response.getDisagreements()));

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
