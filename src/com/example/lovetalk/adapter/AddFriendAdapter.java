package com.example.lovetalk.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.example.lovetalk.R;
import com.example.lovetalk.service.AddRequestService;
import com.example.lovetalk.view.ViewHolder;

import java.util.List;

public class AddFriendAdapter extends BaseListAdapter<AVUser> {
	Context mContext;

	public AddFriendAdapter(Context context, List<AVUser> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	public View getView(int position, View conView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (conView == null) {
			conView = inflater.inflate(R.layout.contact_add_friend_item, null);
		}
		final AVUser user = datas.get(position);
		TextView nameView = ViewHolder.findViewById(conView, R.id.name);
		ImageView avatarView = ViewHolder.findViewById(conView, R.id.avatar);
		Button addBtn = ViewHolder.findViewById(conView, R.id.add);
//    String avatarUrl = contact.getAvatarUrl();
//    UserService.displayAvatar(avatarUrl, avatarView);
		nameView.setText(user.getUsername());
		addBtn.setText(R.string.add);
		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddRequestService.createAddRequestInBackground(mContext, user);
			}
		});
		return conView;
	}

}
