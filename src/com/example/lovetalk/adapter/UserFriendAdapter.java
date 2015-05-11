package com.example.lovetalk.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.example.lovetalk.R;
import com.example.lovetalk.service.UserService;
import com.example.lovetalk.util.CharacterParser;
import com.example.lovetalk.view.ViewHolder;

@SuppressLint("DefaultLocale")
public class UserFriendAdapter extends BaseAdapter implements SectionIndexer {
	private Context context;
	private List<AVUser> data;

	public UserFriendAdapter(Context context, List<AVUser> datas) {
		this.context = context;
		this.data = datas;
	}

	public void updateListView(List<AVUser> list) {
		this.data = list;
		notifyDataSetChanged();
	}

	public void remove(AVUser user) {
		this.data.remove(user);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_user_friend, null);
		}
		TextView alpha = ViewHolder.findViewById(convertView, R.id.alpha);
		TextView nameView = ViewHolder.findViewById(convertView,
				R.id.tv_friend_name);
		ImageView avatarView = ViewHolder.findViewById(convertView,
				R.id.img_friend_avatar);

		AVUser friend = data.get(position);
		final String name = friend.getUsername();
		
		final String avatarUrl = getAvatarUrl(friend);

		UserService.displayAvatar(avatarUrl,avatarView);
		nameView.setText(name);

		int section = getSectionForPosition(position);
		if (position == getPositionForSection(section)) {
			alpha.setVisibility(View.VISIBLE);
			alpha.setText(getSortLetters(friend));
		} else {
			alpha.setVisibility(View.GONE);
		}

		return convertView;
	}

	public int getSectionForPosition(int position) {
		return getSortLetters(data.get(position)).charAt(0);
	}
	public String getAvatarUrl(AVUser user) {
	    AVFile avatar = user.getAVFile("avatar");
	    if (avatar != null) {
	      return avatar.getUrl();
	    } else {
	      return null;
	    }
	  }
	@SuppressLint("DefaultLocale")
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = getSortLetters(data.get(i));
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	private String getSortLetters(AVUser user) {
		String username = user.getUsername();
		if (username != null) {
			String pinyin = CharacterParser.getPingYin(user.getUsername());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			if (sortString.matches("[A-Z]")) {
				return sortString.toUpperCase();
			} else {
				return "#";
			}
		} else {
			return "#";
		}
	}
}
