package com.example.lovetalk.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.lovetalk.DemoApplication;
import com.example.lovetalk.R;
import com.example.lovetalk.adapter.AddFriendAdapter;
import com.example.lovetalk.util.Utils;
import com.example.lovetalk.view.xlist.XListView;

import java.util.ArrayList;
import java.util.List;

public class AddFriendActivity extends BaseActivity implements OnClickListener, XListView.IXListViewListener, OnItemClickListener {
	private EditText mSearchNameEdit;
	private Button mSearchBtn;
	private List<AVUser> mUsers = new ArrayList<AVUser>();//change it first , then adapter
	private XListView mListView;
	private AddFriendAdapter mAdapter;
	private String mSearchName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_add_friend_activity);
		initView();
		search(mSearchName);
	}

	private void initView() {
		initActionBar(DemoApplication.context.getString(R.string.findFriends));
		mSearchNameEdit = (EditText) findViewById(R.id.searchNameEdit);
		mSearchBtn = (Button) findViewById(R.id.searchBtn);
		mSearchBtn.setOnClickListener(this);
		initXListView();
	}

	private void initXListView() {
		mListView = (XListView) findViewById(R.id.searchList);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);

		mAdapter = new AddFriendAdapter(this, mUsers);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.searchBtn:
				mSearchName = mSearchNameEdit.getText().toString();
				if (mSearchName != null) {
					mAdapter.clear();
					search(mSearchName);
				}
				break;
		}
	}

	private void search(String searchName) {
		searchUser(searchName, mAdapter.getCount(), new FindCallback<AVUser>() {
			@Override
			public void done(List<AVUser> users, AVException e) {
				stopLoadMore();
				if (e != null) {
					e.printStackTrace();
					Utils.toast(mContext, "网络错误");
				} else {
					Utils.handleListResult(mListView, mAdapter, users);
				}
			}
		});
	}

	public void searchUser(String searchName, int skip,
						   FindCallback<AVUser> findCallback) {
		AVQuery<AVUser> q = AVUser.getQuery(AVUser.class);
		q.whereContains("username", searchName);
		q.limit(10);
		q.skip(skip);
		AVUser user = AVUser.getCurrentUser();
		List<String> friendIds = getFriendIds();
		friendIds.add(user.getObjectId());
		q.whereNotContainedIn("objectId", friendIds);
		q.orderByDescending("updateAt");
		q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
		q.findInBackground(findCallback);
	}

	private static List<String> getFriendIds() {
		List<AVUser> friends = DemoApplication.context.getFriends();
		List<String> ids = new ArrayList<String>();
		for (AVUser friend : friends) {
			ids.add(friend.getObjectId());
		}
		return ids;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		search(mSearchName);
	}

	private void stopLoadMore() {
		if (mListView.getPullLoading()) {
			mListView.stopLoadMore();
		}
	}
}
