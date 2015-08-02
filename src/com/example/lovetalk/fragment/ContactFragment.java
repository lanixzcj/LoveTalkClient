package com.example.lovetalk.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.example.lovetalk.DemoApplication;
import com.example.lovetalk.R;
import com.example.lovetalk.activity.AddFriendActivity;
import com.example.lovetalk.activity.ChatActivity;
import com.example.lovetalk.activity.NewFriendActivity;
import com.example.lovetalk.adapter.UserFriendAdapter;
import com.example.lovetalk.service.AddRequestService;
import com.example.lovetalk.service.CloudService;
import com.example.lovetalk.service.UserService;
import com.example.lovetalk.util.CharacterParser;
import com.example.lovetalk.util.MyAsyncTask;
import com.example.lovetalk.util.PinyinComparator;
import com.example.lovetalk.util.Utils;
import com.example.lovetalk.view.EnLetterView;
import com.example.lovetalk.view.HeaderLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactFragment extends BaseFragment implements
		OnItemClickListener, OnItemLongClickListener, OnClickListener {
	TextView dialog;
	ListView friendsList;
	EnLetterView rightLetter;
	UserFriendAdapter userAdapter;
	List<AVUser> friends = new ArrayList<AVUser>();
	HeaderLayout headerLayout;
	ImageView msgTipsView;
	LinearLayout newFriendLayout, groupLayout;

	PinyinComparator pinyinComparator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.contact_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		init();
		refresh();
	}

	private void init() {
		pinyinComparator = new PinyinComparator();
		headerLayout = (HeaderLayout) getView().findViewById(R.id.headerLayout);
		headerLayout.showTitle(DemoApplication.context
				.getString(R.string.contact));
		headerLayout.showRightImageButton(
				R.drawable.base_action_bar_add_bg_selector,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Utils.goActivity(context, AddFriendActivity.class);
					}
				});
		initListView();
		initRightLetterView();

	}

	private void fillFriendsData(List<AVUser> datas) {
		friends.clear();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			AVUser user = datas.get(i);
			AVUser sortUser = new AVUser();
			sortUser.put("avatar", user.getAVFile("avatar"));
			sortUser.setUsername(user.getUsername());
			sortUser.setObjectId(user.getObjectId());
			String username = sortUser.getUsername();
			if (username != null) {
				String pinyin = CharacterParser.getPingYin(user.getUsername());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				if (sortString.matches("[A-Z]")) {
					Log.d("lan", sortString.toUpperCase());
				} else {
					Log.d("lan", "#");
				}
			} else {
				Log.d("lan", "#");
			}
			friends.add(sortUser);
		}
		Collections.sort(friends, pinyinComparator);
	}

	private void initListView() {
		friendsList = (ListView) getView().findViewById(R.id.list_friends);
		LayoutInflater mInflater = LayoutInflater.from(context);
		RelativeLayout headView = (RelativeLayout) mInflater.inflate(
				R.layout.contact_include_new_friend, null);
		msgTipsView = (ImageView) headView.findViewById(R.id.iv_msg_tips);
		newFriendLayout = (LinearLayout) headView.findViewById(R.id.layout_new);


		newFriendLayout.setOnClickListener(this);

		friendsList.addHeaderView(headView);
		userAdapter = new UserFriendAdapter(getActivity(), friends);
		friendsList.setAdapter(userAdapter);
		friendsList.setOnItemClickListener(this);
		friendsList.setOnItemLongClickListener(this);
		friendsList.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Utils.hideSoftInputView(getActivity());
				return false;
			}
		});
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		if (isVisibleToUser) {
			// loadMsgsFromDB();
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void initRightLetterView() {
		rightLetter = (EnLetterView) getView().findViewById(R.id.right_letter);
		dialog = (TextView) getView().findViewById(R.id.dialog);
		rightLetter.setTextView(dialog);
		rightLetter.setOnTouchingLetterChangedListener(new
				LetterListViewListener());
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if (viewId == R.id.layout_new) {
			Utils.goActivity(context, NewFriendActivity.class);
		}
	}

	private class LetterListViewListener implements
			EnLetterView.OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(String s) {
			int position = userAdapter.getPositionForSection(s.charAt(0));
			if (position != -1) {
				friendsList.setSelection(position);
			}
		}
	}

	private void setAddRequestTipsAndListView(boolean hasAddRequest,
											  List<AVUser> friends) {
		msgTipsView.setVisibility(hasAddRequest ? View.VISIBLE : View.GONE);

		fillFriendsData(friends);
		if (userAdapter == null) {
			userAdapter = new UserFriendAdapter(getActivity(), friends);
			friendsList.setAdapter(userAdapter);
		} else {
			userAdapter.notifyDataSetChanged();
		}
	}

	private boolean hidden;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
	}

	public void refresh() {
		new MyAsyncTask(context, false) {
			boolean haveAddRequest;
			List<AVUser> friends;

			@Override
			protected void doInBack() throws Exception {
				// TODO Auto-generated method stub
				haveAddRequest = AddRequestService.hasAddRequest();
				friends = UserService.findFriends();
			}

			@Override
			protected void onSucceed() {
				setAddRequestTipsAndListView(haveAddRequest, friends);
			}
		}.execute();
		//
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
							long arg3) {
		//TODO Auto-generated method stub
		AVUser user = (AVUser) arg0.getAdapter().getItem(position);
		ChatActivity.goUserChat(getActivity(), user.getObjectId());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
								   int position, long arg3) {
		// TODO Auto-generated method stub
		AVUser user = (AVUser) userAdapter.getItem(position - 1);
		showDeleteDialog(user);
		return true;
	}

	public void showDeleteDialog(final AVUser user) {
		new AlertDialog.Builder(context)
				.setMessage(R.string.deleteContact)
				.setPositiveButton(R.string.sure,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								deleteFriend(user);
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	private void deleteFriend(final AVUser user) {
		new MyAsyncTask(context) {

			@Override
			protected void doInBack() throws Exception {
				// TODO Auto-generated method stub
				AVUser curUser = AVUser.getCurrentUser();
				CloudService.removeFriendForBoth(curUser, user);
			}

			@Override
			protected void onSucceed() {
				Utils.toast("删除成功");
				userAdapter.remove(user);
			}
		};
	}
}
