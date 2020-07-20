package com.example.argumentree.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Question {
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