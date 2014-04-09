package com.preraktrivedi.apps.tweetr.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.preraktrivedi.apps.tweetr.R;

public class PageSlidingTabStripFragment extends Fragment {

	public static final String TAG = PageSlidingTabStripFragment.class.getSimpleName();
	private static final String TIMELINE = "Timeline", MENTIONS = "Mentions", MESSAGES = "Messages";
	private UserHomeTimelineFragment userHomeTimelineFragment;
	private UserMentionsFragment userMentionsFragment;
	
	public static PageSlidingTabStripFragment newInstance() {
		return new PageSlidingTabStripFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userHomeTimelineFragment = new UserHomeTimelineFragment();
		userMentionsFragment = new UserMentionsFragment();
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.pager, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		tabs.setIndicatorColor(getResources().getColor(R.color.twitter_blue_alpha));
		tabs.setShouldExpand(true);
		tabs.setTextColor(getResources().getColor(R.color.twitter_blue));
		ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
		MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(adapter.getCount());
		tabs.setViewPager(pager);

	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		private final String[] TITLES = {TIMELINE, MENTIONS, MESSAGES};

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			
			if (TITLES[position].equals(TIMELINE)) {
				return userHomeTimelineFragment;
			} else if (TITLES[position].equals(MENTIONS)) {
				return userMentionsFragment;
			} 

			return SuperAwesomeCardFragment.newInstance(position);
		}

	}


}