package com.preraktrivedi.apps.tweetr.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.preraktrivedi.apps.tweetr.R;
import com.preraktrivedi.apps.tweetr.datamodel.User;
import com.preraktrivedi.apps.tweetr.utils.LayoutUtils;

public class FollowingFollowerListAdapter extends ArrayAdapter<User> {

	private Context mContext;

	private static class ViewHolder {
		ImageView ivProfilePic;
		TextView tvFullName;
		TextView tvScreenName;
		TextView tvBody;
	}

	public FollowingFollowerListAdapter(Context context, ArrayList<User> users){
		super(context, R.layout.user_item, users);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		final User user = this.getItem(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.user_item, null);
			viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.ivUserProfilePic);
			viewHolder.tvFullName = (TextView) convertView.findViewById(R.id.tvUserName);
			viewHolder.tvScreenName = (TextView) convertView.findViewById(R.id.tvUserScreenName);
			viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvUserDesc);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		configureView(user, viewHolder);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AdapterUtils.launchUserProfile(mContext, user);		
			}

		});
		return convertView;
	}

	private void configureView(User user, ViewHolder viewHolder) {
		if(user.getName() != null) {
			viewHolder.tvFullName.setText(user.getName());
		}
		if(user.getScreenName() != null) {
			viewHolder.tvScreenName.setText("@" + user.getScreenName());
		}
		if (user.getDescription() != null) {
			viewHolder.tvBody.setText(user.getDescription());
		}
		ImageLoader.getInstance().displayImage(user.getProfileImageUrl(), viewHolder.ivProfilePic, LayoutUtils.getDisplayImageOptionsForRoundedImage(35));
	}

}
