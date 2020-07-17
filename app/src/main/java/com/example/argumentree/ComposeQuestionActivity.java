package com.example.argumentree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.argumentree.models.Question;
import com.example.argumentree.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComposeQuestionActivity extends AppCompatActivity {
    public static final String TAG = "ComposeQuestionActivity";

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
        allChips = new ArrayList<>();

        // Pulling view references in
        etQuestionBody = findViewById(R.id.etQuestionBody);
        btnSubmitQuestion = findViewById(R.id.btnSubmitQuestion);
        chipGroup = findViewById(R.id.chipGroup);
        etAddChip = findViewById(R.id.etAddChip);
        ivAddChip = findViewById(R.id.ivAddChip);
        switchRelaxedMode = findViewById(R.id.switchRelaxedMode);

        // Filling views

        // Setting up listeners
        // Add chip
        ivAddChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Chip chip = new Chip(ComposeQuestionActivity.this);
                final String tagText = etAddChip.getText().toString();
                chip.setText(tagText);
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


        btnSubmitQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String body, List<String> tags, String authorRef, String mediaRef, int descendants, Date createdAt
                String body = etQuestionBody.getText().toString();
                // TODO: Implement chip functionality

                Date createdAt = new Date();


//                db.collection("users").document(username)
//                        .set(user)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d(TAG, "DocumentSnapshot successfully written!");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.e(TAG, "Error adding document", e);
//                            }
//                        });

                finish();
            }
        });

    }

}