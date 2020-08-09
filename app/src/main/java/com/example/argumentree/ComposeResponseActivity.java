package com.example.argumentree;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.argumentree.models.Question;
import com.example.argumentree.models.Response;
import com.example.argumentree.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import org.parceler.Parcels;

import java.util.Date;

public class ComposeResponseActivity extends AppCompatActivity {
    public static final String TAG = "ComposeResponseActivity";
    public static final int BRIEF_LIMIT = 100;

    // View member variables
    private EditText etBrief;
    private EditText etClaim;
    private EditText etSource;
    private Button btnPostResponse;

    // Information about parent to soon-to-be-created child
    private String parentType; // Can either be "response" or "question"
    private String questionRef; // if parent is question then parentRef == questionRoot
    private String parentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_response);

        // pulling info about parent from intent
        Intent passedIn = getIntent();
        parentType = passedIn.getStringExtra(Constants.PARENT_TYPE);

        // pulling info from user from SharedPrefs
        final User user = SharedPrefHelper.getUser(ComposeResponseActivity.this);

        // pulling appropriate Response or Question object from intent
        if (parentType.equals(Constants.QUESTION)) {
            Question question = Parcels.unwrap(passedIn.getParcelableExtra(Constants.QUESTION));
            questionRef = question.getDocID();
            parentRef = question.getDocID();
        }
        else if (parentType.equals(Constants.RESPONSE)){
            Response response = Parcels.unwrap(passedIn.getParcelableExtra(Constants.RESPONSE));
            questionRef = response.getQuestionRef();
            parentRef = response.getDocID();
        }
        else{
            throw new RuntimeException("The question type passed in to ComposeResponseActivity is invalid");
        }

        // Pulling view references in
        etBrief = findViewById( R.id.etBrief );
        etClaim = findViewById( R.id.etClaim );
        etSource = findViewById( R.id.etSource );
        btnPostResponse = findViewById( R.id.btnPostResponse );

        // Setting up listeners
        btnPostResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateInputs()){
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                SharedPrefHelper.hasUserIn(ComposeResponseActivity.this);

                Response response = new Response();

                response.setDescendants( 0 );
                response.setLikes( 0 );
                response.setDislikes( 0 );
                response.setAuthorRef( user.getAuthUserID() );
                response.setParentRef( parentRef );
                response.setQuestionRef( questionRef );
                response.setBrief( etBrief.getText().toString() );
                response.setClaim( etClaim.getText().toString() );
                response.setSource( etSource.getText().toString() );
                response.setSourceQRef( null ); // TODO: implement auto question posting
                response.setCreatedAt( new Date() );

                WriteBatch batch = db.batch();

                // create new response object
                DocumentReference generatedDocID = db.collection(Constants.FB_POSTS).document();
                batch.set(generatedDocID, response);

                // update descendants on question
                DocumentReference questionDoc = db.collection( Constants.FB_POSTS ).document( questionRef );
                batch.update( questionDoc, Constants.QUESTION_DESCENDANTS, FieldValue.increment( 1 ));

                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully posted a response!");
                        Toast.makeText(ComposeResponseActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
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

    // Returns whether the inputs in the form are valid
    private boolean validateInputs() {
        boolean briefIsFilled = !etBrief.getText().toString().isEmpty();
        boolean claimIsFilled = !etClaim.getText().toString().isEmpty();
        boolean sourceIsFilled = !etSource.getText().toString().isEmpty();
        boolean sourceIsSingleWord = !etSource.getText().toString().trim().contains(" ");
        boolean briefWithinSize = etBrief.getText().toString().length() <= BRIEF_LIMIT;

        if (!briefIsFilled){
            etBrief.setError("This box is empty!");
        }

        if (!claimIsFilled){
            etClaim.setError("This box is empty!");
        }

        if (!sourceIsFilled){
            etSource.setError("This box is empty!");
        }

        if (!sourceIsSingleWord){
            etSource.setError("Invalid link!");
        }

        if (!briefWithinSize){
            etBrief.setError("Brief too long!");
        }

        return (briefIsFilled && claimIsFilled &&
                sourceIsFilled && sourceIsSingleWord &&
                briefWithinSize);
    }
}