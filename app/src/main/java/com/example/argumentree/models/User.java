package com.example.argumentree.models;

import com.google.firebase.firestore.ServerTimestamp;

import org.parceler.Parcel;

import java.util.Date;
import java.util.List;

@Parcel
public class User {

    private String email;
    private String username;
    private String profilePic;
    private String bio;
    private String authUserID;
    private @ServerTimestamp Date createdAt;
    private int likes;
    private List<String> firebaseInstanceIDs;

    public User() {

    }

    public User(String email, String username, String profilePic, String bio, String authUserID, Date createdAt, int likes, List<String> firebaseInstanceIDs) {
        this.email = email;
        this.username = username;
        this.profilePic = profilePic;
        this.bio = bio;
        this.authUserID = authUserID;
        this.createdAt = createdAt;
        this.likes = likes;
        this.firebaseInstanceIDs = firebaseInstanceIDs;
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

    public List<String> getFirebaseInstanceIDs() {
        return firebaseInstanceIDs;
    }

    public void setFirebaseInstanceIDs(List<String> firebaseInstanceIDs) {
        this.firebaseInstanceIDs = firebaseInstanceIDs;
    }
}
