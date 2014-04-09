Tweetr - The Twitter Client
=================

Tweetr is a simple twitter client where in you authenticate with your Twitter account (Tweetr does not save password) and view your timeline, reply to a tweet or post a new tweet.

###Features for the app:

* Simple login mechanism using the OAuth flow.
* Once authenticated, land on your user timeline and view list of 25 most recent tweets. 
* The tweets are loaded with an intuitive animation which makes the app more engrossing.
* Swipe left or right on a tweet to hide it temporarily
* The tweets in timeline also contain embedded media images along with clickable links to go to the website.
* More tweets are automatically loaded once you reach the end of page (endless scrolling).
* Compose a new tweet by clicking on the compose button in Menu bar or by clicking on "What's Happening" in the timeline.
* Interactive character counter to make sure you don't exceed your character limit.
* Click on any tweet to view more details for the same including a larger version of the image.
* Directly reply to the author of the tweet from the tweet detail page.

###Screenshots:

1) Login Screen:

![Screenshots](/sampleapk/1-Login.png "Screenshot Login")

2) Authenticate your twitter account to tweetr:

![Screenshots](/sampleapk/2-Authenticate.png "Screenshot authenticate")

3a) Tweetr Timeline screenshots:

![Screenshots](/sampleapk/3a-TweetTimelineImage.png "Screenshot Timeline")

3b) Tweetr Timeline screenshots:

![Screenshots](/sampleapk/3b-TweetTimeline.png "Screenshot Timeline")

3c) Tweetr Timeline - Swipe left or right on a tweet to hide it temporarily:

![Screenshots](/sampleapk/3c - SwipeToHide.png "Screenshot Swipe to hide")

3d) Pull down list to refresh tweets

![Screenshots](/sampleapk/3d-PullToRefresh.png "Screenshot Pull to refresh ")

4) Detailed tweet:

![Screenshots](/sampleapk/4-DetailTweet.png "Screenshot Detailed Tweet ")

5) Reply to author of the tweet:

![Screenshots](/sampleapk/5-ReplyToUser.png "Screenshot Reply To User")

6) Compose a new tweet: 

![Screenshots](/sampleapk/6-Compose.png "Screenshot Compose ")

7) Refresh timeline after posting tweet :

![Screenshots](/sampleapk/7-RefreshTimelineOnResume.png "Screenshot Refresh timeline after posting tweet")

Sample APK is included in the sampleapk folder (Tweetr.apk).


The following libraries are used to make this possible:

 * [scribe-java](https://github.com/fernandezpablo85/scribe-java) - Simple OAuth library for handling the authentication flow.
 * [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
 * [codepath-oauth](https://github.com/thecodepath/android-oauth-handler) - Custom-built library for managing OAuth authentication and signing of requests
 * [UniversalImageLoader](https://github.com/nostra13/Android-Universal-Image-Loader) - Used for async image loading and caching them in memory and on disk.
 * [ListView Animations](https://github.com/nhaarman/ListViewAnimations) - Easily create ListViews with animations. 
 * [PullToRefresh Listview](https://github.com/erikwt/PullToRefresh-ListView) - A generic, customizable, open source Android ListView implementation that has 'Pull to Refresh' functionality.
