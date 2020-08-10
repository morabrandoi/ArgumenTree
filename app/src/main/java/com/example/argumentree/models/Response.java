package com.example.argumentree.models;

import com.example.argumentree.Constants;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class Response extends Post{
    // for local client use
    @Exclude private String docID;

    // Fields which match the Firestore object
    final private String postType = Constants.RESPONSE;
    private int descendants;
    private int likes;
    private int dislikes;
    private boolean relaxed;
    private String authorRef;
    private String parentRef;
    private String questionRef;
    private String brief;
    private String claim;
    private String source;
    private String sourceQRef;

    private @ServerTimestamp Date createdAt;

    public Response(){
    }

    public Response(int descendants, int likes, int dislikes, boolean relaxed, String authorRef, String parentRef, String questionRef, String brief, String claim, String source, String sourceQRef, Date createdAt) {
        this.descendants = descendants;
        this.likes = likes;
        this.dislikes = dislikes;
        this.relaxed = relaxed;
        this.authorRef = authorRef;
        this.parentRef = parentRef;
        this.questionRef = questionRef;
        this.brief = brief;
        this.claim = claim;
        this.source = source;
        this.sourceQRef = sourceQRef;
        this.createdAt = createdAt;

    }

    public String getAuthorRef() {
        return authorRef;
    }

    public void setAuthorRef(String authorRef) {
        this.authorRef = authorRef;
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

    public int getDescendants() {
        return descendants;
    }

    public void setDescendants(int descendants) {
        this.descendants = descendants;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public boolean isRelaxed() {
        return relaxed;
    }

    public void setRelaxed(boolean relaxed) {
        this.relaxed = relaxed;
    }

    public String getParentRef() {
        return parentRef;
    }

    public void setParentRef(String parentRef) {
        this.parentRef = parentRef;
    }

    public String getQuestionRef() {
        return questionRef;
    }

    public void setQuestionRef(String questionRef) {
        this.questionRef = questionRef;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getClaim() {
        return claim;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceQRef() {
        return sourceQRef;
    }

    public void setSourceQRef(String sourceQRef) {
        this.sourceQRef = sourceQRef;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
