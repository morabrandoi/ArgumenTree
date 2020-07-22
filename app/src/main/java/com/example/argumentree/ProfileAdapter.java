package com.example.argumentree;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.argumentree.models.Question;

import org.parceler.Parcels;

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
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
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
                    Parcelable questionWrapped = Parcels.wrap(question);
                    intent.putExtra("parentType", "question");
                    intent.putExtra("question", questionWrapped);
                    context.startActivity(intent);
                }
            });


            iconTreeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: Tree View Icon to tree view page listener
                    Toast.makeText(context, "Got to implement that tree view still", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }
}
