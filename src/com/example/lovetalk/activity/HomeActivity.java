package com.example.lovetalk.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil.log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.lovetalk.DemoApplication;
import com.example.lovetalk.R;
import com.example.lovetalk.fragment.ContactFragment;
import com.example.lovetalk.fragment.LoveFragment;
import com.example.lovetalk.fragment.MeetFragment;
import com.example.lovetalk.fragment.MySpaceFragment;
import com.example.lovetalk.receiver.BluetoothReceiver;
import com.example.lovetalk.receiver.FinishReceiver;
import com.example.lovetalk.service.ChatService;
import com.example.lovetalk.service.PreferenceMap;
import com.example.lovetalk.util.Utils;


public class HomeActivity extends BaseActivity {
	private BluetoothAdapter mBTadapter;
	private BluetoothReceiver mBTreceiver;
	public LocationClient mLocClient;
	public MyLocationListener mLocationListener;
	public static final int DURATION = 30000;

	private Handler handler = new Handler();
	private Button mConversationBtn, contactBtn, mDiscoverBtn, mySpaceBtn;
	private View fragmentContainer;
	private ContactFragment contactFragment;
	private MeetFragment discoverFragment;
	private LoveFragment conversationFragment;
	private MySpaceFragment mySpaceFragment;
	public static final int FRAGMENT_N = 4;
	Button[] tabs;
	public static final int[] tabsNormalBackIds = new int[]{
			R.drawable.tabbar_chat, R.drawable.tabbar_contacts,
			R.drawable.tabbar_discover, R.drawable.tabbar_me};
	public static final int[] tabsActiveBackIds = new int[]{
			R.drawable.tabbar_chat_active, R.drawable.tabbar_contacts_active,
			R.drawable.tabbar_discover_active, R.drawable.tabbar_me_active};
	View recentTips, contactTips;

	public static void goHomeActivity(Activity activity) {
		Intent intent = new Intent(activity, HomeActivity.class);
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_activity);
//		text = (TextView) findViewById(R.id.textView1);
//		btn = (Button) findViewById(R.id.button1);

		FinishReceiver.broadcast(this);
		mBTadapter = BluetoothAdapter.getDefaultAdapter();

		if (!mBTadapter.isEnabled()) {
			mBTadapter.enable();
		}
		Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
		startActivity(enable);

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!mBTadapter.isEnabled()) {
					mBTadapter.enable();
				}
				mBTadapter.startDiscovery();
				handler.postDelayed(this, DURATION);
			}
		}, DURATION);

		initBaiduLocClient();
		mBTreceiver = new BluetoothReceiver();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mBTreceiver, filter);

		findView();
		init();

		//mySpaceBtn.performClick();
		//contactBtn.performClick();
		mConversationBtn.performClick();

//	    UpdateService updateService = UpdateService.getInstance(this);
//	    updateService.checkUpdate();
		DemoApplication.registerUserCache(AVUser.getCurrentUser());
		ChatService.openSession(this);
