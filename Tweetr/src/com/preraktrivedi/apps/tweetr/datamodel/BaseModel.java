package com.preraktrivedi.apps.tweetr.datamodel;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseModel {

	protected JSONObject jsonObject;

	public String getJSONString(){
		return jsonObject.toString();
	}

	protected String getString(String name){
		try {
			return jsonObject.getString(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected long getLong(String name){
		try {
			return jsonObject.getLong(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	protected int getInt(String name){
		try {
			return jsonObject.getInt(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	protected double getDouble(String name){
		try {
			return jsonObject.getDouble(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
	}

	protected boolean getBoolean(String name){
		try {
			return jsonObject.getBoolean(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

}