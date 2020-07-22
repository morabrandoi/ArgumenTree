package com.example.argumentree.models;

import com.example.argumentree.Constants;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import org.parceler.Parcel;

import java.util.Date;
import java.util.List;

@Parcel
public class Question extends Post{
    // Extra field for local use
    @Exclude private String docID;

    // Fields in firestore document
    final private String postType = Constants.KEY_QUESTION_TYPE;
    private String body;
    private List<String> tags;
    private String authorRef;
    private String mediaRef;
    private int descendants;
    private boolean relaxed;
    private @ServerTimestamp Date createdAt;

    public Question() {

    }

    public Question(String body, List<String> tags, String authorRef, String mediaRef, int descendants, boolean relaxed, Date createdAt) {
        this.body = body;
        this.tags = tags;
        this.authorRef = authorRef;
        this.mediaRef = mediaRef;
        this.descendants = descendants;
        this.relaxed = relaxed;
        this.createdAt = createdAt;
    }

    public String getPostType() {
        return postType;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getAuthorRef() {
        return authorRef;
    }

    public void setAuthorRef(String authorRef) {
        this.authorRef = authorRef;
    }

    public String getMediaRef() {
        return mediaRef;
    }

    public void setMediaRef(String mediaRef) {
        this.mediaRef = mediaRef;
    }

    public int getDescendants() {
        return descendants;
    }

    public void setDescendants(int descendants) {
        this.descendants = descendants;
    }

    public boolean isRelaxed() {
        return relaxed;
    }

    public void setRelaxed(boolean relaxed) {
        this.relaxed = relaxed;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}