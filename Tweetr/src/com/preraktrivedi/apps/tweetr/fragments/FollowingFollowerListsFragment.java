package com.preraktrivedi.apps.tweetr.fragments;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.activities.GenericFragmentActivity;
import com.preraktrivedi.apps.tweetr.adapters.FollowingFollowerListAdapter;
import com.preraktrivedi.apps.tweetr.application.TweetrApp;
import com.preraktrivedi.apps.tweetr.datamodel.FollowersList;
import com.preraktrivedi.apps.tweetr.datamodel.FollowingList;
import com.preraktrivedi.apps.tweetr.datamodel.User;
import com.preraktrivedi.apps.tweetr.restclient.TweetrJsonHttpResponseHandler;
import com.preraktrivedi.apps.tweetr.utils.EndlessScrollListener;
import com.preraktrivedi.apps.tweetr.utils.LayoutUtils;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class FollowingFollowerListsFragment extends Fragment {

	private static final String TAG = FollowingFollowerListsFragment.class.getSimpleName();
	private PullToRefreshListView lvFollowingFollowers;
	private Context mContext;
	private FollowingFollowerListAdapter mFollowerFollowingAdapter;
	private String mScreenName;
	private String mPurpose, mRequestUrl;
	private FollowersList mFollowersList;
	private FollowingList mFollowingList;
	private long mCursorId = 0;

	public static FollowingFollowerListsFragment newInstance(String screenName, String purpose) {
		FollowingFollowerListsFragment followingFollowerListsFragment = new FollowingFollowerListsFragment();
		Bundle args = new Bundle();
		args.putString(GenericFragmentActivity.USER_SCREEN_NAME, screenName);
		args.putString(GenericFragmentActivity.PURPOSE_GENERIC_FRAGMENT, purpose);
		followingFollowerListsFragment.setArguments(args);
		return followingFollowerListsFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mContext = getActivity();
		mScreenName = getArguments().getString(GenericFragmentActivity.USER_SCREEN_NAME);
		mPurpose = getArguments().getString(GenericFragmentActivity.PURPOSE_GENERIC_FRAGMENT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragments_following_follower_list, container, false);
		lvFollowingFollowers = (PullToRefreshListView) v.findViewById(R.id.lvFollowingFollower);
		initAdapter();
		initListeners();
		startApiCallBasedOnPurpose(1);
		return v;
	}

	
	private void initAdapter(){
		ArrayList<User> genericUserList = new ArrayList<User>();
		mFollowerFollowingAdapter = new FollowingFollowerListAdapter(mContext, genericUserList);
		lvFollowingFollowers.setAdapter(mFollowerFollowingAdapter);
	}

	private void startApiCallBasedOnPurpose(final int page) {
		if(mPurpose.equals(GenericFragmentActivity.PURPOSE_FOLLOWING)) {
			mRequestUrl = "friends";
			fetchUserFollowing(page);
		} else if(mPurpose.equals(GenericFragmentActivity.PURPOSE_FOLLOWER)){
			mRequestUrl = "followers";
			fetchUserFollowers(page);
		}
	}

	private void fetchUserFollowing(final int page){
		handleProgressBarVisibility(true);
		if(page <= 1){
			mFollowingList = new FollowingList();
			resetAdapter();
		}
		TweetrApp.getRestClient().getUserFollowersFriendsList(mRequestUrl, mScreenName, mCursorId, new TweetrJsonHttpResponseHandler(mContext, TAG) {
			@Override
			public void onSuccess(int code, JSONObject body) {
				Log.d(TAG, ">onSuccess - " + body.toString());
				mFollowingList = FollowingList.fromJson(body);
				mCursorId = mFollowingList.getNextCursor();
				ArrayList<User> followingUserList = mFollowingList.getFollowingList();
				addUsersToAdapter(followingUserList);
				lvFollowingFollowers.onRefreshComplete();
				handleProgressBarVisibility(false);
			}
			public void onFailure(Throwable e, JSONObject error) {
				Log.e(TAG, ">onFailure - " + error.toString());
				LayoutUtils.showToast(mContext, "Something went wrong, please try again later");
				handleProgressBarVisibility(false);
			}				
		});	
	}

	private void fetchUserFollowers(final int page){
		handleProgressBarVisibility(true);
		if(page <= 1){
			mFollowersList = new FollowersList();
			resetAdapter();
		}
		TweetrApp.getRestClient().getUserFollowersFriendsList(mRequestUrl, mScreenName, mCursorId, new TweetrJsonHttpResponseHandler(mContext, TAG) {
			@Override
			public void onSuccess(int code, JSONObject body) {
				Log.d(TAG, ">onSuccess - " + body.toString());
				mFollowersList = FollowersList.fromJson(body);
				mCursorId = mFollowersList.getNextCursor();
				ArrayList<User> followersList = mFollowersList.getFollowersList();
				addUsersToAdapter(followersList);
				lvFollowingFollowers.onRefreshComplete();
				handleProgressBarVisibility(false);
			}
			@Override
			public void onFailure(Throwable e, JSONObject error) {
				Log.e(TAG, ">onFailure - " + error.toString());
				LayoutUtils.showToast(mContext, "Something went wrong, please try again later");
				handleProgressBarVisibility(false);
			}				
		});	
	}

	private void addUsersToAdapter(ArrayList<User> followingFollowerUserList) {
		if (followingFollowerUserList != null && followingFollowerUserList.size() > 0) {
			for (User usr : followingFollowerUserList) {
				Log.d(TAG, "adding user " + usr.getScreenName());
				mFollowerFollowingAdapter.add(usr);
			}
		}
		mFollowerFollowingAdapter.notifyDataSetChanged();
	}
	
	private void initListeners() {
		lvFollowingFollowers.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				doRefresh();
			}
		});

		lvFollowingFollowers.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				doLoadMore(page, totalItemsCount);
			}
		});
	}

	protected void doLoadMore(int page, int totalItemsCount) {
		User last = (User) lvFollowingFollowers.getItemAtPosition(totalItemsCount-1);
		if(last == null){
			startApiCallBasedOnPurpose(1);
		}else{
			startApiCallBasedOnPurpose(page);
		}
	}

	protected void doRefresh() {
		startApiCallBasedOnPurpose(1);
	}

	public void handleProgressBarVisibility(boolean show) {
		getActivity().setProgressBarIndeterminateVisibility(show); 
	}

	public void resetAdapter() {
		mFollowerFollowingAdapter.clear();
		mFollowerFollowingAdapter.notifyDataSetInvalidated();
	}

}
