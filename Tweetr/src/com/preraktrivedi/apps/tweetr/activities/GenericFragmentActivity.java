package com.preraktrivedi.apps.tweetr.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.fragments.FollowingFollowerListsFragment;
import com.preraktrivedi.apps.tweetr.utils.LayoutUtils;


public class GenericFragmentActivity extends ActionBarActivity {
	
	public static final String TAG = GenericFragmentActivity.class.getSimpleName();
	public static final String PURPOSE_GENERIC_FRAGMENT = "purpose", USER_SCREEN_NAME = "screen_name";
	public static final String PURPOSE_FOLLOWING = "Following", PURPOSE_FOLLOWER = "Follower";
	private FollowingFollowerListsFragment mFragment = null;
	private String mScreenName;
	private String mPurpose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_generic_fragment);
		mScreenName = getIntent().getStringExtra(USER_SCREEN_NAME);
		mPurpose = getIntent().getStringExtra(PURPOSE_GENERIC_FRAGMENT);
		styleActionBar();
		addFragmentToView(mScreenName, mPurpose);
	}

	private void styleActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#297ABA")));
		getActionBar().setLogo(R.drawable.ic_logo_tweetr_white);
		getActionBar().setTitle(Html.fromHtml(LayoutUtils.getComposeTitle(mPurpose)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.generic_fragment_container_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mFragment = null;
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void addFragmentToView(String screenName, String purpose) {
		FragmentManager fm = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction fts = fm.beginTransaction();
		if (mFragment == null) {
			fts.add(R.id.flFragmentContainer, FollowingFollowerListsFragment.newInstance(screenName, purpose));
		} else {
			fts.attach(mFragment);
		}
		fts.commit();
	}
}
