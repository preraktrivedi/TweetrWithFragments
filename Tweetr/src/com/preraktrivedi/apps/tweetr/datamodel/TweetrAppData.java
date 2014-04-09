package com.preraktrivedi.apps.tweetr.datamodel;

public class TweetrAppData {

	private static TweetrAppData sInstance;
	private User authenticatedTwitterUser, currentViewedUser;
	private Tweet currentDetailedTweet;

	public static synchronized TweetrAppData getInstance() {
		if (null == sInstance) {
			sInstance = new TweetrAppData();
		}
		return sInstance;
	}

	public User getAuthenticatedTwitterUser() {
		return authenticatedTwitterUser;
	}

	public void setAuthenticatedTwitterUser(User authenticatedTwitterUser) {
		this.authenticatedTwitterUser = authenticatedTwitterUser;
	}

	public Tweet getCurrentDetailedTweet() {
		return currentDetailedTweet;
	}

	public void setCurrentDetailedTweet(Tweet currentDetailedTweet) {
		this.currentDetailedTweet = currentDetailedTweet;
	}

	public User getCurrentViewedUser() {
		return currentViewedUser;
	}

	public void setCurrentViewedUser(User currentViewedUser) {
		this.currentViewedUser = currentViewedUser;
	}
}
