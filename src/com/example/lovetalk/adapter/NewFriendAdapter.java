package com.example.lovetalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.example.lovetalk.R;
import com.example.lovetalk.service.AddRequest;
import com.example.lovetalk.service.CloudService;
import com.example.lovetalk.service.UserService;
import com.example.lovetalk.util.MyAsyncTask;
import com.example.lovetalk.util.Utils;
import com.example.lovetalk.view.ViewHolder;

public class NewFriendAdapter extends BaseListAdapter<AddRequest> {

	public NewFriendAdapter(Context context, List<AddRequest> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View conView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (conView == null) {
			LayoutInflater mInflater = LayoutInflater.from(ctx);
			conView = mInflater.inflate(R.layout.contact_add_friend_item, null);
		}
		final AddRequest addRequest = datas.get(position);
		TextView nameView = ViewHolder.findViewById(conView, R.id.name);
		ImageView avatarView = ViewHolder.findViewById(conView, R.id.avatar);
		final Button addBtn = ViewHolder.findViewById(conView, R.id.add);

		String avatarUrl = getAvatarUrl(addRequest.getFromUser());
		UserService.displayAvatar(avatarUrl, avatarView);

		int status = addRequest.getStatus();
		if (status == AddRequest.STATUS_WAIT) {
			addBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					agreeAdd(addBtn, addRequest);
				}
			});
		} else if (status == AddRequest.STATUS_DONE) {
			toAgreedTextView(addBtn);
		}
		nameView.setText(addRequest.getFromUser().getUsername());
		return conView;
	}

	public String getAvatarUrl(AVUser user) {
		AVFile avatar = user.getAVFile("avatar");
		if (avatar != null) {
			return avatar.getUrl();
		} else {
			return null;
		}
	}

	public void toAgreedTextView(Button addBtn) {
		addBtn.setText(R.string.agreed);
		addBtn.setBackgroundDrawable(null);
		addBtn.setTextColor(Utils.getColor(R.color.base_color_text_black));
		addBtn.setEnabled(false);
	}

	private void agreeAdd(final Button addBtn, final AddRequest addRequest) {
		new MyAsyncTask(ctx) {
			@Override
			protected void doInBack() throws Exception {
				CloudService.agreeAddRequest(addRequest.getObjectId());
			}

			@Override
			protected void onPost(Exception e) {
				if (e != null) {
					Utils.toast(context, e.getMessage());
				} else {
					toAgreedTextView(addBtn);
				}
			}
		}.execute();
	}
}
