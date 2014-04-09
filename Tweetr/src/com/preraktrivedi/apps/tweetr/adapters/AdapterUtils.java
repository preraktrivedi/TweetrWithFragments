package com.preraktrivedi.apps.tweetr.adapters;

import android.content.Context;
import android.content.Intent;

import com.preraktrivedi.apps.tweetr.activities.UserProfileActivity;
import com.preraktrivedi.apps.tweetr.datamodel.TweetrAppData;
import com.preraktrivedi.apps.tweetr.datamodel.User;

public class AdapterUtils {
	
	protected static void launchUserProfile(Context context, User user) {
		TweetrAppData.getInstance().setCurrentViewedUser(user);
		Intent i = new Intent(context, UserProfileActivity.class);
		context.startActivity(i);
	}
}
