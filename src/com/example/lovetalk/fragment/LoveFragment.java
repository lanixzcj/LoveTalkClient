package com.example.lovetalk.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.lovetalk.activity.ItsBlogActivity;
import com.example.lovetalk.util.MyAsyncTask;
import com.example.lovetalk.activity.MyBlogActivity;
import com.example.lovetalk.R;

/**
 * Created by lzw on 14-9-17.
 */
public class LoveFragment extends BaseFragment implements
		android.view.View.OnClickListener {
	// RecentMessageAdapter adapter;
	private Context mContext;
	private RelativeLayout container;
	private TextView status;
	private TextView statusWord;
	private EditText loverPhone;
	private EditText toSay;
	private EditText loverName;
	private Button bind;
	private Button writeBlog;
	private Button myBlog;
	private Button itsBlog;
	private Button changeLove;
	private ImageView arrow;
	private String myPhone;
	private String lPhone;
	private String userId;
	private String itsLovePhone = "";
	private String userStr;
	private String pass;
	final static int VISIBLE = 0;
	final static int NO_VISIBLE = 4;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.lover_main, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		headerLayout.showTitle("暗恋");
		initView();
		refresh();
	}

	private void initView() {
		mContext = context;
		View fragmentView = getView();

		AVUser user = AVUser.getCurrentUser();
		userStr = user.getUsername();
		pass = user.getString("password");

		toQuery();
		container = (RelativeLayout) fragmentView.findViewById(R.id.container);
		status = (TextView) fragmentView.findViewById(R.id.txt_loveStatus);
		statusWord = (TextView) fragmentView.findViewById(R.id.txt_statusWord);
		loverPhone = (EditText) fragmentView.findViewById(R.id.edit_loverPhone);
		loverName = (EditText) fragmentView.findViewById(R.id.edit_loverName);
		toSay = (EditText) fragmentView.findViewById(R.id.edit_blog);
		bind = (Button) fragmentView.findViewById(R.id.btn_bind);
		writeBlog = (Button) fragmentView.findViewById(R.id.btn_toSay);
		myBlog = (Button) fragmentView.findViewById(R.id.btn_myBlog);
		itsBlog = (Button) fragmentView.findViewById(R.id.btn_itsBlog);
		changeLove = (Button) fragmentView.findViewById(R.id.changeLove);
		arrow = (ImageView) fragmentView.findViewById(R.id.img_arrow);

		bind.setOnClickListener(this);
		writeBlog.setOnClickListener(this);
		myBlog.setOnClickListener(this);
		itsBlog.setOnClickListener(this);
		changeLove.setOnClickListener(this);
	}

	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int position,
	// long arg3) {
	// TODO Auto-generated method stub
	// Conversation recent = (Conversation) adapter.getItem(position);
	// if (recent.msg.getRoomType()== RoomType.Single) {
	// ChatActivity.goUserChat(getActivity(), recent.toUser.getObjectId());
	// } else {
	// ChatActivity.goGroupChat(getActivity(), recent.chatGroup.getObjectId());
	// }
	// }

	private boolean hidden;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	public void refresh() {
		// new GetDataTask(ctx, false).execute();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!hidden) {
			refresh();
		}
		// GroupMsgReceiver.addMsgListener(this);
		// MsgReceiver.addMsgListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// MsgReceiver.removeMsgListener(this);
		// GroupMsgReceiver.removeMsgListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btn_bind:
				final String lovePhone = loverPhone.getText().toString();
				if (mobilevalidate()) {
					Thread threadBind = new Thread() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							AVQuery<AVObject> query = new AVQuery<AVObject>("_User");
							query.whereEqualTo("mobilePhoneNumber", lovePhone);
							query.findInBackground(new FindCallback<AVObject>() {
								public void done(List<AVObject> avObjects,
												 AVException e) {
									if (e == null) {
										if (avObjects.size() > 0) {
											Thread toQueryPhone = new Thread() {
												@Override
												public void run() {
													String tableName = "_User";
													AVObject user = new AVObject(
															tableName);
													AVQuery<AVObject> query = new AVQuery<AVObject>(
															tableName);

													try {
														user = query.get(userId);
													} catch (AVException e1) {
														// TODO Auto-generated catch
														// block
														e1.printStackTrace();
													}

													user.put(
															"unrequitedLoverPhone",
															lovePhone);
													user.saveInBackground(new SaveCallback() {
														@Override
														public void done(
																AVException e) {
															if (e == null) {
																AVUser.logInInBackground(
																		userStr,
																		pass,
																		new LogInCallback<AVUser>() {
																			public void done(
																					AVUser user,
																					AVException e) {

																				lPhone = lovePhone;
																				bind.setVisibility(NO_VISIBLE);
																				loverPhone
																						.setVisibility(NO_VISIBLE);
																				loverName
																						.setVisibility(NO_VISIBLE);
																				getLovePhone();
																				if (itsLovePhone
																						.equals(myPhone)) {
																					status.setText("暗恋状态：情投意合");
																					container
																							.setBackgroundResource(R.drawable.twolove);
																					arrow.setVisibility(VISIBLE);
																				} else {
																					status.setText("暗恋状态：单相思");
																				}
																				changeLove
																						.setVisibility(VISIBLE);
																				Toast.makeText(
																						mContext,
																						"暗恋成功",
																						Toast.LENGTH_LONG)
																						.show();
																			}
																		});

															} else {
																Toast.makeText(
																		mContext,
																		"您注销一次，才能重新暗恋",
																		Toast.LENGTH_LONG)
																		.show();
															}
														}
													});
												}
											};

											toQueryPhone.start();

										} else {
											Toast.makeText(mContext,
													"她/他还未注册，快去悄悄邀请哦",
													Toast.LENGTH_LONG).show();
										}
									} else {
										Log.e("LeanCloud", e.getMessage());
									}
								}
							});

						}
					};

					threadBind.start();
				} else {
					Toast.makeText(mContext, "请重新输入对方手机号码哦", Toast.LENGTH_LONG)
							.show();
				}
				break;
			case R.id.btn_toSay:
				if (lPhone == null || lPhone.equals("")) {
					Toast.makeText(mContext, "你还没有暗恋的对象哦", Toast.LENGTH_LONG)
							.show();
				} else {
					if (blogvalidate()) {
						final Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
						final String dailyBlog = toSay.getText().toString();

						Thread threadToSay = new Thread() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								super.run();

								AVUser.logInInBackground(userStr, pass,
										new LogInCallback<AVUser>() {
											public void done(AVUser user,
															 AVException e) {
												if (user != null) {
													Log.i("Leancloud", "保存成功");
												} else {
													Log.e("Leancloud",
															e.getMessage());
												}
											}
										});

								String tableName = "unrequitedBlog";
								final AVObject blog = new AVObject(tableName);

								blog.put("DailyLog", dailyBlog);
								blog.put("Time", curDate);
								blog.put("myPhone", myPhone);
								blog.put("unrequitedLoverPhone", lPhone);

								blog.saveInBackground(new SaveCallback() {
									public void done(AVException e) {
										if (e == null) {
											AVUser user = AVUser.getCurrentUser();
											AVRelation<AVObject> relation = user
													.getRelation("unrequitedLoveInfo");
											relation.add(blog);

											user.saveInBackground(new SaveCallback() {
												public void done(AVException e) {
													if (e == null) {

													} else {
														Log.e("Leancloud",
																e.getMessage());
														Log.i("Leancloud",
																blog.getObjectId());
													}
												}
											});

										} else {
											Toast.makeText(mContext, "日志保存失败",
													Toast.LENGTH_LONG).show();
											Log.e("Leancloud", e.getMessage());
											Log.i("Leancloud", blog.getObjectId());
										}
									}
								});
							}
						};

						threadToSay.start();
					} else {
						Toast.makeText(mContext, "请输入暗恋内容", Toast.LENGTH_LONG)
								.show();
					}
				}
				break;
			case R.id.btn_myBlog:
				Toast.makeText(mContext, "进入我的日志", Toast.LENGTH_LONG).show();
				Intent intent = new Intent();
				intent.setClass(mContext, MyBlogActivity.class);
				intent.putExtra("MYPHONE", myPhone);
				startActivity(intent);
				break;
			case R.id.btn_itsBlog:
				if (itsLovePhone.equals(myPhone)) {
					Toast.makeText(mContext, "进入他/她的日志", Toast.LENGTH_LONG).show();
					Intent itsIntent = new Intent();
					itsIntent.setClass(mContext, ItsBlogActivity.class);
					itsIntent.putExtra("ITSPHONE", lPhone);
					startActivity(itsIntent);
				} else {
					Toast.makeText(mContext, "对方并未暗恋你哦", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.changeLove:
				new AlertDialog.Builder(mContext)
						.setTitle("或许你不想再爱了")
						.setMessage("解除暗恋将删除一切暗恋日志，确定不再坚持了吗")
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										new MyAsyncTask(mContext, false) {
											@Override
											protected void onPost(Exception e) {
												// TODO Auto-generated method stub
												if (e != null) {
													Toast.makeText(mContext,
															"网络错误",
															Toast.LENGTH_LONG)
															.show();
												} else {
													status.setText("未暗恋");
													statusWord
															.setText("我知道，你想重新开始一段新的旅程");
													loverPhone.setText("");
													loverPhone
															.setVisibility(VISIBLE);
													loverName.setText("");
													loverName
															.setVisibility(VISIBLE);
													bind.setVisibility(VISIBLE);
													container
															.setBackgroundResource(R.drawable.nolove);
													changeLove
															.setVisibility(NO_VISIBLE);
													lPhone = "";
													itsLovePhone = "";
													arrow.setVisibility(NO_VISIBLE);
												}
											}

											@Override
											protected void doInBack()
													throws Exception {
												// TODO Auto-generated method stub
												Thread threadChange = new Thread() {

													@Override
													public void run() {
														// TODO Auto-generated
														// method stub
														super.run();

														AVUser.logInInBackground(
																userStr,
																pass,
																new LogInCallback<AVUser>() {
																	public void done(
																			AVUser user,
																			AVException e) {
																		if (user != null) {
																			Log.i("Leancloud",
																					"保存成功");
																		} else {
																			Log.e("Leancloud",
																					e.getMessage());
																		}
																	}
																});

														String tableName = "unrequitedBlog";
														final AVQuery<AVObject> query = new AVQuery<AVObject>(
																tableName);
														query.whereEqualTo(
																"myPhone", myPhone);
														query.findInBackground(new FindCallback<AVObject>() {
															public void done(
																	List<AVObject> avObjects,
																	AVException e) {
																if (e == null) {
																	Log.d("成功",
																			"查询到"
																					+ avObjects
																					.size()
																					+ " 条符合条件的数据");
																	Thread threadDelete = new Thread() {
																		@Override
																		public void run() {
																			// TODO
																			// Auto-generated
																			// method
																			// stub
																			try {
																				query.deleteAll();
																			} catch (AVException e) {
																				// TODO
																				// Auto-generated
																				// catch
																				// block
																				e.printStackTrace();
																			}
																			AVObject user = new AVObject(
																					"_User");
																			AVQuery<AVObject> queryUser = new AVQuery<AVObject>(
																					"_User");

																			try {
																				user = queryUser
																						.get(userId);
																			} catch (AVException e1) {
																				// TODO
																				// Auto-generated
																				// catch
																				// block
																				e1.printStackTrace();
																			}

																			user.put(
																					"unrequitedLoverPhone",
																					null);
																			user.saveInBackground(new SaveCallback() {
																				@Override
																				public void done(
																						AVException e) {
																					if (e == null) {

																					} else {
																						e.getMessage();
																					}
																				}
																			});
																		}
																	};

																	threadDelete
																			.start();
																} else {
																	Log.d("失败",
																			"查询错误: "
																					+ e.getMessage());
																}
															}
														});

													}
												};

												threadChange.start();
											}
										}.execute();
									}
								})
						.setNegativeButton("否",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										Toast.makeText(mContext, "坚持就会有明天",
												Toast.LENGTH_LONG).show();
									}
								}).show();

				break;
		}
	}

	// @Override
	// public boolean onMessageUpdate(String otherId) {
	// refresh();
	// return false;
	// }

	// class GetDataTask extends NetAsyncTask {
	// List<Conversation> conversations;
	//
	// GetDataTask(Context cxt, boolean openDialog) {
	// super(cxt, openDialog);
	// }
	//
	// @Override
	// protected void doInBack() throws Exception {
	// conversations = ChatService.getConversationsAndCache();
	// }
	//
	// @Override
	// protected void onPost(Exception e) {
	// if (e != null) {
	// Utils.toast(ctx, R.string.pleaseCheckNetwork);
	// } else {
	// adapter.setDatas(conversations);
	// adapter.notifyDataSetChanged();
	// }
	// }
	// }

	public void toQuery() {
		AVQuery<AVObject> query = new AVQuery<AVObject>("_User");
		query.whereEqualTo("username", userStr);
		query.findInBackground(new FindCallback<AVObject>() {
			public void done(List<AVObject> avObjects, AVException e) {
				if (e == null) {
					Log.d("成功", "查询到 " + avObjects.size() + " 条符合条件的数据");
					myPhone = avObjects.get(0).getString("mobilePhoneNumber");
					lPhone = avObjects.get(0).getString("unrequitedLoverPhone");

					userId = avObjects.get(0).getObjectId();

					if (lPhone == null || lPhone.equals("")) {
						bind.setVisibility(VISIBLE);
						loverPhone.setVisibility(VISIBLE);
						loverName.setVisibility(VISIBLE);
						Log.d("Leancloud", "没有暗恋对象");
						status.setText("未暗恋");
						statusWord.setText("我懂，你的暗恋苦楚");
					} else {
						Log.d("Leancloud", "有暗恋对象");
						changeLove.setVisibility(VISIBLE);
						getLovePhone();
					}

				} else {
					Log.d("失败", "查询错误: " + e.getMessage());
				}
			}
		});
	}

	public void getLovePhone() {
		AVQuery<AVObject> queryPhone = new AVQuery<AVObject>("_User");
		queryPhone.whereEqualTo("mobilePhoneNumber", lPhone + "");
		queryPhone.findInBackground(new FindCallback<AVObject>() {
			public void done(List<AVObject> avObjects, AVException e) {
				if (e == null) {
					if (avObjects.size() == 0) {
						itsLovePhone = "";
					} else {
						itsLovePhone = avObjects.get(0).getString(
								"unrequitedLoverPhone")
								+ "";
					}

					if (itsLovePhone.equals(myPhone)) {
						status.setText("情投意合");
						statusWord.setText("爱情开始咯");
						container.setBackgroundResource(R.drawable.twolove);
						arrow.setVisibility(VISIBLE);
					} else {
						statusWord.setText("我懂，你的暗恋苦楚");
						status.setText("单相思");
					}
				} else {
					Log.d("失败", "查询错误: " + e.getMessage());
				}
			}
		});
	}

	public boolean mobilevalidate() {
		String mobile = loverPhone.getText().toString();

		if ("".equals(mobile)) {
			loverPhone.setError("电话不能为空");
			return false;
		} else {
			String match = "^((13[0-9])|(15[^4,\\D])|(18[0,2,5-9]))\\d{8}$";
			if (!mobile.matches(match)) {
				loverPhone.setError("电话格式不合法");
				return false;
			}
		}
		return true;
	}

	public boolean blogvalidate() {
		String blog = toSay.getText().toString() + "";

		if ("".equals(blog)) {
			toSay.setError("你就不想说点什么吗");
			return false;
		}

		return true;
	}
}