//		btn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				AVUser user = AVUser.getCurrentUser();
//				AVRelation<AVObject> relation = user.getRelation("MeetingInfo");
//				AVQuery<AVObject> query = relation.getQuery();
//				query.whereExists("YourUser");
//				query.include("YourUser");
//				query.findInBackground(new FindCallback<AVObject>() {
//					
//					@Override
//					public void done(List<AVObject> arg0, AVException arg1) {
//						// TODO Auto-generated method stub
//						if(arg1 == null){
//							for(AVObject meeting:arg0){
//								AVUser user = meeting.getAVUser("YourUser");
//								log.e("lan", user.getUsername());
//							}
//						}
//					}
//				});
//				AVUser.logOut();
//				finish();
//				
//			}
//		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String str = intent.getStringExtra("tel");
		log.d("lan", str);
	}

	private void initBaiduLocClient() {
		mLocClient = new LocationClient(this.getApplicationContext());
		mLocClient.setDebug(true);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		option.setScanSpan(5000);
		option.setIsNeedAddress(false);
		option.setCoorType("bd09ll");
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);

		mLocationListener = new MyLocationListener();
		mLocClient.registerLocationListener(mLocationListener);
		mLocClient.start();
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			int locType = location.getLocType();
			log.d("lan", "onReceiveLocation latitude=" + latitude + " longitude="
					+ longitude + " locType=" + locType + " address="
					+ location.getAddrStr());
			AVUser user = AVUser.getCurrentUser();
			PreferenceMap preferenceMap = new PreferenceMap(HomeActivity.this,
					user.getObjectId());
			if (user != null) {
				AVGeoPoint avGeoPoint = preferenceMap.getLocation();
				if (avGeoPoint != null
						&& avGeoPoint.getLatitude() == location.getLatitude()
						&& avGeoPoint.getLongitude() == location.getLongitude()) {
					Utils.updateUserLocation();
					Utils.updateUserInfo();
					mLocClient.stop();
					return;
				}
			}
			AVGeoPoint avGeoPoint = new AVGeoPoint(location.getLatitude(),
					location.getLongitude());
			preferenceMap.setLocation(avGeoPoint);
			preferenceMap.setAddress(location.getAddrStr());

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(mBTreceiver);
		if (mBTadapter.isEnabled()) {
			mBTadapter.disable();
		}
	}


	private void init() {
		tabs = new Button[]{mConversationBtn, contactBtn, mDiscoverBtn, mySpaceBtn};
	}

	private void findView() {
		mConversationBtn = (Button) findViewById(R.id.btn_message);
		contactBtn = (Button) findViewById(R.id.btn_contact);
		mDiscoverBtn = (Button) findViewById(R.id.btn_discover);
		mySpaceBtn = (Button) findViewById(R.id.btn_my_space);
		fragmentContainer = findViewById(R.id.fragment_container);
		recentTips = findViewById(R.id.iv_recent_tips);
		contactTips = findViewById(R.id.iv_contact_tips);
	}

	public void onTabSelect(View v) {
		int id = v.getId();
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		hideFragments(transaction);
		setNormalBackgrounds();
		if (id == R.id.btn_message) {
			if (conversationFragment == null) {
				conversationFragment = new LoveFragment();
				transaction.add(R.id.fragment_container, conversationFragment);
			}
			transaction.show(conversationFragment);
		} else if (id == R.id.btn_contact) {
			if (contactFragment == null) {
				contactFragment = new ContactFragment();
				transaction.add(R.id.fragment_container, contactFragment);
			}
			transaction.show(contactFragment);
		} else if (id == R.id.btn_discover) {
			if (discoverFragment == null) {
				discoverFragment = new MeetFragment();
				transaction.add(R.id.fragment_container, discoverFragment);
			}
			transaction.show(discoverFragment);
		} else if (id == R.id.btn_my_space) {
			if (mySpaceFragment == null) {
				mySpaceFragment = new MySpaceFragment();
				transaction.add(R.id.fragment_container, mySpaceFragment);
			}
			transaction.show(mySpaceFragment);
		}
		int pos;
		for (pos = 0; pos < FRAGMENT_N; pos++) {
			if (tabs[pos] == v) {
				break;
			}
		}
		transaction.commit();
		setTopDrawable(tabs[pos], tabsActiveBackIds[pos]);
	}

	private void setNormalBackgrounds() {
		for (int i = 0; i < tabs.length; i++) {
			Button v = tabs[i];
			setTopDrawable(v, tabsNormalBackIds[i]);
		}
	}

	private void setTopDrawable(Button v, int resId) {
		v.setCompoundDrawablesWithIntrinsicBounds(null, mContext.getResources().getDrawable(resId), null, null);
	}

	private void hideFragments(FragmentTransaction transaction) {
		Fragment[] fragments = new Fragment[]{
				conversationFragment, contactFragment,
				discoverFragment, mySpaceFragment
		};
		for (Fragment f : fragments) {
			if (f != null) {
				transaction.hide(f);
			}
		}
	}

}
