package com.example.argumentree.models;

import com.google.firebase.firestore.ServerTimestamp;

import org.parceler.Parcel;

import java.util.Date;
import java.util.List;

@Parcel
public class User {

    private String authUserID;
    private String bio;
    private String email;
    private String phoneNumber;
    private String profilePic;
    private String providerType;
    private String username;
    private int likes;
    private List<String> firebaseInstanceIDs;
    private @ServerTimestamp Date createdAt;

    public User() {

    }

    public User(String authUserID, String bio, String email, String phoneNumber, String profilePic, String providerType, String username, int likes, List<String> firebaseInstanceIDs, Date createdAt) {
        this.authUserID = authUserID;
        this.bio = bio;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePic = profilePic;
        this.providerType = providerType;
        this.username = username;
        this.likes = likes;
        this.firebaseInstanceIDs = firebaseInstanceIDs;
        this.createdAt = createdAt;
    }

    public String getAuthUserID() {
        return authUserID;
    }

    public void setAuthUserID(String authUserID) {
        this.authUserID = authUserID;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
