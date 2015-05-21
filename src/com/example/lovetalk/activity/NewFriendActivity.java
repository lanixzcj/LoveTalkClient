package com.example.lovetalk.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.avos.avoscloud.AVUser;
import com.example.lovetalk.R;
import com.example.lovetalk.adapter.NewFriendAdapter;
import com.example.lovetalk.service.AddRequest;
import com.example.lovetalk.service.AddRequestService;
import com.example.lovetalk.service.PreferenceMap;
import com.example.lovetalk.util.MyAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class NewFriendActivity extends BaseActivity implements OnItemLongClickListener {
	ListView listview;
	NewFriendAdapter adapter;
	List<AddRequest> addRequests = new ArrayList<AddRequest>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_new_friend_activity);
		initView();
		refresh();
	}

	private void refresh() {
		new MyAsyncTask(mContext) {
			List<AddRequest> subAddRequests;

			@Override
			protected void doInBack() throws Exception {
				subAddRequests = AddRequestService.findAddRequests();
			}

			@Override
			protected void onSucceed() {
				AVUser user = AVUser.getCurrentUser();
				String id = user.getObjectId();
				PreferenceMap preferenceMap = new PreferenceMap(context, id);
				preferenceMap.setAddRequestN(subAddRequests.size());
				adapter.addAll(subAddRequests);
			}

		}.execute();
	}

	private void initView() {
		initActionBar(R.string.new_friends);
		listview = (ListView) findViewById(R.id.newfriendList);
		listview.setOnItemLongClickListener(this);
		adapter = new NewFriendAdapter(this, addRequests);
		listview.setAdapter(adapter);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
								   long arg3) {
		// TODO Auto-generated method stub
		AddRequest invite = (AddRequest) adapter.getItem(position);
		showDeleteDialog(position, invite);
		return true;
	}

	public void showDeleteDialog(final int position, final AddRequest addRequest) {
		new AlertDialog.Builder(mContext).setMessage(R.string.deleteFriendRequest)
				.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deleteAddRequest(position, addRequest);
					}
				}).setNegativeButton(R.string.cancel, null).show();
	}

	private void deleteAddRequest(final int position, final AddRequest addRequest) {
		new MyAsyncTask(mContext) {

			@Override
			protected void doInBack() throws Exception {
				// TODO Auto-generated method stub
				addRequest.delete();
			}

			@Override
			protected void onSucceed() {
				adapter.remove(position);
			}
		}.execute();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
