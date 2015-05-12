package com.example.lovetalk.service;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;


import com.avos.avoscloud.AVUser;
import com.example.lovetalk.DemoApplication;

import java.util.List;

/**
 * Created by lzw on 14-9-27.
 */
public class AddRequestService {
	public static int countAddRequests() throws AVException {
		AVQuery<AddRequest> q = AVObject.getQuery(AddRequest.class);
		q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
		q.whereEqualTo(AddRequest.TO_USER, AVUser.getCurrentUser());
		return q.count();
	}

	public static List<AddRequest> findAddRequests() throws AVException {
		AVUser user = AVUser.getCurrentUser();
		AVQuery<AddRequest> q = AVObject.getQuery(AddRequest.class);
		q.include(AddRequest.FROM_USER);
		q.whereEqualTo(AddRequest.TO_USER, user);
		q.orderByDescending("createdAt");
		q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
		return q.find();
	}

	public static boolean hasAddRequest() throws AVException {
		PreferenceMap preferenceMap = PreferenceMap.getMyPrefDao(DemoApplication.context);
		int addRequestN = preferenceMap.getAddRequestN();
		int requestN = countAddRequests();
		if (requestN > addRequestN) {
			return true;
		} else {
			return false;
		}
	}
}
