package com.att.guide;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.att.AppBaseFun;
import com.att.HttpApp;
import com.att.JsonValidator;
import com.att.MainIdleActivity;
import com.att.R;
import com.att.SettingPara;
import com.att.act.ChildCard;
import com.att.act.ParentArray;
import com.att.act.PicCard;
import com.att.act.TeacherInfo;
import com.att.act.WriteUnit;
import com.att.act.ZwCardInfo;
import com.att.server.NlServer;
import com.att.server.TelpoService;
import com.att.server.ZwService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StepFourthActivity extends Activity {

	private Button next_step;
	boolean isSet = false;
	private ImageView iv_loading_anim;
	private TextView tv_loading;

	private SettingPara settingPara = new SettingPara();
	private SharedPreferences sp;

	private AppBaseFun appBaseFun = new AppBaseFun();

	private Handler mHandler = null;
	Timer timer;
	private int times = 0;
	private Handler showHandler = null;

	boolean isSucceed = false;
	boolean isLoading = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.step_fourth_seven);
		init();
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
	}

	private void init() {
		isSet = getIntent().getBooleanExtra("isSet", false);
		next_step = (Button) findViewById(R.id.next_step);
		next_step.setVisibility(View.GONE);
		iv_loading_anim = (ImageView) findViewById(R.id.iv_loading_anim);
		iv_loading_anim.setBackgroundResource(R.anim.loading_anim);// 设置动画背景
		AnimationDrawable anim = (AnimationDrawable) iv_loading_anim.getBackground();// 获得动画对象
		// 最后，就可以启动动画了，代码如下：
		anim.setOneShot(false);// 是否仅仅启动一次？
		if (anim.isRunning()) {// 是否正在运行？
			anim.stop();// 停止
		}
		anim.start();// 启动

		tv_loading = (TextView) findViewById(R.id.tv_loading);

		sp = getSharedPreferences("json", Activity.MODE_PRIVATE);

		mHandler = new Handler() {
			@Override
			public void handleMessage(final Message msg) {
				switch (msg.what) {
					case 0:
						int mm1 = msg.getData().getInt("num");
						tv_loading.setText("卡信息已下载 " + mm1 + "%");
						break;
					case 1:
						int mm = msg.getData().getInt("num");
						tv_loading.setText("图片已下载 " + mm + "%");
						break;

					case 2:
						tv_loading.setText("卡信息下载完成");
						break;

					case 3:
						iv_loading_anim.setVisibility(View.VISIBLE);
						tv_loading.setText("数据同步开始");
						break;

					case 4:
						isSucceed = true;
						tv_loading.setText("数据同步完成");
						stopATTService(StepFourthActivity.this, settingPara);
						startATTService(StepFourthActivity.this, settingPara);
						// TtsPlay.speaktts("数据初始化成功");
						iv_loading_anim.setVisibility(View.INVISIBLE);
						next_step.setVisibility(View.VISIBLE);
						next_step.setEnabled(true);
						next_step.setText("完成");
						next_step.setFocusable(true);
						//next_step.setFocusableInTouchMode(true);
						next_step.requestFocus();
						//next_step.requestFocusFromTouch();
						next_step.setOnClickListener(new OnClickListener() {
							public void onClick(View arg0) {
								Intent intent = new Intent(StepFourthActivity.this, MainIdleActivity.class);
								// 如果之前启动过这个Activity，并还没有被destroy的话，而是无论是否存在，都重新启动新的activity
								// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 获取应用的上下文，生命周期是整个应用，应用结束才会结束
								startActivity(intent);// 跳转
								StepFourthActivity.this.finish();// 结束本Activity
							}
						});
						try {
							Thread.sleep(1000);// 用线程暂停3秒来模拟做了一个耗时3秒的检测操作,为了省时间，改为1秒
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;

					case 5:
						iv_loading_anim.setVisibility(View.INVISIBLE);
						tv_loading.setText("无法连接到平台");// 无法连接到平台，请退出APP重试
						next_step.setVisibility(View.VISIBLE);
						next_step.setEnabled(true);
						next_step.setText("返回参数设置");
						next_step.setFocusable(true);
						//next_step.setFocusableInTouchMode(true);
						next_step.requestFocus();
						//next_step.requestFocusFromTouch();
						next_step.setOnClickListener(new OnClickListener() {
							public void onClick(View arg0) {
								Intent intent = new Intent(StepFourthActivity.this, StepSecondActivity.class);
								intent.putExtra("isSet", isSet);
								startActivity(intent);
								overridePendingTransition(R.anim.back_left_in, R.anim.back_right_out);
								StepFourthActivity.this.finish();
							}
						});
						break;

					case 6:
						iv_loading_anim.setVisibility(View.INVISIBLE);
						String dataString = msg.getData().getString("data");
						tv_loading.setText(dataString);
						next_step.setVisibility(View.VISIBLE);
						next_step.setEnabled(true);
						next_step.setText("返回设置页面");
						next_step.setFocusable(true);
						//next_step.setFocusableInTouchMode(true);
						next_step.requestFocus();
						//next_step.requestFocusFromTouch();
						next_step.setOnClickListener(new OnClickListener() {
							public void onClick(View arg0) {
								Intent intent = new Intent(StepFourthActivity.this, StepFirstActivity.class);
								intent.putExtra("isSet", isSet);
								startActivity(intent);
								overridePendingTransition(R.anim.back_left_in, R.anim.back_right_out);
								StepFourthActivity.this.finish();
							}
						});
						break;

					default:
						break;
				}
			}
		};

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				times++;
				if (!isSucceed) {
					if (times > 3) {
						Message mge = new Message();
						mge.what = 5;
						mHandler.sendMessage(mge);

					}
					Message message1 = new Message();
					message1.what = 0;
					showHandler.sendMessage(message1);
				}
			}
		}, 1000 * 10, 1000 * 60*3);

		HandlerThread hs = new HandlerThread("telbs");
		hs.start();
		showHandler = new Handler(hs.getLooper()) {
			@Override
			public void handleMessage(final Message msg) {
				switch (msg.what) {
					case 0:
						updatedata();
						break;
					default:
						break;
				}
			}
		};

		Message message1 = new Message();
		message1.what = 0;
		showHandler.sendMessage(message1);
		tv_loading.setText("正在连接平台中");
	}

	private void updatedata() {
		if (!MainIdleActivity.isNetworkAvailables(getApplicationContext(), appBaseFun, settingPara)) {
			Message mg = new Message();
			mg.what = 6;
			Bundle bundle = new Bundle();
			bundle.putString("data", "网络异常，请检查网络配置");
			mg.setData(bundle);
			mHandler.sendMessage(mg);
			return;
		}
		if (!isSucceed) {
			new Thread(new Runnable() {
				public void run() {
					if(!isLoading){
						isLoading = true;
						if (settingPara.getAtt_pic_platform() == 0) {// ww
							loadDataWW(mHandler, sp);
						} else if (settingPara.getAtt_pic_platform() == 1) {// nl
							loadDataNL(mHandler, sp);
						} else if (settingPara.getAtt_pic_platform() == 2) {// zw
							String imei = ((TelephonyManager) getApplication().getSystemService(TELEPHONY_SERVICE))
									.getDeviceId();
							loadDataZW(mHandler, sp, imei);
						}
						isLoading = false;
					}
				}
			}).start();
		}
	}

	public static boolean loadDataWW(Handler mHandler, SharedPreferences sp) {
		String strUrl = null;
		String strRes = null;
		List<ChildCard> lc = null;
		List<ChildCard> listca = null;
		SettingPara settingPara = new SettingPara();
		HttpApp httpApp = new HttpApp();
		AppBaseFun appBaseFun = new AppBaseFun();

		String province=settingPara.getProvincCode();
		if (province!=null&&!"".equals(province)) {
			strUrl = settingPara.getCardInfoUrl() + "?SchoolID=" + settingPara.getSchoolID() + "&ProvinceCode="
					+ province;
		}else {
			strUrl = settingPara.getCardInfoUrl() + "?SchoolID=" + settingPara.getSchoolID() + "&ProvinceCode="
					+ "62000000";
		}


		try {
			Log.i("TPATT", "下载卡信息请求:" + strUrl);
			if (settingPara.getCardInfoUrl().startsWith("https")) {
				strRes=httpApp.gethttps(settingPara.getCardInfoUrl() + "?SchoolID="+ settingPara.getSchoolID());
			}else {
				strRes = httpApp.getSendAndReceive(strUrl);
			}
			Log.i("TPATT", "下载卡信息返回:" + strRes);

			if (strRes != null) {

				JsonValidator jsonValidator = new JsonValidator();
				try {
					boolean isjson = jsonValidator.validate(strRes);
					if (!isjson) {
						if (mHandler != null) {
							Message mg = new Message();
							mg.what = 6;
							Bundle bundle = new Bundle();
							bundle.putString("data", strRes);
							mg.setData(bundle);
							mHandler.sendMessage(mg);
						}
						Log.i("tapp", "不是正确josn格式");
						WriteUnit.loadlist("不是正确josn格式为" + strRes);
						return false;
					}
				} catch (Exception e) {

				}

				Message mji = new Message();
				mji.what = 3;
				mHandler.sendMessage(mji);

				JSONObject jsonObject;
				Gson gson = new Gson();
				String cc = sp.getString("studentin", null);
				Log.i("ppppp", "print" + cc);
				if (cc != null) {
					lc = gson.fromJson(cc, new TypeToken<ArrayList<ChildCard>>() {
					}.getType());
				}

				jsonObject = new JSONObject(strRes).getJSONObject("_metadata");
				if (jsonObject.getString("code").equals("200")) {
					String name;
					String strPhotoPath;
					@SuppressWarnings("unused")
					String cardid = null;

					String strdata = new JSONObject(strRes).getString("data");

					savesp("studentin", strdata, sp);

					if (cc != null && cc.equals(strdata)) {
						Log.i("tapp", "数据一样，不更新");
						WriteUnit.loadlist("数据一样，不更新");
						if (mHandler != null) {
							Message msg = new Message();
							msg.what = 2;
							mHandler.sendMessage(msg);
						}
						loadPicWW(settingPara, httpApp, sp, appBaseFun, mHandler);
						return false;
					}

					listca = gson.fromJson(strdata, new TypeToken<ArrayList<ChildCard>>() {
					}.getType());
					Log.i("msg", "" + listca);

					clearDataWW(listca, lc, sp);

					JSONArray jsonArray = new JSONArray(strdata);
					Log.i("TPATT", "下载卡信息请求:jsonArray.length=" + String.valueOf(jsonArray.length()));
					// cardload.setMax(jsonArray.length());
					int cardmax = jsonArray.length();
					savesp("allnum", "" + jsonArray.length(), sp);
					for (int i = 0; i < jsonArray.length(); i++) {

						try {

							JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
							name = jsonObject2.getString("child_name");
							Log.i("TPATT", "学生姓名:" + name);

							// 家长图片保存文件夹
							strPhotoPath = jsonObject2.getString("child_id");
							savesp(jsonObject2.getString("card_id") + "idimfo", strPhotoPath, sp);
							savesp(jsonObject2.getString("card_id1") + "idimfo", strPhotoPath, sp);
							savesp(jsonObject2.getString("card_id2") + "idimfo", strPhotoPath, sp);
							savesp(jsonObject2.getString("card_id3") + "idimfo", strPhotoPath, sp);
							try {
								savesp(jsonObject2.getString("card_id4") + "idimfo", strPhotoPath, sp);
								savesp(jsonObject2.getString("card_id5") + "idimfo", strPhotoPath, sp);
							} catch (Exception e) {

							}

							savesp(jsonObject2.getString("card_id") + "name", name, sp);
							savesp(jsonObject2.getString("card_id1") + "name", name, sp);
							savesp(jsonObject2.getString("card_id2") + "name", name, sp);
							savesp(jsonObject2.getString("card_id3") + "name", name, sp);

							try {
								savesp(jsonObject2.getString("card_id4") + "name", name, sp);
								savesp(jsonObject2.getString("card_id5") + "name", name, sp);
							} catch (Exception e) {

							}
							Log.i("TPATT", "创建文件夹:" + strPhotoPath);
							appBaseFun.makeRootDirectory(
									appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/" + strPhotoPath);
						} catch (Exception e) {

						}
						if (mHandler != null) {
							Message msg = new Message();
							msg.what = 0;
							Bundle bundle = new Bundle();
							int mm1 = ((i + 1) * 100) / cardmax;
							bundle.putInt("num", mm1);
							msg.setData(bundle);
							mHandler.sendMessage(msg);
						}
					}

				}
				loadPicWW(settingPara, httpApp, sp, appBaseFun, mHandler);
			} else {
				// if (TtsPlay.istts()) {
				// TtsPlay.speaktts("网络异常");
				// }

			}

		} catch (Exception e) {
			Log.i("TPATT", "加载万维数据出错"+e.toString());
		}
		return false;
	}

	private static void loadPicWW(SettingPara settingPara, HttpApp httpApp, SharedPreferences sp, AppBaseFun appBaseFun,
								  Handler mHandler) {
		SharedPreferences.Editor se = sp.edit();
		String loacalsd = appBaseFun.getPhoneCardPath();
		String strUrl;
		String strRes;
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss").format(new Date(System.currentTimeMillis()));

		String province=settingPara.getProvincCode();
		if (province!=null&&!"".equals(province)) {
			strUrl = settingPara.getCardPhotoInfoUrl() + "?provinceCode=" + province + "&deviceID="
					+ settingPara.getDevicID() + "&timeStamp=" + timeStamp;
		}else {
			strUrl = settingPara.getCardPhotoInfoUrl() + "?provinceCode=62000000"  + "&deviceID="
					+ settingPara.getDevicID() + "&timeStamp=" + timeStamp;

		}


		Log.i("TPATT", "下载卡图片信息请求:" + strUrl);
		if (settingPara.getCardPhotoInfoUrl().startsWith("https")) {
			strRes=httpApp.gethttps(strUrl);
		}else {
			strRes = httpApp.getSendAndReceive(strUrl);
		}

		Log.i("TPATT", "下载卡图片信息返回:" + strRes);
		if (strRes != null) {
			try {
				String strdata;
				String strPhotoPath;
				String userName;
				String photoUrl;
				String strparentsPhotos;
				String parentsName;
				String parentsPhotoUrl;
				strdata = new JSONObject(strRes).getString("code");
				if (strdata.equals("200")) {
					Log.i("TPATT", "下载卡图片信息请求:" + "code 200");
					strdata = new JSONObject(strRes).getString("msg");
					if (strdata.equals("交易成功")) {
						Log.i("TPATT", "下载卡图片信息请求:" + "交易成功");
						strdata = new JSONObject(strRes).getString("result");
						if (strdata != null && !"null".equals(strdata)&&!strdata.equals("[]")) {
							Gson gson = new Gson();
							List<PicCard> pi = gson.fromJson(strdata, new TypeToken<ArrayList<PicCard>>() {
							}.getType());
							Log.i("hhg", "...." + pi);
							savesp("picimfo", strdata, sp);
							JSONArray jsonArray = new JSONArray(strdata);
							int picmax = jsonArray.length();
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);// 家长图片保存文件夹
								strPhotoPath = jsonObject2.getString("userId");// 与卡信息中的child_id一致
								userName = jsonObject2.getString("userName");// 学生姓名
								photoUrl = jsonObject2.getString("photoUrl");// 学生图片下载路径
								if (photoUrl != null && !"".equals(photoUrl)) {
									savesp(strPhotoPath + "userpic", photoUrl, sp);
								} else {
									savesp(strPhotoPath + "userpic", null, sp);
								}
								if (photoUrl.length() > 0) {
									if (photoUrl.endsWith(".jpg")) {
										Log.i("TPATT", "下载卡图片信息请求0:学生=" + userName + ";路径=" + photoUrl);
										if (!appBaseFun.fileIsExists(loacalsd + "/tpatttp/CardInfo/Photo/" + strPhotoPath
												+ "/" + filename(photoUrl))) {
											Bitmap mBitmap = httpApp.getNetWorkBitmap(photoUrl);
											if (mBitmap != null) {
												boolean issu = appBaseFun.saveBitmapjpg(mBitmap,
														loacalsd + "/tpatttp/CardInfo/Photo/" + strPhotoPath + "/"
																+ filename(photoUrl));
												pi.get(i).setIssucc(issu);
											}
										} else {
											pi.get(i).setIssucc(true);
										}
									} else {
										Log.i("TPATT", "下载卡图片信息请求1:学生=" + userName + ";路径=" + photoUrl);
									}
								} else {
									Log.i("TPATT", "下载卡图片信息请求2:学生=" + userName + ";路径=" + photoUrl);
								}
								String schoolName = jsonObject2.getString("schoolName");// 学校名称
								savesp("school", schoolName, sp);
								savesp(strPhotoPath + "school", schoolName, sp);
								@SuppressWarnings("unused")
								String addTime = jsonObject2.getString("addTime");// 添加时间
								strparentsPhotos = jsonObject2.getString("parentsPhotos");// 家长图片parentsPhotos
								JSONArray jsonArrayPhotos = new JSONArray(strparentsPhotos);
								try {
									for (int j2 = jsonArrayPhotos.length(); j2 < 6; j2++) {
										se.remove(strPhotoPath + j2);
										se.commit();
									}
								} catch (Exception e) {
								}
								for (int j = 0; j < jsonArrayPhotos.length(); j++) {
									JSONObject jsonObjectparents = (JSONObject) jsonArrayPhotos.opt(j);
									parentsName = jsonObjectparents.getString("userName");// 家长名称
									parentsPhotoUrl = jsonObjectparents.getString("photoUrl");// 家长图片下载路径
									Log.i("strPhotoPath", strPhotoPath + j + "........." + parentsPhotoUrl);
									savesp(strPhotoPath + j, parentsPhotoUrl, sp);
									if (parentsPhotoUrl.length() > 0) {
										if (parentsPhotoUrl.endsWith(".jpg")) {
											Log.i("TPATT", "下载卡图片信息请求0:家长=" + parentsName + ";路径=" + parentsPhotoUrl);
											if (parentsPhotoUrl.startsWith("http://wx.qlogo.cn")) {
												String sub = parentsPhotoUrl.substring(25, parentsPhotoUrl.length());
												String mm = sub.replaceAll("/", "_");
												Log.i("Tappp", "截取后的图片:" + sub + "   截取/后:" + mm);
												if (!appBaseFun.fileIsExists(loacalsd + "/tpatttp/CardInfo/Photo/"
														+ strPhotoPath + "/" + mm)) {
													Bitmap mBitmap = httpApp.getNetWorkBitmap(parentsPhotoUrl);
													if (mBitmap != null) {
														boolean isok = appBaseFun.saveBitmapjpg(mBitmap, loacalsd
																+ "/tpatttp/CardInfo/Photo/" + strPhotoPath + "/" + mm);
														pi.get(i).getParentsPhotos().get(j).setIssucc(isok);
													}
												} else {
													pi.get(i).getParentsPhotos().get(j).setIssucc(true);
												}
											} else {
												if (!appBaseFun.fileIsExists(loacalsd + "/tpatttp/CardInfo/Photo/"
														+ strPhotoPath + "/" + filename(parentsPhotoUrl))) {
													Bitmap mBitmap = httpApp.getNetWorkBitmap(parentsPhotoUrl);
													if (mBitmap != null) {
														boolean isok = appBaseFun.saveBitmapjpg(mBitmap,
																loacalsd + "/tpatttp/CardInfo/Photo/" + strPhotoPath + "/"
																		+ filename(parentsPhotoUrl));
														pi.get(i).getParentsPhotos().get(j).setIssucc(isok);
													}
												} else {
													pi.get(i).getParentsPhotos().get(j).setIssucc(true);
												}
											}
										} else {
											Log.i("TPATT", "下载卡图片信息请求1:家长=" + parentsName + ";路径=" + parentsPhotoUrl);
										}
									} else {
										Log.i("TPATT", "下载卡图片信息请求2:家长=" + parentsName + ";路径=" + parentsPhotoUrl);
									}
								}
								if (mHandler != null) {
									int mm = ((i + 1) * 100) / picmax;
									Message msg = new Message();
									if (mm == 100) {
										msg.what = 4;
									} else {
										msg.what = 1;
										Bundle bundle = new Bundle();
										bundle.putInt("num", mm);
										msg.setData(bundle);
									}
									mHandler.sendMessage(msg);
								}
							}
						} else {
							Log.i("TPATT", "下载卡图片为空");
							if (mHandler != null) {
								Message message = new Message();
								message.what = 4;
								mHandler.sendMessage(message);
							}
						}
					}
				} else {
					Log.i("TPATT", "下载卡图片信息异常");
					if (mHandler != null) {
						Message message = new Message();
						message.what = 4;
						mHandler.sendMessage(message);
					}
				}
			} catch (JSONException e) {
				Log.i("TPATT", "下载卡图片信息异常");
				e.printStackTrace();
				if (mHandler != null) {
					Message message = new Message();
					message.what = 4;
					mHandler.sendMessage(message);
				}
			}
		} else {
			if (mHandler != null) {
				Message message = new Message();
				message.what = 4;
				mHandler.sendMessage(message);
			}
		}
	}

	private static void clearDataWW(List<ChildCard> listca, List<ChildCard> lc, SharedPreferences sp) {
		SharedPreferences.Editor se = sp.edit();
		try {
			if (listca != null && listca.size() > 0 && lc != null) {
				for (int i = 0; i < lc.size(); i++) {
					String mid = lc.get(i).getCard_id();
					String mid1 = lc.get(i).getCard_id1();
					String mid2 = lc.get(i).getCard_id2();
					String mid3 = lc.get(i).getCard_id3();
					String mid4 = lc.get(i).getCard_id4();
					String mid5 = lc.get(i).getCard_id5();
					boolean iscom = true;
					boolean iscom1 = true;
					boolean iscom2 = true;
					boolean iscom3 = true;
					boolean iscom4 = true;
					boolean iscom5 = true;
					for (int j = 0; j < listca.size(); j++) {
						String fid = listca.get(i).getCard_id();
						String fid1 = listca.get(i).getCard_id1();
						String fid2 = listca.get(i).getCard_id2();
						String fid3 = listca.get(i).getCard_id3();
						String fid4 = listca.get(i).getCard_id4();
						String fid5 = listca.get(i).getCard_id5();
						if (fid == null && fid1 == null && fid2 == null && fid3 == null && fid4 == null
								&& fid5 == null) {
							continue;
						}
						if (mid.equals(fid) || mid.equals(fid1) || mid.equals(fid2) || mid.equals(fid3)
								|| mid.equals(fid4) || mid.equals(fid5)) {
							iscom = true;
						} else {
							iscom = false;
						}
						if (mid1.equals(fid) || mid1.equals(fid1) || mid1.equals(fid2) || mid1.equals(fid3)
								|| mid1.equals(fid4) || mid1.equals(fid5)) {
							iscom1 = true;
						} else {
							iscom1 = false;
						}
						if (mid2.equals(fid) || mid2.equals(fid1) || mid2.equals(fid2) || mid2.equals(fid3)
								|| mid2.equals(fid4) || mid2.equals(fid5)) {
							iscom2 = true;
						} else {
							iscom2 = false;
						}
						if (mid3.equals(fid) || mid3.equals(fid1) || mid3.equals(fid2) || mid3.equals(fid3)
								|| mid3.equals(fid4) || mid3.equals(fid5)) {
							iscom3 = true;
						} else {
							iscom3 = false;
						}
						if (mid4.equals(fid) || mid4.equals(fid1) || mid4.equals(fid2) || mid4.equals(fid3)
								|| mid4.equals(fid4) || mid4.equals(fid5)) {
							iscom4 = true;
						} else {
							iscom4 = false;
						}
						if (mid5.equals(fid) || mid5.equals(fid1) || mid5.equals(fid2) || mid5.equals(fid3)
								|| mid5.equals(fid4) || mid5.equals(fid5)) {
							iscom5 = true;
						} else {
							iscom5 = false;
						}
					}
					if (!iscom) {
						se.remove(mid + "idimfo");
						se.remove(mid + "name");
						se.commit();
					}
					if (!iscom1) {
						se.remove(mid1 + "idimfo");
						se.remove(mid1 + "name");
						se.commit();
					}
					if (!iscom2) {
						se.remove(mid2 + "idimfo");
						se.remove(mid2 + "name");
						se.commit();
					}
					if (!iscom3) {
						se.remove(mid3 + "idimfo");
						se.remove(mid3 + "name");
						se.commit();
					}
					if (!iscom4) {
						se.remove(mid4 + "idimfo");
						se.remove(mid4 + "name");
						se.commit();
					}
					if (!iscom5) {
						se.remove(mid5 + "idimfo");
						se.remove(mid5 + "name");
						se.commit();
					}
				}
			}
		} catch (Exception e) {

		}
	}

	public static boolean loadDataNL(Handler mHandler, SharedPreferences sp) {
		savesp("studentin", "{}", sp);
		Message message = new Message();
		message.what = 4;
		mHandler.sendMessage(message);
		return true;
	}

	public static boolean loadDataZW(Handler mHandler, SharedPreferences sp, String imei) {
		String strRes = null;
		List<ZwCardInfo> lc = null;
		List<ZwCardInfo> listca = null;
		HttpApp httpApp = new HttpApp();
		AppBaseFun appBaseFun = new AppBaseFun();
		Log.i("imei", imei);
		try {
			strRes = ZwService.Attinfo(imei, "attend/allCardInfo");
			Log.i("TPATT", "下载卡信息返回:" + strRes);
			if (strRes != null) {
				// if (TtsPlay.istts()) {
				// TtsPlay.speaktts("网络正常");
				// }
				JsonValidator jsonValidator = new JsonValidator();
				try {
					boolean isjson = jsonValidator.validate(strRes);
					if (!isjson) {
						if (mHandler != null) {
							Message mg = new Message();
							mg.what = 6;
							Bundle bundle = new Bundle();
							bundle.putString("data", strRes);
							mg.setData(bundle);
							mHandler.sendMessage(mg);
						}
						Log.i("tapp", "不是正确josn格式：" + strRes);
						WriteUnit.loadlist("不是正确josn格式：" + strRes);
						return false;
					}
				} catch (Exception e) {

				}
				JSONObject jsonObject;
				Gson gson = new Gson();
				String cc = sp.getString("studentin", null);
				if (cc != null) {
					try {
						lc = gson.fromJson(cc, new TypeToken<ArrayList<ZwCardInfo>>() {
						}.getType());
					} catch (Exception e) {
						Log.e("tapp", "studentin数据出错："+e.toString());
					}

				}

				jsonObject = new JSONObject(strRes);
				if (jsonObject.getString("status_code") != null && jsonObject.getString("status_code").equals("200")) {
					String name = "";
					String strPhotoPath;

					String strdata = new JSONObject(strRes).getString("result");
					savesp("studentin", strdata, sp);
					WriteUnit.loadlist("下载卡信息" + strdata);
					listca = gson.fromJson(strdata, new TypeToken<ArrayList<ZwCardInfo>>() {
					}.getType());
					Log.i("msg", "" + listca);

					clearDataZW(lc, listca, sp);

					JSONArray jsonArray = new JSONArray(strdata);
					Log.i("TPATT", "下载卡信息请求:jsonArray.length=" + jsonArray.length());
					Log.i("TPATT", "下载卡信息请求:listca.length=" + listca.size());

					WriteUnit.loadlist("下载卡信息请求:jsonArray.length=" + jsonArray.length());
					WriteUnit.loadlist("下载卡信息请求:listca.length=" + listca.size());
					savesp("rundata", "no", sp);
					for (int i = 0; i < listca.size(); i++) {

						try {

							try {
								name = listca.get(i).getBabyName();
							} catch (Exception e) {

							}

							Log.i("TPATT", "学生姓名:" + name);

							String[] cardNolist = listca.get(i).getCardNo();
							strPhotoPath = listca.get(i).getBabyUuid();
							for (int j = 0; j < cardNolist.length; j++) {
								try {
									savesp(cardNolist[j] + "name", name, sp);
								} catch (Exception e) {
								}
								try {
									savesp(cardNolist[j] + "idimfo", strPhotoPath, sp);
								} catch (Exception e) {
								}
							}
							WriteUnit.loadlist("卡信息加载次数是:" + i);
							Log.i("TPATT", "创建文件夹:" + strPhotoPath);
							appBaseFun.makeRootDirectory(
									appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/" + strPhotoPath);

							String photoUrl = listca.get(i).getBabyPic();
							if (photoUrl != null && !"".equals(photoUrl)) {
								savesp(strPhotoPath + "userpic", photoUrl, sp);
							} else {
								savesp(strPhotoPath + "userpic", null, sp);
							}

							if (photoUrl != null && photoUrl.length() > 0) {
								if (photoUrl.endsWith(".jpg")) {
									Log.i("TPATT", "下载卡图片信息请求0:学生=" + name + ";路径=" + photoUrl);
									if (!appBaseFun.fileIsExists(appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/"
											+ strPhotoPath + "/" + filename(photoUrl))) {
										Bitmap mBitmap = httpApp.getNetWorkBitmap(photoUrl);
										if (mBitmap != null) {
											@SuppressWarnings("unused")
											boolean issu = appBaseFun.saveBitmapjpg(mBitmap,
													appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/" + strPhotoPath
															+ "/" + filename(photoUrl));
											// pi.get(i).setIssucc(issu);
										}
									}

								} else {
									Log.i("TPATT", "下载卡图片信息请求1:学生=" + name + ";路径=" + photoUrl);
								}
							} else {
								Log.i("TPATT", "下载卡图片信息请求2:学生=" + name + ";路径=" + photoUrl);
							}

							List<ParentArray> pa = listca.get(i).getParentArray();

							for (int j = 0; j < 6; j++) {

								savesp(strPhotoPath + j, null, sp);

							}

							for (int j = 0; j < pa.size(); j++) {

								String path = pa.get(j).getParent_pic();

								if (path != null && path.length() > 0) {
									savesp(strPhotoPath + j, filename(path), sp);
									if (path.endsWith(".jpg")) {
										Log.i("TPATT", "下载卡图片信息请求0:学生=" + name + ";路径=" + path);
										if (!appBaseFun.fileIsExists(appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/"
												+ strPhotoPath + "/" + filename(path))) {
											Bitmap mBitmap = httpApp.getNetWorkBitmap(path);
											if (mBitmap != null) {
												@SuppressWarnings("unused")
												boolean issu = appBaseFun.saveBitmapjpg(mBitmap,
														appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/" + strPhotoPath
																+ "/" + filename(path));
											}
										}

									} else {
										Log.i("TPATT", "下载卡图片信息请求1:家长=" + name + ";路径=" + photoUrl);
									}
								} else {
									Log.i("TPATT", "下载卡图片信息请求2:家长=" + name + ";路径=" + photoUrl);
								}

							}

						} catch (Exception e) {
							WriteUnit.loadlist("解析数据出现异常：" + e.getMessage());
						}
						if (mHandler != null) {
							Message msg = new Message();
							msg.what = 0;
							Bundle bundle = new Bundle();
							int mm1 = ((i + 1) * 50) / listca.size();
							bundle.putInt("num", mm1);
							msg.setData(bundle);
							mHandler.sendMessage(msg);
						}

					}
					savesp("rundata", "ok", sp);

				}
				loadTeacherZW(imei, mHandler, httpApp, sp, appBaseFun);
				return true;
			} else {
				// mhandle.postDelayed(net, 1000 * 30);
			}

		} catch (Exception e) {

		}
		return false;
	}

	private static boolean loadTeacherZW(String imei, Handler mHandler, HttpApp httpApp, SharedPreferences sp,
										 AppBaseFun appBaseFun) {

		String strRes = null;
		List<TeacherInfo> listtc = null;
		List<TeacherInfo> ltc = null;
		try {

			strRes = ZwService.Attinfo(imei, "attend/allTeacherCardInfo");
			Log.i("TPATT", "下载教师卡信息返回:" + strRes);
			if (strRes != null) {
				JsonValidator jsonValidator = new JsonValidator();
				try {
					boolean isjson = jsonValidator.validate(strRes);
					if (!isjson) {
						Log.i("tapp", "不是正确josn格式");
						WriteUnit.loadlist("不是正确josn格式为" + strRes);
					}
				} catch (Exception e) {

				}

				JSONObject jsonObject;
				Gson gson = new Gson();
				String cc = sp.getString("teacherin", null);
				if (cc != null) {
					ltc = gson.fromJson(cc, new TypeToken<ArrayList<TeacherInfo>>() {
					}.getType());
				}

				jsonObject = new JSONObject(strRes);
				if (jsonObject.getString("status_code") != null && jsonObject.getString("status_code").equals("200")) {
					String name = "";
					String strPhotoPath;

					String strdata = new JSONObject(strRes).getString("result");
					savesp("teacherin", strdata, sp);
					WriteUnit.loadlist("下载卡信息" + strdata);
					listtc = gson.fromJson(strdata, new TypeToken<ArrayList<TeacherInfo>>() {
					}.getType());
					Log.i("msg", "" + listtc);

					clearTeacherDataZW(listtc, ltc, sp);

					JSONArray jsonArray = new JSONArray(strdata);
					Log.i("TPATT", "下载卡信息请求:jsonArray.length=" + jsonArray.length());
					Log.i("TPATT", "下载卡信息请求:listtc.length=" + listtc.size());

					WriteUnit.loadlist("下载卡信息请求:jsonArray.length=" + jsonArray.length());
					WriteUnit.loadlist("下载卡信息请求:listtc.length=" + listtc.size());
					for (int i = 0; i < listtc.size(); i++) {

						try {

							try {
								name = listtc.get(i).getTeacher_name();
							} catch (Exception e) {

							}

							Log.i("TPATT", "老师姓名:" + name);

							try {
								savesp(listtc.get(i).getCard_no() + "name", name, sp);
							} catch (Exception e) {

							}
							// 家长图片保存文件夹
							strPhotoPath = listtc.get(i).getTeacher_uuid();
							try {
								savesp(listtc.get(i).getCard_no() + "idimfo", strPhotoPath, sp);
							} catch (Exception e) {

							}

							WriteUnit.loadlist("卡信息加载次数是:" + i);
							Log.i("TPATT", "创建文件夹:" + strPhotoPath);
							appBaseFun.makeRootDirectory(
									appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/" + strPhotoPath);

							String photoUrl = listtc.get(i).getPicture_url();
							if (photoUrl != null && !"".equals(photoUrl)) {
								savesp(strPhotoPath + "userpic", photoUrl, sp);
							} else {
								savesp(strPhotoPath + "userpic", null, sp);
							}

							if (photoUrl != null && photoUrl.length() > 0) {
								if (photoUrl.endsWith(".jpg")) {
									Log.i("TPATT", "下载卡图片信息请求0:学生=" + name + ";路径=" + photoUrl);
									if (!appBaseFun.fileIsExists(appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/"
											+ strPhotoPath + "/" + filename(photoUrl))) {
										Bitmap mBitmap = httpApp.getNetWorkBitmap(photoUrl);
										if (mBitmap != null) {
											@SuppressWarnings("unused")
											boolean issu = appBaseFun.saveBitmapjpg(mBitmap,
													appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/" + strPhotoPath
															+ "/" + filename(photoUrl));
										}
									}

								} else {
									Log.i("TPATT", "下载卡图片信息请求1:老师=" + name + ";路径=" + photoUrl);
								}
							} else {
								Log.i("TPATT", "下载卡图片信息请求2:老师=" + name + ";路径=" + photoUrl);
							}

						} catch (Exception e) {
							WriteUnit.loadlist("解析数据出现异常：" + e.toString());
						}
						if (mHandler != null) {
							int mm = 50 + ((i + 1) * 50) / listtc.size();
							Message msg = new Message();
							if (mm == 100) {
								msg.what = 4;
							} else {
								msg.what = 1;
								Bundle bundle = new Bundle();
								bundle.putInt("num", mm);
								msg.setData(bundle);
							}
							mHandler.sendMessage(msg);
						}
					}
					return true;
				} else {
					if (mHandler != null) {
						Message message = new Message();
						message.what = 4;
						mHandler.sendMessage(message);
					}
				}
			} else {
				if (mHandler != null) {
					Message message = new Message();
					message.what = 4;
					mHandler.sendMessage(message);
				}
			}
		} catch (Exception e) {
			if (mHandler != null) {
				Message message = new Message();
				message.what = 4;
				mHandler.sendMessage(message);
			}
		}
		return false;
	}

	private static void clearDataZW(List<ZwCardInfo> lc, List<ZwCardInfo> listca, SharedPreferences sp) {
		SharedPreferences.Editor se = sp.edit();
		try {
			if (listca != null && listca.size() > 0 && lc != null) {

				for (int i = 0; i < lc.size(); i++) {

					String[] mid = lc.get(i).getCardNo();

					boolean iscom = true;
					boolean iscom1 = true;
					boolean iscom2 = true;
					boolean iscom3 = true;
					boolean iscom4 = true;
					boolean iscom5 = true;

					for (int j = 0; j < listca.size(); j++) {

						String[] fid = listca.get(i).getCardNo();

						if (fid == null) {
							continue;
						}

						for (int k = 0; k < mid.length; k++) {

							for (int k2 = 0; k2 < fid.length; k2++) {

								if (mid[k].equals(fid[k2])) {

									if (k == 0) {
										iscom = true;
									} else if (k == 1) {
										iscom1 = true;
									} else if (k == 2) {
										iscom2 = true;
									} else if (k == 3) {
										iscom3 = true;
									} else if (k == 4) {
										iscom4 = true;
									} else if (k == 4) {
										iscom4 = true;
									} else if (k == 5) {
										iscom5 = true;
									}

								} else {

									if (k == 0) {
										iscom = false;
									} else if (k == 1) {
										iscom1 = false;
									} else if (k == 2) {
										iscom2 = false;
									} else if (k == 3) {
										iscom3 = false;
									} else if (k == 4) {
										iscom4 = false;
									} else if (k == 4) {
										iscom4 = false;
									} else if (k == 5) {
										iscom5 = false;
									}

								}

							}

						}

					}

					if (!iscom) {

						se.remove(mid[0] + "idimfo");
						se.remove(mid[0] + "name");
						se.commit();
					}

					if (!iscom1) {

						se.remove(mid[1] + "idimfo");
						se.remove(mid[1] + "name");
						se.commit();
					}
					if (!iscom2) {

						se.remove(mid[2] + "idimfo");
						se.remove(mid[2] + "name");
						se.commit();
					}
					if (!iscom3) {

						se.remove(mid[3] + "idimfo");
						se.remove(mid[3] + "name");
						se.commit();
					}
					if (!iscom4) {

						se.remove(mid[4] + "idimfo");
						se.remove(mid[4] + "name");
						se.commit();
					}

					if (!iscom5) {

						se.remove(mid[5] + "idimfo");
						se.remove(mid[5] + "name");
						se.commit();
					}

				}
			}
		} catch (Exception e) {

		}

	}

	private static void clearTeacherDataZW(List<TeacherInfo> listtc, List<TeacherInfo> ltc, SharedPreferences sp) {
		SharedPreferences.Editor se = sp.edit();
		try {
			if (listtc != null && listtc.size() > 0 && ltc != null) {

				for (int i = 0; i < ltc.size(); i++) {

					String mid = ltc.get(i).getCard_no();

					boolean iscom = true;

					for (int j = 0; j < listtc.size(); j++) {

						String fid = listtc.get(i).getCard_no();

						if (fid == null) {
							continue;
						}
						if (mid.equals(fid)) {

							iscom = true;

						} else {
							iscom = false;
						}

					}
					if (!iscom) {
						se.remove(mid + "idimfo");
						se.remove(mid + "name");
						se.commit();
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public static void startATTService(Context packageContext, SettingPara settingPara) {
		if (settingPara.getAtt_pic_platform() == 0) {
			Intent intents = new Intent(packageContext, TelpoService.class);
			packageContext.startService(intents);
		} else if (settingPara.getAtt_pic_platform() == 1) {
			Intent intents = new Intent(packageContext, NlServer.class);
			packageContext.startService(intents);
		} else if (settingPara.getAtt_pic_platform() == 2) {
			Intent intents = new Intent(packageContext, ZwService.class);
			packageContext.startService(intents);
		}
	}

	public static void stopATTService(Context packageContext, SettingPara settingPara) {
		if (settingPara.getAtt_pic_platform() == 0) {
			Intent intents = new Intent(packageContext, TelpoService.class);
			packageContext.stopService(intents);
		} else if (settingPara.getAtt_pic_platform() == 1) {
			Intent intents = new Intent(packageContext, NlServer.class);
			packageContext.stopService(intents);
		} else if (settingPara.getAtt_pic_platform() == 2) {
			Intent intents = new Intent(packageContext, ZwService.class);
			packageContext.stopService(intents);
		}
	}

	private static void savesp(String key, String value, SharedPreferences sp) {
		SharedPreferences.Editor se = sp.edit();
		se.putString(key, value);
		se.commit();
	}

	private static String filename(String url) {
		if (url == null) {
			return null;
		}
		return url.substring(url.lastIndexOf("/") + 1);
	}
}
