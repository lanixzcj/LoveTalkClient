package com.example.lovetalk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.example.lovetalk.DemoApplication;
import com.example.lovetalk.R;
import com.example.lovetalk.service.CloudService;
import com.example.lovetalk.service.UserService;
import com.example.lovetalk.util.MyAsyncTask;
import com.example.lovetalk.util.Utils;

import java.util.List;

public class PersonInfoActivity extends BaseActivity implements OnClickListener {
  public static final String USER_ID = "userId";
  TextView usernameView, genderView;
  ImageView avatarView, avatarArrowView;
  LinearLayout allLayout;
  Button chatBtn, addFriendBtn;
  RelativeLayout avatarLayout,  genderLayout;

  String userId = "";
  AVUser user;
  
  public static String[] genderStrings = new String[] {
		DemoApplication.context.getString(R.string.male),
		DemoApplication.context.getString(R.string.female) };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    //meizu?
    int currentApiVersion = Build.VERSION.SDK_INT;
    if (currentApiVersion >= 14) {
      getWindow().getDecorView().setSystemUiVisibility(
          View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    setContentView(R.layout.contact_person_info_activity);
    findView();
    initData();

  }

  private void initData() {
    userId = getIntent().getStringExtra(USER_ID);
    
    user = DemoApplication.lookupUser(userId);
    if(user == null){
    	AVQuery<AVObject> query = new AVQuery<AVObject>("_User");
    	query.getInBackground(userId, new GetCallback<AVObject>() {
			@Override
			public void done(AVObject arg0, AVException arg1) {
				user = (AVUser) arg0;
				// TODO Auto-generated method stub
				initView();
				return;
			}
		});
    }else{
    	initView();
    }
    
    
  }

  private void findView() {
    allLayout = (LinearLayout) findViewById(R.id.all_layout);
    avatarView = (ImageView) findViewById(R.id.avatar_view);
    avatarArrowView = (ImageView) findViewById(R.id.avatar_arrow);
    usernameView = (TextView) findViewById(R.id.username_view);
    avatarLayout = (RelativeLayout) findViewById(R.id.head_layout);
    genderLayout = (RelativeLayout) findViewById(R.id.sex_layout);

    genderView = (TextView) findViewById(R.id.sexView);
    chatBtn = (Button) findViewById(R.id.chatBtn);
    addFriendBtn = (Button) findViewById(R.id.addFriendBtn);
  }

  private void initView() {
    AVUser curUser = AVUser.getCurrentUser();
    if (curUser.equals(user)) {
      initActionBar(R.string.personalInfo);
      avatarLayout.setOnClickListener(this);
      genderLayout.setOnClickListener(this);
      avatarArrowView.setVisibility(View.VISIBLE);
      chatBtn.setVisibility(View.GONE);
      addFriendBtn.setVisibility(View.GONE);
    } else {
      initActionBar(R.string.detailInfo);
      avatarArrowView.setVisibility(View.INVISIBLE);
      try {
        List<AVUser> cacheFriends = UserService.findFriends(true);
        boolean isFriend = cacheFriends.contains(user);
        if (isFriend) {
          chatBtn.setVisibility(View.VISIBLE);
          chatBtn.setOnClickListener(this);
        } else {
          chatBtn.setVisibility(View.GONE);
          addFriendBtn.setVisibility(View.VISIBLE);
          addFriendBtn.setOnClickListener(this);
        }
      } catch (AVException e) {
        e.printStackTrace();
      }

    }
    updateView(user);
  }

  public static void goPersonInfo(Context ctx, String userId) {
    Intent intent = new Intent(ctx, PersonInfoActivity.class);
    intent.putExtra(USER_ID, userId);
    ctx.startActivity(intent);
  }
  
  public String getAvatarUrl(AVUser user) {
		AVFile avatar = user.getAVFile("avatar");
		if (avatar != null) {
			return avatar.getUrl();
		} else {
			return null;
		}
	}
  
  private void updateView(AVUser user) {
    String avatar = getAvatarUrl(user);
    UserService.displayAvatar(avatar, avatarView);
    usernameView.setText(user.getUsername());
    genderView.setText(genderStrings[user.getInt("gender")]);
  }

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    switch (v.getId()) {
      case R.id.chatBtn:// 发起聊天
//        ChatActivity.goUserChat(this, user.getObjectId());
    	Intent intent = new Intent();
    	intent.putExtra("tel", "18200156521");
    	intent.setClass(PersonInfoActivity.this, HomeActivity.class);
    	startActivity(intent);
        finish();
        break;
      case R.id.addFriendBtn:// 添加好友
        new MyAsyncTask(context) {
          @Override
          protected void doInBack() throws Exception {
            CloudService.tryCreateAddRequest(user);
          }

          @Override
          protected void onPost(Exception e) {
            if (e != null) {
              Utils.toast(context,e.getMessage());
            } else {
              Utils.toast(context,"已发出请求");
            }
          }
        }.execute();
        break;
    }
  }
}
