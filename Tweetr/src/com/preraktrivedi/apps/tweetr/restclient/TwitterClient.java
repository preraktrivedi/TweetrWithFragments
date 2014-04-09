package com.preraktrivedi.apps.tweetr.restclient;

import java.io.ByteArrayInputStream;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.preraktrivedi.apps.tweetr.utils.TweetrConstants;

public class TwitterClient extends OAuthBaseClient {

	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = TweetrConstants.TWEETR_REST_BASE_URL;
	public static final String REST_CONSUMER_KEY = TweetrConstants.TWEETR_REST_CONSUMER_KEY;
	public static final String REST_CONSUMER_SECRET = TweetrConstants.TWEETR_REST_CONSUMER_SECRET; 
	public static final String REST_CALLBACK_URL = TweetrConstants.TWEETR_REST_CALLBACK_URL;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY,REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getUserProfile(AsyncHttpResponseHandler handler) {
		String url = getApiUrl("account/verify_credentials.json");
		client.get(url, null, handler);
	}

	public void getSpecifiedUserProfile(String screenName, AsyncHttpResponseHandler handler) {
		String url = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		client.get(url, params, handler);
	}

	public void getUserTimeLine(String screenName ,AsyncHttpResponseHandler handler) {
		String url = getApiUrl("statuses/user_timeline.json");
		RequestParams params = null;
		if(!TextUtils.isEmpty(screenName)){
			params = new RequestParams();
			params.put("screen_name", screenName);
		}
		client.get(url, (params == null) ? null : params, handler);
	}

	public void getMentionsTimeline(long maxId, long sinceId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = getRequestParams(maxId, sinceId);
		if(params == null){
			return;
		}
		params.put("count", Integer.toString(25));
		client.get(apiUrl, params, handler);
	}

	public void getHomeTimeLineTweets(long maxId, long sinceId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = getRequestParams(maxId, sinceId);
		if(params == null){
			return;
		}
		params.put("count", Integer.toString(25));
		client.get(apiUrl, params, handler);
	}

	public void postTweet(String apiUrl, String tweetText, ByteArrayInputStream imageToTweet, String replyToUserTweet, AsyncHttpResponseHandler handler) {
		String url = getApiUrl("statuses/" + apiUrl + ".json");
		RequestParams params = new RequestParams();
		params.put("status", tweetText);
		if (imageToTweet != null) {
			params.put("media[]", imageToTweet);
		}
		if(!TextUtils.isEmpty(replyToUserTweet)) {
			params.put("in_reply_to_status_id", replyToUserTweet);
		}
		client.post(url, params, handler);
	}

	private RequestParams getRequestParams(long maxId, long sinceId) {
		RequestParams params = new RequestParams();
		if(maxId > 0 && sinceId >0){
			Log.e(this.getClass().getSimpleName() , "Both maxId and sinceId cannot coexist");
			return null;
		}
		if (maxId > 0) {
			params.put("max_id", Long.toString(maxId));
		} else if(sinceId > 0){
			params.put("since_id", Long.toString(sinceId));
		}
		return params;
	}

	public void handleFavoriteAction(boolean isFav, String tweetId, AsyncHttpResponseHandler handler){
		String apiUrl;
		apiUrl = isFav ? getApiUrl("favorites/destroy.json") : getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		getClient().post(apiUrl, params, handler);
	}

	public void postRetweet(String tweetId, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/retweet/" +tweetId+ ".json");
		getClient().post(apiUrl, null, handler);
	}

	public void postSearchTweet (String searchQuery, long max_id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("search/tweets.json");
		RequestParams params = new RequestParams();
		params.put("count", String.valueOf(25));
		if(!TextUtils.isEmpty(searchQuery)) {
			params.put("q", searchQuery);
		}
		if(max_id != 0){
			params.put("max_id", String.valueOf(max_id));
		}
		getClient().get(apiUrl, params, handler);
	}

	public void getUserFollowersFriendsList(String request, String screenName, long cursorId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl(request + "/list.json");
		RequestParams params = null;
		if(!TextUtils.isEmpty(screenName)){
			params = new RequestParams();
			params.put("screen_name", screenName);
		}
		if(cursorId > 0){
			params.put("cursor", Long.toString(cursorId));
		}
		params.put("count", Integer.toString(15));
		params.put("skip_status", "true");
		params.put("include_user_entities", "false");
		client.get(apiUrl, params, handler);
	}

}