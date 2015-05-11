package com.example.lovetalk.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;

public class MeetingInfo {
	static List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
	Context context;
	public MeetingInfo(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		list = new ArrayList<HashMap<String,Object>>();
	}
	
	public static void addMeeting(AVObject meetinfo){
		AVUser adduser = meetinfo.getAVUser("YourUser");
		String username = adduser.getUsername();
		for(HashMap<String, Object> meet :list){
			AVUser user = (AVUser) meet.get("user");
			String name = user.getUsername();
			if(name.equals(username)){
				int times = Integer.valueOf(meet.get("times").toString());
				meet.put("times", times+1);
				return;
			}
		}
		
		HashMap<String, Object> newuser = new HashMap<String, Object>();
		newuser.put("user", adduser);
		newuser.put("times", 1);
		
		list.add(newuser);
	}
	
	public static List<HashMap<String, Object>> getList(){
		return list;
	}
	
	public static  void Clear(){
		list.clear();
	}
}
