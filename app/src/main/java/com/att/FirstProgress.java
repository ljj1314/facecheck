package com.att;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.att.act.ChildCard;
import com.att.act.PicCard;
import com.att.act.TtsPlay;
import com.att.act.WriteUnit;
import com.att.server.NlServer;
import com.att.server.TelpoService;
import com.att.server.ZwService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirstProgress extends Activity {

	private SharedPreferences.Editor se = null;
	private Handler mHandler = null;
	private TextView cardnum, picnum;
	ExecutorService transThread;
	private int cardmax = 0, picmax = 0;
	private ProgressBar cardload = null;
	private ProgressBar picload = null;
	private SharedPreferences sp;
	private ProgressDialog pd;
	private SettingPara settingPara = new SettingPara();
	// private TextView tvversion;

	// private LinearLayout pbly=null;
	private String loacalsd = null;
	private AppBaseFun appBaseFun = new AppBaseFun();
	private HttpApp httpApp = new HttpApp();
	private List<ChildCard> listca = null;
	private List<ChildCard> lc = null;
	Timer timer;
	private int times = 0;
	private Handler showHandler = null;
	private TextView tverror = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progressly);

		pd = new ProgressDialog(FirstProgress.this);
		pd.setMessage("正在连接平台中");

		// pd.setCancelable(false);
		pd.show();

		loacalsd = appBaseFun.getPhoneCardPath();

		cardload = (ProgressBar) findViewById(R.id.cardidload);
		picload = (ProgressBar) findViewById(R.id.picload);
		transThread = Executors.newSingleThreadExecutor();

		cardnum = (TextView) findViewById(R.id.cardnum);
		picnum = (TextView) findViewById(R.id.picnum);
		tverror = (TextView) findViewById(R.id.tverror);
		cardnum.setText("0%");
		picnum.setText("0%");

		sp = getSharedPreferences("json", Activity.MODE_PRIVATE);
		se = sp.edit();

		mHandler = new Handler() {

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:

						int pix = msg.getData().getInt("num");
						cardload.setProgress(pix);
						int mm1 = (pix * 100) / cardmax;
						Log.i(".....", "..." + mm1);
						cardnum.setText(mm1 + "%");

						break;

					case 1:

						int px = msg.getData().getInt("num");
						picload.setProgress(px);
						int mm = (px * 100) / picmax;
						Log.i(".....", "..." + mm);
						picnum.setText(mm + "%");

						if (mm == 100) {

							if (settingPara.getAtt_pic_platform() == 0) {
								Intent intents = new Intent(FirstProgress.this, TelpoService.class);
								startService(intents);
							} else if (settingPara.getAtt_pic_platform() == 1) {
								Intent intents = new Intent(FirstProgress.this, NlServer.class);
								startService(intents);
							} else if (settingPara.getAtt_pic_platform() == 2) {
								Intent intents = new Intent(FirstProgress.this, ZwService.class);
								startService(intents);
							}

							try {
								Thread.sleep(2500);
							} catch (InterruptedException e) {

								e.printStackTrace();
							}
							// 用线程暂停3秒来模拟做了一个耗时3秒的检测操作,为了省时间，改为1秒
							final Intent intent = new Intent(FirstProgress.this, MainIdleActivity.class);

							// 如果之前启动过这个Activity，并还没有被destroy的话，而是无论是否存在，都重新启动新的activity
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							// 获取应用的上下文，生命周期是整个应用，应用结束才会结束
							startActivity(intent);// 跳转

							FirstProgress.this.finish();// 结束本欢迎画面Activity

						}

						break;

					case 2:

						cardload.setMax(3);
						cardload.setProgress(3);
						cardnum.setText("100%");
						break;

					case 3:

						// pbly.setVisibility(View.VISIBLE);

						pd.dismiss();

						break;

					case 4:

						picload.setMax(3);
						picload.setProgress(3);
						picnum.setText("100%");

						if (settingPara.getAtt_pic_platform() == 0) {
							Intent intents = new Intent(FirstProgress.this, TelpoService.class);
							startService(intents);
						} else if (settingPara.getAtt_pic_platform() == 1) {
							Intent intents = new Intent(FirstProgress.this, NlServer.class);
							startService(intents);
						} else if (settingPara.getAtt_pic_platform() == 2) {
							Intent intents = new Intent(FirstProgress.this, ZwService.class);
							startService(intents);
						}

//					TtsPlay.speaktts("数据初始化成功");

						try {
							Thread.sleep(2500);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						// 用线程暂停3秒来模拟做了一个耗时3秒的检测操作,为了省时间，改为1秒
						final Intent intent = new Intent(FirstProgress.this, MainIdleActivity.class);

						// 如果之前启动过这个Activity，并还没有被destroy的话，而是无论是否存在，都重新启动新的activity
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						// 获取应用的上下文，生命周期是整个应用，应用结束才会结束
						startActivity(intent);// 跳转

						FirstProgress.this.finish();// 结束本欢迎画面Activity

						break;

					case 5:

						Toast.makeText(FirstProgress.this, "无法连接到平台，请退出APP重试", Toast.LENGTH_SHORT).show();

						break;

					case 6:
						pd.dismiss();
						String dataString = msg.getData().getString("data");
						// Toast.makeText(SetActivity.this, dataString,
						// Toast.LENGTH_SHORT).show();
						tverror.setText(dataString);
						break;

					default:
						break;
				}

			}

		};

		// transThread.submit(thread);

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				times++;

				if (times > 4) {
					Message mge = new Message();
					mge.what = 5;
					mHandler.sendMessage(mge);

				}

				if (pd.isShowing()) {
					Message message1 = new Message();
					message1.what = 0;
					showHandler.sendMessage(message1);

				}

			}
		}, 1000 * 50, 1000 * 50);

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
	}

	// Thread thread=new Thread(new Runnable() {
	//
	// public void run() {
	//
	// updatedata();
	// }
	// });

	private void updatedata() {

		String strUrl = null;
		String strRes = null;

		if (MainIdleActivity.isNetworkAvailables(getApplicationContext(), appBaseFun, settingPara)) {

			Message mg = new Message();
			mg.what = 6;
			Bundle bundle = new Bundle();
			bundle.putString("data", "网络异常，请检查网络配置");
			mg.setData(bundle);
			mHandler.sendMessage(mg);
			return;

		}

		strUrl = settingPara.getCardInfoUrl() + "?SchoolID=" + settingPara.getSchoolID() + "&ProvinceCode="
				+ settingPara.getProvincCode();
		try {
			// if ( sp.getString("schoolname", null)==null) {
			// return;
			// }
			Log.i("TPATT", "下载卡信息请求:" + strUrl);
			strRes = httpApp.getSendAndReceive(strUrl);
			Log.i("TPATT", "下载卡信息返回:" + strRes);

			if (strRes != null) {

				JsonValidator jsonValidator = new JsonValidator();
				try {
					boolean isjson = jsonValidator.validate(strRes);
					if (!isjson) {
						Message mg = new Message();
						mg.what = 6;
						Bundle bundle = new Bundle();
						bundle.putString("data", strRes);
						mg.setData(bundle);
						mHandler.sendMessage(mg);
						Log.i("tapp", "不是正确josn格式");
						// WriteUnit.loadlist("不是正确josn格式" );
						WriteUnit.loadlist("不是正确josn格式为" + strRes);
						// appBaseFun.writeinfile("不是正确josn格式" );
						// appBaseFun.writeinfile("不是正确josn格式为"+strRes);
						return;
					}
				} catch (Exception e) {

				}

				Message mji = new Message();
				mji.what = 3;
				mHandler.sendMessage(mji);

				// savesp("childimfo", strRes);
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

					savesp("studentin", strdata);

					if (cc != null && cc.equals(strdata)) {
						Log.i("tapp", "数据一样，不更新");
						WriteUnit.loadlist("数据一样，不更新");
						// appBaseFun.writeinfile("数据一样，不更新" );
						Message msg = new Message();
						msg.what = 2;
						// Bundle bundle=new Bundle();
						// bundle.putInt("num", i+1);
						// msg.setData(bundle);
						mHandler.sendMessage(msg);
						loadpic();
						return;
					}

					listca = gson.fromJson(strdata, new TypeToken<ArrayList<ChildCard>>() {
					}.getType());
					Log.i("msg", "" + listca);

					cleardata();

					JSONArray jsonArray = new JSONArray(strdata);
					Log.i("TPATT", "下载卡信息请求:jsonArray.length=" + String.valueOf(jsonArray.length()));
					cardload.setMax(jsonArray.length());
					cardmax = jsonArray.length();
					savesp("allnum", "" + jsonArray.length());
					for (int i = 0; i < jsonArray.length(); i++) {

						try {

							JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
							name = jsonObject2.getString("child_name");
							Log.i("TPATT", "学生姓名:" + name);

							// cardid = jsonObject2.getString("card_id");
							// 家长图片保存文件夹
							strPhotoPath = jsonObject2.getString("child_id");
							savesp(jsonObject2.getString("card_id") + "idimfo", strPhotoPath);
							savesp(jsonObject2.getString("card_id1") + "idimfo", strPhotoPath);
							savesp(jsonObject2.getString("card_id2") + "idimfo", strPhotoPath);
							savesp(jsonObject2.getString("card_id3") + "idimfo", strPhotoPath);
							try {
								savesp(jsonObject2.getString("card_id4") + "idimfo", strPhotoPath);
								savesp(jsonObject2.getString("card_id5") + "idimfo", strPhotoPath);
							} catch (Exception e) {

							}

							savesp(jsonObject2.getString("card_id") + "name", name);
							savesp(jsonObject2.getString("card_id1") + "name", name);
							savesp(jsonObject2.getString("card_id2") + "name", name);
							savesp(jsonObject2.getString("card_id3") + "name", name);

							try {
								savesp(jsonObject2.getString("card_id4") + "name", name);
								savesp(jsonObject2.getString("card_id5") + "name", name);
							} catch (Exception e) {

							}

							Log.i("TPATT", "创建文件夹:" + strPhotoPath);
							appBaseFun.makeRootDirectory(loacalsd + "/tpatt/CardInfo/Photo/" + strPhotoPath);

						} catch (Exception e) {

						}

						Message msg = new Message();
						msg.what = 0;
						Bundle bundle = new Bundle();
						bundle.putInt("num", i + 1);
						msg.setData(bundle);
						mHandler.sendMessage(msg);

					}

				}
				loadpic();
			} else {
				if (TtsPlay.istts()) {
					TtsPlay.speaktts("网络异常");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.i("llllll", e.getMessage());
		}

		transThread.shutdown();
	}

	private void savesp(String key, String value) {

		se.putString(key, value);
		se.commit();

	}

	private void cleardata() {

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
								|| mid.equals(fid4) || mid.equals(fid5)

								) {

							iscom = true;

						} else {
							iscom = false;
						}

						if (mid1.equals(fid) || mid1.equals(fid1) || mid1.equals(fid2) || mid1.equals(fid3)
								|| mid1.equals(fid4) || mid1.equals(fid5)

								) {

							iscom1 = true;

						} else {
							iscom1 = false;
						}

						if (mid2.equals(fid) || mid2.equals(fid1) || mid2.equals(fid2) || mid2.equals(fid3)
								|| mid2.equals(fid4) || mid2.equals(fid5)

								) {

							iscom2 = true;

						} else {
							iscom2 = false;
						}

						if (mid3.equals(fid) || mid3.equals(fid1) || mid3.equals(fid2) || mid3.equals(fid3)
								|| mid3.equals(fid4) || mid3.equals(fid5)

								) {

							iscom3 = true;

						} else {
							iscom3 = false;
						}

						if (mid4.equals(fid) || mid4.equals(fid1) || mid4.equals(fid2) || mid4.equals(fid3)
								|| mid4.equals(fid4) || mid4.equals(fid5)

								) {

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

	private void loadpic() {

		String strUrl;
		String strRes;

		String timeStamp = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss").format(new Date(System.currentTimeMillis()));
		strUrl = settingPara.getCardPhotoInfoUrl() + "?provinceCode=" + settingPara.getProvincCode() + "&deviceID="
				+ settingPara.getDevicID() + "&timeStamp=" + timeStamp;

		Log.i("TPATT", "下载卡图片信息请求:" + strUrl);
		strRes = httpApp.getSendAndReceive(strUrl);
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

						if (strdata != null && !"null".equals(strdata)) {

							Gson gson = new Gson();
							List<PicCard> pi = gson.fromJson(strdata, new TypeToken<ArrayList<PicCard>>() {
							}.getType());
							Log.i("hhg", "...." + pi);
							savesp("picimfo", strdata);

							// String mm=gson.toJson(pi);
							// Log.i("ocmm", mm);
							//
							JSONArray jsonArray = new JSONArray(strdata);
							picload.setMax(jsonArray.length());
							picmax = jsonArray.length();
							for (int i = 0; i < jsonArray.length(); i++) {

								JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);

								// 家长图片保存文件夹
								strPhotoPath = jsonObject2.getString("userId");// 与卡信息中的child_id一致

								// 学生姓名
								userName = jsonObject2.getString("userName");

								// 学生图片下载路径
								photoUrl = jsonObject2.getString("photoUrl");
								if (photoUrl != null && !"".equals(photoUrl)) {
									savesp(strPhotoPath + "userpic", photoUrl);
								} else {
									savesp(strPhotoPath + "userpic", null);
								}
								if (photoUrl.length() > 0) {
									if (photoUrl.endsWith(".jpg")) {
										Log.i("TPATT", "下载卡图片信息请求0:学生=" + userName + ";路径=" + photoUrl);
										if (!appBaseFun.fileIsExists(loacalsd + "/tpatt/CardInfo/Photo/" + strPhotoPath
												+ "/" + filename(photoUrl))) {
											Bitmap mBitmap = httpApp.getNetWorkBitmap(photoUrl);
											if (mBitmap != null) {
												boolean issu = appBaseFun.saveBitmapjpg(mBitmap,
														loacalsd + "/tpatt/CardInfo/Photo/" + strPhotoPath + "/"
																+ filename(photoUrl));
												pi.get(i).setIssucc(issu);
											}
										}

										else {
											pi.get(i).setIssucc(true);
										}
									} else {
										Log.i("TPATT", "下载卡图片信息请求1:学生=" + userName + ";路径=" + photoUrl);
									}
								} else {
									Log.i("TPATT", "下载卡图片信息请求2:学生=" + userName + ";路径=" + photoUrl);
								}

								// 学校名称
								String schoolName = jsonObject2.getString("schoolName");
								savesp("school", schoolName);
								savesp(strPhotoPath + "school", schoolName);
								// 添加时间
								@SuppressWarnings("unused")
								String addTime = jsonObject2.getString("addTime");

								// 家长图片parentsPhotos
								strparentsPhotos = jsonObject2.getString("parentsPhotos");
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

									// String
									// jj=jsonObjectparents.getString("");

									// studentId
									// String studentId =
									// jsonObjectparents.getString("studentId");//与卡信息中的child_id/userId一致

									// 家长名称
									parentsName = jsonObjectparents.getString("userName");

									// 家长图片下载路径
									parentsPhotoUrl = jsonObjectparents.getString("photoUrl");
									Log.i("strPhotoPath", strPhotoPath + j + "........." + parentsPhotoUrl);
									savesp(strPhotoPath + j, parentsPhotoUrl);
									if (parentsPhotoUrl.length() > 0) {
										if (parentsPhotoUrl.endsWith(".jpg")) {
											Log.i("TPATT", "下载卡图片信息请求0:家长=" + parentsName + ";路径=" + parentsPhotoUrl);

											if (parentsPhotoUrl.startsWith("http://wx.qlogo.cn")) {
												String sub = parentsPhotoUrl.substring(25, parentsPhotoUrl.length());
												String mm = sub.replaceAll("/", "_");
												Log.i("Tappp", "截取后的图片:" + sub + "   截取/后:" + mm);

												if (!appBaseFun.fileIsExists(loacalsd + "/tpatt/CardInfo/Photo/"
														+ strPhotoPath + "/" + mm)) {
													Bitmap mBitmap = httpApp.getNetWorkBitmap(parentsPhotoUrl);
													if (mBitmap != null) {
														boolean isok = appBaseFun.saveBitmapjpg(mBitmap, loacalsd
																+ "/tpatt/CardInfo/Photo/" + strPhotoPath + "/" + mm);

														pi.get(i).getParentsPhotos().get(j).setIssucc(isok);
													}
												} else {
													pi.get(i).getParentsPhotos().get(j).setIssucc(true);
												}

											} else {

												if (!appBaseFun.fileIsExists(loacalsd + "/tpatt/CardInfo/Photo/"
														+ strPhotoPath + "/" + filename(parentsPhotoUrl))) {
													Bitmap mBitmap = httpApp.getNetWorkBitmap(parentsPhotoUrl);
													if (mBitmap != null) {
														boolean isok = appBaseFun.saveBitmapjpg(mBitmap,
																loacalsd + "/tpatt/CardInfo/Photo/" + strPhotoPath + "/"
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

									// addTime
									// String parentsAddTime =
									// jsonObjectparents.getString("addTime");

									// 保存图片信息:0表示学生姓名,1表示入园时间,2表示学校,3~7表示家长称呼
									// appBaseFun.savePhotoInfoFile(strPhotoPath,userName
									// + "\r\n" + addTime + "\r\n" +
									// schoolName);
								}

								Message msg = new Message();
								msg.what = 1;
								Bundle bundle = new Bundle();
								bundle.putInt("num", i + 1);
								msg.setData(bundle);
								mHandler.sendMessage(msg);

							}

						} else {

							Log.i("tappo", "下载卡图片为空");

							Message message = new Message();
							message.what = 4;
							mHandler.sendMessage(message);

						}
					}

				} else {
					Log.i("TPATT", "下载卡图片信息异常");

					Message message = new Message();
					message.what = 4;
					mHandler.sendMessage(message);
				}

			} catch (JSONException e) {

				Log.i("TPATT", "下载卡图片信息异常");

				e.printStackTrace();
				Message message = new Message();
				message.what = 4;
				mHandler.sendMessage(message);
			}
		} else {

			Message message = new Message();
			message.what = 4;
			mHandler.sendMessage(message);

		}

	}

	private String filename(String url) {

		if (url == null) {
			return null;
		}

		return url.substring(url.lastIndexOf("/") + 1);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (((keyCode == KeyEvent.KEYCODE_BACK)) && event.getRepeatCount() == 0) {

			Intent intent = new Intent();
			intent.setClass(FirstProgress.this, SettingParaActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();

		}
		return false;
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		timer.cancel();
		timer = null;

	}

}
