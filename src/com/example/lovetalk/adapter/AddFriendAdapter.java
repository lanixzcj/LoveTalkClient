package com.example.lovetalk.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.avos.avoscloud.AVUser;
import com.example.lovetalk.R;
import com.example.lovetalk.service.CloudService;
import com.example.lovetalk.util.MyAsyncTask;
import com.example.lovetalk.util.Utils;
import com.example.lovetalk.view.ViewHolder;

public class AddFriendAdapter extends BaseListAdapter<AVUser> {
  public AddFriendAdapter(Context context, List<AVUser> list) {
    super(context, list);
    // TODO Auto-generated constructor stub
  }

  @Override
  public View getView(int position, View conView, ViewGroup parent) {
    // TODO Auto-generated method stub
    if (conView == null) {
      conView = inflater.inflate(R.layout.contact_add_friend_item, null);
    }
    final AVUser contact = datas.get(position);
    TextView nameView = ViewHolder.findViewById(conView, R.id.name);
    ImageView avatarView = ViewHolder.findViewById(conView, R.id.avatar);
    Button addBtn = ViewHolder.findViewById(conView, R.id.add);
//    String avatarUrl = contact.getAvatarUrl();
//    UserService.displayAvatar(avatarUrl, avatarView);
    nameView.setText(contact.getUsername());
    addBtn.setText(R.string.add);
    addBtn.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        new MyAsyncTask(ctx) {
          @Override
          protected void doInBack() throws Exception {
            CloudService.tryCreateAddRequest(contact);
          }

          @Override
          protected void onPost(Exception e) {
            if (e != null) {
              Utils.toast(context,e.getMessage());
            } else {
              Utils.toast(context,"已经发出请求");
            }
          }
        }.execute();
      }
    });
    return conView;
  }

}
