package com.preraktrivedi.apps.tweetr.adapters;

import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.activities.ComposeTweetActivity;
import com.preraktrivedi.apps.tweetr.activities.UserProfileActivity;
import com.preraktrivedi.apps.tweetr.application.TweetrApp;
import com.preraktrivedi.apps.tweetr.datamodel.Tweet;
import com.preraktrivedi.apps.tweetr.datamodel.TweetrAppData;
import com.preraktrivedi.apps.tweetr.datamodel.User;
import com.preraktrivedi.apps.tweetr.restclient.TweetrJsonHttpResponseHandler;
import com.preraktrivedi.apps.tweetr.utils.LayoutUtils;

public class TweetrAdapter extends ArrayAdapter<Tweet> {

	private static final String TAG = TweetrAdapter.class.getSimpleName();
	private Context mContext;

	private static class ViewHolder {
		ImageView profilePic;
		ImageView mediaImage;
		TextView name;
		TextView body;
		TextView tvTimeStamp;
		RelativeLayout rlRetweetAction;
		ImageView ivRetweetAction;
		TextView tvRetweetAction;
		RelativeLayout rlFavoriteAction;
		ImageView ivFavoriteAction;
		TextView tvFavoriteAction;
		RelativeLayout rlReplyAction;
	}

	public TweetrAdapter(Context context, List<Tweet> tweets) {
		super(context, R.layout.tweet_item, tweets);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final Tweet tweet = getItem(position);
		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.tweet_item, null, false);
			viewHolder.body = (TextView) convertView.findViewById(R.id.tvBody);
			viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
			viewHolder.profilePic = (ImageView) convertView.findViewById(R.id.ivProfile);
			viewHolder.tvTimeStamp = (TextView) convertView.findViewById(R.id.tvTimeStamp);
			viewHolder.mediaImage = (ImageView) convertView.findViewById(R.id.ivMediaImageThumb);
			viewHolder.rlRetweetAction = (RelativeLayout) convertView.findViewById(R.id.rlRetweetAction);
			viewHolder.ivRetweetAction = (ImageView) convertView.findViewById(R.id.ivRetweetAction);
			viewHolder.tvRetweetAction = (TextView) convertView.findViewById(R.id.tvRetweetCount);
			viewHolder.rlFavoriteAction = (RelativeLayout) convertView.findViewById(R.id.rlFavoriteAction);
			viewHolder.ivFavoriteAction = (ImageView) convertView.findViewById(R.id.ivFavoriteAction);
			viewHolder.tvFavoriteAction = (TextView) convertView.findViewById(R.id.tvFavoriteCount);
			viewHolder.rlReplyAction = (RelativeLayout) convertView.findViewById(R.id.rlReplyUserAction);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		configureView(tweet, viewHolder);

