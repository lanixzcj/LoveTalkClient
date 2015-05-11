package com.example.lovetalk.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.SaveCallback;
import com.example.lovetalk.DemoApplication;
import com.example.lovetalk.R;
import com.example.lovetalk.adapter.BaseListAdapter;
import com.example.lovetalk.service.PreferenceMap;
import com.example.lovetalk.view.xlist.XListView;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Utils {
	public static ProgressDialog showSpinnerDialog(Activity activity) {
		// activity = modifyDialogContext(activity);

		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(true);
		dialog.setMessage("加载中...");
		if (activity.isFinishing() == false) {
			dialog.show();
		}
		return dialog;
	}

	public static void goActivity(Context context, Class<?> clz) {
		Intent intent = new Intent(context, clz);
		context.startActivity(intent);
	}

	public static void toast(Context context, String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

	public static void updateUserInfo() {
		AVUser user = AVUser.getCurrentUser();
		if (user != null) {
			user.fetchInBackground("friends", new GetCallback<AVObject>() {
				@Override
				public void done(AVObject avObject, AVException e) {
					if (e == null) {
						// User avUser = (User) avObject;
						// App.registerUserCache(avUser);
					}
				}
			});
		}
	}

	public static void updateUserLocation() {
		PreferenceMap preferenceMap = PreferenceMap
				.getCurUserPrefDao(DemoApplication.context);
		AVGeoPoint lastLocation = preferenceMap.getLocation();
		String lastaddress = preferenceMap.getAddress();
		if (lastLocation != null) {
			final AVUser user = AVUser.getCurrentUser();
			final AVGeoPoint location = user.getAVGeoPoint("location");
			final String Address = user.getString("address");
			if (location == null
					|| !Utils.doubleEqual(location.getLatitude(),
							lastLocation.getLatitude())
					|| !Utils.doubleEqual(location.getLongitude(),
							lastLocation.getLongitude())) {
				user.put("location", lastLocation);
				user.put("address", lastaddress);
				user.saveInBackground(new SaveCallback() {
					@Override
					public void done(AVException e) {
						if (e != null) {
							e.printStackTrace();
						} else {
							log.v("lastLocation save "
									+ user.getAVGeoPoint("location"));
						}
					}
				});
			}
		}
	}

	public static boolean doubleEqual(double a, double b) {
		return Math.abs(a - b) < 1E-8;
	}

	public static void handleListResult(XListView listView,
			BaseListAdapter adapter, List datas) {
		if (Utils.isListNotEmpty(datas)) {
			adapter.addAll(datas);
			if (datas.size() == 10) {
				listView.setPullLoadEnable(true);
			} else {
				listView.setPullLoadEnable(false);
			}
		} else {
			listView.setPullLoadEnable(false);
			if (adapter.getCount() == 0) {
				Utils.toast(DemoApplication.context, "无结果");
			} else {
				Utils.toast(DemoApplication.context, "加载完毕");
			}
		}
	}

	public static boolean isListNotEmpty(Collection<?> collection) {
		if (collection != null && collection.size() > 0) {
			return true;
		}
		return false;
	}

	public static String md5(String string) {
		byte[] hash = null;
		try {
			hash = string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh,UTF-8 should be supported?", e);
		}
		return computeMD5(hash);
	}

	public static String computeMD5(byte[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input, 0, input.length);
			byte[] md5bytes = md.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < md5bytes.length; i++) {
				String hex = Integer.toHexString(0xff & md5bytes[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String myUUID() {
		StringBuilder sb = new StringBuilder();
		int start = 48, end = 58;
		appendChar(sb, start, end);
		appendChar(sb, 65, 90);
		appendChar(sb, 97, 123);
		String charSet = sb.toString();
		StringBuilder sb1 = new StringBuilder();
		for (int i = 0; i < 24; i++) {
			int len = charSet.length();
			int pos = new Random().nextInt(len);
			sb1.append(charSet.charAt(pos));
		}
		return sb1.toString();
	}

	public static void appendChar(StringBuilder sb, int start, int end) {
		int i;
		for (i = start; i < end; i++) {
			sb.append((char) i);
		}
	}

	public static void downloadFileIfNotExists(String url, File toFile)
			throws IOException {
		if (toFile.exists()) {
		} else {
			downloadFile(url, toFile);
		}
	}

	public static void downloadFile(String url, File toFile) throws IOException {
		toFile.createNewFile();
		FileOutputStream outputStream = new FileOutputStream(toFile);
		InputStream inputStream = Utils.inputStreamFromUrl(url);
		Utils.inputToOutput(outputStream, inputStream);
	}

	public static InputStream inputStreamFromUrl(String url)
			throws IOException, ClientProtocolException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		InputStream stream = entity.getContent();
		return stream;
	}

	public static Bitmap bitmapFromFile(File file) throws FileNotFoundException {
		return BitmapFactory.decodeStream(new BufferedInputStream(
				new FileInputStream(file)));
	}

	public static void inputToOutput(FileOutputStream outputStream,
			InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		inputStream.close();
	}

	public static void hideSoftInputView(Activity activity) {
		if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			InputMethodManager manager = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			View currentFocus = activity.getCurrentFocus();
			if (currentFocus != null) {
				manager.hideSoftInputFromWindow(currentFocus.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}
	
	public static int getColor(int resId) {
	    return DemoApplication.context.getResources().getColor(resId);
	  }
}
