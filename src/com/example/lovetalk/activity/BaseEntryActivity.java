package com.example.lovetalk.activity;

import com.example.lovetalk.receiver.FinishReceiver;

import android.app.Activity;
import android.os.Bundle;


public class BaseEntryActivity extends Activity {
  FinishReceiver finishReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    finishReceiver = FinishReceiver.register(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(finishReceiver);
  }
}
