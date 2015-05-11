package com.example.lovetalk.receiver;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.SaveCallback;
import com.example.lovetalk.DemoApplication;
import com.example.lovetalk.fragment.MeetFragment;
import com.example.lovetalk.object.MeetingInfo;
import com.example.lovetalk.service.PreferenceMap;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class BluetoothReceiver extends BroadcastReceiver {
	int count = 0;
	String updateAction = "com.example.lovetalk.update";
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			count++;
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			final String YourAddress = device.getAddress();
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			final String MyAddress = adapter.getAddress();
			PreferenceMap preferenceMap = PreferenceMap
					.getCurUserPrefDao(DemoApplication.context);
			final String lastaddress = preferenceMap.getAddress();

			final AVObject meeting = new AVObject("Meeting");

			AVQuery<AVObject> query = new AVQuery<AVObject>("_User");
			query.findInBackground(new FindCallback<AVObject>() {

				@Override
				public void done(List<AVObject> arg0, AVException arg1) {
					// TODO Auto-generated method stub
					if (arg1 == null) {
						for (AVObject user : arg0) {
							String address = user.getString("MacAddress");
							if (address != null && address.equals(YourAddress)) {
								meeting.put("YourUser", user);
								meeting.put("Time", new Date());
								meeting.put("MyAddress", MyAddress);
								meeting.put("YourAddress", YourAddress);
								meeting.put("address", lastaddress);
								meeting.saveInBackground(new SaveCallback() {

									@Override
									public void done(AVException arg0) {
										// TODO Auto-generated method stub
										if (arg0 == null) {
											AVUser user = AVUser
													.getCurrentUser();
											AVRelation<AVObject> relation = user
													.getRelation("MeetingInfo");
											relation.add(meeting);
											user.saveInBackground(new SaveCallback() {
												@Override
												public void done(
														AVException arg0) {
													// TODO Auto-generated
													// method stub
													MeetingInfo.addMeeting(meeting);
													
													
													for(HashMap<String, Object> meeting:MeetingInfo.getList()){
														AVUser user = (AVUser)meeting.get("user");
														Log.d("lan", user.getUsername());
														Log.d("lan", meeting.get("times").toString());
													}
													
													log.e("lan",
															meeting.getString("YourAddress"));
												}
											});
										} else {
											Toast.makeText(context,
													arg0.getMessage(),
													Toast.LENGTH_SHORT).show();
											log.e("lan", arg0.getMessage());
										}
									}
								});
								log.e("lan", meeting.getString("YourAddress"));
							}
						}
					} else if (arg1 != null) {
						log.e("lan", arg1.getMessage());
					}
				}
			});

		}
		if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			log.e("lan", "" + count);
			if(count != 0){
				Intent intent2 = new Intent();
				intent2.setAction(updateAction);
				context.sendBroadcast(intent2);
				count = 0;
			}
		}
	}

}
