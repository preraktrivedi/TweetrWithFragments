package com.preraktrivedi.apps.tweetr.datamodel;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class FollowersList {

	private long previousCursor = 0, nextCursor = 0;
	private ArrayList<User> followersList = new ArrayList<User>();
	
	public long getPreviousCursor() {
		return previousCursor;
	}

	public void setPreviousCursor(long previousCursor) {
		this.previousCursor = previousCursor;
	}
	
	public long getNextCursor() {
		return nextCursor;
	}

	public void setNextCursor(long nextCursor) {
		this.nextCursor = nextCursor;
	}

	public ArrayList<User> getFollowersList() {
		return followersList;
	}

	public void setFollowersList(ArrayList<User> followersList) {
		this.followersList = followersList;
	}

	public static FollowersList fromJson(JSONObject jo) {
		FollowersList list = new FollowersList();
		
		try {
			list.setNextCursor(jo.getLong("next_cursor"));
			list.setPreviousCursor(jo.getLong("previous_cursor"));
		} catch (Exception e) {
			list.setNextCursor(0);
			list.setPreviousCursor(0);
		}
		
		try {
			JSONArray followersArray = jo.getJSONArray("users");
			if (followersArray.length() > 0) {
				for (int i = 0; i < followersArray.length(); i++) {
					User user =  User.fromJson(followersArray.getJSONObject(i));
					list.getFollowersList().add(user);
				}
			}
		} catch (Exception e) {
			list.setFollowersList(null);
		}
	
		return list;
	}
	
}
