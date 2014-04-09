package com.preraktrivedi.apps.tweetr.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.datamodel.TweetrAppData;
import com.preraktrivedi.apps.tweetr.datamodel.User;
import com.preraktrivedi.apps.tweetr.fragments.PageSlidingTabStripFragment;
import com.preraktrivedi.apps.tweetr.utils.LayoutUtils;

public class TweetrLandingActivity extends ActionBarActivity {
	
	public static final String TAG = TweetrLandingActivity.class.getSimpleName();
	private Context mContext;
	private boolean isSavedInstanceState;
	DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle, mTitle;
	private String[] mPlanetTitles;
	private TweetrAppData mAppData = TweetrAppData.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		isSavedInstanceState = savedInstanceState != null;
		intitUi();
	}

	private void intitUi() {
		LayoutUtils.showToast(mContext, "Signed in with user - " + mAppData.getAuthenticatedTwitterUser().getScreenName());
		setContentView(R.layout.activity_tweetr_landing);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		styleActionBar();
		initAdapter();
		setupListeners();
	}

	private void initAdapter() {
		mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		View header = getLayoutInflater().inflate(R.layout.nav_drawer_header, null);
		ImageView ivProfilePic = (ImageView) header.findViewById(R.id.ivProfileHeader);
		TextView tvProfileName = (TextView) header.findViewById(R.id.tvUserNameHeader);
		TextView tvScreenName = (TextView) header.findViewById(R.id.tvScreenNameHeader);
		User user = mAppData.getAuthenticatedTwitterUser();
		tvProfileName.setText(user.getName());
		tvScreenName.setText("@" + user.getScreenName());
		ImageLoader.getInstance().displayImage(user.getProfileImageUrl(), ivProfilePic, LayoutUtils.getDisplayImageOptionsForRoundedImage(35));
		mDrawerList.addHeaderView(header, null, false);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "> onResume menuloader");
	}


	private void styleActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		mTitle = mDrawerTitle = Html.fromHtml(LayoutUtils.getComposeTitle(getString(R.string.app_name)));
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#297ABA")));
		getActionBar().setLogo(R.drawable.ic_logo_tweetr_white);
		getActionBar().setTitle(mTitle);
	}

	private void setupListeners() {

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerToggle = new ActionBarDrawerToggle(this, 
				mDrawerLayout, 
				R.drawable.ic_drawer, 
				R.string.drawer_open, 
				R.string.drawer_close 
				) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); 
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); 
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (!isSavedInstanceState) {
			selectItem(0);
		}

	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "> onCreateOptionsMenu  menuloader");
		getMenuInflater().inflate(R.menu.tweetr_main, menu);
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		MenuItem composeTweet = menu.findItem(R.id.mi_compose_tweet);
		MenuItem userProfile = menu.findItem(R.id.mi_user_profile);
		composeTweet.setVisible(!drawerOpen);
		userProfile.setVisible(!drawerOpen);
		if(!drawerOpen) {
        	composeTweet.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        	userProfile.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "> onOptionsItemSelected  menuloader");
		switch (item.getItemId()) {
		
		case android.R.id.home: 
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			return true;
		
		case R.id.mi_compose_tweet:
			composeNewTweet();
			return true;
			
		case R.id.mi_user_profile:
			showAuthenticatedUserProfile();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}


	private void showAuthenticatedUserProfile() {
		mAppData.setCurrentViewedUser(mAppData.getAuthenticatedTwitterUser());
		Intent i = new Intent(this, UserProfileActivity.class);
		startActivity(i);
	}

	private void composeNewTweet() {
		Intent i = new Intent(this, ComposeTweetActivity.class);
		i.putExtra(ComposeTweetActivity.QUOTE_USER, "");
		startActivity(i);
	}

	// The click listener for ListView in the navigation drawer
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void launchFollowingFollowerActivity(String purpose) {
		Intent i = new Intent(mContext, GenericFragmentActivity.class);
		i.putExtra(GenericFragmentActivity.USER_SCREEN_NAME, mAppData.getAuthenticatedTwitterUser().getScreenName());
		i.putExtra(GenericFragmentActivity.PURPOSE_GENERIC_FRAGMENT, purpose);
		startActivity(i);
	}
	
	private void selectItem(int position) {

		switch (position) {
		case 0:
			getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.content,
					PageSlidingTabStripFragment.newInstance(),
					PageSlidingTabStripFragment.TAG).commit();
			break;
		case 1:
			showAuthenticatedUserProfile();
			break;
		case 2:
			launchFollowingFollowerActivity(GenericFragmentActivity.PURPOSE_FOLLOWING);
			break;
		case 3:
			launchFollowingFollowerActivity(GenericFragmentActivity.PURPOSE_FOLLOWER);
			break;
		case 5:
			promptForQuitConfirmation();
			break;
		default:
			LayoutUtils.showToast(mContext, "Coming Soon in v3 :)");
			break;
		}

		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void promptForQuitConfirmation() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage("Are you sure you want to exit this awesome app?");
		adb.setTitle("Quit");
		adb.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				quitApp();
			}

		});
		adb.setNegativeButton("Stay", null);
		adb.create().show();
	}
	
	private void quitApp() {
		finish();
	}
}
