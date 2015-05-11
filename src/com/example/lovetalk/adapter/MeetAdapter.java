package com.example.lovetalk.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.LogUtil.log;
import com.example.lovetalk.R;
import com.example.lovetalk.activity.MeetActivity;
import com.example.lovetalk.service.UserService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.sax.StartElementListener;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MeetAdapter extends BaseAdapter{
	private List<HashMap<String, Object>> MeetList; 
	private Context context;
	public MeetAdapter(Context context, List<HashMap<String, Object>> MeetList){
		this.context = context;
		this.MeetList = MeetList;	
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return MeetList.size();
	}
	
	public static class ViewHolder
    {
       public ImageView meet_avatar = null;
       public TextView meet_times = null;
       public TextView meet_name = null;
    }
	
	public String getAvatarUrl(AVUser user) {
		AVFile avatar = user.getAVFile("avatar");
		if (avatar != null) {
			return avatar.getUrl();
		} else {
			return null;
		}
	}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		final Map<String, Object> item = new HashMap<String, Object>();
		
		AVUser user = (AVUser) MeetList.get(position).get("user");
		String name = user.getUsername();
		AVFile avatar = user.getAVFile("avatar");
		String avatarUrl = getAvatarUrl(user);
		String times = MeetList.get(position).get("times").toString();
		times = "相遇"+times+"次";
		
		item.put("name", name);
		item.put("avatar", avatarUrl);
		item.put("times", times);

		return item;

	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.meetinginfo_cell, null);
			viewHolder = new ViewHolder();

			viewHolder.meet_avatar = (ImageView) convertView.findViewById(R.id.avatar);
			viewHolder.meet_times = (TextView) convertView.findViewById(R.id.times);
			viewHolder.meet_name = (TextView) convertView.findViewById(R.id.name);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final Map<String, Object> map = (Map<String, Object>) getItem(position);
		viewHolder.meet_avatar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AVUser user = (AVUser) MeetList.get(position).get("user");
				String userid = user.getObjectId();
				// TODO Auto-generated method stub
				MeetActivity.goMeetActivity(context, userid);
			}
		});
		initItem(map, position, viewHolder);
		return convertView;
	}

	private void initItem(Map<String, Object> item,final int position, final ViewHolder viewHolder) 
	{
		
		if (item != null) {
			viewHolder.meet_times.setText(item.get("times").toString());
			viewHolder.meet_name.setText(item.get("name").toString());
			if(item.get("avatar") != null ){
				UserService.displayAvatar(item.get("avatar").toString(), viewHolder.meet_avatar);
			}
			
		}
	}
	
	
}