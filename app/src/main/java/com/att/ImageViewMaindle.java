///**************************************************************************
//Copyright (C) 广东天波教育科技有限公司　版权所有
//文 件 名：
//创 建 人：
//创建时间：2015.10.30
//功能描述：待机界面
//**************************************************************************/
//package com.att;
//
//import java.io.File;
//import java.io.IOException;
//import java.sql.Date;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.att.act.AppAction;
//import com.att.act.OKHttpUtils;
//import com.att.act.OkHttpPost;
//import com.att.act.WriteUnit;
//import com.att.guide.StepFourthActivity;
//import com.att.server.HttppostAst;
//import com.att.server.Nlpost;
//import com.att.server.Nlpostast;
//import com.att.server.WwpostServer;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.VideoView;
//
//public class ImageViewMaindle extends Activity {//MainIdleActivity
//	private Handler didplayTimer = new Handler();
//	private Handler serialPortTimer = new Handler();
//	private Handler photoAndTVTimer = new Handler();
//	private LinearLayout idlelayout;
//
//	private DBOpneHelper sqlTpatt;
//
//	private AttUploadThread mAttUploadThread;
//	private File playPhotof;
//	private String playPhotoPath;
//	private File[] playPhotofiles;
//	private ImageView playPhotoimage;
//	private int playPhotoIndex = 0;
////	private EditText ed_adminpsw;
////	private TextView dtime;
////	private TextView attInfo;
//	private int downloadCardInfoSendCount = 0; // 下载卡信息重发请求次数
//	private int exitDownloadCardInfoTips = 8; // 8S后退出更新卡信息提示窗口
//	private AlertDialog downloadCardInfoTipsDialog;
//
//	private byte[] midbytes;
//
//	private String[] attinfoText = new String[3];
//	private SettingPara settingPara = new SettingPara();
//	private AttPlatformProto attPlatformProto = new AttPlatformProto();
//	private AppBaseFun appBaseFun = new AppBaseFun();
//	private String onetimes = null;
////	@SuppressWarnings("unused")
////	private TextView camerainfo = null;
//	public static Handler tm = null;
////	@SuppressWarnings("unused")/
//	private boolean iscame = true;
//	private boolean isintent = false;
//	private VideoView videoview = null;
//	private int videonum = 0;
////	private ImageButton photoimg = null;
//	private List<String> filelist = null;
//	@SuppressWarnings("unused")
//	private boolean isstart = false;
//<<<<<<< .mine
////	private TextView tvsc;
//=======
////	private TextView tvsc;
//
//	Bitmap bm = null;
//>>>>>>> .r110
//
//	@SuppressWarnings("static-access")
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		// 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
//		// 设置你申请的应用appid
//		// StringBuffer param = new StringBuffer();
//		// param.append("appid="+getString(R.string.app_id));
//		// param.append(",");
//		// 设置使用v5+
//		// param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
//		// SpeechUtility.createUtility(MainIdleActivity.this, param.toString());
//		super.onCreate(savedInstanceState);
//
//		// 加入bugly
//		// ss CrashReport.initCrashReport(MainIdleActivity.this,
//		// getString(R.string.app_id), false);
//
//		setContentView(R.layout.imagemains);
//		idlelayout = (LinearLayout) findViewById(R.id.mainidlelayout);
//		idlelayout.setBackground(getResources().getDrawable(R.drawable.albumyd));// 移动logo
//		// idlelayout.setBackground(getResources().getDrawable(R.drawable.album));//电信logo
//
//		sqlTpatt = new DBOpneHelper(ImageViewMaindle.this);
//		try {
//			Log.v("TPATT", "createDataBase MainIdleActivity");
//			sqlTpatt.createDataBase();
//		} catch (IOException e) {
//			Log.v("TPATT", "createDataBase 失败");
//		}
//
//		if (AttPlatformProto.getPlatformProtoStatus() == 0) {
//			// 下载卡和图片信息
//			exitDownloadCardInfoTips = 8;
//			// downloadCardInfoTipsDialog =
//			// attPlatformProto.DownloadCardInfoTips(MainIdleActivity.this);
//			attPlatformProto.setPlatformProtoStatus(3);
//		}
//
////		dtime = (TextView) findViewById(R.id.viewdtime);
////		attInfo = (TextView) findViewById(R.id.viewattInfo);
//		attinfoText = sqlTpatt.readAttInfo(settingPara);
////		attInfo.setText(attinfoText[2]);
////		dtime.setText(new SimpleDateFormat("yyyy年MM月dd日  HH:mm").format(new Date(System.currentTimeMillis())));
//
////		camerainfo = (TextView) findViewById(R.id.camerinfo);
//
//		videoview = (VideoView) findViewById(R.id.videoview);
//<<<<<<< .mine
//	//	photoimg = (ImageButton) findViewById(R.id.photoimg);
//=======
////		photoimg = (ImageButton) findViewById(R.id.photoimg);
//>>>>>>> .r110
//
//<<<<<<< .mine
//	//	tvsc = (TextView) findViewById(R.id.tvscid);
//=======
////		tvsc = (TextView) findViewById(R.id.tvscid);
//>>>>>>> .r110
//
//<<<<<<< .mine
//
//=======
////		if (settingPara.isIslg()) {
//////			tvsc.setVisibility(View.GONE);
//////			photoimg.setVisibility(View.VISIBLE);
////			if (settingPara.getAtt_pic_platform() == 0) {
////				if (settingPara.getAtt_url() != null && settingPara.getAtt_url().length() > 0) {
////					int index = -1;
////					for (int i = 0; i < SettingPara.ATT_URLS.length; i++) {
////						if (SettingPara.ATT_URLS[i].equals(settingPara.getAtt_url())) {
////							index = i;
////						}
////					}
////					if (index >= 0) {
////						switch (index) {
////						case 0:
////							photoimg.setImageResource(R.drawable.ltelogo);
////							break;
////						case 1:
////							photoimg.setImageResource(R.drawable.ltelogo);
////							break;
////						case 2:
////							photoimg.setImageResource(R.drawable.logomobile);
////							break;
////						case 3:
////							photoimg.setImageResource(R.drawable.telpo);
////							break;
////						case 4:
////							photoimg.setImageResource(R.drawable.dadilogo);
////							break;
////						default:
////							photoimg.setVisibility(View.GONE);
////							break;
////						}
////					}
////				}
////			} else if (settingPara.getAtt_pic_platform() == 1) {
////				photoimg.setImageResource(R.drawable.ltelogo);
////			} else if (settingPara.getAtt_pic_platform() == 2) {
////				photoimg.setImageResource(R.drawable.ltelogo);
////			} else {
////				photoimg.setVisibility(View.GONE);
////			}
////		} else {
////			photoimg.setVisibility(View.GONE);
////			if (settingPara.isIssgid()) {
////				if (settingPara.getSchoolname() != null && !"".equals(settingPara.getSchoolname())) {
//////					tvsc.setVisibility(View.VISIBLE);
//////					tvsc.setText(settingPara.getSchoolname());
////				}
////			}
////		}
//>>>>>>> .r110
//
//		videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//			public void onCompletion(MediaPlayer mp) {
//
//				if (filelist != null && filelist.size() > 0) {
//					videoview.setVideoURI(Uri.parse(appBaseFun.getSDPath() + "/tp/" + filelist.get(videonum) + ".mp4"));
//					videoview.start();
//					videonum++;
//					if (videonum == (filelist.size())) {
//						videonum = 0;
//					}
//				}
//
//
//			}
//		});
//
//		/* Create a mAttUploadThread thread */
//		mAttUploadThread = new AttUploadThread();
//		mAttUploadThread.start();
//
//		// thread.start();
//
//		tm = new Handler() {
//
//			@Override
//			public void handleMessage(final Message msg) {
//
//				switch (msg.what) {
//
//				case 1:
//					showdlg();
//
//					break;
//				default:
//					break;
//				}
//
//			}
//		};
//
//		// 刷屏定时器
////		didplayTimer.postDelayed(mainIdleRedrawDidplayTimer, 1000); // 1s
//
//		// 刷卡定时器
//		serialPortTimer.postDelayed(mReadSerialPortTimer, 200); // 100ms
//
//		// 播放图片定时器
//		playPhotoPath = appBaseFun.getSDPath() + "/tpatt/PlayPhoto/";
//		playPhotoimage = (ImageView) findViewById(R.id.photoImageView); // 获得ImageView对象
//		if (settingPara.isIdle_vedio_switch()) {
//			playPhotoimage.setVisibility(View.GONE);
//			videoview.setVisibility(View.VISIBLE);
//
//			File file = new File(appBaseFun.getSDPath() + File.separator + "tp");
//			File[] files = file.listFiles();// 读取
//			filelist = new ArrayList<String>();
//			getFileName(files);
//
//			if (filelist != null && filelist.size() > 0) {
//				videoview.setVideoURI(Uri.parse(appBaseFun.getSDPath() + "/tp/" + filelist.get(videonum) + ".mp4"));
//				videoview.start();
//				videonum++;
//				if (videonum == (filelist.size())) {
//					videonum = 0;
//				}
//			}
//			isstart = true;
//
//		} else {
//			videoview.setVisibility(View.GONE);
//			playPhotoimage.setVisibility(View.VISIBLE);
//			File file = new File(playPhotoPath);
//			if (file.exists() && file.isDirectory()) {
//				if (file.list().length > 0) {
//					playPhotofiles = new File(playPhotoPath).listFiles();// 获取图片
//					if (playPhotofiles.length > 0) {
//						photoAndTVTimer.postDelayed(PlayPhotoAndTVTimer, 1); // 1ms后执行
//					}
//				} else {
//					Log.v("TPATT", "播放图片为空0");
//				}
//			} else {
//				Log.v("TPATT", "播放图片为空1");
//			}
//			isstart = false;
//			if (settingPara.getAtt_pic_platform() == 2) {
//				OkHttpPost request = new OkHttpPost(getApplicationContext()) {
//					@Override
//					public void onSuccess(String resposeBody) {
//						Log.i(TAG, "全部图片下载完成");
//						File file = new File(playPhotoPath);
//						if (file.exists() && file.isDirectory()) {
//							if (file.list().length > 0) {
//								playPhotofiles = new File(playPhotoPath).listFiles();// 获取图片
//								if (playPhotofiles.length > 0) {
//									photoAndTVTimer.postDelayed(PlayPhotoAndTVTimer, 1); // 1ms后执行
//								}
//							}
//						}
//					}
//
//					@Override
//					public void onFailure(String exceptionMsg) {
//						Log.i(TAG, "图片下载失败");
//					}
//				};
//				TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//				String no = tm.getDeviceId();// 考勤机编号
//				request.getAttendDisplay(no, playPhotoPath);
//			}
//		}
//
//<<<<<<< .mine
//
//=======
//		// 触发设置参数事件
////		photoimg.setOnClickListener(new View.OnClickListener() {
////			public void onClick(View paramView) {
////				showindlg();
////			}
////		});
//
////		tvsc.setOnClickListener(new View.OnClickListener() {
////
////			public void onClick(View v) {
////
////				showindlg();
////			}
////		});
//
////		dtime.setOnClickListener(new View.OnClickListener() {
////
////			public void onClick(View v) {
////
////				showindlg();
////			}
////		});
//>>>>>>> .r110
//
//
//	}
//
//	private void showindlg() {
//
//		if ((AttPlatformProto.getPlatformProtoStatus() == 1) || (AttPlatformProto.getPlatformProtoStatus() == 2)) {
//			new AlertDialog.Builder(ImageViewMaindle.this).setTitle("提示").setMessage("当前正在下载卡信息，不允许设置！")
//					.setNegativeButton("确定", null).show();
//		} else {
////			ed_adminpsw = new EditText(ImageViewMaindle.this);
////			new AlertDialog.Builder(ImageViewMaindle.this).setTitle("请输入管理员密码")
////					.setIcon(android.R.drawable.ic_dialog_info).setView(ed_adminpsw)
////					.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
////						public void onClick(DialogInterface arg0, int arg1) {
////							if (!ed_adminpsw.getText().toString().equals("")) {
////								if (ed_adminpsw.getText().toString().equals(settingPara.getAdminPassword())) {
////									sqlTpatt.close();
////									mAttUploadThread.close();
////									serialPortTimer.removeCallbacks(mReadSerialPortTimer);
////									photoAndTVTimer.removeCallbacks(PlayPhotoAndTVTimer);
////									didplayTimer.removeCallbacks(mainIdleRedrawDidplayTimer);
////									final Intent intent = new Intent(ImageViewMaindle.this, SettingParaActivity.class);
////									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////									getApplicationContext().startActivity(intent);// 跳转
////									/*
////									 * //关闭此线程 if ( playPhotoimage != null ) {
////									 * playPhotoimage.setImageDrawable(null);//
////									 * playPhotoimage.setImageResource(0);//
////									 * playPhotoimage.destroyDrawingCache(); }
////									 */
////									finish();// 结束本MainIdleActivity
////								} else {
////									new AlertDialog.Builder(ImageViewMaindle.this).setTitle("提示")
////											.setMessage("管理员密码不正确！").setNegativeButton("确定", null).show();
////								}
////							} else {
////								new AlertDialog.Builder(ImageViewMaindle.this).setTitle("提示").setMessage("密码不能为空！")
////										.setNegativeButton("确定", null).show();
////							}
////						}
////					}).setNegativeButton("取消", null).show();
//		}
//
//	}
//
//
//
//
//
//	protected void onResume() {
//		super.onResume();
//		Log.i("csv", "重启vdeo");
//		if (settingPara.isIdle_vedio_switch()) {
//			if (videoview != null) {
//				// if (!videoview.isPlaying()) {
//				Log.i("csv", "重启vdeo开始");
//				videoview.start();
//				// }
//
//			}
//
//		}
//
//	};
//
//	protected void onPause() {
//		super.onPause();
//		if (settingPara.isIdle_vedio_switch()) {
//			if (videoview != null) {
//				videoview.pause();
//			}
//
//		}
//
//	};
//
//	/*
//	 * 刷卡检测线程
//	 *
//	 * @param
//	 *
//	 * @return
//	 */
//	/*
//	 * 定时更新时间
//	 *
//	 * @param
//	 *
//	 * @return
//	 */
//	Runnable mReadSerialPortTimer = new Runnable() {
//		public void run() {
//			try {
//				String strTemp;
//				String cardid = null;
//				int i = 0;
//				try {
//					cardid = SwingCard.AttChkIdCardNormal(settingPara.getCard_upload(), settingPara.isCard_reversal());
//				} catch (Exception e) {
//
//					Log.v("TPATT", e.getMessage());
//				}
//
//				if (cardid != null) {
//					if (isintent) {
//						return;
//					}
//					isintent = true;
//					Log.v("TPATT", "刷卡检测:有效卡" + cardid);
//
//					// 处理串口数据,正常触发SwingCardAttActivity
//					midbytes = cardid.getBytes();
//					if (settingPara.isIsenzero()) {
//
//						if (settingPara.getCard_upload() == 0) {
//
//							if (cardid.length() > 8) {
//								cardid = "0000000000".substring(0, 10 - cardid.length()) + cardid;
//							}
//
//						} else {
//
//							if (cardid.length() < 8) {
//								cardid = "00000000".substring(0, 8 - cardid.length()) + cardid;
//							}
//
//						}
//
//					} else {
//						for (i = 0; i < cardid.length(); i++) {
//							strTemp = new String(midbytes, i, 1);
//							if (strTemp.equals("0")) {
//							} else {
//								cardid = new String(midbytes, i, cardid.length() - i);
//								break;
//							}
//						}
//					}
//					sqlTpatt.close();
//					mAttUploadThread.close();
//					if (downloadCardInfoTipsDialog != null && downloadCardInfoTipsDialog.isShowing()) {
//						downloadCardInfoTipsDialog.dismiss();
//					}
//					/*
//					 * //关闭此线程 if ( playPhotoimage != null ) {
//					 * playPhotoimage.setImageDrawable(null);//playPhotoimage.
//					 * setImageResource(0);//playPhotoimage.destroyDrawingCache(
//					 * ); }
//					 */
//					photoAndTVTimer.removeCallbacks(PlayPhotoAndTVTimer);
//					didplayTimer.removeCallbacks(mainIdleRedrawDidplayTimer);
//					// 从MainIdleActivity跳转到SwingCardAttActivity
//					final Intent intent = new Intent(ImageViewMaindle.this, SwingCardAttActivity.class);
//					// 如果之前启动过这个SwingCardAttActivity，并还没有被destroy的话，而是无论是否存在，都重新启动新的MainIdleActivity
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					Bundle bundle = new Bundle();
//					bundle.putString("swingcardid", cardid);
//					intent.putExtras(bundle);
//					Log.i("tappo", "start to intent");
//					// 获取应用的上下文，生命周期是整个应用，应用结束才会结束
//					getApplicationContext().startActivity(intent);// 跳转
//					finish();// 结束本欢迎画面MainIdleActivity
//				} else {
//					serialPortTimer.postDelayed(mReadSerialPortTimer, 200); // 100ms
//				}
//			} catch (Exception e) {
//
//				Log.v("TPATT", "定时更新时间线程: Exception");
//				serialPortTimer.postDelayed(mReadSerialPortTimer, 200); // 100ms
//			}
//		}
//	};
//
//	/*
//	 * 考勤上报检测线程
//	 *
//	 * @param
//	 *
//	 * @return
//	 */
//	private class AttUploadThread extends Thread {
//		private boolean mRunning = false;
//
//		@Override
//		public void run() {
//			try {
//				Log.v("TPATT", "考勤上报线程");
//
//				// 检测是否有未上报内容
//				mRunning = true;
//				while (mRunning) {
//					switch (AttPlatformProto.getPlatformProtoStatus()) {
//					case 1:
//						if (downloadCardInfoSendCount > 3) {
//							downloadCardInfoSendCount = 0;
//							AttPlatformProto.setPlatformProtoStatus(2);
//						} else {
//							downloadCardInfoSendCount++;
//							Log.v("TPATT", "下载卡信息:准备");
//
//							attPlatformProto.DownloadCardInfo(ImageViewMaindle.this);
//
//							AttUploadThread.sleep(4000);
//							if (AttPlatformProto.getPlatformProtoStatus() == 2) {
//								Log.v("TPATT", "下载卡信息完成,准备下载图片信息");
//								downloadCardInfoSendCount = 0;
//							}
//						}
//						break;
//
//					case 2:
//						if (downloadCardInfoSendCount > 3) {
//							downloadCardInfoSendCount = 0;
//							AttPlatformProto.setPlatformProtoStatus(3);
//						} else {
//							downloadCardInfoSendCount++;
//							Log.v("TPATT", "下载卡家长图片信息:准备");
//
//							attPlatformProto.DownloadCardPhotoInfo();
//
//							AttUploadThread.sleep(4000);
//							if (AttPlatformProto.getPlatformProtoStatus() == 3) {
//								Log.v("TPATT", "下载卡家长图片信息:完成");
//								downloadCardInfoSendCount = 0;
//							}
//						}
//						break;
//
//					case 3:
//						int uploadAttCount;
//						int uploadAttPhotoCount;
//
//						uploadAttCount = 0;
//						uploadAttPhotoCount = 0;
//						if (settingPara.getAtt_pic_platform() == 0) {
//							if (attinfoText[0] != null) {
//								uploadAttCount = attPlatformProto.CheckNotUploadAtt(ImageViewMaindle.this, sqlTpatt);
//								AttUploadThread.sleep(1000);
//							}
//							if (uploadAttCount == 0) {
//								if (attinfoText[1] != null) {
//									uploadAttPhotoCount = attPlatformProto.CheckNotUploadAttPhoto(ImageViewMaindle.this,
//											sqlTpatt);
//								}
//								if (uploadAttPhotoCount == 0) {
//									Log.v("TPATT", "考勤上报线程:sleep(2000)");
//									AttUploadThread.sleep(2000);
//								}
//							}
//
//							if ((uploadAttCount > 0) || (uploadAttPhotoCount != 0)) {
//								sqlTpatt.saveUploadAttInfo(uploadAttCount, uploadAttPhotoCount);
//							}
//						} else if (settingPara.getAtt_pic_platform() == 1) {
//
//							SharedPreferences sp = getSharedPreferences("json", Activity.MODE_PRIVATE);
//							String too = sp.getString("token", null);
//							if (too != null) {
//								uploadAttCount = Nlpost.postform(ImageViewMaindle.this, sqlTpatt, too);
//							}
//							if (uploadAttCount == 0) {
//								Log.v("TPATT", "考勤上报线程:sleep(2000)");
//								AttUploadThread.sleep(2000);
//							}
//							if ((uploadAttCount > 0)) {
//								sqlTpatt.saveUploadAttInfo(1, 1);
//
//							}
//
//						} else if (settingPara.getAtt_pic_platform() == 2) {
//							OkHttpPost request = new OkHttpPost(getApplicationContext()) {
//								@Override
//								public void onSuccess(String resposeBody) {
//									Log.i(OkHttpPost.TAG, "上报完毕");
//									sqlTpatt.saveUploadAttInfo(1, 1);
//								}
//
//								@Override
//								public void onFailure(String exceptionMsg) {
//									try {
//										AttUploadThread.sleep(2000);
//									} catch (InterruptedException e) {
//										Log.i(TAG, e.toString());
//									}
//									Log.i(TAG, exceptionMsg);
//								}
//							};
//							request.postform(sqlTpatt);
//						}
//
//						int uploadMeasureCount = WwpostServer.CheckNotUploadAttMeasure(ImageViewMaindle.this, sqlTpatt);
//						if (uploadMeasureCount > 0) {
//							sqlTpatt.saveUploadAttMeasure(uploadMeasureCount);
//						}
//
//						break;
//
//					default:
//						break;
//					}
//				}
//			} catch (Exception e) {
//
//				e.printStackTrace();
//				System.out.println("exception...");
//			}
//		}
//
//		public void close() {
//			mRunning = false;
//		}
//	}
//
//	/*
//	 * 转播放图片和视频检测线程
//	 *
//	 * @param
//	 *
//	 * @return
//	 */
//	Runnable PlayPhotoAndTVTimer = new Runnable() {
//		public void run() {
//			// handler自带方法实现定时器
//			try {
//				photoAndTVTimer.postDelayed(PlayPhotoAndTVTimer, settingPara.getPlayPhotoTime());
//
//				if (playPhotofiles == null) {
//					Log.v("TPATT", "转播放图片线程:无图片");
//				} else {
//					Log.v("TPATT", "转播放图片线程");
//
//					playPhotof = playPhotofiles[playPhotoIndex];
//					playPhotoIndex++;
//					if (playPhotoIndex >= playPhotofiles.length) {
//						playPhotoIndex = 0;
//					}
//					if (playPhotof.isFile()) {
//						if (playPhotof.getTotalSpace() > 80 * 1024) {
//							if (playPhotof.getName().contains(".jpg")) // 判断扩展名
//							{
//								if (playPhotoimage != null) {
//									// playPhotoimage.destroyDrawingCache();
//									// playPhotoimage.setImageDrawable(Drawable.createFromPath(playPhotof.getPath()));
//									// Bitmap bitmap = appBaseFun.getLoacalBitmap(playPhotof.getPath());
//									AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
//										@Override
//										protected String doInBackground(String... arg0) {
//											bm = OKHttpUtils.getImage(playPhotof.getPath());
//											return null;
//										}
//										protected void onPostExecute(String result) {
//											if(bm!=null){
//												playPhotoimage.setImageResource(0);
//												playPhotoimage.setImageBitmap(bm);// 设置Bitmap
//											}
//										};
//									};
//									asyncTask.execute("");
//								}
//							}
//						}
//					}
//				}
//			} catch (Exception e) {
//
//				Log.v("TPATT", "转播放图片线程: Exception");
//				// appBaseFun.writeinfile( "转播放图片线程: Exception");
//				WriteUnit.loadlist("转播放图片线程: Exception");
//			}
//		}
//	};
//
//	/*
//	 * 定时更新时间
//	 *
//	 * @param
//	 *
//	 * @return
//	 */
//	Runnable mainIdleRedrawDidplayTimer = new Runnable() {
//		public void run() {
//			// handler自带方法实现定时器
//			try {
//				if (AttPlatformProto.getPlatformProtoStatus() == 0) {
//					if (exitDownloadCardInfoTips > 0) {
//						exitDownloadCardInfoTips--;
//						if (exitDownloadCardInfoTips == 0) {
//							Log.v("TPATT", "主动退出提示下载卡信息窗口");
//							AttPlatformProto.setPlatformProtoStatus(3);
//							// downloadCardInfoTipsDialog.dismiss();
//						}
//					}
//				}
//
//				// 日期时间
//				attinfoText = sqlTpatt.readAttInfo(settingPara);
//				String strTemp = attinfoText[2];
////				if (AttPlatformProto.getPlatformProtoStatus() == 1) {
////					if ((AttPlatformProto.getDownloadCardInfoTotalCount() > 0) && (AttPlatformProto
////							.getDownloadCardInfoTotalCount() > AttPlatformProto.getDownloadCardInfoCount())) {
////						attInfo.setText(strTemp + "\r\n下载卡信息:"
////								+ String.valueOf((AttPlatformProto.getDownloadCardInfoCount() * 100)
////										/ AttPlatformProto.getDownloadCardInfoTotalCount())
////								+ "%");
////					} else {
////						attInfo.setText(strTemp + "\r\n下载卡信息:准备");
////					}
////					didplayTimer.postDelayed(mainIdleRedrawDidplayTimer, 1000); // 1s
////				} else if (AttPlatformProto.getPlatformProtoStatus() == 2) {
////					if ((AttPlatformProto.getDownloadCardInfoTotalCount() > 0) && (AttPlatformProto
////							.getDownloadCardInfoTotalCount() > AttPlatformProto.getDownloadCardInfoCount())) {
////						attInfo.setText(strTemp + "\r\n下载图片信息:"
////								+ String.valueOf((AttPlatformProto.getDownloadCardInfoCount() * 100)
////										/ AttPlatformProto.getDownloadCardInfoTotalCount())
////								+ "%\r\n下载图片失败数:" + String.valueOf(AttPlatformProto.getDownloadCardInfoErrorCount()));
////					} else {
////						attInfo.setText(strTemp + "\r\n下载图片信息:准备");
////					}
////					didplayTimer.postDelayed(mainIdleRedrawDidplayTimer, 1000); // 1s
////				} else {
////					attInfo.setText(strTemp);
////					// 刷屏定时器
////					didplayTimer.postDelayed(mainIdleRedrawDidplayTimer, 5000); // 1s
////				}
////				dtime.setText(new SimpleDateFormat("yyyy年MM月dd日  HH:mm").format(new Date(System.currentTimeMillis())));
//				onetimes = new SimpleDateFormat("HH:mm").format(new Date(System.currentTimeMillis()));
//				if (onetimes.equals("00:00")) {
//					long size = appBaseFun.getAutoFileOrFilesSize(appBaseFun.getSDPath() + "/tpatt/AttPhoto");
//					Log.v("TPATT", "文件夹大小:" + String.valueOf(size));
//					if (size > (5000 * 20 * 1024)) {
//						appBaseFun.delAllFile(appBaseFun.getSDPath() + "/tpatt/AttPhoto", 1);
//					}
//				}
//			} catch (Exception e) {
//
//				Log.v("TPATT", "定时更新时间线程: Exception");
//				didplayTimer.postDelayed(mainIdleRedrawDidplayTimer, 5000); // 1s
//			}
//		}
//	};
//
//	public void dialog_Exit(Context context) {
//		AlertDialog.Builder builder = new Builder(context);
//		builder.setMessage("确定要退出吗?");
//		builder.setTitle("提示");
//		builder.setIcon(android.R.drawable.ic_dialog_alert);
//		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				// android.os.Process.killProcess(android.os.Process.myPid());
//
//				StepFourthActivity.stopATTService(ImageViewMaindle.this, settingPara);
//
//				if (settingPara.isIsap()) {
//
//					if (settingPara.getAtt_pic_platform() == 0 || settingPara.getAtt_pic_platform() == 2) {
//						Intent intentst = new Intent(ImageViewMaindle.this, WwpostServer.class);
//						stopService(intentst);
//					} else {
//						Intent intentst = new Intent(ImageViewMaindle.this, Nlpostast.class);
//						stopService(intentst);
//					}
//
//				}
//
//				if (settingPara.isIstcap()) {
//
//					Intent intentst = new Intent(ImageViewMaindle.this, HttppostAst.class);
//					stopService(intentst);
//
//				}
//
//				ImageViewMaindle.this.finish();
//				System.exit(0);
//			}
//		});
//		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
//		builder.create().show();
//	}
//
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//		if (((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_HOME)) && event.getRepeatCount() == 0) {
//			dialog_Exit(ImageViewMaindle.this);
//		}
//		return false;
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//
//		mAttUploadThread.close();
//		videoview.stopPlayback();
//
//	}
//
//	private void showdlg() {
//		Log.i("Tapp", "弹出界面");
//
//		StringBuffer sb = new StringBuffer();
//		sb.append("已经下载最新版的app，是否安装");
//		Dialog dialog = new AlertDialog.Builder(ImageViewMaindle.this).setTitle("版本升级").setMessage(sb.toString())
//				.setPositiveButton("更新", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//
//						AppAction.install(Environment.getExternalStorageDirectory().toString() + "/TAPP.apk",
//								ImageViewMaindle.this);
//					}
//				}).setNegativeButton("以后提醒", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//
//						// 启动intent对应的Activity
//
//						dialog.dismiss();
//					}
//				}).create();
//		dialog.show();
//
//	}
//
//	private void getFileName(File[] files) {
//		if (files != null) {// 先判断目录是否为空，否则会报空指针
//			for (File file : files) {
//				if (file.isDirectory()) {
//					Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString() + file.getPath().toString());
//
//				} else {
//					String fileName = file.getName();
//					if (fileName.endsWith(".mp4")) {
//
//						String s = fileName.substring(0, fileName.lastIndexOf(".")).toString();
//						Log.i("zeng", "文件名mp4：：   " + s);
//						filelist.add(s);
//					}
//				}
//			}
//		}
//	}
//
//	// 0表示不在时间段内,1表示在时间段内
//	public Boolean GetAttTimesStatus(String cTime, String sTime, String eTime) {
//		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
//		try {
//			java.util.Date cDate = df.parse(cTime);
//			java.util.Date sDate = df.parse(sTime);
//			java.util.Date eDate = df.parse(eTime);
//			if ((sDate.getTime() < cDate.getTime()) && (cDate.getTime() < eDate.getTime())) {
//				return true;
//			} else {
//				return false;
//			}
//		} catch (Exception exception) {
//			exception.printStackTrace();
//		}
//
//		return false;
//	}
//
//}
