package com.example.lovetalk.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;

public class PreferenceMap {
	public static final String ADD_REQUEST_N = "addRequestN";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String ADDRESS = "address";
	public static final String NOTIFY_WHEN_NEWS = "notifyWhenNews";

	Context context;
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	// int addRequestN;
	// String latitude;
	// String longitude;
	public static PreferenceMap currentUserPreferenceMap;

	public PreferenceMap(Context context, String prefName) {
		this.context = context;
		pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		editor = pref.edit();
	}

	public static PreferenceMap getCurUserPrefDao(Context context) {
		if (currentUserPreferenceMap == null) {
			currentUserPreferenceMap = new PreferenceMap(context, AVUser.getCurrentUser().getObjectId());
		}
		return currentUserPreferenceMap;
	}

	public static PreferenceMap getMyPrefDao(Context context) {
		AVUser user = AVUser.getCurrentUser();
		if (user == null) {
			throw new RuntimeException("user is null");
		}
		return new PreferenceMap(context, user.getObjectId());
	}

	public int getAddRequestN() {
		return pref.getInt(ADD_REQUEST_N, 0);
	}

	public void setAddRequestN(int addRequestN) {
		editor.putInt(ADD_REQUEST_N, addRequestN).commit();
	}

	private String getLatitude() {
		return pref.getString(LATITUDE, null);
	}

	private void setLatitude(String latitude) {
		editor.putString(LATITUDE, latitude).commit();
	}

	private String getLongitude() {
		return pref.getString(LONGITUDE, null);
	}

	private void setLongitude(String longitude) {
		editor.putString(LONGITUDE, longitude).commit();
	}

	public String getAddress() {
		return pref.getString(ADDRESS, null);
	}

	public void setAddress(String address) {
		editor.putString(ADDRESS, address).commit();
	}

	public AVGeoPoint getLocation() {
		String latitudeStr = getLatitude();
		String longitudeStr = getLongitude();
		if (latitudeStr == null || longitudeStr == null) {
			return null;
		}
		double latitude = Double.parseDouble(latitudeStr);
		double longitude = Double.parseDouble(longitudeStr);
		return new AVGeoPoint(latitude, longitude);
	}

	public void setLocation(AVGeoPoint location) {
		if (location == null) {
			throw new NullPointerException("location is null");
		}
		setLatitude(location.getLatitude() + "");
		setLongitude(location.getLongitude() + "");
	}
}
