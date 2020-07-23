package com.example.argumentree;

public class Constants {
    // For users document
    public static final String USER_EMAIL = "email";
    public static final String USER_USERNAME = "username";
    public static final String USER_PROFILE_PIC = "profilePic";
    public static final String USER_BIO = "bio";
    public static final String USER_AUTH_USER_ID = "authUserID";
    public static final String USER_LIKES = "likes";
    public static final String USER_CREATED_AT = "createdAt";

    // for generic Post
    public static final String POST_TYPE = "postType";
    public static final String POST_CREATED_AT = "createdAt";
    public static final String QUESTION = "question";
    public static final String RESPONSE = "response";
    public static final String AUTHOR_REF = "authorRef";

    // for QUESTION
    public static final String QUESTION_BODY = "body";
    public static final String QUESTION_TAGS = "tags";
    public static final String QUESTION_AUTHOR = AUTHOR_REF;
    public static final String QUESTION_MEDIA = "media";
    public static final String QUESTION_DESCENDANTS = "descendants";
    public static final String QUESTION_CREATED_AT = POST_CREATED_AT;

    // for RESPONSE
    public static final String RESPONSE_PARENT = "parent";
    public static final String RESPONSE_QUESTION_ROOT = "question_root";
    public static final String RESPONSE_DESCENDANTS = "descendants";
    public static final String RESPONSE_AGREEMENTS = "agreements";
    public static final String RESPONSE_DISAGREEMENTS = "disagreements";
    public static final String RESPONSE_TITLE = "title";
    public static final String RESPONSE_CLAIM = "claim";
    public static final String RESPONSE_SOURCE = "source";
    public static final String RESPONSE_SOURCE_QUESTION = "sourceQuestion";
    public static final String RESPONSE_CREATED_AT = POST_CREATED_AT;

    // Shared Preferences
    public static final String SP_CURRENT_USER = "com.example.argumentree.currentUser";
}
