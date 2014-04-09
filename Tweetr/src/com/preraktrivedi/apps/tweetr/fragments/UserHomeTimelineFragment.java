package com.preraktrivedi.apps.tweetr.fragments;

import org.json.JSONArray;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.preraktrivedi.apps.tweetr.activities.DetailedTweetActivity;
import com.preraktrivedi.apps.tweetr.adapters.TweetrAdapter;
import com.preraktrivedi.apps.tweetr.application.TweetrApp;
import com.preraktrivedi.apps.tweetr.datamodel.Tweet;
import com.preraktrivedi.apps.tweetr.datamodel.TweetrAppData;
import com.preraktrivedi.apps.tweetr.restclient.TweetrJsonHttpResponseHandler;
import com.preraktrivedi.apps.tweetr.utils.EndlessScrollListener;
import com.preraktrivedi.apps.tweetr.utils.LayoutUtils;

import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class UserHomeTimelineFragment extends BaseTweetsListFragment {
	
	public static final String TAG = UserHomeTimelineFragment.class.getSimpleName();
	private TweetrAppData mAppData;
	private TweetrAdapter mTweetrAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAppData = TweetrAppData.getInstance();
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mTweetrAdapter = getTweetrAdapter();
		
		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadMoreTweets(-1, mTweetrAdapter.getItem(0).getId() + 1);
				lvTweets.onRefreshComplete();
			}
		});

		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (mTweetrAdapter.getCount() > 0) {
					loadMoreTweets(mTweetrAdapter.getItem(mTweetrAdapter.getCount() - 1).getId() - 1, -1);
				}
			}
		});
		
		lvTweets.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mAppData.setCurrentDetailedTweet(mTweetrAdapter.getItem(position));
				Intent i = new Intent(getFragmentContext(), DetailedTweetActivity.class);
				startActivity(i);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		loadTweets();
	}
	
	
	private void loadTweets() {
		Log.d(TAG, ">loadTweets");
		if (mTweetrAdapter.getCount() > 0) {
			loadMoreTweets(-1, mTweetrAdapter.getItem(0).getId() + 1);
		} else {
			handleProgressBarVisibility(true);
			TweetrApp.getRestClient().getHomeTimeLineTweets(-1, -1,  new TweetrJsonHttpResponseHandler(getFragmentContext(), TAG) {
				@Override
				public void onSuccess(JSONArray jsonTweets) {
					Log.d(TAG, ">onSuccess TimelineTweets initial " + jsonTweets.toString());
					handleProgressBarVisibility(false);
					mTweetsList = Tweet.fromJson(jsonTweets);
					mTweetrAdapter.addAll(mTweetsList);
					notifyDatasetChanged(false);
				}
				public void onFailure(Throwable e) {
					handleProgressBarVisibility(false);
					Log.d(TAG, ">onFailure TimelineTweets: " + e.toString());
				}
			});
		}
	}


	public void loadMoreTweets(final long maxId, final long sinceId) {
		Log.d(TAG, ">loadMoreTweets");
		handleProgressBarVisibility(true);
		TweetrApp.getRestClient().getHomeTimeLineTweets(maxId, sinceId, new TweetrJsonHttpResponseHandler(getFragmentContext(), TAG) {
			@Override
			public void onSuccess(JSONArray jsonTweets) {
				handleProgressBarVisibility(false);
				Log.d(TAG, ">onSuccess TimelineTweets loadmore " + jsonTweets.toString());
				if(jsonTweets.length() > 0) {
					mTweetsList = Tweet.fromJson(jsonTweets);
					if (maxId > 0) {
						mTweetrAdapter.addAll(mTweetsList);
					}
					if (sinceId > 0) {
						for (int i = 0; i < mTweetsList.size(); i++) {
							mTweetrAdapter.insert(mTweetsList.get(i), i);
						}
					}
					notifyDatasetChanged(false);
				} else {
					LayoutUtils.showToast(getFragmentContext(), "No new tweets");
				}
			}
			
			public void onFailure(Throwable e) {
				handleProgressBarVisibility(false);
				Log.d(TAG, ">onFailure TimelineTweets: " + e.toString());
			}
		});
	}

}