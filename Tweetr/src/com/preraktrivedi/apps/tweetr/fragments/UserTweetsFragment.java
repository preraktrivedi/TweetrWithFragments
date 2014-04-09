package com.preraktrivedi.apps.tweetr.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.util.Log;

import com.preraktrivedi.apps.tweetr.application.TweetrApp;
import com.preraktrivedi.apps.tweetr.datamodel.Tweet;
import com.preraktrivedi.apps.tweetr.restclient.TweetrJsonHttpResponseHandler;

public class UserTweetsFragment extends BaseTweetsListFragment {

	public static final String TAG = UserTweetsFragment.class.getSimpleName();

	public static UserTweetsFragment newInstance(String screenName) {

		UserTweetsFragment userTimeLineFragment = new UserTweetsFragment();
		Bundle args = new Bundle();
		args.putString("screen_name", screenName);
		userTimeLineFragment.setArguments(args);
		return userTimeLineFragment;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		handleProgressBarVisibility(true);
		TweetrApp.getRestClient().getUserTimeLine(getArguments().getString("screen_name"), new TweetrJsonHttpResponseHandler(getFragmentContext(), TAG) {
			@Override
			public void onSuccess(JSONArray jsonTweetsArray) {
				try {
					Log.d(TAG, ">onSuccess -  " + jsonTweetsArray.getJSONObject(0).toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweetsArray);
				getTweetrAdapter().addAll(tweets);
				handleProgressBarVisibility(false);
			}

			public void onFailure(Throwable e) {
				Log.d(TAG, "Timeline Error: " + e.toString());
				handleProgressBarVisibility(false);
			}
		});

	}

}