		return convertView;
	}

	private void configureView(final Tweet tweet, final ViewHolder viewHolder) {

		ImageLoader.getInstance().displayImage(tweet.getUser().getProfileImageUrl(), viewHolder.profilePic, LayoutUtils.getDisplayImageOptionsForRoundedImage(45));
		viewHolder.profilePic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchUserProfile(tweet.getUser());
			}
		});
		viewHolder.name.setText(Html.fromHtml(LayoutUtils.getFormattedUsername(tweet.getUser())));
		viewHolder.body.setText(Html.fromHtml(tweet.getBody()));
		viewHolder.tvTimeStamp.setText(LayoutUtils.getFormattedTimestamp(mContext, tweet.getTimestamp()));
		viewHolder.mediaImage.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(tweet.getMediaUrl())) {
			String mediaUrl = tweet.getMediaUrl() + ":thumb";
			ImageLoader.getInstance().displayImage(mediaUrl, viewHolder.mediaImage, LayoutUtils.getDisplayImageOptionsForRoundedImage(22));
			viewHolder.mediaImage.setVisibility(View.VISIBLE);
		}
		if (tweet.getRetweetCount() > 0) {
			viewHolder.tvRetweetAction.setText(LayoutUtils.getFormattedCount(tweet.getRetweetCount()));
		} else {
			viewHolder.tvRetweetAction.setVisibility(View.GONE);
		}
		if (tweet.getFavouritesCount() > 0) {
			viewHolder.tvFavoriteAction.setText(LayoutUtils.getFormattedCount(tweet.getFavouritesCount()));
		} else {
			viewHolder.tvFavoriteAction.setVisibility(View.GONE);
		}

		int favImageResource = tweet.isFavorited() ? R.drawable.ic_favorite_selected : R.drawable.ic_favorite_unselected;
		viewHolder.ivFavoriteAction.setImageResource(favImageResource);
		int retweetImageResource = tweet.isRetweeted() ? R.drawable.ic_retweeted_action : R.drawable.ic_retweet_action;
		viewHolder.ivRetweetAction.setImageResource(retweetImageResource);

		setupListeners(viewHolder, tweet);
	}

	private void setupListeners(final ViewHolder viewHolder, final Tweet tweet) {
		viewHolder.rlFavoriteAction.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleFavoriteAction(tweet, viewHolder);
			}
		});

		viewHolder.rlRetweetAction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmRetweet(tweet);
			}
		});

		viewHolder.rlReplyAction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				replyToAuthor(tweet.getUser().getScreenName());
			}
		});
	}

	private void launchUserProfile(User user) {
		TweetrAppData.getInstance().setCurrentViewedUser(user);
		Intent i = new Intent(mContext, UserProfileActivity.class);
		mContext.startActivity(i);
	}

	private void replyToAuthor(String screenName) {
		String body = "@" + screenName + " ";
		Intent i = new Intent(mContext, ComposeTweetActivity.class);
		i.putExtra(ComposeTweetActivity.QUOTE_USER, body);
		mContext.startActivity(i);
	}

	private void handleFavoriteAction(final Tweet tweet, final ViewHolder viewholder) {
		TweetrApp.getRestClient().handleFavoriteAction(tweet.isFavorited(), String.valueOf(tweet.getId()), new TweetrJsonHttpResponseHandler(mContext, TAG) {
			@Override
			public void onSuccess(JSONObject body) {
				Log.d(TAG, "onSuccess Favorite " + body.toString());
				if(!tweet.isFavorited()){
					tweet.setFavorited(true);
					viewholder.ivFavoriteAction.setImageResource(R.drawable.ic_favorite_selected);
					viewholder.tvFavoriteAction.setText(LayoutUtils.getFormattedCount(tweet.getFavouritesCount() + 1));
				} else {
					tweet.setFavorited(false);
					viewholder.ivFavoriteAction.setImageResource(R.drawable.ic_favorite_unselected);
					viewholder.tvFavoriteAction.setText(LayoutUtils.getFormattedCount(tweet.getFavouritesCount() - 1));
				}
				super.onSuccess(body);
			}

			public void onFailure(Throwable e, JSONObject error) {
				Log.e(TAG, "onFailure Favorite " + error.toString());
				LayoutUtils.showToast(mContext, "Could not complete action at this time, Please try again."); 
			}
		});
	}

	private void confirmRetweet(final Tweet tweet) {
		AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
		adb.setMessage(R.string.retweet_msg);
		adb.setTitle(R.string.retweet_title);
		adb.setNeutralButton(R.string.quote_retweet, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				quoteTweet(tweet.getBody());
			}

		});
		adb.setPositiveButton(R.string.retweet_title, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				if(!tweet.isRetweeted()){
					postRetweet(tweet);
				} else {
					LayoutUtils.showToast(mContext, "Already retweeted this earlier");
				}
			}
		});
		adb.setNegativeButton("Cancel", null);
		adb.create().show();
	}

	private void quoteTweet(String body) {
		String quoteText = "\"" + body + "\"";
		Intent i = new Intent(mContext, ComposeTweetActivity.class);
		i.putExtra(ComposeTweetActivity.QUOTE_USER, quoteText);
		mContext.startActivity(i);
	}
	
	private void postRetweet(Tweet tweet) {

		TweetrApp.getRestClient().postRetweet(String.valueOf(tweet.getId()), new TweetrJsonHttpResponseHandler(mContext, TAG) {
			@Override
			public void onSuccess(JSONObject body) {
				Log.d(TAG, "onSuccess postRetweet " + body.toString());
				super.onSuccess(body);
			}

			public void onFailure(Throwable e, JSONObject error) {
				Log.e(TAG, "onFailure postRetweet " + error.toString());
				LayoutUtils.showToast(mContext, "Could not complete action at this time, Please try again."); 
			}
		});
	}

}