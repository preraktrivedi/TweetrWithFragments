package com.preraktrivedi.apps.tweetr.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.application.TweetrApp;
import com.preraktrivedi.apps.tweetr.datamodel.Tweet;
import com.preraktrivedi.apps.tweetr.datamodel.TweetrAppData;
import com.preraktrivedi.apps.tweetr.datamodel.User;
import com.preraktrivedi.apps.tweetr.restclient.TweetrJsonHttpResponseHandler;
import com.preraktrivedi.apps.tweetr.utils.LayoutUtils;

public class DetailedTweetActivity extends Activity {

	private static final String TAG = DetailedTweetActivity.class.getSimpleName();
	private Context mContext;
	private TweetrAppData mAppData;
	private Tweet currentDetailedTweet;
	private EditText etTweetBox;
	private TextView tvScreenName, tvUsername, tvTweetBody, tvTimeCreated, tvFavouritesCount, tvRetweetsCounts, tvTweetCharCount, tvSendTweet;
	private ImageView ivProfileImage, ivDetailImage, ivShareAction, ivFavoriteAction, ivReplyAction, ivRetweetAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mAppData = TweetrAppData.getInstance();
		if (mAppData.getCurrentDetailedTweet() != null) {
			currentDetailedTweet = mAppData.getCurrentDetailedTweet();
			initializeView();
		} else {
			LayoutUtils.showToast(mContext, getString(R.string.error_something_wrong));
			this.finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail_tweet, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			goBackToTimeline();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void goBackToTimeline() {
		mAppData.setCurrentDetailedTweet(null);
		showLoader(false);
		finish();
	}

	private void initializeView() {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		styleActionBar();
		setContentView(R.layout.activity_detailed_tweet);
		User user = currentDetailedTweet.getUser();
		ivProfileImage = (ImageView) findViewById(R.id.ivProfileCompose);
		ivDetailImage = (ImageView) findViewById(R.id.ivDetailImage);
		tvScreenName = (TextView) findViewById(R.id.tvScreenNameCompose);
		tvUsername = (TextView) findViewById(R.id.tvUserNameCompose);
		tvTweetBody = (TextView) findViewById(R.id.tvTweetBody);
		tvTimeCreated = (TextView) findViewById(R.id.tvTimeCreated);
		tvFavouritesCount = (TextView) findViewById(R.id.tvFavoritesCount); 
		tvRetweetsCounts  = (TextView) findViewById(R.id.tvRetweetsCount);
		tvTweetCharCount  = (TextView) findViewById(R.id.tv_replyTweetCount); 
		ivShareAction = (ImageView) findViewById(R.id.ivShareAction);
		ivFavoriteAction = (ImageView) findViewById(R.id.ivFavoriteAction);
		ivReplyAction = (ImageView) findViewById(R.id.ivReplyUserAction);
		ivRetweetAction = (ImageView) findViewById(R.id.ivRetweetAction);
		tvSendTweet = (TextView) findViewById(R.id.tv_sendTweet);
		etTweetBox = (EditText) findViewById(R.id.et_tweetbox);
		populateFields(user);
		setupListeners();
	}

	private void populateFields(User user) {
		ImageLoader.getInstance().displayImage(user.getProfileImageUrl(), ivProfileImage, LayoutUtils.getDisplayImageOptionsForRoundedImage(45));
		if (!TextUtils.isEmpty(currentDetailedTweet.getMediaUrl())) {
			ImageLoader.getInstance().displayImage(currentDetailedTweet.getMediaUrl(), ivDetailImage, LayoutUtils.getDisplayImageOptionsForRoundedImage(60));
			ivDetailImage.setVisibility(View.VISIBLE);
		}
		tvScreenName.setText("@" + user.getScreenName());
		tvUsername.setText(user.getName());
		tvTweetBody.setText(currentDetailedTweet.getBody());
		tvTimeCreated.setText(LayoutUtils.getTweetCreationTimestamp(currentDetailedTweet.getTimestamp()));
		tvRetweetsCounts.setText(currentDetailedTweet.getRetweetCount() + " RETWEETS");
		tvFavouritesCount.setText(currentDetailedTweet.getFavouritesCount() + " FAVORITES");
		etTweetBox.setHint("Reply to " + user.getName());
		int favImageResource = currentDetailedTweet.isFavorited() ? R.drawable.ic_favorite_selected : R.drawable.ic_favorite_unselected;
		ivFavoriteAction.setImageResource(favImageResource);
		int retweetImageResource = currentDetailedTweet.isRetweeted() ? R.drawable.ic_retweeted_action : R.drawable.ic_retweet_action;
		ivRetweetAction.setImageResource(retweetImageResource);
	}

	private void setupListeners() {
		etTweetBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s!=null) {
					String tweetMsg = s.toString();
					LayoutUtils.validateTweetMsg(tweetMsg, tvTweetCharCount);
				}
			}
		});
		ivProfileImage.requestFocus();

		etTweetBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String screenName = "@" + currentDetailedTweet.getUser().getScreenName() + " ";
				etTweetBox.setText(screenName);
				etTweetBox.setSelection(screenName.length());
			}
		});

		tvSendTweet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				postTweet();
			}
		});

		ivProfileImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				launchUserProfile();
			}
		});

		ivShareAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startShareIntent();
			}
		});

		ivFavoriteAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleFavoriteAction();
			}
		});

		ivRetweetAction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmRetweet();
			}
		});

		ivReplyAction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etTweetBox.performClick();
				LayoutUtils.handleKeyboardVisibility(true, mContext, etTweetBox);
			}
		});
	}

	private void styleActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#297ABA")));
		getActionBar().setLogo(R.drawable.ic_logo_tweetr_white);
		getActionBar().setTitle(Html.fromHtml(LayoutUtils.getComposeTitle("Tweet")));
	}

	private void postTweet() {
		String tweetMsg = etTweetBox.getText().toString();
		if (TextUtils.isEmpty(tweetMsg)) {
			LayoutUtils.showToast(mContext, "Message cannot be empty");
			return;
		} 

		if (!tweetMsg.contains(currentDetailedTweet.getUser().getScreenName())) {
			LayoutUtils.showToast(mContext, "Replies to this tweet should mention the author! - @" + currentDetailedTweet.getUser().getScreenName());
			return;
		}

		showLoader(true);
		TweetrApp.getRestClient().postTweet("update", tweetMsg, null, String.valueOf(currentDetailedTweet.getId()), new TweetrJsonHttpResponseHandler(mContext, TAG) {
			@Override
			public void onSuccess(JSONObject jsonTweet) {
				goBackToTimeline();
			}
		});
	}

	private void showLoader(boolean show) {
		setProgressBarIndeterminateVisibility(show);
	}

	private void startShareIntent() {
		String strToShare = LayoutUtils.getStringToShare(currentDetailedTweet);
		Log.i(TAG, "sharetext - " + strToShare.toString());
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, strToShare.toString());
		startActivity(Intent.createChooser(shareIntent, "Complete Action Using:"));
	}

	private void confirmRetweet() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage(R.string.retweet_msg);
		adb.setTitle(R.string.retweet_title);
		adb.setNeutralButton(R.string.quote_retweet, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				quoteTweet();
			}

		});
		adb.setPositiveButton(R.string.retweet_title, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if(!currentDetailedTweet.isRetweeted()){
					postRetweet();
				} else {
					LayoutUtils.showToast(mContext, "Already retweeted this earlier");
				}
			}
		});
		adb.setNegativeButton("Cancel", null);
		adb.create().show();
	}
	
	private void quoteTweet() {
		String quoteText = "\"" + currentDetailedTweet.getBody() + "\"";
		Intent i = new Intent(this, ComposeTweetActivity.class);
		i.putExtra(ComposeTweetActivity.QUOTE_USER, quoteText);
		startActivity(i);
	}
	
	private void postRetweet() {

		TweetrApp.getRestClient().postRetweet(String.valueOf(currentDetailedTweet.getId()), new TweetrJsonHttpResponseHandler(mContext, TAG) {
			@Override
			public void onSuccess(JSONObject body) {
				Log.d(TAG, "onSuccess postRetweet " + body.toString());
				goBackToTimeline();
				super.onSuccess(body);
			}

			public void onFailure(Throwable e, JSONObject error) {
				Log.e(TAG, "onFailure postRetweet " + error.toString());
				showLoader(false);
				LayoutUtils.showToast(mContext, "Could not complete action at this time, Please try again."); 
			}
		});
	}

	private void handleFavoriteAction() {
		showLoader(true);
		TweetrApp.getRestClient().handleFavoriteAction(currentDetailedTweet.isFavorited(), String.valueOf(currentDetailedTweet.getId()), new TweetrJsonHttpResponseHandler(mContext, TAG) {
			@Override
			public void onSuccess(JSONObject body) {
				showLoader(false);
				if(!currentDetailedTweet.isFavorited()){
					currentDetailedTweet.setFavorited(true);
					ivFavoriteAction.setImageResource(R.drawable.ic_favorite_selected);
				} else {
					currentDetailedTweet.setFavorited(false);
					ivFavoriteAction.setImageResource(R.drawable.ic_favorite_unselected);
				}
				super.onSuccess(body);
			}

			public void onFailure(Throwable e, JSONObject error) {
				Log.e(TAG, "onFailure Favorite " + error.toString());
				showLoader(false);
				LayoutUtils.showToast(mContext, "Could not complete action at this time, Please try again."); 
			}
		});
	}

	private void launchUserProfile() {
		TweetrAppData.getInstance().setCurrentViewedUser(currentDetailedTweet.getUser());
		Intent i = new Intent(mContext, UserProfileActivity.class);
		startActivity(i);
	}

}
