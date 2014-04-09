package com.preraktrivedi.apps.tweetr.restclient;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.utils.LayoutUtils;

public class TweetrJsonHttpResponseHandler extends JsonHttpResponseHandler {

	private String tag;
	private Context mContext;

	public TweetrJsonHttpResponseHandler(Context context, String tag) {
		this.mContext = context;
		this.tag = tag;
	}

	@Override
	public void onFailure(Throwable error) {
		error.printStackTrace();
		showGenericFailureMsg();
	}

	@Override
	public void onFailure(Throwable error, JSONObject errorResponse) {
		Log.e(tag, ">TweetrJsonHttpResponseHandler onFailure " + errorResponse.toString());
		showGenericFailureMsg();
	}

	@Override
	public void onFailure(Throwable error, JSONArray errorResponse) {
		Log.e(tag, ">TweetrJsonHttpResponseHandler onFailure " + errorResponse.toString());
		showGenericFailureMsg();
	}

	private void showGenericFailureMsg() {
		String msg =  mContext.getResources().getString(R.string.error_something_wrong);
		if(!isConnecedToInternet()) {
			msg =  mContext.getResources().getString(R.string.error_no_internet);
		}
		LayoutUtils.showToast(mContext, msg);
	}

	public boolean isConnecedToInternet(){
		ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
