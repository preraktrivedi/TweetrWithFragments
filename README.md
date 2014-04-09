Tweetr - The Twitter Client (v2)
=================

Tweetr is a simple twitter client where in you authenticate with your Twitter account (Tweetr does not save password) and view your timeline or mentions, reply to a tweet or post a new tweet.
You can even view your own list or any users' list by clicking on their display picture. You can also see the Following/Follower user list

###Features for the app:

* Simple login mechanism using the OAuth flow.
* Once authenticated, land on your user timeline and view list of 25 most recent tweets. 
* The tweets are loaded with an intuitive animation which makes the app more engrossing.
* Swipe right to view the tweets in which your username was Mentioned. This is under the "Mentions" tab.
* The tweets in timeline also contain embedded media images along with clickable links to go to the website.
* You can directly click on "reply", "favorite" or "retweet" options from the action bar on each tweet on the timeline.
* More tweets are automatically loaded once you reach the end of page (endless scrolling).
* Tweets can be refreshed by pulling down the list and releasing it (Pull to Refresh).
* Compose a new tweet by clicking on the compose button in Menu bar.
* Interactive character counter to make sure you don't exceed your character limit.
* You can click on the "gallery" or "camera" icon in the compose tweet screen to pick image from gallery or click a new image.
* Click on any tweet to view more details for the same including a larger version of the image.
* Directly reply to the author of the tweet from the tweet detail page, for your convenience the user screen name is pre-populated.
* You can use the action panel to perform "Reply", "Favorite", "Retweet" or "Share" actions on the tweet.

Sample APK is included in the sampleapk folder (Tweetr.apk).

###Screenshots:



![Screenshots](/sampleapk/01a1.png "Screenshot")
.
.
.
![Screenshots](/sampleapk/01a1.png "Screenshot")
.
.
.





The following libraries are used to make this possible:

 * [scribe-java](https://github.com/fernandezpablo85/scribe-java) - Simple OAuth library for handling the authentication flow.
 * [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
 * [codepath-oauth](https://github.com/thecodepath/android-oauth-handler) - Custom-built library for managing OAuth authentication and signing of requests
 * [UniversalImageLoader](https://github.com/nostra13/Android-Universal-Image-Loader) - Used for async image loading and caching them in memory and on disk.
 * [ListView Animations](https://github.com/nhaarman/ListViewAnimations) - Easily create ListViews with animations. 
 * [PullToRefresh Listview](https://github.com/erikwt/PullToRefresh-ListView) - A generic, customizable, open source Android ListView implementation that has 'Pull to Refresh' functionality.
 * [Navigation Drawer Design] (https://developer.android.com/design/patterns/navigation-drawer.html) - The navigation drawer is a panel that transitions in from the left edge of the screen and displays the app’s main navigation options.