package com.example.lovetalk.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.FindCallback;
import com.example.lovetalk.R;
import com.example.lovetalk.adapter.BlogAdapter;
import com.example.lovetalk.adapter.BlogDateAdapter;
import com.example.lovetalk.util.MyAsyncTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItsBlogActivity extends Activity {
	private TextView showDate;
	private TextView showNumber;
	private TextView showNumberWord;
	private Button setDate;
	private int year;
	private int month;
	private int day;
	private String lPhone;
	private String chooseTime;
	private List<AVObject> allBlog;
	private Date time;
	private ListView list;
	private static int flagsTime = DateUtils.FORMAT_SHOW_TIME;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itsblog);

		mContext = ItsBlogActivity.this;

		Intent intent = getIntent();
		lPhone = intent.getStringExtra("ITSPHONE");

		showDate = (TextView) findViewById(R.id.showTime);
		showNumber = (TextView) findViewById(R.id.number);
		showNumberWord = (TextView) findViewById(R.id.numberWord);
		setDate = (Button) findViewById(R.id.setDate);
		list = (ListView) findViewById(R.id.seeBlog);

		//初始化Calendar日历对象
		Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
		Date myDate = new Date(); //获取当前日期Date对象
		mycalendar.setTime(myDate);////为Calendar对象设置时间为当前日期

		year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
		month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
		day = mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
		showDate.setText("当前日志日期：" + year + "-" + (month + 1) + "-" + day); //显示当前的年月日

		//添加单击事件--设置日期
		setDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//创建DatePickerDialog对象
				DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
						Datelistener, year, month, day);
				datePickerDialog.show();//显示DatePickerDialog组件
			}
		});


		toQuery();
	}

	private DatePickerDialog.OnDateSetListener Datelistener =
			new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int myYear, int monthOfYear, int dayOfMonth) {
					//修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
					year = myYear;
					month = monthOfYear;
					day = dayOfMonth;
					//更新日期
					chooseTime = year + "-" + (month + 1) + "-" + day;
					updateDate();

				}

				//当DatePickerDialog关闭时，更新日期显示
				private void updateDate() {
					//在TextView上显示日期

					new MyAsyncTask(mContext, false) {

						@Override
						protected void doInBack() throws Exception {
							// TODO Auto-generated method stub

							AVQuery<AVObject> query = new AVQuery<AVObject>("_User");
							query.whereEqualTo("mobilePhoneNumber", lPhone);
							query.findInBackground(new FindCallback<AVObject>() {
								public void done(List<AVObject> avObjects, AVException e) {
									if (e == null) {
										Log.d("成功", "查询到 " + avObjects.size() + " 条符合条件的数据");
										AVRelation<AVObject> relation = avObjects.get(0).getRelation("unrequitedLoveInfo");
										relation.getQuery().findInBackground(new FindCallback<AVObject>() {
											public void done(List<AVObject> results, AVException e) {
												if (e != null) {
													Log.d("错误", "查询错误: " + e.getMessage());
												} else {
													ArrayList<String> blogList = new ArrayList<String>();
													ArrayList<String> blogListTime = new ArrayList<String>();
													allBlog = results;
													Log.d("成功", "查询到 " + allBlog.size() + " 条关联数据");
													String blogAllContent = "";
													for (int i = 0; i < allBlog.size(); i++) {
														time = allBlog.get(i).getDate("Time");
														Long timeLong = time.getTime();

														String blogTimeAll = (String) DateUtils.formatDateTime(mContext,
																timeLong, flagsTime);
														String blogTime = ConverToString(time);

														if (chooseTime.equals(blogTime)) {
															blogList.add(allBlog.get(i).getString("DailyLog"));
															blogListTime.add(blogTimeAll);
															blogAllContent += allBlog.get(i).getString("DailyLog");
														}
													}

													showNumber.setText("共：" + blogList.size() + " 条日志");
													showNumberWord.setText("共：" + blogAllContent.length() + " 个字的情话");

													BlogAdapter blogAdapter = new BlogAdapter(blogList,
															blogListTime, mContext);
													list.setAdapter(blogAdapter);
												}
											}
										});
									} else {
										Toast.makeText(mContext, "网络错误", Toast.LENGTH_LONG).show();
										Log.d("失败", "查询错误: " + e.getMessage());
									}
								}
							});

							showDate.setText("当前日志日期：" + year + "-" + (month + 1) + "-" + day);

						}

						@Override
						protected void onSucceed() {

						}
					}.execute();
				}
			};

	public void toQuery() {
		new MyAsyncTask(mContext, false) {

			@Override
			protected void doInBack() throws Exception {
				// TODO Auto-generated method stub
				AVQuery<AVObject> query = new AVQuery<AVObject>("_User");
				query.whereEqualTo("mobilePhoneNumber", lPhone);
				query.findInBackground(new FindCallback<AVObject>() {
					public void done(List<AVObject> avObjects, AVException e) {
						if (e == null) {
							Log.d("成功", "查询到 " + avObjects.size() + " 条符合条件的数据");
							AVRelation<AVObject> relation = avObjects.get(0).getRelation("unrequitedLoveInfo");
							relation.getQuery().findInBackground(new FindCallback<AVObject>() {
								public void done(List<AVObject> results, AVException e) {
									if (e != null) {
										Log.d("错误", "查询错误: " + e.getMessage());
									} else {
										allBlog = results;
										Log.d("成功", "查询到 " + allBlog.size() + " 条关联数据");

										String blogAllContent = "";

										ArrayList<String> blogList = new ArrayList<String>();
										ArrayList<String> blogListTime = new ArrayList<String>();

										for (int i = 0; i < allBlog.size(); i++) {
											time = allBlog.get(i).getDate("Time");

											String blogTime = ConverToString(time);
											blogList.add("点击查看内容");
											blogListTime.add(blogTime);
											blogAllContent = blogAllContent + allBlog.get(i).getString("DailyLog");
										}

										showNumber.setText("共：" + allBlog.size() + " 条日志");
										showNumberWord.setText("共：" + blogAllContent.length() + " 个字的情话");

										final BlogDateAdapter blogDateAdapter = new BlogDateAdapter(blogList,
												blogListTime, mContext);
										list.setAdapter(blogDateAdapter);
										list.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(AdapterView<?> parent,
																	View view, int position, long id) {
												// TODO Auto-generated method stub
												String blogChooseTime = blogDateAdapter.getItem(position).toString();
												String blogAllContent = "";
												showDate.setText("当前日志日期：" + blogChooseTime);

												ArrayList<String> blogList = new ArrayList<String>();
												ArrayList<String> blogListTime = new ArrayList<String>();

												for (int i = 0; i < allBlog.size(); i++) {
													time = allBlog.get(i).getDate("Time");
													Long timeLong = time.getTime();
													String blogTime = ConverToString(time);
													String blogTimeAll = (String) DateUtils.formatDateTime(mContext,
															timeLong, flagsTime);

													if (blogChooseTime.equals(blogTime)) {
														blogList.add(allBlog.get(i).getString("DailyLog"));
														blogListTime.add(blogTimeAll);
														blogAllContent += allBlog.get(i).getString("DailyLog");
													}
												}

												showNumber.setText("共：" + blogList.size() + " 条日志");
												showNumberWord.setText("共：" + blogAllContent.length() + " 个字的情话");

												BlogAdapter blogAdapter = new BlogAdapter(blogList, blogListTime, mContext);
												list.setAdapter(blogAdapter);
											}

										});
									}
								}
							});
						} else {
							Toast.makeText(mContext, "网络错误", Toast.LENGTH_LONG).show();
							Log.d("失败", "查询错误: " + e.getMessage());
						}
					}
				});
			}

			@Override
			protected void onSucceed() {

			}
		}.execute();

	}

	public static String ConverToString(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date);
	}

}