package com.preraktrivedi.apps.tweetr.application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.preraktrivedi.apps.tweetr.restclient.TwitterClient;

import android.content.Context;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 * RestClient client = RestClientApp.getRestClient();
 * // use client to send requests to API
 *
 */
public class TweetrApp extends com.activeandroid.app.Application {
	
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		TweetrApp.context = this;

		// Create global configuration and initialize ImageLoader with this configuration
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
				cacheInMemory().cacheOnDisc().build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.defaultDisplayImageOptions(defaultOptions)
		.build();
		ImageLoader.getInstance().init(config);
	}

	public static TwitterClient getRestClient() {
		return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, TweetrApp.context);
	}
}