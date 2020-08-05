package com.example.argumentree.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.argumentree.Constants;
import com.example.argumentree.NotificationsAdapter;
import com.example.argumentree.R;
import com.example.argumentree.models.Notification;
import com.example.argumentree.models.Question;
import com.example.argumentree.models.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    public static final String TAG = "NotificationsFragment";

    private RecyclerView rvNotifications;
    private List<Notification> notifications;
    private NotificationsAdapter notificationsAdapter;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvNotifications = view.findViewById(R.id.rvNotifications);
        notifications = new ArrayList<Notification>();
        notificationsAdapter = new NotificationsAdapter(getContext(), notifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.setAdapter(notificationsAdapter);
        rvNotifications.addItemDecoration(new DividerItemDecoration(rvNotifications.getContext(), DividerItemDecoration.VERTICAL));

        // fill view with data
        fillNotificationsFromFirestiore();
    }

    private void fillNotificationsFromFirestiore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Log.i(TAG, "fillNotificationsFromFirestiore: User is not signed in, Returning");
            return;
        }

        Query query = db.collection(Constants.NOTIFICATIONS).whereEqualTo(Constants.NOTI_NOTIFIED_USER, auth.getUid());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Notification notification = document.toObject(Notification.class);
                        notifications.add(notification);
                    }
                    notificationsAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}