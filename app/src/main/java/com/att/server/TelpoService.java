package com.att.server;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.att.AppBaseFun;
import com.att.HelperAV;
import com.att.HttpApp;
import com.att.JsonValidator;
import com.att.MainIdleActivity;
import com.att.SettingPara;
import com.att.act.AppAction;
import com.att.act.ChildCard;
import com.att.act.Client;
import com.att.act.OKHttpUtils;
import com.att.act.PicCard;
import com.att.act.WriteUnit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jxl.Workbook;
import jxl.read.biff.BiffException;

public class TelpoService extends Service {

	private Timer getdataTimer = null;
	private SettingPara settingPara;
	private HttpApp httpApp = new HttpApp();
	private SharedPreferences sp = null;
	private SharedPreferences.Editor se = null;
	private AppBaseFun appBaseFun = new AppBaseFun();
	// private String hearttext = null;
	// private Handler handle = null;
	// private static final String TAG = "Update";
	// private ProgressDialog pBar;
	// private String appName = "";
	// private String verName = "";
	// private String appVersion = "幼儿宝ver.json";
	// private int newVerCode = 0;
	// private HttpHandler hh;
	// private boolean isdownup = false;
	// private long beattime = 0;
	// private boolean isbeat = true;
	private List<ChildCard> listca = null;
	private List<ChildCard> lc = null;
	// private boolean isnet = false;
	private Handler mhandle = new Handler();
	private int times = 1800;
	private int nettime=0;
	private boolean isok=false;

	private boolean status_platform = false;
	public static Handler sethandle=null;
	private String content=null;
	private String piccontent=null;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	public void onCreate() {

		settingPara = new SettingPara(getApplicationContext());

		Timer d = new Timer();
		d.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					File file = new File("/data/data/" + getPackageName() + "/shared_prefs");
					if (file != null) {
						File[] files = file.listFiles();// 读取
						for (File filename : files) {
							String fileName = filename.getName();
							if (fileName.endsWith(".bak")) {
								file.delete();
							}
						}
					}

				} catch (Exception e) {

				}
			}
		}, 0, 1000 * 60 * 10);

		try {

			sp = getSharedPreferences("json", Context.MODE_PRIVATE);
			se = sp.edit();
		} catch (Exception e) {

		}
		if (settingPara.getUpdatetime() != 0) {
			times = settingPara.getUpdatetime();
		}

		getdataTimer = new Timer();
		getdataTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				status_platform = getdata();
				MainIdleActivity.sendPlatformApBroadcast(getApplicationContext(), settingPara, true, status_platform);
				if (!status_platform) {
					Intent intent = new Intent();
					intent.setAction("com.telpoedu.omc.FROM_ATT_ACTION");
					intent.putExtra("type", "sync_data_error");
					if (content!=null) {
						intent.putExtra("content", "卡信息下载"+content+"|||图片下载"+piccontent);
					}
					intent.putExtra("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(new Date(System.currentTimeMillis())));
					sendBroadcast(intent);
				}
			}
		}, 1000 * 60 , times * 1000);

