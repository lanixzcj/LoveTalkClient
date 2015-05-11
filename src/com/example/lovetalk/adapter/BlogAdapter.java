package com.example.lovetalk.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class BlogAdapter extends BaseAdapter {
	private ArrayList<String> listBlog = null;
	private ArrayList<String> listTime = null;
	Context mContext = null;
	public BlogAdapter(ArrayList<String> blogList, ArrayList<String> blogTime , Context context) {
		listBlog = blogList;
		listTime = blogTime;
		mContext = context;
	}
	
	//用以构造显示在ListView中的TextView
	TextView getTextView() {
		//设置TextView的样式
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, 64);
        TextView textView = new TextView(
        		mContext);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        //设置TextView的Padding值
        textView.setPadding(16, -10, 0, 32);
        //设置TextView的字体大小
        textView.setTextSize(14);
        //设置TextView的字体颜色
        textView.setTextColor(Color.WHITE);
        //设置字体加粗
        TextPaint txt = textView.getPaint();
        txt.setFakeBoldText(true);
        return textView;
    }
	
	//用以构造显示在ListView中的ImageView
	ImageView getImageView() {
		ImageView imageview = new ImageView(
				mContext);
		LayoutParams params = new LayoutParams(24, 24);
		imageview.setLayoutParams(params);
		return imageview;
	}

	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listBlog.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listBlog.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		RelativeLayout ll = new RelativeLayout(mContext);
		ll.setLayoutParams(lp);
		ll.setGravity(Gravity.CENTER);
		
        RelativeLayout.LayoutParams lpblogTime = new RelativeLayout.LayoutParams(
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lpblogTime.setMargins(250, 10, 0, 0);
        final TextView blogTime = getTextView();
        blogTime.setLayoutParams(lpblogTime);
        blogTime.setTextColor(Color.RED);
        blogTime.setText(listTime.get(position).toString());
        blogTime.setTextSize(14);
        blogTime.setPadding(0, 0, 0, 0);
        
        RelativeLayout.LayoutParams lpblogContent = new RelativeLayout.LayoutParams(
        		600, LayoutParams.WRAP_CONTENT);
        lpblogContent.setMargins(0, 70, 0, 0);
        final TextView blogContent = getTextView();
        blogContent.setLayoutParams(lpblogContent);
        blogContent.setTextColor(Color.GRAY);
        blogContent.setText(listBlog.get(position).toString());
        blogContent.setTextSize(14);
        blogContent.setPadding(0, 0, 0, 0);
        
        ll.addView(blogTime);
        ll.addView(blogContent);
       
        ll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				blogContent.setTextColor(Color.BLACK);
			}
		});
        
        return ll;
	}
	
}