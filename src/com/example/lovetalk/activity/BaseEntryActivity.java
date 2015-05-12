package com.example.lovetalk.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.lovetalk.receiver.FinishReceiver;


public class BaseEntryActivity extends Activity {
	FinishReceiver mFinishReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFinishReceiver = FinishReceiver.register(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mFinishReceiver);
	}
}
