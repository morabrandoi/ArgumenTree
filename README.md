# ArgumenTree

## Table of Contents
1. [Overview](##Overview)
2. [Product Spec](##Product-Spec)
3. [Wireframes](##Wireframes)
4. [Database](##Database)

## Overview
### Description
Regular app comment sections like twitter or youtube comments are not structured properly to have productive coversations. A dedicated platform where the conversation is more naturally structured to how conversations play out (a tree like structure).

### App Evaluation
- **Category:**
Social Media / Forum / Political
- **Mobile:**
Reddit-Like UI at least for the homepage. Would be able to browse through infinte list of topics with ability to sort bt category.
    - What makes your app more than a glorified website?
        - Will be easy to pick up and put down by loading directly into the listView screen and offer infinite scrolling. There will be the ability to share comments to other apps like facebook. It can also send push notifications and integrate with camera for associated media with posts.
- **Story:**
    - How clear is the value of this app to your audience?
        - Arguing/Debating online can be frustrating since the structure of comments on other websites is too linear to have meaningful conversations. There would also be a strong emphasis on sources as well as easy integration for allowing them.
    - How well would your friends or peers respond to this product idea?
        - They seemed to like the idea since it would at the very least be a big change in pace from the norm of linear conversations online.
- **Market:**
    - What's the size and scale of your potential user base?
        - Possibly the majority of people that use any form of social media since arguing and debating one's opinions is a primary way users engage.
    - Does this app provide huge value to a niche group of people?
        - I believe it does since those who share their opinions online in apps like Instagram or Tik Tok aren't even able to post clickable links.
    - Do you have a well-defined audience of people for this app?
        - Even a small percentage of people who frequently use comment sections to debate / argue would make a large audience for this application so I believe there is an audience for this kind of app.
- **Habit:**
    - How frequently would an average user open and use this app?
        - I foresee a significant portion of the traffic from this app coming from links in other parts of the internet that link to specific topics someone may be discussing. So about 1/2 as often as people go on other social media apps. A user might open this app 5 times a week if they frequent the internet comment sections.
    - Does an average user just consume your app or do they create?
        - I would think the average user creates 25% of the time and just watches 75% of the time since this app would most likely be used to settle debates or make points in a conversation happening elsewhere.
- **Scope:**
    - How technically challenging will it be to complete this app by the end of the program?
        - The biggest technical challenge would be to display the tree structure of the argument. Other than that, it would be a lot of moving between screens with intents.
    - Is a stripped-down version of this app still interesting to build?
        - Yes I believe it would be. The core functionality is quite simple and reddit-like (with the twist of a tree structure for conversation) and a large emphasis on sources.
    - How clearly defined is the product you want to build?
        - Well defined.
## Product Spec
### 1. User Stories (Required "R" and Optional "O")
#### **Visible Layout** ####
##### Home Timeline Tab #####
* R: Has navbar on the bottom
    * R: Has Profile, home_time line and search tabs
        * R: Clicking on profile tab leads to log in page when not logged in
* R: Has recyclerView list of globally popular nodes (questions or responses)
    * R: Each item in list has an interaction count (weighted sum of upvotes, downvotes, and comments)
    * O: Each item has an icon to launch into Graph view 
    * Each item displays the text of the question.
        * O: If the item is a child node of a tree, it shows the question node above it in twitter's current "retweet" fashion.
    * O: Questions are anoymous but replies to it are not.
    * R: Clicking on item leads to detailed view of the node.
##### Log in page #####
* R: Accepts logging in from Facebook
* R: Accepts logging in with Username and password
* R: On login, take the user directly to Home timeline
##### Detailed Node View #####
* O: Have back button in top left which takes you back to prev screen
* R: Show node in question. With context of question if its a response 
* R: Show responses to the response you clicked on
* R: Show Tree icon which jumps to tree view
* O: When scroll down all the way, half the screen is tree view with option for maximizing into normal tree view
* O: Can upvote or downvote post based on agree or disagree.
* R: All questions and responses have icon for graph view
* R: Each item has a reply button in bottom left of item
* R: NavBar is still at the bottom
##### Search Tab #####
* R: Has search bar at the top
* O: Can search with filters like by:
    * tag
    * category
    * interactions
    * time
    * etc.
* R: List of items (questions / responses) appear
* R: Can click on items to go into detail view
##### O: Notifications Tab #####
* O: Ability to view notifications
    * John smith commented, on "THING"
    * Post "Who is the coolest jonas brother" has 500 interactions!
* O: Navbar
##### Tree View #####
* R: Visual Tree structure of questions and responses only showing headers
* R-O: nodes are clickable
* O: close button on top left
##### Profile Tab #####
* R: Profile Pic, Username, and small Bio
* R: Shows Discussions they have contribtued to
    * Recycler View of items
* O: Can *like* profile
##### Compose Question #####
* R: Text box to type in question
* O: Dropdown menu to choose correct category for question
* O: Optional Tags you can add to the question
* O: Option for strict or relaxed mode
* R: Post button
* R: Cancel Button
* O: Save to drafts button
##### Compose Response #####
* R: Text box for title
* R: Text box for overview
* R: Text box for Claim 1
* R: Text Box for Source 1
* O: + button to add new claim and source
* O: Sample Output
* O: Save to draft button
* R: Post button
* R: Cancel button in top left
#### **Less visible Features** ####
##### Algorithms #####
* R: algorithm to traverse topic tree and generate summary of argument
* O: algorithm to search for items based on a text
* O: Algorithm to order home time_line and rank topics
* R-O (I think it would be okay to fake this or only show small tree sizes): Algorithm to dynamically load in argument tree in tree view
* O: Algorithm to assign credibilty score to sources
##### Functionality #####
* O: If responding to question with relaxed mode on, responses don't need to provide sources. // Still questioning whether relaxed mode is good idea.
* O: Way to detect for duplicate posts and warn person trying to post
* O: Way for posts to merge if they are very similar. Along with incenctive to merge (maybe add together upvotes).

### 2. Navigation

**Tab Navigation** (Tab to Screen)

* MyProfile tab
* Home timeline tab
* Search tab
* Notifications tab

**Flow Navigation** (Screen to Screen)

* Home Timeline
   * All navbar options
        * If clicked
   * Tree View
       * If clicked on tree icon of item
* Log-In Page
   * Home Timeline
       * on log in or on cancel
* My Profile Page
    * Node Detail view
        * If clicked on previous screen
        * Or clicked on one of the items person contributed to
    * All navbar options
        * If clicked
    * Tree view if click on item's Tree icon
    * Profile Page
* Detailed Node/Item View
    * Compose Response
        * If click reply on any item
    * Tree View
        * If clicked on tree icon
        * O: Or scroll far enough and click maximize
    * Profile Page
        * If click on responders profile picture and username
    * All Navbar Options
* Notifications Tab
    * Detail Node View
        * If Click on notification about node
    * All Navbar options
* Search Tab
    * Detail Node view
        * If click on node in list
    * Tree View
        * If click on tree icon of node
    * All Navbar Options
* Tree View
    * Detail Node View
        * If node clicked
    * Previous Page
        * If click in x in top left
    * All Navbar options
* Post Reponse
    * Previous page
        * On post or cancel
* Post Question
    * Previous page
        * On post or cancel

## Wireframes

![](https://i.imgur.com/Js2HsmU.jpg)
![](https://i.imgur.com/y2rM4h4.jpg)
![](https://i.imgur.com/rBzd2K6.jpg)

<!-- ### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype
-->
## Database

### Collections
- users
- questions
- responses

### Document Schemas
#### User
   | Property      | Type     | Description                                     |
   | ------------- | -------- | ----------------------------------------------- |
   | email         | String   | email user signed up with                       |
   | username      | String   | username of user                                |
   | profile_pic   | String   | reference to firestorage address of profile pic |
   | bio           | String   | short text a person can set for their profile   |
   | auth_user_id  | String   | The ID of the user in Firebase Auth             |
   | likes         | Number   | The number of likes that have been given to a profile|
   | createdAt     | Timestamp| The time the user was created                   | 
   
#### Post - Question variation
   | Property    | Type         | Description                                   |
   | ------------| ----------   | ----------------------------------------------|
   | postType    | String       | Always has value "question"                   |
   | body        | String       | body of the question, should end with a "?"   |
   | tags        | List\<String>| The categories the person attaches to their Q | 
   | authorRef   | ref\<User>   | question author                               |
   | mediaRef    | Reference    | reference to firestorage location for image   |
   | descendants | Number       | total number of nodes below it in the tree    |
   | createdAt   | Timestamp    | the time the post was created                 |
   | relaxed     | Boolean      | whether responses require sources or not      |
   

#### Post - Response Variation
   | Property       | Type           | Description                                |
   | -------------  | -------------- | ------------------------------------------ |
   | postType       | String         | Always has value "response"                |
   | parentRef      | ref\<Post>     | Response above.                            |
   | questionRef    | ref\<Post>     | the Head question this is answering        |
   | authorRef      | ref\<User>     | the reference to the person who posted     |
   | descendants    | Number         | total number of nodes below it in the tree |
   | agreements     | Number         | number of up Votes for the post            |
   | disagreements  | Number         | number of down Votes                       |
   | brief          | String         | short text summarizing claim               |
   | claim          | String         | longer piece of text stating the claim     |
   | source         | String         | reference to source document               |
   | sourceQRef     | ref\<Post>     | reference to automated question post asking if source is valid |
   | createdAt      | Timestamp      | time the post was created |
   
   
   