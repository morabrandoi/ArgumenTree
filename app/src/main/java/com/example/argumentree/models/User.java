package com.example.argumentree.models;

import com.google.firebase.firestore.QueryDocumentSnapshot;

public class User {
    public static User UserFromDocument(QueryDocumentSnapshot doc){
        User user = new User();
        user.email = doc.get("email").toString();
        user.username = doc.get("username").toString();
        user.bio =  doc.get("bio").toString();
        user.auth_user_id = doc.get("auth_user_id").toString();

        if (doc.get("profile_picture") != null){
            user.profile_pic = doc.get("profile_picture").toString();
        }

        return user;
    }

    public String email;
    public String username;
    public String profile_pic;
    public String bio;
    public String auth_user_id;

    public User() {

    }
}
