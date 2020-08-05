package com.example.argumentree;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argumentree.models.Notification;
import com.example.argumentree.models.Post;
import com.example.argumentree.models.Response;
import com.example.argumentree.models.User;
import com.example.argumentree.models.Vote;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private static final String TAG = "NotificationsAdapter";

    private Context context;
    private List<Notification> notifications;

    public NotificationsAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void clear() {
        notifications.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Notification> notifications) {
        this.notifications.addAll(notifications);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // declaring UI variables for class
        private TextView tvNotification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNotification = itemView.findViewById(R.id.tvNotification);
        }

        public void bind(final Notification notification)
        {
            queryNotificationInfoAndUpdate(notification);
        }

        private void queryNotificationInfoAndUpdate(Notification notification) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference notifiedUser = db.collection(Constants.FB_POSTS).document(notification.getRepliedTo());

            notifiedUser.get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot docSnap) {
                    String msg;
                    if ( docSnap.get( Constants.POST_TYPE ).equals(Constants.RESPONSE) ){
                        msg = "Someone replied to you on: " + docSnap.get(Constants.RESPONSE_BRIEF);
                    }
                    else{
                        msg = "Someone replied to you on: " + docSnap.get(Constants.QUESTION_BODY);
                    }

                    tvNotification.setText(msg);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: didnt update notifications", e);
                }
            });
        }
    }
}
