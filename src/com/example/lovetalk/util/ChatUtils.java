package com.example.lovetalk.util;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.*;
import com.example.lovetalk.DemoApplication;
import com.example.lovetalk.adapter.BaseListAdapter;
import com.example.lovetalk.service.PreferenceMap;
import com.example.lovetalk.service.UserService;
import com.example.lovetalk.view.ViewHolder;
import com.example.lovetalk.view.xlist.XListView;

import java.util.List;

/**
 * Created by lzw on 14-9-30.
 */

public class ChatUtils {
	public static void handleListResult(XListView listView, BaseListAdapter adapter, List datas) {
		if (Utils.isListNotEmpty(datas)) {
			adapter.addAll(datas);
			if (datas.size() == 10) {
				listView.setPullLoadEnable(true);
			} else {
				listView.setPullLoadEnable(false);
			}
		} else {
			listView.setPullLoadEnable(false);
			if (adapter.getCount() == 0) {
				Utils.toast(DemoApplication.context, "无结果");
			} else {
				Utils.toast(DemoApplication.context, "加载完毕");
			}
		}
	}

	public static void updateUserInfo() {
		AVUser user = AVUser.getCurrentUser();
		if (user != null) {
			user.fetchInBackground("friends", new GetCallback<AVObject>() {
				@Override
				public void done(AVObject avObject, AVException e) {
					if (e == null) {
						AVUser avUser = (AVUser) avObject;
						DemoApplication.registerUserCache(avUser);
					}
				}
			});
		}
	}

	public static void updateUserLocation() {
		PreferenceMap preferenceMap = PreferenceMap.getCurUserPrefDao(DemoApplication.context);
		AVGeoPoint lastLocation = preferenceMap.getLocation();
		if (lastLocation != null) {
			final AVUser user = AVUser.getCurrentUser();
			final AVGeoPoint location = user.getAVGeoPoint("location");
			if (location == null || !Utils.doubleEqual(location.getLatitude(), lastLocation.getLatitude())
					|| !Utils.doubleEqual(location.getLongitude(), lastLocation.getLongitude())) {
				user.put("location", lastLocation);
				user.saveInBackground(new SaveCallback() {
					@Override
					public void done(AVException e) {
						if (e != null) {
							e.printStackTrace();
						} else {
							Log.v("lan", "lastLocation save " + user.getAVGeoPoint("location"));
						}
					}
				});
			}
		}
	}

	public static void stopRefresh(XListView xListView) {
		if (xListView.getPullRefreshing()) {
			xListView.stopRefresh();
		}
	}

	public static void setUserView(View conView, AVUser user) {
//    ImageView avatarView = ViewHolder.findViewById(conView, R.id.avatar);
//    TextView nameView = ViewHolder.findViewById(conView, R.id.username);
////    UserService.displayAvatar(user.getAvatarUrl(), avatarView);
//    nameView.setText(user.getUsername());
	}
}
