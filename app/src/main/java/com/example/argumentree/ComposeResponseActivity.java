package com.example.argumentree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_response);

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
                response.setParentRef(null); // TODO: Pass question OR response object to this activity from previous page
                response.setQuestionRef(null); // TODO: pass question object to this
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
                                Log.i(TAG, "Successfully posted a question!");
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