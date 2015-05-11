package com.example.lovetalk;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.baidu.mapapi.SDKInitializer;
import com.example.lovetalk.service.AddRequest;
import com.example.lovetalk.util.PhotoUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class DemoApplication extends Application {
	public static DemoApplication context;
	private static Map<String, AVUser> usersCache = new HashMap<String, AVUser>();
	List<AVUser> friends = new ArrayList<AVUser>();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = this;
		AVOSCloud.initialize(this,
				"h1yik0j4fq0wb7759ruubwdcc7qi4bod880i1joe98jk160s",
				"yl8j5oyb3avv54pbjsl76no4lcnt6lq0z2jngx2dqze4b4h8");
		AVObject.registerSubclass(AddRequest.class);
		SDKInitializer.initialize(this);
		initImageLoader(context);
	}

	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"leanchat/Cache");
		ImageLoaderConfiguration config = PhotoUtil.getImageLoaderConfig(
				context, cacheDir);
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public List<AVUser> getFriends() {
		return friends;
	}

	public void setFriends(List<AVUser> friends) {
		this.friends = friends;
	}

	public static AVUser lookupUser(String userId) {
		return usersCache.get(userId);
	}

	public static void registerUserCache(String userId, AVUser user) {
		usersCache.put(userId, user);
	}

	public static void registerUserCache(AVUser user) {
		registerUserCache(user.getObjectId(), user);
	}

	public static void registerBatchUserCache(List<AVUser> users) {
		for (AVUser user : users) {
			registerUserCache(user);
		}
	}

}
