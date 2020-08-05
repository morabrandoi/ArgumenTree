package com.example.argumentree.models;

public class Notification {
    private String notifiedUser;
    private String questionRoot;
    private String repliedTo;
    private String reply;
    private String replyUser;
    private String type;

    public Notification(){}

    public Notification(String notifiedUser, String questionRoot, String repliedTo, String reply, String replyUser, String type) {
        this.notifiedUser = notifiedUser;
        this.questionRoot = questionRoot;
        this.repliedTo = repliedTo;
        this.reply = reply;
        this.replyUser = replyUser;
        this.type = type;
    }

    public String getNotifiedUser() {
        return notifiedUser;
    }

    public void setNotifiedUser(String notifiedUser) {
        this.notifiedUser = notifiedUser;
    }

    public String getQuestionRoot() {
        return questionRoot;
    }

    public void setQuestionRoot(String questionRoot) {
        this.questionRoot = questionRoot;
    }

    public String getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(String repliedTo) {
        this.repliedTo = repliedTo;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(String replyUser) {
        this.replyUser = replyUser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
