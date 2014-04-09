package com.preraktrivedi.apps.tweetr.activities;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.codepath.oauth.OAuthLoginActivity;
import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.application.TweetrApp;
import com.preraktrivedi.apps.tweetr.datamodel.TweetrAppData;
import com.preraktrivedi.apps.tweetr.datamodel.User;
import com.preraktrivedi.apps.tweetr.restclient.TweetrJsonHttpResponseHandler;
import com.preraktrivedi.apps.tweetr.restclient.TwitterClient;
import com.preraktrivedi.apps.tweetr.utils.LayoutUtils;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

	private static final String TAG = LoginActivity.class.getSimpleName();
	private final int TIME_DELAY_FOR_AUTHENTICATION = 3500;
	private TweetrAppData mAppData;
	private Context mContext;
	private Button btLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mAppData = TweetrAppData.getInstance();
		initUi();

		new Handler().postDelayed(new Runnable(){
			@Override
			public void run() {
			}
		}, TIME_DELAY_FOR_AUTHENTICATION);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, ">onResume - ");
		if (getClient() != null) {
			Log.d(TAG, ">isAuthenticated - " + getClient().isAuthenticated());
			if(getClient().isAuthenticated()) {
				btLogin.setVisibility(View.GONE);
			} else {
				btLogin.setVisibility(View.VISIBLE);
			}
		}
	}

	private void initUi() {
		setContentView(R.layout.activity_login);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E0EAEF")));
		getActionBar().setTitle(Html.fromHtml("<font color=\"#2E82FF\">" + getString(R.string.app_name) + "</font>"));
		btLogin = (Button) findViewById(R.id.bt_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	@Override
	public void onLoginSuccess() {
		btLogin.setVisibility(View.VISIBLE);
		if (mAppData.getAuthenticatedTwitterUser() == null){
			TweetrApp.getRestClient().getUserProfile(new TweetrJsonHttpResponseHandler(mContext, TAG) {
				@Override
				public void onSuccess(JSONObject jsonUser) {
					Log.d(TAG, ">onSuccess getUserProfile" + jsonUser.toString());
					User user = User.fromJson(jsonUser);
					mAppData.setAuthenticatedTwitterUser(user);
					loadTimeline();
				}
			});
		}
	}

	private void loadTimeline() {
		if (mAppData.getAuthenticatedTwitterUser() != null) {
			Intent i = new Intent(this, TweetrLandingActivity.class);
			startActivity(i);
			this.finish();
		} else {
			showLoginError("Please Sign In with your Twitter Account");
			btLogin.setVisibility(View.VISIBLE);
		}
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
		btLogin.setVisibility(View.VISIBLE);
		showLoginError("Login Failed, please authorize again");
	}

	private void showLoginError(String msg) {
		LayoutUtils.showToast(mContext, msg);
	}

	public void loginToRest(View view) {
		getClient().connect();
	}

}