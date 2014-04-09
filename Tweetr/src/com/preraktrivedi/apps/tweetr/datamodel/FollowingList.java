package com.preraktrivedi.apps.tweetr.datamodel;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class FollowingList {
	
	private long previousCursor = 0, nextCursor = 0;
	private ArrayList<User> followingList= new ArrayList<User>();
	
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

	public ArrayList<User> getFollowingList() {
		return followingList;
	}

	public void setFollowingList(ArrayList<User> followingList) {
		this.followingList = followingList;
	}

	public static FollowingList fromJson(JSONObject jo) {
		FollowingList list = new FollowingList();
		
		try {
			list.setNextCursor(jo.getLong("next_cursor"));
			list.setPreviousCursor(jo.getLong("previous_cursor"));
		} catch (Exception e) {
			list.setNextCursor(0);
			list.setPreviousCursor(0);
		}
		
		try {
			JSONArray followingArray = jo.getJSONArray("users");
			if (followingArray.length() > 0) {
				for (int i = 0; i < followingArray.length(); i++) {
					User user =  User.fromJson(followingArray.getJSONObject(i));
					list.getFollowingList().add(user);
				}
			}
		} catch (Exception e) {
			list.setFollowingList(null);
		}
	
		return list;
	}
}
