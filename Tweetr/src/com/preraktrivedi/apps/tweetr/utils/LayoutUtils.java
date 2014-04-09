package com.preraktrivedi.apps.tweetr.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.datamodel.Tweet;
import com.preraktrivedi.apps.tweetr.datamodel.User;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class LayoutUtils {

	private static final long WEEK_IN_MILLIS = 604800000;
	private static final long SECOND_IN_MILLIS = 1000;
	private static final int MAX_TWEET_CHAR_COUNT = 140;

	public static void handleKeyboardVisibility(boolean show, Context context, View view) {

		if (show) {
			InputMethodManager inputMethodManager=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
		} else {
			InputMethodManager imm =(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public static void validateTweetMsg(String tweetMsg, TextView tvTweetCount) {
		int tweetCountLeft = MAX_TWEET_CHAR_COUNT - tweetMsg.length();
		tvTweetCount.setText("" + tweetCountLeft);

		if(tweetCountLeft < 10) {
			tvTweetCount.setTextColor(Color.parseColor("#99E53D38"));
		} else if(tweetCountLeft < 70) {
			tvTweetCount.setTextColor(Color.parseColor("#99E88E23"));
		} else {
			tvTweetCount.setTextColor(Color.parseColor("#9900AB17"));
		}
	}

	public static void showToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 300);
		toast.show();
	}

	public static String getTweetCreationTimestamp(String timestamp) {
		StringBuilder tweetCreationTimestamp = new StringBuilder("");
		tweetCreationTimestamp.append(getDateHours(timestamp) + " \u2022 " );
		tweetCreationTimestamp.append(getDateYears(timestamp));
		return tweetCreationTimestamp.toString();
	}
	public static String getStringToShare(Tweet tweet) {
		final User user = tweet.getUser();
		StringBuilder strToShare = new StringBuilder(user.getName() + " (@" );
		strToShare.append(user.getScreenName() + ") tweeted at " );
		strToShare.append(getDateHours(tweet.getTimestamp()) + " (");
		strToShare.append(Calendar.getInstance().getTimeZone().getDisplayName() + ") on ");
		strToShare.append(getDateYears(tweet.getTimestamp()) + " : ");
		strToShare.append(tweet.getBody());
		return strToShare.toString();
	}
	public static String getDateHours(String input) {
		String strTimeHours = "";
		SimpleDateFormat formatTime = new SimpleDateFormat("h:mm a", Locale.US);
		try {
			strTimeHours = formatTime.format(getTwitterDateFormat(input));
		} catch (IllegalArgumentException e) {
			strTimeHours = "";
		} catch (Exception e) {
			strTimeHours = "";
		}
		return strTimeHours;
	}

	public static String getDateYears(String input) {
		String strTimeHours = "";
		SimpleDateFormat formatDate = new SimpleDateFormat("dd MMM yy", Locale.US);
		try {
			strTimeHours = formatDate.format(getTwitterDateFormat(input));
		} catch (IllegalArgumentException e) {
			strTimeHours = "";
		} catch (Exception e) {
			strTimeHours = "";
		}
		return strTimeHours;
	}

	public static String getUserTitleText(Context context, String username) {
		StringBuilder str = new StringBuilder("<font color=\"#E0EAEF\">");
		str.append(context.getResources().getString(R.string.app_name) + " - @");
		str.append(username);
		str.append("</font>");

		return str.toString();
	}

	public static String getComposeTitle(String msg) {
		StringBuilder str = new StringBuilder("<font color=\"#E0EAEF\">");
		str.append(msg);
		str.append("</font>");

		return str.toString();
	}

	public static Date getTwitterDateFormat(String input) {
		Date date = null;
		try {
			date = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.US).parse(input);
		} catch (ParseException e) {
			Log.e("getFormattedTimestamp", e.toString());
		}
		return date;
	}

	public static String getFormattedTimestamp(Context context, String timestamp) {
		String shortTimeFormat = "";
		Date date = getTwitterDateFormat(timestamp);
		if (date !=null) {
			CharSequence charSeq = DateUtils.getRelativeDateTimeString(context, date.getTime(), SECOND_IN_MILLIS,  WEEK_IN_MILLIS,  0);
			String formattedTime = charSeq.toString();
			formattedTime = formattedTime.substring(0, formattedTime.indexOf(","));
			Log.d("getFormattedTimestamp", " formatted time - " + formattedTime);
			shortTimeFormat = getShortFormatTime(formattedTime);
		}

		return shortTimeFormat;
	}

	private static String getShortFormatTime(String formattedTime) {
		String input = formattedTime.replace(" ", "");
		StringBuilder shortFormat = new StringBuilder();

		for (int i = 0; i < input.length(); i++) {
			if(Character.isLetter(input.charAt(i))) {
				shortFormat.append(input.charAt(i));
				break;
			} else {
				shortFormat.append(input.charAt(i));
			}
		}
		Log.d("getFormattedTimestamp", " short formatted time - " + shortFormat.toString());
		return shortFormat.toString();
	}

	public static String getFormattedUsername(User user) {
		StringBuilder formattedName = new StringBuilder("<b>");
		formattedName.append(user.getName());
		formattedName.append("</b><small> <font color = '#777777'>@");
		formattedName.append(user.getScreenName());
		formattedName.append("</font></small>");
		return formattedName.toString();
	}

	public static String refineInput(double input) {
		BigDecimal bd = new BigDecimal(input);
		BigDecimal rounded = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
		DecimalFormat df = new DecimalFormat("#0.0");
		return df.format(rounded.doubleValue());
	}

	public static String getFormattedCount(long input) {
		String formattedString = "";

		if (input >= 0 && input <= 9999) {
			formattedString = formattedString + input;
		} else if (input > 9999 && input <= 999999) {
			formattedString = refineInput((double)input/1000.0) + "K";
		} else if (input > 999999 && input <= 999999999) {
			formattedString = refineInput((double)input/1000000.0) + "M";
		} else {
			formattedString = refineInput((double)input/1000000000.0) + "B";
		}

		Log.d("DEBUG", "Formatted string getFormattedCount" + formattedString);

		return formattedString;
	}

	public static DisplayImageOptions getDisplayImageOptionsForRoundedImage(int radius) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.ic_launcher) 
		.showImageOnFail(R.drawable.ic_launcher) 
		.bitmapConfig(Bitmap.Config.ARGB_8888) 
		.resetViewBeforeLoading()
		.displayer(new RoundedBitmapDisplayer(radius)) 
		.build();

		return options;
	}
}
