package com.example.argumentree;

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

                Log.i(TAG, "Response being binded" + response.getClaim());

                holder.bindResponse(response);
            } else {
                Question question = (Question) post;

                Log.i(TAG, "Question being binded " + question.getBody());
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
            private TextView tvQuestionBody;
            private TextView tvQuestionDescendants;
            private ImageView iconQuestionReply;
            private ImageView iconQuestionTreeView;

            // UI elements for response portion of UI
            private ConstraintLayout clResponse;
            private TextView tvResponseBrief;
            private TextView tvResponseClaim;
            private TextView tvResponseSource;
            private TextView tvResponseInteractions;
            private ImageView iconResponseReply;
            private ImageView iconResponseTreeView;
            private ImageView iconAgree;
            private ImageView iconDisagree;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // Instantiating references to previously declared UI elements
                // Pull in references to question portion of layout
                tvQuestionBody = itemView.findViewById(R.id.tvQuestionBody);
                tvQuestionDescendants = itemView.findViewById(R.id.tvQuestionDescendants);
                iconQuestionReply = itemView.findViewById(R.id.iconQuestionReply);
                iconQuestionTreeView = itemView.findViewById(R.id.iconQuestionTreeView);

                // Pull in references to response portion of layout
                clResponse = itemView.findViewById(R.id.clResponse);
                tvResponseBrief = itemView.findViewById(R.id.tvResponseBrief);
                tvResponseClaim = itemView.findViewById(R.id.tvResponseClaim);
                tvResponseSource = itemView.findViewById(R.id.tvResponseSource);
                tvResponseInteractions = itemView.findViewById(R.id.tvResponseInteractions);
                iconResponseReply = itemView.findViewById(R.id.iconResponseReply);
                iconResponseTreeView = itemView.findViewById(R.id.iconResponseTreeView);
                iconAgree = itemView.findViewById(R.id.iconAgree);
                iconDisagree = itemView.findViewById(R.id.iconDisagree);
            }

            public void bindQuestion(final Question question) {
                tvQuestionBody.setText(question.getBody());
                tvQuestionDescendants.setText(Integer.toString(question.getDescendants()));

                iconQuestionReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ComposeResponseActivity.class);
                        intent.putExtra("parentType", Constants.KEY_QUESTION_TYPE);
                        Parcelable wrappedQuestion = Parcels.wrap(question);
                        intent.putExtra(Constants.KEY_QUESTION_TYPE, wrappedQuestion);
                        context.startActivity(intent);
                    }
                });

                iconQuestionTreeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: Fill functionality
                        Toast.makeText(context, "Got to implement that tree view still", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            // Necessary because binding the response requires a query for the parent
            public void bindResponse(final Response response) {
                // Bind associated question object and fill its information in
                getResponseQuestionAndFill(response.getQuestionRef());

                clResponse.setVisibility(View.VISIBLE);
                tvResponseBrief.setText(response.getBrief());
                tvResponseClaim.setText(response.getClaim());
                tvResponseSource.setText(response.getSource());
                tvResponseInteractions.setText(Integer.toString(response.getAgreements() + response.getDisagreements()));

                iconResponseReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ComposeResponseActivity.class);
                        intent.putExtra("parentType", Constants.KEY_RESPONSE_TYPE);
                        Parcelable wrappedResponse = Parcels.wrap(response);
                        intent.putExtra(Constants.KEY_RESPONSE_TYPE, wrappedResponse);
                        context.startActivity(intent);
                    }
                });
            }

            private void getResponseQuestionAndFill(String questionRef) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("posts")
                        .document(questionRef)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Question question = document.toObject(Question.class);

                                    bindQuestion(question);

                                }
                            }
                        });
            }
        }

}
