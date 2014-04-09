package com.preraktrivedi.apps.tweetr.activities;

import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.SwipeDismissAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;
import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.adapters.TweetrAdapter;
import com.preraktrivedi.apps.tweetr.application.TweetrApp;
import com.preraktrivedi.apps.tweetr.datamodel.Tweet;
import com.preraktrivedi.apps.tweetr.datamodel.TweetrAppData;
import com.preraktrivedi.apps.tweetr.restclient.TweetrJsonHttpResponseHandler;
import com.preraktrivedi.apps.tweetr.utils.EndlessScrollListener;
import com.preraktrivedi.apps.tweetr.utils.LayoutUtils;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

/**Deprecated from v1. Use TweetrLandingActivity instead**/
@Deprecated
public class TweetrMainActivity extends Activity  implements OnDismissCallback {

	public static final String TAG = TweetrMainActivity.class.getSimpleName();
	private ArrayList<Tweet>  tweets;
	private TweetrAdapter tweetrAdapter;
	private PullToRefreshListView lvTweets;
	private Context mContext;
	private RelativeLayout rlTweetContainer;
	private SwingRightInAnimationAdapter swingRightInAnimationAdapter;
	private TweetrAppData mAppData = TweetrAppData.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		intitUi();
	}

	private void intitUi() {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		LayoutUtils.showToast(mContext, "Signed in with user - " + mAppData.getAuthenticatedTwitterUser().getScreenName());
		setContentView(R.layout.activity_tweetr_main);
		styleActionBar();
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		rlTweetContainer = (RelativeLayout) findViewById(R.id.rl_tweet_container);
		initAdapter();
		setupListeners();
	}

	private void initAdapter() {
		tweets = new ArrayList<Tweet>();
		tweetrAdapter = new TweetrAdapter(mContext, tweets);
		swingRightInAnimationAdapter = new SwingRightInAnimationAdapter(new SwipeDismissAdapter(tweetrAdapter, (OnDismissCallback) mContext));
		swingRightInAnimationAdapter.setInitialDelayMillis(700);
		swingRightInAnimationAdapter.setAbsListView(lvTweets);
		swingRightInAnimationAdapter.setAnimationDurationMillis(400);
		lvTweets.setAdapter(swingRightInAnimationAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "> onResume menuloader");
		loadTweets();
	}

	private void loadTweets() {

		if (tweetrAdapter.getCount() > 0) {
			fetchTimelineAsync(-1, tweetrAdapter.getItem(0).getId() + 1);
		} else {
			showLoader(true);
			TweetrApp.getRestClient().getHomeTimeLineTweets(-1, -1,  new TweetrJsonHttpResponseHandler(mContext, TAG) {
				@Override
				public void onSuccess(JSONArray jsonTweets) {
					Log.d(TAG, ">onSuccess TimelineTweets " + jsonTweets.toString());
					showLoader(false);
					tweets = Tweet.fromJson(jsonTweets);
					tweetrAdapter.addAll(tweets);
					tweetrAdapter.notifyDataSetChanged();
					swingRightInAnimationAdapter.notifyDataSetChanged();
				}
				public void onFailure(Throwable e) {
					showLoader(false);
					Log.d(TAG, ">onFailure TimelineTweets: " + e.toString());
				}
			});
		}
	}

	private void styleActionBar() {
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#297ABA")));
		getActionBar().setLogo(R.drawable.ic_logo_tweetr_white);
		getActionBar().setTitle(Html.fromHtml(LayoutUtils.getUserTitleText(mContext, mAppData.getAuthenticatedTwitterUser().getScreenName())));
	}

	private void setupListeners() {

		rlTweetContainer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				composeNewTweet();
			}
		});

		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if (tweetrAdapter.getCount() > 0) {
					fetchTimelineAsync(tweetrAdapter.getItem(tweetrAdapter.getCount() - 1).getId() - 1, -1);
				}
			}
		});

		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				fetchTimelineAsync(-1, tweetrAdapter.getItem(0).getId() + 1);
				lvTweets.onRefreshComplete();
			}
		});



		lvTweets.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mAppData.setCurrentDetailedTweet(tweetrAdapter.getItem(position));
				Intent i = new Intent(mContext, DetailedTweetActivity.class);
				startActivity(i);
			}
		});


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "> onCreateOptionsMenu  menuloader");
		getMenuInflater().inflate(R.menu.tweetr_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "> onOptionsItemSelected  menuloader");
		switch (item.getItemId()) {
		case R.id.mi_compose_tweet:
			composeNewTweet();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showLoader(boolean show) {
		setProgressBarIndeterminateVisibility(show);
	}

	private void composeNewTweet() {
		Intent i = new Intent(this, ComposeTweetActivity.class);
		startActivity(i);
	}

	@Override
	public void onDismiss(AbsListView arg0, int[] reverseSortedPositions) {
		for (int position : reverseSortedPositions) {
			tweetrAdapter.remove(tweetrAdapter.getItem(position));
		}
		tweetrAdapter.notifyDataSetChanged();
		swingRightInAnimationAdapter.notifyDataSetChanged();
	}

	public void fetchTimelineAsync(final long maxId, final long sinceId) {
		Log.d(TAG, ">fetchTimelineAsync");
		showLoader(true);
		TweetrApp.getRestClient().getHomeTimeLineTweets(maxId, sinceId, new TweetrJsonHttpResponseHandler(mContext, TAG) {
			@Override
			public void onSuccess(JSONArray jsonTweets) {
				showLoader(false);
				Log.d(TAG, ">onSuccess TimelineTweets " + jsonTweets.toString());
				if(jsonTweets.length() > 0) {
					tweets = Tweet.fromJson(jsonTweets);
					if (maxId > 0) {
						tweetrAdapter.addAll(tweets);
					}
					if (sinceId > 0) {
						for (int i = 0; i < tweets.size(); i++) {
							tweetrAdapter.insert(tweets.get(i), i);
						}
					}
					tweetrAdapter.notifyDataSetChanged();
					swingRightInAnimationAdapter.notifyDataSetChanged();
				} else {
					LayoutUtils.showToast(mContext, "No new tweets");
				}
			}
		});
	}
}
