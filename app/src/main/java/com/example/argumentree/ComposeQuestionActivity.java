package com.example.argumentree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.argumentree.models.Question;
import com.example.argumentree.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// TODO: figure out animations
public class ComposeQuestionActivity extends AppCompatActivity {
    public static final String TAG = "ComposeQuestionActivity";
    public static final int QUESTION_LIMIT = 150;

    // View member variables
    private EditText etQuestionBody;
    private ChipGroup chipGroup;
    private EditText etAddChip;
    private ImageView ivAddChip;
    private Switch switchRelaxedMode;
    private Button btnSubmitQuestion;

    // Model member variables
    private List<String> allChips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_question);

        // set screen transition
        Transition enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide_up);
        Transition exitTransition = TransitionInflater.from(this).inflateTransition(R.transition.slide_down);
        getWindow().setEnterTransition(enterTransition);
        getWindow().setExitTransition(exitTransition);

        allChips = new ArrayList<>();

        // Pulling view references in
        etQuestionBody = findViewById(R.id.etQuestionBody);
        btnSubmitQuestion = findViewById(R.id.btnSubmitQuestion);
        chipGroup = findViewById(R.id.chipGroup);
        etAddChip = findViewById(R.id.etAddChip);
        ivAddChip = findViewById(R.id.ivAddChip);
        switchRelaxedMode = findViewById(R.id.switchRelaxedMode);

        // Add chip click listener
        ivAddChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Chip chip = new Chip(ComposeQuestionActivity.this);
                final String tagText = etAddChip.getText().toString().toLowerCase();
                allChips.add(tagText);
                chip.setText(tagText);
                etAddChip.setText(null);
                chip.setCloseIconVisible(true);

                //Adding click listener on close icon to remove tag from ChipGroup
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        allChips.remove(tagText);
                        chipGroup.removeView(chip);
                    }
                });

                chipGroup.addView(chip);
            }
        });

        // Submit Question click listener
        btnSubmitQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateInputs()){
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                SharedPrefHelper.hasUserIn(ComposeQuestionActivity.this);
                User user = SharedPrefHelper.getUser(ComposeQuestionActivity.this);

                String body = etQuestionBody.getText().toString();
                List<String> tags = allChips;
                String authorRef = user.getAuthUserID();
                String mediaRef = null; // TODO: STRETCH: Add media to posts
                int descendants = 0;
                boolean relaxed = switchRelaxedMode.isChecked();
                Date createdAt = new Date();

                Question question = new Question(body, tags, authorRef, mediaRef, descendants, relaxed, createdAt);

                db.collection(Constants.FB_POSTS)
                        .add(question)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i(TAG, "Successfully posted a question!");
                                Toast.makeText(ComposeQuestionActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error adding document", e);
                                Toast.makeText(ComposeQuestionActivity.this, "Something went wrong posting question", Toast.LENGTH_SHORT).show();
                            }
                        });
                finish();
            }
        });
    }

    private boolean validateInputs() {
        boolean questionIsFilled = !etQuestionBody.getText().toString().isEmpty();
        boolean questionWithinLimit = etQuestionBody.getText().toString().length() <= QUESTION_LIMIT;

        if (!questionIsFilled){
            etQuestionBody.setError("There is nothing in the question box!");
        }

        if (!questionWithinLimit){
            etQuestionBody.setError("This question is too long!");
        }

        return questionIsFilled && questionWithinLimit;
    }
}