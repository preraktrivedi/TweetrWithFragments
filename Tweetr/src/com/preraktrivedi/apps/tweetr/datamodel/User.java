package com.preraktrivedi.apps.tweetr.datamodel;

import org.json.JSONObject;

import android.text.TextUtils;

public class User {

	private long id;
	private String name, screenName, profileImageUrl, profileBackGroundImageUrl, description;
	private long statusesCount, followersCount, friendsCount;
	private boolean isFollowing, isFollower;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getProfileBackGroundImageUrl() {
		return profileBackGroundImageUrl;
	}

	public void setProfileBackGroundImageUrl(String profileBackGroundImageUrl) {
		this.profileBackGroundImageUrl = profileBackGroundImageUrl;
	}

	public long getStatusesCount() {
		return statusesCount;
	}

	public void setStatusesCount(long statusesCount) {
		this.statusesCount = statusesCount;
	}

	public long getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(long followersCount) {
		this.followersCount = followersCount;
	}

	public long getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(long friendsCount) {
		this.friendsCount = friendsCount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isFollowing() {
		return isFollowing;
	}

	public void setFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
	}

	public static User fromJson(JSONObject jo) {
		User user = new User();
		try {
			if (!jo.isNull("name")) {
				user.setName(jo.getString("name"));
			}
			if (!jo.isNull("following")) {
				user.setFollowing(jo.getBoolean("following"));
			}
			if (!jo.isNull("profile_image_url")) {
				user.setProfileImageUrl(jo.getString("profile_image_url"));
			}
			if (!jo.isNull("profile_banner_url")) {
				user.setProfileBackGroundImageUrl(jo.getString("profile_banner_url"));
			}
			if (TextUtils.isEmpty(user.getProfileBackGroundImageUrl()) && !jo.isNull("profile_background_image_url")) {
				user.setProfileBackGroundImageUrl(jo.getString("profile_background_image_url"));
			}
			if (!jo.isNull("description")) {
				user.setDescription(jo.getString("description"));
			}
			try {
				if (jo.getLong("id") > 0) {
					user.setId(Long.valueOf(jo.getLong("id")));
				}
			} catch (Exception e) {
			}

			if (!jo.isNull("screen_name")) {
				user.setScreenName(jo.getString("screen_name"));
			}
			
			try {
				if (jo.has("friends_count") && jo.getLong("friends_count") >= 0) {
					user.setFriendsCount(jo.getLong("friends_count"));
				}
			} catch (Exception e) {
				user.setFriendsCount(0);
			}
			try {
				if (jo.has("followers_count") && jo.getLong("followers_count") >= 0) {
					user.setFollowersCount(jo.getLong("followers_count"));
				}
			} catch (Exception e) {
				user.setFollowersCount(0);
			}
			try {
				if (jo.has("statuses_count") && jo.getLong("statuses_count") >= 0) {
					user.setStatusesCount(jo.getLong("statuses_count"));
				}
			} catch (Exception e) {
				user.setStatusesCount(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return user;

	}
}