package com.example.argumentree.models;

import com.example.argumentree.Constants;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.type.Date;

public class User {
    public static User UserFromDocument(QueryDocumentSnapshot doc){
        User user = new User();
        String email = doc.get(Constants.KEY_USER_EMAIL).toString();
        String username = doc.get(Constants.KEY_USER_USERNAME).toString();
        String bio = doc.get(Constants.KEY_USER_BIO).toString();
        String authUserID = doc.get(Constants.KEY_USER_AUTH_USER_ID).toString();
        Date createdAt = (Date) doc.get(Constants.KEY_USER_CREATED_AT);
        Object profilePic = doc.get(Constants.KEY_USER_PROFILE_PIC);
        Object likes = doc.get(Constants.KEY_USER_LIKES);

        user.setEmail(email);
        user.setUsername(username);
        user.setBio(bio);
        user.setAuthUserID(authUserID);
        user.setCreatedAt(createdAt);
        user.setLikes((Integer) likes);

        if (profilePic != null){
            user.setProfilePic(profilePic.toString());
        }

        return user;
    }

    private String email;
    private String username;
    private String profilePic;
    private String bio;
    private String authUserID;
    private Date createdAt;
    private int likes;

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAuthUserID() {
        return authUserID;
    }

    public void setAuthUserID(String authUserID) {
        this.authUserID = authUserID;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
