package com.example.lovetalk.activity;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.example.lovetalk.R;
import com.example.lovetalk.util.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ForgetActivity extends Activity implements OnClickListener,OnFocusChangeListener{
	private EditText mail;
	private Button sendbtn;
	private ImageView back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forget);
		
		
		mail = (EditText) findViewById(R.id.email);
		back = (ImageView) findViewById(R.id.back);
		sendbtn = (Button) findViewById(R.id.send);

		
		sendbtn.setOnClickListener(this);
		back.setOnClickListener(this);
		mail.setOnFocusChangeListener(this);

	}
	
	private boolean Mailvalidate(EditText mail) {
		String email = mail.getText().toString();
		if ("".equals(email)) {
			mail.setError("邮箱不能为空");
			return false;
		} else {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]"
					+ "@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			if (!email.matches(check)) {
				mail.setError("邮箱格式不合法");
				return false;
			}
		}
		
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.send:
			if(Mailvalidate(mail)){
				SendMail();
			}
			break;
		case R.id.back:
			finish();
			break;
		}
	}
	
	private void SendMail(){
		String email = mail.getText().toString();
		
		AVUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
			
			@Override
			public void done(AVException arg0) {
				// TODO Auto-generated method stub
				if(arg0 == null){
					Utils.toast(ForgetActivity.this, "发送成功");
					finish();
				}else{
					Utils.toast(ForgetActivity.this, "发送失败:"+arg0.getMessage());
					log.e("lan", arg0.getMessage());
				}
			}
		});
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus == true) {
			v.setBackgroundResource(R.drawable.renotext);
		} else {
			switch (v.getId()) {
			case R.id.email:
				if (isEmpty(mail)) {
					mail.setBackgroundResource(R.drawable.emailtext);
				}
				break;
			}
		}
	}
	
	boolean isEmpty(EditText et) {
		return "".equals(et.getText().toString());
	}

}