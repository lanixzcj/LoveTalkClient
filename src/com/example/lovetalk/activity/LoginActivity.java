package com.example.lovetalk.activity;

import com.avos.avoscloud.AVUser;
import com.example.lovetalk.R;
import com.example.lovetalk.receiver.FinishReceiver;
import com.example.lovetalk.util.MyAsyncTask;
import com.example.lovetalk.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends BaseEntryActivity implements OnClickListener,
		OnFocusChangeListener {
	private EditText enterLogin;
	private EditText enterPassword;
	private ImageView registebtn, loginbtn, forgetbtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		enterLogin = (EditText) findViewById(R.id.enterlogin);
		enterPassword = (EditText) findViewById(R.id.enterpassword);
		registebtn = (ImageView) findViewById(R.id.registebtn);
		loginbtn = (ImageView) findViewById(R.id.loginbtn);
		forgetbtn = (ImageView) findViewById(R.id.forget);

		enterLogin.setOnFocusChangeListener(this);
		enterPassword.setOnFocusChangeListener(this);
		registebtn.setOnClickListener(this);
		loginbtn.setOnClickListener(this);
		forgetbtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.registebtn:
			Utils.goActivity(this, RegisteActivity.class);
			break;
		case R.id.loginbtn:
//			if (validate()) {
				login();
//			}
			break;
		case R.id.forget:
			Utils.goActivity(this, ForgetActivity.class);
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus == true) {
			v.setBackgroundResource(R.drawable.notext);
		} else {
			switch (v.getId()) {
			case R.id.enterlogin:
				if (isEmpty(enterLogin)) {
					enterLogin.setBackgroundResource(R.drawable.logintext);
				}
				break;
			case R.id.enterpassword:
				if (isEmpty(enterPassword)) {
					enterPassword
							.setBackgroundResource(R.drawable.passwordtext);
				}
				break;
			}
		}
	}

	boolean isEmpty(EditText et) {
		return "".equals(et.getText().toString());
	}

	// 登录方法
	private void login() {
		final String email = enterLogin.getText().toString();
		final String pwd = enterPassword.getText().toString();
		
		new MyAsyncTask(this) {
			
			@Override
			protected void onPost(Exception e) {
				// TODO Auto-generated method stub
				if(e != null){
					Utils.toast(context, "登录失败:"+e.getMessage());
				}else{
					Utils.toast(context, "登录成功");
					Utils.goActivity(context, HomeActivity.class);
				}
			}
			
			@Override
			protected void doInBack() throws Exception {
				// TODO Auto-generated method stub
				AVUser.logIn(email, pwd);
			}
		}.execute();
	}

	// 验证方法
	private boolean validate() {
		String email = enterLogin.getText().toString();
		String pwd = enterPassword.getText().toString();
		if (email.equals("")) {
			Toast.makeText(getApplicationContext(), "用户名称是必填项！",
					Toast.LENGTH_LONG).show();
			return false;
		}

		if (pwd.equals("")) {
			Toast.makeText(getApplicationContext(), "用户密码是必填项！",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

}
