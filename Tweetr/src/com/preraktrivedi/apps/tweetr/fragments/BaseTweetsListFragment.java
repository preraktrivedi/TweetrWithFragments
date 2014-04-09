package com.preraktrivedi.apps.tweetr.fragments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;
import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.adapters.TweetrAdapter;
import com.preraktrivedi.apps.tweetr.datamodel.Tweet;

import eu.erikw.PullToRefreshListView;

public class BaseTweetsListFragment extends Fragment {

	private TweetrAdapter mTweetrAdapter;
	private LinkedHashSet<Long> mTweetsSanityChecker;
	protected SwingRightInAnimationAdapter swingRightInAnimationAdapter;
	protected PullToRefreshListView lvTweets;
	protected ArrayList<Tweet> mTweetsList;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragments_tweets_list, container,false);
		lvTweets = (PullToRefreshListView) v.findViewById(R.id.lvTweets);	
		initAdapter();
		return v;
	}

	private void initAdapter() {
		mTweetsList = new ArrayList<Tweet>();
		mTweetsSanityChecker = new LinkedHashSet<Long>();
		mTweetrAdapter = new TweetrAdapter(getActivity(), mTweetsList);
		swingRightInAnimationAdapter = new SwingRightInAnimationAdapter(mTweetrAdapter);
		swingRightInAnimationAdapter.setInitialDelayMillis(700);
		swingRightInAnimationAdapter.setAbsListView(lvTweets);
		swingRightInAnimationAdapter.setAnimationDurationMillis(400);
		lvTweets.setAdapter(swingRightInAnimationAdapter);
		mTweetrAdapter.clear();
	}

	public TweetrAdapter getTweetrAdapter() {
		return mTweetrAdapter;
	}

	public LinkedHashSet<Long> getTweetsSanityChecker() {
		return mTweetsSanityChecker;
	}


	public void handleProgressBarVisibility(boolean show) {
		getActivity().setProgressBarIndeterminateVisibility(show);
	}

	public Context getFragmentContext() {
		return mContext;
	}

	public void notifyDatasetChanged(boolean sanityCheck) {
		if(sanityCheck) {
			runSanityCheck();
		}
		mTweetrAdapter.notifyDataSetChanged();
		swingRightInAnimationAdapter.notifyDataSetChanged();
	}

	//Makes sure the tweets are only displayed once in adapter.
	private void runSanityCheck() {
		Log.d("DEBUG", "Input mTweetrAdapter count - " + mTweetrAdapter.getCount());
		LinkedHashSet<Tweet> lhsTweet = new LinkedHashSet<Tweet>();
		LinkedHashSet<Long> lhsId = new LinkedHashSet<Long>();
		for (int i = 0; i < mTweetrAdapter.getCount(); i++) {
			lhsTweet.add(mTweetrAdapter.getItem(i));
			lhsId.add(mTweetrAdapter.getItem(i).getId());
		}
		mTweetrAdapter.clear();
		mTweetrAdapter.notifyDataSetInvalidated();
		
		Iterator<Tweet> iterator = lhsTweet.iterator();
		
		while(iterator.hasNext()) {
			Tweet tweet = (Tweet) iterator.next();
			if(lhsId.contains(tweet.getId())) {
				mTweetrAdapter.add(tweet);
				lhsId.remove(tweet.getId());
			}
		}
		Log.d("DEBUG", "Output mTweetrAdapter count - " + mTweetrAdapter.getCount());
	}
}