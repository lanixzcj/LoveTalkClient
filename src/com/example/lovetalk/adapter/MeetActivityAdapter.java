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

public class MeetActivityAdapter extends BaseAdapter{
	private List<AVObject> MeetList; 
	private Context context;
	public MeetActivityAdapter(Context context, List<AVObject> MeetList){
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
       public ImageView meet_leftline = null;
       public TextView meet_time = null;
       public TextView meet_location = null;
       public TextView meet_num = null;
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
		
		String time = getTime(MeetList.get(position).getDate("Time"));
		String location = MeetList.get(position).getString("address");
		
		item.put("time", time);
		item.put("location", location);

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
			convertView = inflater.inflate(R.layout.meet_item, null);
			viewHolder = new ViewHolder();

			viewHolder.meet_leftline = (ImageView) convertView.findViewById(R.id.header);
			viewHolder.meet_time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.meet_location = (TextView) convertView.findViewById(R.id.location);
			viewHolder.meet_num = (TextView) convertView.findViewById(R.id.num);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final Map<String, Object> map = (Map<String, Object>) getItem(position);
		
		
		initItem(map, position, viewHolder);
		return convertView;
	}

	private void initItem(Map<String, Object> item,final int position, final ViewHolder viewHolder) 
	{
		
		if (item != null) {
			viewHolder.meet_time.setText(item.get("time").toString());
			viewHolder.meet_location.setText(item.get("location").toString());
			viewHolder.meet_num.setText(position+1+"");
			final int size = MeetList.size()-1;
			if(position == 0){
				viewHolder.meet_leftline.setBackgroundResource(R.drawable.meet_start);
			}else if(position == size){
				viewHolder.meet_leftline.setBackgroundResource(R.drawable.meet_end);
			}else{
				viewHolder.meet_leftline.setBackgroundResource(R.drawable.meet_middle);
			}
		}
	}
	
	private String getTime(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String time = sdf.format(date);
		
		return time;
	}
	
	
}