import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

const ANY_POST : string = "posts/{postID}";

const POST_AUTHOR_REF = "authorRef";
const POST_QUESTION_REF = "questionRef";
const POST_RESPONSE_REF = "parentRef"

const POST_COLLECTION : string = "posts";
const USER_COLLECTION : string = "users";
const NOTIFICATION_COLLECTION : string = "notifications";

const NOTIFICATION_TYPE : string = "postReply";

type EventContext = functions.EventContext;
type QueryDocSnap = FirebaseFirestore.DocumentSnapshot<FirebaseFirestore.DocumentData>;
type DocSnap = FirebaseFirestore.DocumentSnapshot<FirebaseFirestore.DocumentData>;
type MsgResponse = admin.messaging.MessagingDevicesResponse;

admin.initializeApp();
const db = admin.firestore()

export const notifyPostRepliedTo = functions.firestore.document(ANY_POST).onCreate(async (snap : QueryDocSnap, context : EventContext) => {
    try {
        const authorRef : string = snap.get( POST_AUTHOR_REF );
        const parentRef : string = snap.get( POST_RESPONSE_REF );
        const questionRef : string = snap.get( POST_QUESTION_REF )
        
        // getting user and question info concurrently
        const parentPost : DocSnap = await db.collection( POST_COLLECTION ).doc( parentRef ).get();

        const notifiedUserRef = parentPost.get( POST_AUTHOR_REF ); 
        
        // getting notfied user object
        const notifiedUser : DocSnap = await db.collection( USER_COLLECTION ).doc( notifiedUserRef ).get();

        // make notification post
        db.collection( NOTIFICATION_COLLECTION ).add(
            {
                type : NOTIFICATION_TYPE,
                reply: snap.id,
                repliedTo : parentRef,
                questionRoot : questionRef,
                notifiedUser : notifiedUserRef,
                replyingUser : authorRef
            }
        );

        // sending out FCM message
        const instanceIDs : Array<String> | undefined = notifiedUser.get('firebaseInstanceIDs');
        let titleVar;
        if (parentPost.get('postType') === "response")
        { titleVar = parentPost.get('brief'); }
        else
        { titleVar = parentPost.get('body'); }

        const payload = {
            notification : {
                title : `Someone has replied to your post: ${titleVar}!`,
                body : "Tap here to check it out!"
            }
        }
        
        if (instanceIDs === undefined){
            console.log("there were no instanceIDs to notify on");
        }

        const promises : Array<Promise<MsgResponse>> = [];

        if (instanceIDs){
            instanceIDs.forEach(element => {
                promises.push( admin.messaging().sendToDevice(element.valueOf(), payload) )
            });
        }

        return Promise.all(promises);
    }
    catch(err){
        return new Promise(err);
    }
    

});