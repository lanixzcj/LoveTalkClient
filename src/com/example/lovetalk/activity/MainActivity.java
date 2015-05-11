package com.example.lovetalk.activity;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.example.lovetalk.R;
import com.example.lovetalk.R.layout;
import com.example.lovetalk.receiver.FinishReceiver;
import com.example.lovetalk.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.Window;



public class MainActivity extends BaseEntryActivity {
	private static final int GO_MAIN_MSG = 1;
	private static final int GO_LOGIN_MSG = 2;
	public static final int DURATION = 2000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		
		
		AVUser user = AVUser.getCurrentUser();
	    if (user != null) {
	      Utils.updateUserInfo();
	      handler.sendEmptyMessageDelayed(GO_MAIN_MSG, DURATION);
	    } else {
	      handler.sendEmptyMessageDelayed(GO_LOGIN_MSG, DURATION);
	    }
	  }

	  private Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	      switch (msg.what) {
	        case GO_MAIN_MSG:
	          HomeActivity.goHomeActivity(MainActivity.this);
	          finish();
	          break;
	        case GO_LOGIN_MSG:
	          Utils.goActivity(MainActivity.this, LoginActivity.class);
	          finish();
	          break;
	      }
	    }
	  };
		
}
