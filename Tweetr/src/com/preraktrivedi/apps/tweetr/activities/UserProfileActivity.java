package com.preraktrivedi.apps.tweetr.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.datamodel.TweetrAppData;
import com.preraktrivedi.apps.tweetr.datamodel.User;
import com.preraktrivedi.apps.tweetr.fragments.UserTweetsFragment;
import com.preraktrivedi.apps.tweetr.utils.LayoutUtils;

public class UserProfileActivity extends ActionBarActivity {

	private static final String TAG = UserProfileActivity.class.getSimpleName();
	private Context mContext;
	private TweetrAppData mAppData;
	private User mCurrentUser;
	private TextView tvProfileName, tvProfileTag, tvFollower, tvFollowing, tvScreenName, tvTweetCount;
	private ImageView ivProfileImage;
	private RelativeLayout rlProfileHeader;
	private RelativeLayout rlFollowingTab, rlFollowerTab;
	private UserTweetsFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mAppData = TweetrAppData.getInstance();
		if (mAppData.getCurrentViewedUser() != null) {
			mCurrentUser = mAppData.getCurrentViewedUser();
			initUi();
		} else {
			LayoutUtils.showToast(mContext, getString(R.string.error_something_wrong));
			this.finish();
		}
	}

	private void initUi() {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		styleActionBar();
		setContentView(R.layout.activity_user_profile);
		ivProfileImage = (ImageView) findViewById(R.id.iv_profile_image);
		tvProfileName = (TextView) findViewById(R.id.tv_profile_name);
		tvProfileTag = (TextView) findViewById(R.id.tv_profile_tag);
		tvFollower = (TextView) findViewById(R.id.tv_profile_follower);
		tvFollowing = (TextView) findViewById(R.id.tv_profile_following);
		tvTweetCount = (TextView) findViewById(R.id.tv_tweet_count);
		tvScreenName = (TextView) findViewById(R.id.tv_screen_name);
		rlFollowerTab = (RelativeLayout) findViewById(R.id.rl_profile_follower);
		rlFollowingTab = (RelativeLayout) findViewById(R.id.rl_profile_following);
		rlProfileHeader = (RelativeLayout) findViewById(R.id.rl_profile_header);
		populateFields();
	}

	private void styleActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#297ABA")));
		getActionBar().setLogo(R.drawable.ic_logo_tweetr_white);
		getActionBar().setTitle(Html.fromHtml(LayoutUtils.getUserTitleText(mContext, mCurrentUser.getScreenName())));
	}

	@SuppressLint("NewApi")
	private void populateFields() {
		ImageLoader.getInstance().loadImage(mCurrentUser.getProfileBackGroundImageUrl(), new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String arg0, View arg1) {
			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
				Log.i(TAG, "Image Loading Completed");
				Drawable background = new BitmapDrawable(getResources(), bitmap);
				background.setAlpha(97);
				if (Build.VERSION.SDK_INT >= 16) {
					rlProfileHeader.setBackground(background);
				} else {
					rlProfileHeader.setBackgroundDrawable(background);
				}

			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
			}
		});
		//ImageLoader.getInstance().displayImage(mCurrentUser.getProfileBackGroundImageUrl(), rlProfileHeader);
		ImageLoader.getInstance().displayImage(mCurrentUser.getProfileImageUrl(), ivProfileImage, LayoutUtils.getDisplayImageOptionsForRoundedImage(80));
		tvProfileName.setText(mCurrentUser.getName());
		tvFollower.setText(LayoutUtils.getFormattedCount(mCurrentUser.getFollowersCount()));
		tvFollowing.setText(LayoutUtils.getFormattedCount(mCurrentUser.getFriendsCount()));
		tvProfileTag.setText(mCurrentUser.getDescription());
		tvScreenName.setText("@" + mCurrentUser.getScreenName());
		tvTweetCount.setText(LayoutUtils.getFormattedCount(mCurrentUser.getStatusesCount()));
		rlFollowerTab.setOnClickListener(getOnClickListener(GenericFragmentActivity.PURPOSE_FOLLOWER));
		rlFollowingTab.setOnClickListener(getOnClickListener(GenericFragmentActivity.PURPOSE_FOLLOWING));
		addFragmentToView(mCurrentUser.getScreenName());
	}

	private View.OnClickListener getOnClickListener(final String purpose) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchFollowingFollowerActivity(purpose);
			}
		};
	}

	private void launchFollowingFollowerActivity(String purpose) {
		Intent i = new Intent(mContext, GenericFragmentActivity.class);
		i.putExtra(GenericFragmentActivity.USER_SCREEN_NAME, mCurrentUser.getScreenName());
		i.putExtra(GenericFragmentActivity.PURPOSE_GENERIC_FRAGMENT, purpose);
		startActivity(i);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			mAppData.setCurrentViewedUser(null);
			this.finish();
			return true;

		case R.id.mi_compose_tweet:
			addressTweetToUser();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile_user, menu);
		MenuItem composeTweet = menu.findItem(R.id.mi_compose_tweet);
		composeTweet.setVisible(true);
		composeTweet.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	public void addFragmentToView(String screenName) {
		FragmentManager fm = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction fts = fm.beginTransaction();
		if (mFragment == null) {
			fts.add(R.id.fragment_container, UserTweetsFragment.newInstance(screenName));
		} else {
			fts.attach(mFragment);
		}
		fts.commit();
	}

	private void addressTweetToUser() {
		String body = "@" + mCurrentUser.getScreenName() + " ";
		Intent i = new Intent(this, ComposeTweetActivity.class);
		i.putExtra(ComposeTweetActivity.QUOTE_USER, body);
		startActivity(i);
	}


}