package com.example.argumentree;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.argumentree.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    public static final String TAG = "EditProfileActivity";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1000;

    private LinearLayout llChangeProfilePic;
    private EditText etChangeBio;
    private EditText etChangeUsername;
    private ImageView ivChangeProfilePic;
    private Button btnCancelChanges;
    private Button btnSaveChanges;

    private User currentUser;
    private Bitmap currentBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        currentUser = SharedPrefHelper.getUser(this);

        llChangeProfilePic = findViewById(R.id.llChangeProfilePic);
        etChangeBio = findViewById(R.id.etChangeBio);
        etChangeUsername = findViewById(R.id.etChangeUsername);
        ivChangeProfilePic = findViewById(R.id.ivChangeProfilePic);
        btnCancelChanges = findViewById(R.id.btnCancelChanges);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // set view contents
        etChangeBio.setText(currentUser.getBio());
        etChangeUsername.setText(currentUser.getUsername());
        if (currentUser.getProfilePic() != null) {
            Glide.with(this).load(currentUser.getProfilePic()).circleCrop().into(ivChangeProfilePic);
        }


        // Launch Camera action for result
        llChangeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });

        btnCancelChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUser.setBio(etChangeBio.getText().toString());
                currentUser.setUsername(etChangeUsername.getText().toString());
                if (currentBitmap != null){
                    uploadToFirestorage(currentBitmap);
                }
                else{
                    SharedPrefHelper.putUserIn(getApplicationContext(), currentUser);
                    syncChangesToFirestore(currentUser);
                    setResult(Activity.RESULT_OK);
                    finish();
                }

            }
        });


    }

    private void uploadToFirestorage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child(Constants.PROFILE_IMAGES).child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                currentUser.setProfilePic(uri.toString());

                                SharedPrefHelper.putUserIn(getApplicationContext(), currentUser);
                                syncChangesToFirestore(currentUser);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "failure in storage upload: ", e.getCause());
                    }
                });
    }

    private void syncChangesToFirestore(User currentUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> updates = new HashMap<>();
        updates.put(Constants.USER_BIO, currentUser.getBio());
        updates.put(Constants.USER_USERNAME, currentUser.getUsername());
        updates.put(Constants.USER_PROFILE_PIC, currentUser.getProfilePic());

        db.collection(Constants.FB_USERS)
                .document(user.getUid())
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "onSuccess: Update of User info success");
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onSuccess: Update of User info failed", e);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If return was successful and okay
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            currentBitmap = bitmap;
            Glide.with(this).load(bitmap).circleCrop().into(ivChangeProfilePic);
        }
    }


}