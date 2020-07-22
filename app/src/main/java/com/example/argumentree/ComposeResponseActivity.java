package com.example.argumentree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.argumentree.fragments.SharedPrefHelper;
import com.example.argumentree.models.Question;
import com.example.argumentree.models.Response;
import com.example.argumentree.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComposeResponseActivity extends AppCompatActivity {

    public static final String TAG = "ComposeResponseActivity";

    // View member variables
    private TextView tvBrief;
    private EditText etBrief;
    private TextView tvClaim;
    private EditText etClaim;
    private TextView tvSource;
    private EditText etSource;
    private Button btnPostResponse;

    // Information about parent to soon-to-be-created child
    private String parentType; // Can either be "response" or "question"
    private String questionRef; // if parent is question then parentType == questionRoot
    private String parentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_response);

        // pulling info about parent from intent
        Intent passedIn = getIntent();
        parentType = passedIn.getStringExtra("parentType");

        if (parentType.equals("question")) {
            // pull question object out of extras

            Question question = Parcels.unwrap(passedIn.getParcelableExtra("question"));
            questionRef = question.getDocID();
            parentRef = question.getDocID();
        }
        else if (parentType.equals("response")){
            Response response = Parcels.unwrap(passedIn.getParcelableExtra("response"));
            questionRef = response.getQuestionRef();
            parentRef = response.getDocID();
        }
        else{
            throw new RuntimeException("The question type passed in to ComposeResponseActivity is invalid");
        }

        // Pulling view references in
        tvBrief = findViewById(R.id.tvBrief);
        etBrief = findViewById(R.id.etBrief);
        tvClaim = findViewById(R.id.tvClaim);
        etClaim = findViewById(R.id.etClaim);
        tvSource = findViewById(R.id.tvSource);
        etSource = findViewById(R.id.etSource);
        btnPostResponse = findViewById(R.id.btnPostResponse);

        // Setting up listeners
        btnPostResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                SharedPrefHelper.hasUserIn(ComposeResponseActivity.this);
                User user = SharedPrefHelper.getUser(ComposeResponseActivity.this);

                Response response = new Response();

                response.setDescendants(0);
                response.setAgreements(0);
                response.setDisagreements(0);
                response.setParentRef(parentRef);
                response.setQuestionRef(questionRef);
                response.setBrief( etBrief.getText().toString() );
                response.setClaim( etClaim.getText().toString() );
                response.setSource( etClaim.getText().toString() );
                response.setSourceQRef(null);// TODO: implement auto question posting
                response.setCreatedAt(new Date());

                db.collection("responses")
                        .add(response)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i(TAG, "Successfully posted a response!");
                                Toast.makeText(ComposeResponseActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error adding document", e);
                                Toast.makeText(ComposeResponseActivity.this, "Something went wrong posting question", Toast.LENGTH_SHORT).show();
                            }
                        });
                finish();
            }
        });
    }
}