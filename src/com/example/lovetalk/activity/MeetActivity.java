package com.example.lovetalk.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.example.lovetalk.R;
import com.example.lovetalk.adapter.MeetActivityAdapter;
import com.example.lovetalk.service.UserService;
import com.example.lovetalk.util.MyAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class MeetActivity extends BaseActivity {
	TextView times, myName, yournName;
	ImageView myAvatar, yourAvatar;
	ListView meetinginfo;
	MeetActivityAdapter adapter;
	AVUser yourUser;
	String userid;

	String updateAction = "com.example.lovetalk.update";

	public class UpdateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.d("lan", "更新");
			String action = intent.getAction();
			if (action.equals(updateAction)) {
				refresh();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meet);
		Intent intent = getIntent();
		userid = intent.getStringExtra("userid");
		initView();
		refresh();
	}

	private void refresh() {
		new MyAsyncTask(mContext) {
			List<AVObject> meet = new ArrayList<AVObject>();

			@Override
			protected void doInBack() throws Exception {
				AVQuery<AVObject> query = new AVQuery<AVObject>("_User");
				yourUser = (AVUser) query.get(userid);
				AVUser user = AVUser.getCurrentUser();
				AVRelation<AVObject> relation = user.getRelation("MeetingInfo");
				query = relation.getQuery();
				query.whereExists("YourUser");
				query.whereEqualTo("YourUser", yourUser);
				query.include("YourUser");
				query.orderByDescending("Time");
				query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);

				query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
				query.setLimit(1000);
				int count = query.count();

				for (int i = 0;i < count / 1000f + 1;i++) {
					query.skip(1000 * i);
					meet.addAll(query.find());
				}
			}

			@Override
			protected void onSucceed() {
				int num = meet.size();
				times.setText("总共相遇" + num + "次");
				updateView();
				adapter = new MeetActivityAdapter(context, meet);
				meetinginfo.setAdapter(adapter);
			}

		}.execute();
	}

	private void updateView() {
		AVUser Myuser = AVUser.getCurrentUser();
		String MyUrl = getAvatarUrl(Myuser);
		String MyName = Myuser.getUsername();

		String YourUrl = getAvatarUrl(yourUser);
		String YourName = yourUser.getUsername();

		UserService.displayAvatar(MyUrl, myAvatar);
		UserService.displayAvatar(YourUrl, yourAvatar);
		myName.setText(MyName);
		yournName.setText(YourName);
	}

	public String getAvatarUrl(AVUser user) {
		AVFile avatar = user.getAVFile("avatar");
		if (avatar != null) {
			return avatar.getUrl();
		} else {
			return null;
		}
	}

	private void initView() {
		initActionBar("擦肩而过");
		times = (TextView) findViewById(R.id.times);
		yournName = (TextView) findViewById(R.id.yournmae);
		myName = (TextView) findViewById(R.id.myname);

		myAvatar = (ImageView) findViewById(R.id.myavatar);
		yourAvatar = (ImageView) findViewById(R.id.youravatar);

		meetinginfo = (ListView) findViewById(R.id.datashow);

		yourAvatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PersonInfoActivity.goPersonInfo(mContext, userid);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public static void goMeetActivity(Context context, String userId) {
		Intent intent = new Intent(context, MeetActivity.class);
		intent.putExtra("userid", userId);
		context.startActivity(intent);
	}
}
