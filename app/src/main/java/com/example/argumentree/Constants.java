package com.example.argumentree;

public class Constants {
    // For users document
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_USERNAME = "username";
    public static final String KEY_USER_PROFILE_PIC = "profilePic";
    public static final String KEY_USER_BIO = "bio";
    public static final String KEY_USER_AUTH_USER_ID = "authUserID";
    public static final String KEY_USER_LIKES = "likes";
    public static final String KEY_USER_CREATED_AT = "createdAt";

    // for generic Post
    public static final String KEY_POST_TYPE = "postType";
    public static final String KEY_POST_CREATED_AT = "createdAt";
    public static final String KEY_QUESTION_TYPE = "question";
    public static final String KEY_RESPONSE_TYPE = "response";

    // for QUESTION
    public static final String KEY_QUESTION_BODY = "body";
    public static final String KEY_QUESTION_TAGS = "tags";
    public static final String KEY_QUESTION_AUTHOR = "authorRef";
    public static final String KEY_QUESTION_MEDIA = "media";
    public static final String KEY_QUESTION_DESCENDANTS = "descendants";
    public static final String KEY_QUESTION_CREATED_AT = KEY_POST_CREATED_AT;

    // for RESPONSE
    public static final String KEY_RESPONSE_PARENT = "parent";
    public static final String KEY_RESPONSE_QUESTION_ROOT = "question_root";
    public static final String KEY_RESPONSE_DESCENDANTS = "descendants";
    public static final String KEY_RESPONSE_AGREEMENTS = "agreements";
    public static final String KEY_RESPONSE_DISAGREEMENTS = "disagreements";
    public static final String KEY_RESPONSE_TITLE = "title";
    public static final String KEY_RESPONSE_CLAIM = "claim";
    public static final String KEY_RESPONSE_SOURCE = "source";
    public static final String KEY_RESPONSE_SOURCE_QUESTION = "sourceQuestion";
    public static final String KEY_RESPONSE_CREATED_AT = KEY_POST_CREATED_AT;

    // Shared Preferences
    public static final String KEY_SP_CURRENT_USER = "com.example.argumentree.currentUser";
}
