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
  EditText searchNameEdit;
  Button searchBtn;
  List<AVUser> users = new ArrayList<AVUser>();//change it first , then adapter
  XListView listView;
  AddFriendAdapter adapter;
  String searchName = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.contact_add_friend_activity);
    initView();
    search(searchName);
  }

  private void initView() {
    initActionBar(DemoApplication.context.getString(R.string.findFriends));
    searchNameEdit = (EditText) findViewById(R.id.searchNameEdit);
    searchBtn = (Button) findViewById(R.id.searchBtn);
    searchBtn.setOnClickListener(this);
    initXListView();
  }

  private void initXListView() {
    listView = (XListView) findViewById(R.id.searchList);
    listView.setPullLoadEnable(false);
    listView.setPullRefreshEnable(false);
    listView.setXListViewListener(this);

    adapter = new AddFriendAdapter(this, users);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    // TODO Auto-generated method stub
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.searchBtn:
        searchName = searchNameEdit.getText().toString();
        if (searchName != null) {
          adapter.clear();
          search(searchName);
        }
        break;
    }
  }

  private void search(String searchName) {
    searchUser(searchName, adapter.getCount(), new FindCallback<AVUser>() {
      @Override
      public void done(List<AVUser> users, AVException e) {
        stopLoadMore();
        if (e != null) {
          e.printStackTrace();
          Utils.toast(context, "网络错误");
        } else {
          Utils.handleListResult(listView, adapter, users);
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
    search(searchName);
  }

  private void stopLoadMore() {
    if (listView.getPullLoading()) {
      listView.stopLoadMore();
    }
  }
}
