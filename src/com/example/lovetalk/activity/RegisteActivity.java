package com.example.lovetalk.activity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.avos.avoscloud.AVUser;
import com.example.lovetalk.R;
import com.example.lovetalk.util.MyAsyncTask;
import com.example.lovetalk.util.Utils;

public class RegisteActivity extends BaseEntryActivity implements OnClickListener,
		OnFocusChangeListener {
	private EditText emailtext, passtext, pass2text, mobiletext, usernametext;
	private ImageView back, next;
	private RadioGroup sexgGroup;
	private final static int MALE = 0, FEMALE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registe);

		usernametext = (EditText) findViewById(R.id.usernametext);
		emailtext = (EditText) findViewById(R.id.emailtext);
		passtext = (EditText) findViewById(R.id.passtext);
		pass2text = (EditText) findViewById(R.id.pass2text);
		mobiletext = (EditText) findViewById(R.id.mobiletext);
		back = (ImageView) findViewById(R.id.back);
		next = (ImageView) findViewById(R.id.next);
		sexgGroup = (RadioGroup) findViewById(R.id.sex);

		back.setOnClickListener(this);
		next.setOnClickListener(this);
		usernametext.setOnFocusChangeListener(this);
		emailtext.setOnFocusChangeListener(this);
		passtext.setOnFocusChangeListener(this);
		pass2text.setOnFocusChangeListener(this);
		mobiletext.setOnFocusChangeListener(this);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus == true) {
			switch (v.getId()) {
				case R.id.emailtext:
				case R.id.mobiletext:
				case R.id.usernametext:
					v.setBackgroundResource(R.drawable.renotext);
					break;
				case R.id.passtext:
				case R.id.pass2text:
					v.setBackgroundResource(R.drawable.pasnotext);
					break;
			}
		} else {
			switch (v.getId()) {
				case R.id.usernametext:
					if (isEmpty(usernametext)) {
						usernametext.setBackgroundResource(R.drawable.registeusername);
					} else {
						emailvalidate();
					}
					break;
				case R.id.emailtext:
					if (isEmpty(emailtext)) {
						emailtext.setBackgroundResource(R.drawable.emailtext);
					} else {
						emailvalidate();
					}
					break;
				case R.id.mobiletext:
					if (isEmpty(mobiletext)) {
						mobiletext.setBackgroundResource(R.drawable.mobiletext);
					} else {
						mobilevalidate();
					}
					break;
				case R.id.passtext:
					if (isEmpty(passtext)) {
						passtext.setBackgroundResource(R.drawable.pass);
					} else {
						passvalidate();
					}
					break;
				case R.id.pass2text:
					if (isEmpty(pass2text)) {
						pass2text.setBackgroundResource(R.drawable.pass2);
					} else {
						passvalidate();
					}
					break;
			}
		}
	}

	boolean isEmpty(EditText et) {
		return "".equals(et.getText().toString());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.next:
				if (emailvalidate() && passvalidate() && mobilevalidate()) {
					register();

				}
				break;

		}
	}

	// 拼写检测，检测输入内容是否合乎要求
	public boolean emailvalidate() {
		String email = emailtext.getText().toString();

		if ("".equals(email)) {
			emailtext.setError("邮箱不能为空");
			return false;
		} else {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]"
					+ "@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			if (!email.matches(check)) {
				emailtext.setError("邮箱格式不合法");
				return false;
			}
		}
		return true;
	}

	public boolean passvalidate() {
		String pass = passtext.getText().toString();
		String pass2 = pass2text.getText().toString();

		if ("".equals(pass)) {
			passtext.setError("密码不能为空");
			return false;
		} else {
			if (pass.length() < 6 || pass.length() > 16) {
				passtext.setError("密码长度要在6位到16位之间");
				return false;
			}
			if ("".equals(pass2)) {
				pass2text.setError("请再次输入密码");
				return false;
			} else {
				if (pass2.length() < 6 || pass2.length() > 16) {
					pass2text.setError("密码长度要在6位到16位之间");
					return false;
				}
				if (!pass.equals(pass2)) {
					pass2text.setError("两次密码不相同");
					return false;
				}
			}
		}
		return true;
	}

	public boolean mobilevalidate() {
		String mobile = mobiletext.getText().toString();

		if ("".equals(mobile)) {
			mobiletext.setError("电话不能为空");
			return false;
		} else {
			String match = "^((13[0-9])|(15[^4,\\D])|(18[0,2,5-9]))\\d{8}$";
			if (!mobile.matches(match)) {
				mobiletext.setError("电话格式不合法");
				return false;
			}
		}
		return true;
	}


	private void register() {
		new MyAsyncTask(this) {

			@Override
			protected void doInBack() throws Exception {
				// TODO Auto-generated method stub
				String username = usernametext.getText().toString();
				String email = emailtext.getText().toString();
				String pwd = passtext.getText().toString();
				String mobile = mobiletext.getText().toString();
				int checked = sexgGroup.getCheckedRadioButtonId();
				int sex = checked == R.id.male ? MALE : FEMALE;

				BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
				if (!adapter.isEnabled()) {
					adapter.enable();
				}
				String MyAddress = adapter.getAddress();

				AVUser user = new AVUser();
				user.setUsername(username);
				user.setEmail(email);
				user.setPassword(pwd);
				user.put("gender", sex);
				user.put("mobilePhoneNumber", mobile);
				user.put("MacAddress", MyAddress);
				user.signUp();
			}

			@Override
			protected void onSucceed() {
				Utils.toast("注册成功");
				HomeActivity.goHomeActivity(RegisteActivity.this);
			}
		}.execute();
	}

}
