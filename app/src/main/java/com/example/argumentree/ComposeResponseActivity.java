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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.Date;

public class ComposeResponseActivity extends AppCompatActivity {

    public static final String TAG = "ComposeResponseActivity";

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

                db.collection(Constants.FB_POSTS)
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