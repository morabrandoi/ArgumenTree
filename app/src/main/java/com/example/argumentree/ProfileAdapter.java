package com.example.argumentree;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.argumentree.models.Question;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private static final String TAG = "ProfileAdapter";
    private Context context;
    private List<Question> ownQuestions;

    public ProfileAdapter(Context context, List<Question> ownQuestions) {
        this.context = context;
        this.ownQuestions = ownQuestions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = ownQuestions.get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return ownQuestions.size();
    }

    public void clear() {
        ownQuestions.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Question> ownQuestions) {
        this.ownQuestions.addAll(ownQuestions);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        // Declare what view elements I am pulling in from item
        private TextView tvQuestionBody;
        private TextView tvInteractionCount;
        private ImageView iconReply;
        private ImageView iconTreeView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Pull in item reference to view elements
            tvQuestionBody = itemView.findViewById(R.id.tvQuestionBody);
            tvInteractionCount = itemView.findViewById(R.id.tvInteractionCount);
            iconReply = itemView.findViewById(R.id.iconReply);
            iconTreeView = itemView.findViewById(R.id.iconTreeView);
        }

        public void bind(final Question question) {
            // Set UI elements
            tvQuestionBody.setText(question.getBody());

            // Set up listeners
            iconReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ComposeResponseActivity.class);
                    context.startActivity(intent);
                }
            });

            // TODO: Tree View Icon to tree view page listener

        }
    }
}
