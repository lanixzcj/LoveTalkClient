package com.example.lovetalk.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.example.lovetalk.DemoApplication;
import com.example.lovetalk.R;
import com.example.lovetalk.service.ChatService;
import com.example.lovetalk.service.UserService;
import com.example.lovetalk.util.MyAsyncTask;
import com.example.lovetalk.util.PathUtils;
import com.example.lovetalk.util.PhotoUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lzw on 14-9-17.
 */
public class MySpaceFragment extends BaseFragment implements
		View.OnClickListener {
	private static final int IMAGE_PICK_REQUEST = 1;
	private static final int CROP_REQUEST = 2;
	TextView usernameView, genderView, mobileView, emailView, logoutView;
	ImageView avatarView;
	View usernameLayout, avatarLayout, logoutLayout, genderLayout;
	public static String[] genderStrings = new String[]{
			DemoApplication.context.getString(R.string.male),
			DemoApplication.context.getString(R.string.female)};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.my_space_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		headerLayout.showTitle(R.string.me);
		findView();
		refresh();
	}

	public String getAvatarUrl(AVUser user) {
		AVFile avatar = user.getAVFile("avatar");
		if (avatar != null) {
			return avatar.getUrl();
		} else {
			return null;
		}
	}

	private void refresh() {
		AVUser curUser = AVUser.getCurrentUser();
		usernameView.setText(curUser.getUsername());
		genderView.setText(genderStrings[curUser.getInt("gender")]);
		UserService.displayAvatar(getAvatarUrl(curUser), avatarView);
		mobileView.setText(curUser.getMobilePhoneNumber());
		emailView.setText(curUser.getEmail());
	}

	private void findView() {
		View fragmentView = getView();
		usernameView = (TextView) fragmentView.findViewById(R.id.username);
		avatarView = (ImageView) fragmentView.findViewById(R.id.avatar);
		usernameLayout = fragmentView.findViewById(R.id.usernameLayout);
		avatarLayout = fragmentView.findViewById(R.id.avatarLayout);
		logoutLayout = fragmentView.findViewById(R.id.logoutLayout);
		genderLayout = fragmentView.findViewById(R.id.sexLayout);
		genderView = (TextView) fragmentView.findViewById(R.id.sex);
		mobileView = (TextView) fragmentView.findViewById(R.id.mobile);
		emailView = (TextView) fragmentView.findViewById(R.id.email);
		logoutView = (TextView) fragmentView.findViewById(R.id.logout);

		avatarLayout.setOnClickListener(this);
		logoutView.setOnClickListener(this);
		genderLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.avatarLayout) {
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, IMAGE_PICK_REQUEST);
		} else if (id == R.id.logout) {
			ChatService.closeSession();
			AVUser.logOut();
			getActivity().finish();
		} else if (id == R.id.sexLayout) {
			showSexChooseDialog();
		}
	}

	SaveCallback saveCallback = new SaveCallback() {
		@Override
		public void done(AVException e) {
			refresh();
		}
	};

	private void showSexChooseDialog() {
		AVUser user = AVUser.getCurrentUser();
		int checkItem = user.getInt("gender") == 0 ? 0 : 1;
		new AlertDialog.Builder(context)
				.setTitle(R.string.sex)
				.setSingleChoiceItems(genderStrings, checkItem,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								int gender;
								if (which == 0) {
									gender = 0;
								} else {
									gender = 1;
								}
								UserService.saveSex(gender, saveCallback);
								dialog.dismiss();
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("lan", "on Activity result " + requestCode + " " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == IMAGE_PICK_REQUEST) {
				Uri uri = data.getData();
				startImageCrop(uri, 200, 200, CROP_REQUEST);
			} else if (requestCode == CROP_REQUEST) {
				final String path = saveCropAvatar(data);
				new MyAsyncTask(context) {

					@Override
					protected void doInBack() throws Exception {
						// TODO Auto-generated method stub
						UserService.saveAvatar(path);
					}

					@Override
					protected void onSucceed() {
						refresh();
					}
				}.execute();
			}
		}
	}

	public Uri startImageCrop(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = null;
		intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		String outputPath = PathUtils.getAvatarTmpPath();
		Log.d("lan", "outputPath=" + outputPath);
		Uri outputUri = Uri.fromFile(new File(outputPath));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", false); // face detection
		startActivityForResult(intent, requestCode);
		return outputUri;
	}

	private String saveCropAvatar(Intent data) {
		Bundle extras = data.getExtras();
		String path = null;
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date());
				path = PathUtils.getAvatarDir() + filename;
				Log.d("lan", "save bitmap to " + path);
				PhotoUtil.saveBitmap(PathUtils.getAvatarDir(), filename,
						bitmap, true);
				if (bitmap != null && bitmap.isRecycled() == false) {
					// bitmap.recycle();
				}
			}
		}
		return path;
	}
}