//		Timer timeRunCheck = new Timer();
//		timeRunCheck.schedule(new TimerTask() {
//
//			@Override
//			public void run() {
//
//				try {
//
//					String strdata = sp.getString("picimfo", "");
//					if (strdata != null && !strdata.equals("")) {
//						Gson gson = new Gson();
//						List<PicCard> pi = gson.fromJson(strdata, new TypeToken<ArrayList<PicCard>>() {
//						}.getType());
//
//						for (int i = 0; i < pi.size(); i++) {
//
//							List<ParentsPhotos> lpList = pi.get(i).getParentsPhotos();
//							String photoUrl = pi.get(i).getPhotoUrl();
//
//							if (photoUrl.length() > 0) {
//								if (photoUrl.endsWith(".jpg")||photoUrl.endsWith(".png")||photoUrl.endsWith(".jpeg")) {
//
//									if (!appBaseFun.fileIsExists(appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo/Photo/"
//											+ pi.get(i).getUserId() + "/" + filename(photoUrl))) {
//										Bitmap mBitmap = httpApp.getNetWorkBitmap(photoUrl);
//										if (mBitmap != null) {
//											boolean issu = appBaseFun.saveBitmapjpg(mBitmap,
//													appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo/Photo/"
//															+ pi.get(i).getUserId() + "/" + filename(photoUrl));
//											pi.get(i).setIssucc(issu);
//										}
//									}
//
//									else {
//										pi.get(i).setIssucc(true);
//									}
//								}
//							}
//
//							for (int j = 0; j < lpList.size(); j++) {
//
//								boolean isok = lpList.get(j).isIssucc();
//								if (!isok) {
//									String parentsPhotoUrl = lpList.get(j).getPhotoUrl();
//									String strPhotoPath = lpList.get(j).getStudentId();
//									if (parentsPhotoUrl.length() > 0) {
//										if (parentsPhotoUrl.endsWith(".jpg")||parentsPhotoUrl.endsWith(".png")||photoUrl.endsWith(".jpeg")) {
//
//											if (!appBaseFun
//													.fileIsExists(appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo/Photo/"
//															+ strPhotoPath + "/" + filename(parentsPhotoUrl))) {
//												Bitmap mBitmap = httpApp.getNetWorkBitmap(parentsPhotoUrl);
//												if (mBitmap != null) {
//													boolean issucc = appBaseFun.saveBitmapjpg(mBitmap,
//															appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo/Photo/"
//																	+ strPhotoPath + "/" + filename(parentsPhotoUrl));
//
//													pi.get(i).getParentsPhotos().get(j).setIssucc(issucc);
//												}
//											} else {
//												pi.get(i).getParentsPhotos().get(j).setIssucc(true);
//											}
//
//										}
//
//									}
//
//								}
//
//							}
//
//						}
//
//					}
//
//				} catch (Exception e) {
//
//				}
//			}
//		}, 1000 * 60 * 60, 1000 * 60 * 60);

		Timer hearb = new Timer();
		hearb.schedule(new TimerTask() {
			//
			@Override
			public void run() {

				try {

					SharedPreferences sps = getSharedPreferences("river", Context.MODE_PRIVATE);
					SharedPreferences.Editor ses = sps.edit();

					Client.main(ses, sps, se, sp);
					// appBaseFun.writeinfile("发送心跳成功");
					WriteUnit.loadlist("发送心跳成功");
				} catch (UnknownHostException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		}, 0, 1000 * settingPara.getHeartbeat());

		Timer checkOmcStatusTime = new Timer();
		checkOmcStatusTime.schedule(new TimerTask() {
			@Override
			public void run() {
				OKHttpUtils.checkOmcStatus(getApplicationContext());
				//	MainIdleActivity.sendPlatformApBroadcast(getApplicationContext(), settingPara, true, status_platform);
			}
		}, 1000 * 60*10, OKHttpUtils.CHECK_OMC_STATUS_TIME_PERIOD);

		Timer clearMoreLogTime = new Timer();
		clearMoreLogTime.schedule(new TimerTask() {
			@Override
			public void run() {
				WriteUnit.clearMoreLog();
			}
		}, 1000*60*30, 1000 * 60 * 60 * 2);

		Timer del = new Timer();
		del.schedule(new TimerTask() {

			@Override
			public void run() {

				try {

					File file = new File(appBaseFun.getPhoneCardPath() + File.separator + "tpatt/AttPhoto");
					File[] files = file.listFiles();// 读取
					List<String> filelist = new ArrayList<String>();

					Log.i("tapoo", "图片顺序是:"+files.length);

					Arrays.sort(files, new Comparator<File>() {

						public int compare(File lhs, File rhs) {
							// TODO Auto-generated method stub
							if (lhs.lastModified()<rhs.lastModified()) {
								return -1;
							}else {
								return 1;
							}


						}
					});


					for (File file2 : files) {

						if (file2.isDirectory()) {
							continue;
						} else {

							filelist.add(file2.getName());

						}

					}



					int filesize = filelist.size();
					if (filelist.size() > 1000) {
						for (int j = 0; j < filelist.size(); j++) {
							Log.i("tpattpic",  filelist.get(j));
							File fileopen = new File(
									appBaseFun.getPhoneCardPath() + File.separator + "tpatt/AttPhoto/" + filelist.get(j));
							fileopen.delete();
							filesize--;
							if (filesize < 1000) {
								break;
							}
							// Log.i("tapoo", "图片顺序是:"+filelist.get(j));
						}
					}

					// filelist=files.

					// long size =
					// appBaseFun.getAutoFileOrFilesSize(appBaseFun.getSDPath()+"/tpatt/AttPhoto");
					// Log.i("TPATT","文件夹大小:"+ String.valueOf(size) );
					// if ( size > (1000*27*1024) )
					// {
					// appBaseFun.delAllFile(appBaseFun.getSDPath()+"/tpatt/AttPhoto",1);
					// }

				} catch (Exception e) {
					e.printStackTrace();
					Log.i("tapoo", "图片顺序是出错:"+e.getMessage());
				}

			}
			// }, 1000*60*30, 1000*60*30);
		},  1000*60*30, 1000 * 60 * 30);



		sethandle=new Handler(){

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 1:
						settingPara=new SettingPara(getApplicationContext());
						break;

					default:
						break;
				}


			}
		};

		Timer net=new Timer();
		net.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String	strswingCardDtime = new SimpleDateFormat("HHmm").format(new java.util.Date());
				Log.i("tiame", strswingCardDtime);
				if (strswingCardDtime.equals("0110")) {
					if (android.os.Build.MODEL.startsWith("rk3066"))
					{
						HelperAV.exec("reboot");

					} else {
						AppBaseFun.execSuCmd("reboot");
					}
				}



			}
		}, 1000*60*5, 1000*25);


	}

//	Runnable net = new Runnable() {
//
//		public void run() {
//
//			if (MainIdleActivity.isNetworkAvailables(getApplicationContext(), appBaseFun, settingPara)) {
//				status_platform = getdata();
//				MainIdleActivity.sendPlatformApBroadcast(getApplicationContext(), settingPara,  true, status_platform);
//			} else {
//				mhandle.postDelayed(net, 1000 * 30);
//			}
//
//		}
//	};

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

	private boolean loadpic() {

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

						Gson gson = new Gson();
						List<PicCard> pi = gson.fromJson(strdata, new TypeToken<ArrayList<PicCard>>() {
						}.getType());
						Log.i("hhg", "...." + pi);
						savesp("picimfo", strdata);

						// String mm=gson.toJson(pi);
						// Log.i("ocmm", mm);
						//
						JSONArray jsonArray = new JSONArray(strdata);

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
								if (photoUrl.endsWith(".jpg")||photoUrl.endsWith(".png")||photoUrl.endsWith(".jpeg")) {
									Log.i("TPATT", "下载卡图片信息请求0:学生=" + userName + ";路径=" + photoUrl);
									if (!appBaseFun.fileIsExists(appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/"
											+ strPhotoPath + "/" + filename(photoUrl))) {
										Bitmap mBitmap = httpApp.getNetWorkBitmap(photoUrl);
										if (mBitmap != null) {
											boolean issu = appBaseFun.saveBitmapjpg(mBitmap,
													appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/" + strPhotoPath
															+ "/" + filename(photoUrl));
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

								// String jj=jsonObjectparents.getString("");

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
									if (parentsPhotoUrl.endsWith(".jpg")||parentsPhotoUrl.endsWith(".png")||photoUrl.endsWith(".jpeg")) {
										Log.i("TPATT", "下载卡图片信息请求0:家长=" + parentsName + ";路径=" + parentsPhotoUrl);

										if (parentsPhotoUrl.startsWith("http://wx.qlogo.cn")) {
											String sub = parentsPhotoUrl.substring(25, parentsPhotoUrl.length());
											String mm = sub.replaceAll("/", "_");
											Log.i("Tappp", "截取后的图片:" + sub + "   截取/后:" + mm);

											if (!appBaseFun.fileIsExists(appBaseFun.getPhoneCardPath()
													+ "/tpatttp/CardInfo/Photo/" + strPhotoPath + "/" + mm)) {
												Bitmap mBitmap = httpApp.getNetWorkBitmap(parentsPhotoUrl);
												if (mBitmap != null) {
													boolean isok = appBaseFun.saveBitmapjpg(mBitmap,
															appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/"
																	+ strPhotoPath + "/" + mm);

													pi.get(i).getParentsPhotos().get(j).setIssucc(isok);
												}
											} else {
												pi.get(i).getParentsPhotos().get(j).setIssucc(true);
											}

										} else {

											if (!appBaseFun
													.fileIsExists(appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/"
															+ strPhotoPath + "/" + filename(parentsPhotoUrl))) {
												Bitmap mBitmap = httpApp.getNetWorkBitmap(parentsPhotoUrl);
												if (mBitmap != null) {
													boolean isok = appBaseFun.saveBitmapjpg(mBitmap,
															appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/"
																	+ strPhotoPath + "/" + filename(parentsPhotoUrl));

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
								// + "\r\n" + addTime + "\r\n" + schoolName);
							}
						}
						return true;
					}
				}else{//{"code":"400","msg":"设备没有绑定学校","result":null}
					return false;
				}
			} catch (JSONException e) {

				Log.i("TPATT", "下载卡图片信息异常");
				piccontent="下载卡图片信息异常";
				e.printStackTrace();
			}
		}else {
			piccontent="没有图片信息";
		}
		return false;
	}

	private String filename(String url) {

		if (url == null) {
			return null;
		}

		return url.substring(url.lastIndexOf("/") + 1);
	}

	// check new version and update

	@Override
	public void onDestroy() {

		super.onDestroy();
		// hh.cancel();
		// TelpoService.this.stopService(name)
	}

	public static String getCurrentActivityName(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);

		// get the info from the currently running task
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

		ComponentName componentInfo = taskInfo.get(0).topActivity;
		return componentInfo.getClassName();
	}

	public void getexcel() {

		try {
			@SuppressWarnings("unused")
			Workbook book = Workbook
					.getWorkbook(new File(Environment.getExternalStorageDirectory().getPath() + "/mytest.xls"));

		} catch (BiffException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private boolean getdata() {

		String strUrl = null;
		String strRes = null;

		strUrl = settingPara.getCardInfoUrl() + "?SchoolID=" + settingPara.getSchoolID() + "&ProvinceCode="
				+ settingPara.getProvincCode();
		try {

			Log.i("TPATT", "下载卡信息请求:" + strUrl+"...."+settingPara.getPlatformID());
			if (settingPara.getCardInfoUrl().startsWith("https")) {
				strRes=httpApp.gethttps(settingPara.getCardInfoUrl() + "?SchoolID="+ settingPara.getSchoolID());
			}else {
				strRes = httpApp.getSendAndReceive(strUrl);
			}

			Log.i("TPATT", "下载卡信息返回:" + strRes);
			if (strRes != null) {
				isok=true;
				// se.clear();
				// se.commit();
				// if (TtsPlay.istts()) {
				// TtsPlay.speaktts("网络正常");
				// }
				// savesp("childimfo", strRes);
				JsonValidator jsonValidator = new JsonValidator();
				try {
					boolean isjson = jsonValidator.validate(strRes);
					if (!isjson) {
						content="不是正确josn格式" + strRes;
						Log.i("tapp", "不是正确josn格式");
						// appBaseFun.writeinfile("不是正确josn格式" );
						// appBaseFun.writeinfile("不是正确josn格式为"+strRes);
						WriteUnit.loadlist("不是正确josn格式为" + strRes);
						return false;
					}
				} catch (Exception e) {

				}

				JSONObject jsonObject;
				Gson gson = new Gson();
				String cc = sp.getString("studentin", null);
				if (cc != null) {
					lc = gson.fromJson(cc, new TypeToken<ArrayList<ChildCard>>() {
					}.getType());
				}

				jsonObject = new JSONObject(strRes).getJSONObject("_metadata");
				if (jsonObject.getString("code").equals("200")) {
					String name = "";
					String classname="";
					String strPhotoPath;
					@SuppressWarnings("unused")
					String cardid = null;

					String strdata = new JSONObject(strRes).getString("data");
					savesp("studentin", strdata);
					String rundata = sp.getString("rundata", "no");
					if (cc != null && cc.equals(strdata) && "ok".equals(rundata)) {
						Log.i("tapp", "数据一样，不更新");
						// appBaseFun.writeinfile("数据一样，不更新" );
						WriteUnit.loadlist("数据一样，不更新");
						loadpic();
						String files=appBaseFun.getPhoneCardPath()+"/duoyin.csv";

						if (appBaseFun.fileIsExists(files)) {
							AppAction.readerCsvduoy(appBaseFun.getPhoneCardPath() + "/duoyin.csv", se, listca);
						}
						return true;
					}
					// appBaseFun.writeinfile("下载卡信息"+strdata);
					WriteUnit.loadlist("下载卡信息" + strdata);
					listca = gson.fromJson(strdata, new TypeToken<ArrayList<ChildCard>>() {
					}.getType());
					Log.i("msg", "" + listca);

					cleardata();

					JSONArray jsonArray = new JSONArray(strdata);
					Log.i("TPATT", "下载卡信息请求:jsonArray.length=" + jsonArray.length());
					Log.i("TPATT", "下载卡信息请求:listca.length=" + listca.size());
					// appBaseFun.writeinfile("下载卡信息请求:jsonArray.length="
					// +jsonArray.length());
					// appBaseFun.writeinfile("下载卡信息请求:listca.length="
					// +listca.size());

					WriteUnit.loadlist("下载卡信息请求:jsonArray.length=" + jsonArray.length());
					WriteUnit.loadlist("下载卡信息请求:listca.length=" + listca.size());
					savesp("usernum", ""+ listca.size());
					savesp("rundata", "no");
					for (int i = 0; i < listca.size(); i++) {

						try {

							// JSONObject jsonObject2 =
							// (JSONObject)jsonArray.opt(i);

							// if (listca.get(i).getCard_id()==null&&
							// jsonObject2.getString("card_id1")==null
							// &&jsonObject2.getString("card_id2")==null
							// &&jsonObject2.getString("card_id4")==null
							// &&jsonObject2.getString("card_id3")==null
							// &&jsonObject2.getString("card_id5")==null
							//
							// ) {
							//
							// continue;
							//
							// }

							try {
								name = listca.get(i).getChild_name();
							} catch (Exception e) {

							}


							try {
								classname=listca.get(i).getClass_name();
							} catch (Exception e) {
								// TODO: handle exception
							}


							Log.i("TPATT", "学生姓名:" + name+"...班级:"+classname);


							try {
								savesp(listca.get(i).getCard_id() + "name", name);
							} catch (Exception e) {
								// TODO: handle exception
							}


							try {
								savesp(listca.get(i).getCard_id() + "name", name);
							} catch (Exception e) {

							}

							try {
								savesp(listca.get(i).getCard_id1() + "name", name);
							} catch (Exception e) {

							}
							try {
								savesp(listca.get(i).getCard_id2() + "name", name);
							} catch (Exception e) {

							}
							try {
								savesp(listca.get(i).getCard_id3() + "name", name);
							} catch (Exception e) {

							}

							try {
								savesp(listca.get(i).getCard_id4() + "name", name);
								savesp(listca.get(i).getCard_id5() + "name", name);
							} catch (Exception e) {

							}

							// cardid = jsonObject2.getString("card_id");
							// 家长图片保存文件夹
							strPhotoPath = listca.get(i).getChild_id();
							try {
								savesp(listca.get(i).getCard_id() + "idimfo", strPhotoPath);
							} catch (Exception e) {

							}
							try {
								savesp(listca.get(i).getCard_id1() + "idimfo", strPhotoPath);
							} catch (Exception e) {

							}
							try {
								savesp(listca.get(i).getCard_id2() + "idimfo", strPhotoPath);
							} catch (Exception e) {

							}
							try {
								savesp(listca.get(i).getCard_id3() + "idimfo", strPhotoPath);
							} catch (Exception e) {

							}

							try {
								savesp(listca.get(i).getCard_id4() + "idimfo", strPhotoPath);
								savesp(listca.get(i).getCard_id5() + "idimfo", strPhotoPath);
							} catch (Exception e) {

							}



							try {
								savesp(strPhotoPath + "class", classname);
							} catch (Exception e) {
								// TODO: handle exception
							}




							// appBaseFun.writeinfile("卡信息加载次数是:" +i);
							WriteUnit.loadlist("卡信息加载次数是:" + i);
							Log.i("TPATT", "创建文件夹:" + strPhotoPath);
							appBaseFun.makeRootDirectory(
									appBaseFun.getPhoneCardPath() + "/tpatttp/CardInfo/Photo/" + strPhotoPath);
						} catch (Exception e) {

							Log.i("TPATT", "解析数据出现异常：" + e.toString());
							WriteUnit.loadlist("解析数据出现异常：" + e.toString());
						}

					}
					savesp("rundata", "ok");

				}
//				if (!settingPara.getCardInfoUrl().startsWith("https")) {
//					loadpic();
//				}

				//	return true;
			} else {
				content="没有数据返回";
				// if (TtsPlay.istts()) {
				// TtsPlay.speaktts("网络异常");
				// }
				isok=false;
				nettime++;
//				if (nettime<5) {
//					mhandle.postDelayed(net, 1000 * 30);
//				}

			}

			if (settingPara.isIscsv()) {
				if (listca == null) {
					listca = new ArrayList<ChildCard>();
				}
				try {
					SharedPreferences spPreferences = getSharedPreferences("json", Activity.MODE_PRIVATE);
					SharedPreferences.Editor se = spPreferences.edit();
					AppAction.readerCsv(appBaseFun.getPhoneCardPath() + "/referinfo.csv", se, listca);
				} catch (Exception e1) {

					e1.printStackTrace();
				}
			}

			String files=appBaseFun.getPhoneCardPath()+"/duoyin.csv";

			if (appBaseFun.fileIsExists(files)) {
				AppAction.readerCsvduoy(appBaseFun.getPhoneCardPath() + "/duoyin.csv", se, listca);
			}


			//
			// long size =
			// appBaseFun.getAutoFileOrFilesSize(appBaseFun.getSDPath()+"/tpatt/AttPhoto");
			// Log.i("TPATT","文件夹大小:"+ String.valueOf(size) );
			// if ( size > (5000*20*1024) )
			// {
			// appBaseFun.delAllFile(appBaseFun.getSDPath()+"/tpatt/AttPhoto",1);
			// }
			//
			if (isok) {
				return true;
			}

		} catch (Exception e) {

		}

		return false;
	}

}
