/**************************************************************************
 Copyright (C) 广东天波教育科技有限公司　版权所有
 文 件 名：
 创 建 人：
 创建时间：2015.10.30
 功能描述：待机界面
 **************************************************************************/
package com.att;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.arcsoft.sdk.DetecterActivity;
import com.arcsoft.sdk.MainActivity;
import com.att.act.AppAction;
import com.att.act.OKHttpUtils;
import com.att.act.OkHttpPost;
import com.att.act.UtilImfo;
import com.att.act.WriteUnit;
import com.att.guide.MsgDialog;
import com.att.guide.StepFirstActivity;
import com.att.guide.StepFourthActivity;
import com.att.guide.WifiPswDialog;
import com.att.guide.WifiPswDialog.OnCustomDialogListener;
import com.att.server.HttpConnect;
import com.att.server.HttppostAst;
import com.att.server.NlServer;
import com.att.server.Nlpostast;
import com.att.server.OmcLoadService;
import com.att.server.TelpoService;
import com.att.server.TpBleService;
import com.att.server.WwpostServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainIdleActivity extends Activity {
	private Handler didplayTimer = new Handler();
	private Handler serialPortTimer = new Handler();
	private Handler photoAndTVTimer = new Handler();
	private LinearLayout idlelayout;

	private DBOpenHelper sqlTpatt;

	private AttUploadThread mAttUploadThread;
	private File playPhotof;
	private String playPhotoPath;
	private File filePhoto;
	private String playVideoPath;
	private File fileVideo;
	private List<String> videoFileList;
	private File[] playPhotofiles;
	private ImageView playPhotoimage;
	private Bitmap playPhotobm = null;
	private int playPhotoIndex = 0;
	// private EditText ed_adminpsw;/
	// /private TextView dtime;
	private TextView attInfo;
	private int downloadCardInfoSendCount = 0; // 下载卡信息重发请求次数
	private int exitDownloadCardInfoTips = 8; // 8S后退出更新卡信息提示窗口
	// private AlertDialog downloadCardInfoTipsDialog;

	private byte[] midbytes;

	private String[] attinfoText = new String[3];
	private SettingPara settingPara;
	private AttPlatformProto attPlatformProto = new AttPlatformProto();
	private AppBaseFun appBaseFun = new AppBaseFun();
	private String onetimes = null;
	// private TextView camerainfo = null;
	public static Handler tm = null;
	@SuppressWarnings("unused")
	private boolean iscame = true;
	private boolean isintent = false;
	private VideoView videoview = null;
	private int videonum = 0;
	private ImageButton photoimg = null;
	private boolean isStop = false;
	private TextView tvsc;
	private Button kaoqin,kaoqinset;

	private FrameLayout fl_logo;
	private LinearLayout ll_photo_video;
	private LinearLayout ll_time;

	public static final String IS_PLATFORM_OR_AP = "is_platform_or_ap";
	public static final String STATUS_PLATFORM = "status_platform";
	public static final String STATUS_AP = "status_ap";
	public static final String ACTION_UPDATE_PLATFORM_AP = "action.update.platform";
	public static final String ACTION_UPDATE_AP = "action.update.ap";
	// private ImageView iv_icon_platform;
	// private ImageView iv_icon_ap;
	// UpdatePlatformAPBroadcastReceiver broadcastReceiver;
	private boolean isover = true;
	WifiPswDialog pswDialog;
	SharedPreferences sp;
	private boolean mRunning = false;
	private boolean issqlrun = true;
	public static boolean isback = true;
	private Timer scanTimer = null;
	// private ImageView tpble;
	private Calendar cad;
	private List<com.att.DBOpenHelper.AttInfo> ad;
	private HashMap<String, String> mk;
	private ImageView imb;
	private Handler bh = new Handler();
	private HttpApp ha = new HttpApp();
	private SharedPreferences.Editor edit;
	String TAG = this.getClass().getCanonicalName();

	//add by ljj
	EditText mEditText;

	@SuppressWarnings("static-access")
	// 08-17 16:12:08.300: I/TPATT(2133):
	// 接收卡号0：d1d1e042dc97d311d2d2
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
		// 设置你申请的应用appid
		// StringBuffer param = new StringBuffer();
		// param.append("appid="+getString(R.string.app_id));
		// param.append(",");
		// 设置使用v5+
		// param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
		// SpeechUtility.createUtility(MainIdleActivity.this, param.toString());
		super.onCreate(savedInstanceState);

		settingPara = new SettingPara(getApplicationContext());
		cad = Calendar.getInstance();
		// 加入bugly
		// ss CrashReport.initCrashReport(MainIdleActivity.this,
		// getString(R.string.app_id), false);

		setContentView(R.layout.mains);

		//add by ljj for 人脸识别结果
		kaoqin = (Button) findViewById(R.id.kaoqin);
		kaoqinset = (Button) findViewById(R.id.kaoqinset);
		kaoqin.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent it = new Intent(MainIdleActivity.this, DetecterActivity.class);
				it.putExtra("Camera", 0);
				startActivityForResult(it, 1);
			}
		});
		kaoqinset.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {

				//add by ljj check password
				LayoutInflater inflater = LayoutInflater.from(MainIdleActivity.this);
				View layout = inflater.inflate(com.arcsoft.sdk.R.layout.dialog_register, null);
				mEditText = (EditText) layout.findViewById(com.arcsoft.sdk.R.id.editview);
				mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
				mEditText.setHint("请输入密码");
				new AlertDialog.Builder(MainIdleActivity.this)
						.setTitle("请输入密码")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(layout)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if(!mEditText.getText().toString().equals("86337898")) {
									dialog.dismiss();
									Toast.makeText(MainIdleActivity.this,"密码认证错误，请重新输入",Toast.LENGTH_SHORT).show();
									return;
								}
								else {
									Intent it = new Intent(MainIdleActivity.this, MainActivity.class);
									startActivity(it);
								}

							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
						.show();
//				Intent it = new Intent(MainIdleActivity.this, MainActivity.class);
//				startActivity(it);
			}
		});
		// ---end 人脸识别结果


		idlelayout = (LinearLayout) findViewById(R.id.mainidlelayout);
		// idlelayout.setBackground(getResources().getDrawable(R.drawable.albumyd));//
		// 移动logo
		// idlelayout.setBackground(getResources().getDrawable(R.drawable.album));//电信logo

		imb = (ImageView) findViewById(R.id.iv_set);
		imb.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showSettingdlg();
			}
		});
		//
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(ACTION_UPDATE_PLATFORM_AP);
		// filter.addAction("tp.ble");
		// broadcastReceiver = new UpdatePlatformAPBroadcastReceiver();
		// registerReceiver(broadcastReceiver, filter);

		// turning();
		fl_logo = (FrameLayout) findViewById(R.id.fl_logo);
		ll_photo_video = (LinearLayout) findViewById(R.id.ll_photo_video);
		ll_time = (LinearLayout) findViewById(R.id.ll_time);

		if (appBaseFun.getSDPath() == null) {
			playPhotoPath = appBaseFun.getPhoneCardPath()
					+ "/tpatttp/PlayPhoto/";
			playVideoPath = appBaseFun.getPhoneCardPath() + "/tpatttp/tp";
		} else {
			playPhotoPath = appBaseFun.getSDPath() + "/tpatttp/PlayPhoto/";
			playVideoPath = appBaseFun.getSDPath() + "/tp";
		}

		filePhoto = new File(playPhotoPath);
		fileVideo = new File(playVideoPath);
		if (settingPara.isIsallscreen()
				&& ((filePhoto.exists() && filePhoto.isDirectory() && filePhoto
				.list().length > 0) || (fileVideo.exists()
				&& fileVideo.isDirectory() && fileVideo.list().length > 0))) {
			fl_logo.setVisibility(View.GONE);
			ll_time.setVisibility(View.GONE);
			ll_photo_video.setBackgroundColor(getResources().getColor(
					R.color.color_BEE7E9));
			idlelayout.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					showSettingdlg();
				}
			});
		} else {
			fl_logo.setVisibility(View.VISIBLE);
			ll_time.setVisibility(View.VISIBLE);
		}

		sqlTpatt = new DBOpenHelper(MainIdleActivity.this);
		try {
			Log.i("TPATT", "createDataBase MainIdleActivity");
			sqlTpatt.createDataBase();
		} catch (Exception e) {
			Log.i("TPATT", "createDataBase 失败" + e.toString());
		}

		if (AttPlatformProto.getPlatformProtoStatus() == 0) {
			// 下载卡和图片信息
			exitDownloadCardInfoTips = 8;
			// downloadCardInfoTipsDialog =
			// attPlatformProto.DownloadCardInfoTips(MainIdleActivity.this);
			attPlatformProto.setPlatformProtoStatus(3);
		}

		// dtime = (TextView) findViewById(R.id.viewdtime);
		attInfo = (TextView) findViewById(R.id.viewattInfo);
		attinfoText = sqlTpatt.readAttInfo(settingPara);
		// attInfo.setText(attinfoText[2]);

		// camerainfo = (TextView) findViewById(R.id.camerinfo);

		videoview = (VideoView) findViewById(R.id.videoview);
		photoimg = (ImageButton) findViewById(R.id.photoimg);

		tvsc = (TextView) findViewById(R.id.tvscid);
		tvsc.setText(new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss")
				.format(new Date(System.currentTimeMillis()))
				+ "        "
				+ weeknum((cad.get(Calendar.DAY_OF_WEEK) - 1)));

		videoview
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					public void onCompletion(MediaPlayer mp) {

						if (appBaseFun.getSDPath() == null) {
							if (videoFileList != null
									&& videoFileList.size() > 0) {
								videoview.setVideoURI(Uri.parse(appBaseFun
										.getPhoneCardPath()
										+ "/tpatt/tp/"
										+ videoFileList.get(videonum)));
								videoview.start();
								videonum++;
								if (videonum == (videoFileList.size())) {
									videonum = 0;
								}
							}
						} else {
							if (videoFileList != null
									&& videoFileList.size() > 0) {
								videoview.setVideoURI(Uri.parse(appBaseFun
										.getSDPath()
										+ "/tp/"
										+ videoFileList.get(videonum)));
								videoview.start();
								videonum++;
								if (videonum == (videoFileList.size())) {
									videonum = 0;
								}
							}
						}

					}
				});

		/* Create a mAttUploadThread thread */
		mAttUploadThread = new AttUploadThread();
		mAttUploadThread.start();

		// thread.start();

		tm = new Handler() {

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {

					case 1:
						showdlg();

						break;

					case 2:

						final Intent intent = new Intent(MainIdleActivity.this,
								MainIdleActivity.class);
						// 如果之前启动过这个SwingCardAttActivity，并还没有被destroy的话，而是无论是否存在，都重新启动新的MainIdleActivity
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						getApplicationContext().startActivity(intent);// 跳转
						MainIdleActivity.this.finish();// 结束本欢迎画面MainIdleActivity

						break;

					case 3:
						settingPara = new SettingPara();
						turning();
						break;

					case 4:
						Intent intent2 = new Intent();
						intent2.setAction("com.telpoedu.omc.FROM_ATT_ACTION");
						intent2.putExtra("type", "reset_app");
						sendBroadcast(intent2);
						exit0();

						break;

					case 5:

						issqlrun = false;
						isStop = true;

						break;

					case 6:
						issqlrun = true;
						isStop = false;

						break;

					case 7:

						issqlrun = false;
						isStop = true;
						UtilImfo.clearsql(MainIdleActivity.this);

						break;
					case 11:

						if (!settingPara.isIssportatt()) {
							String ms = msg.getData().getString("tv");
							attInfo.setText(ms);
						} else {
							attInfo.setVisibility(View.INVISIBLE);
						}

						break;

					default:
						break;
				}

			}
		};

		isStop = false;

		// 刷屏定时器
		didplayTimer.postDelayed(mainIdleRedrawDidplayTimer, 1000); // 1s

		sp = getSharedPreferences("json", Context.MODE_PRIVATE);
		edit = sp.edit();

		playPhotoimage = (ImageView) findViewById(R.id.photoImageView); // 获得ImageView对象

		turning();

		// 刷卡定时器
		serialPortTimer.postDelayed(mReadSerialPortTimer, 1000); // 100ms

		if (!isNetworkAvailables(getApplicationContext(), appBaseFun,
				settingPara)) {

			initdata();
			bh.postDelayed(runnable, 1000);
		} else {

			String atttp = sp.getString("atttp", null);
			if (atttp != null) {
				attInfo.setText(atttp);
			} else {
				String num = sp.getString("usernum", "" + 0);
				int nums = Integer.valueOf(num);
				attInfo.setText("出勤率:0/" + nums);
			}
		}
		scanTimer = new Timer();
		scanTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (isNetworkAvailables(getApplicationContext(), appBaseFun,
						settingPara)) {
					String result = null;
					try {
						result = ha
								.getSendAndReceive("http://jktx1.jiankangtongxue.cn/openAPI/GetCheckStat.ashx?SchoolID="
										+ settingPara.getSchoolID());
					} catch (Exception e) {
						// TODO: handle exception
					}
					Log.i("temp", "" + result);
					if (result != null && result.length() > 4) {

						JsonValidator jsonValidator = new JsonValidator();
						try {
							boolean isjson = jsonValidator.validate(result);
							if (!isjson) {

								Log.i("tapp", "不是正确josn格式");
								// appBaseFun.writeinfile("不是正确josn格式" );
								// appBaseFun.writeinfile("不是正确josn格式为"+strRes);
								WriteUnit.loadlist("不是正确josn格式为" + result);
								return;
							}
						} catch (Exception e) {

						}

						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(result)
									.getJSONObject("_metadata");

							if (jsonObject.getString("code").equals("200")) {

								String strdata = new JSONObject(result)
										.getString("data");

								JSONArray jsonArray = new JSONArray(strdata);

								for (int i = 0; i < jsonArray.length(); i++) {

									JSONObject jsonObject2 = (JSONObject) jsonArray
											.opt(i);
									String StudentCheckCount = jsonObject2
											.getString("StudentCheckCount");
									String StudentAllCount = jsonObject2
											.getString("StudentAllCount");

									savesp("atttp", "出勤率:" + StudentCheckCount
											+ "/" + (StudentAllCount));

									Message msg = new Message();
									msg.what = 11;
									Bundle b = new Bundle();
									b.putString("tv", "出勤率:"
											+ StudentCheckCount + "/"
											+ (StudentAllCount));
									msg.setData(b);
									tm.sendMessage(msg);

								}
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}
		}, 1000, 1000 * 60);

	}

	//add by ljj 人脸结果处理
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
			Log.i(TAG, "RESULT =" + resultCode);
			if (data == null) {
				return;
			}
			Bundle bundle = data.getExtras();
			//String path = bundle.getString("imagePath");
			String cardid = bundle.getString("cardid");
			Log.i(TAG, "cardid="+cardid);

			if (issqlrun) {
				sqlTpatt.close();
				mAttUploadThread.close();
				isStop = true;
				photoAndTVTimer.removeCallbacks(PlayPhotoAndTVTimer);
				didplayTimer
						.removeCallbacks(mainIdleRedrawDidplayTimer);
				// 从MainIdleActivity跳转到SwingCardAttActivity
				final Intent intent = new Intent(MainIdleActivity.this,
						SwingCardAttActivity.class);
				// 如果之前启动过这个SwingCardAttActivity，并还没有被destroy的话，而是无论是否存在，都重新启动新的MainIdleActivity
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Bundle bundle1 = new Bundle();
				bundle1.putString("swingcardid", cardid);
				intent.putExtras(bundle1);
				// Log.i("TPATT", "start to intent");
				// 获取应用的上下文，生命周期是整个应用，应用结束才会结束
				getApplicationContext().startActivity(intent);// 跳转
				finish();// 结束本欢迎画面MainIdleActivity
			}
		}
	}


	private void savesp(String name, String value) {
		edit.putString(name, value);
		edit.commit();

	}

	public static String weeknum(int num) {
		String week = null;
		if (num == 0) {
			week = "周日";

		} else if (num == 1) {
			week = "周一";
		} else if (num == 2) {
			week = "周二";
		} else if (num == 3) {
			week = "周三";
		} else if (num == 4) {
			week = "周四";
		} else if (num == 5) {
			week = "周五";
		} else if (num == 6) {
			week = "周六";
		}

		return week;
	}

	private void turning() {

		// 播放图片定时器

		if (settingPara.isIdle_vedio_switch()) {
			playPhotoimage.setVisibility(View.GONE);
			videoview.setVisibility(View.VISIBLE);

			if (fileVideo.exists() && fileVideo.isDirectory()
					&& fileVideo.list().length > 0) {
				File[] files = fileVideo.listFiles();// 读取
				videoFileList = getVideoFileNames(files);

				if (videoFileList != null && videoFileList.size() > 0) {

					if (appBaseFun.getSDPath() != null) {
						videoview.setVideoURI(Uri.parse(appBaseFun.getSDPath()
								+ "/tp/" + videoFileList.get(videonum)));
						videoview.start();
						videonum++;
						if (videonum == (videoFileList.size())) {
							videonum = 0;
						}
					} else {
						videoview.setVideoURI(Uri.parse(appBaseFun
								.getPhoneCardPath()
								+ "/tpatt/tp/"
								+ videoFileList.get(videonum)));
						videoview.start();
						videonum++;
						if (videonum == (videoFileList.size())) {
							videonum = 0;
						}
					}

				}
			}

		} else {
			videoview.setVisibility(View.GONE);
			playPhotoimage.setVisibility(View.VISIBLE);
			if (filePhoto.exists() && filePhoto.isDirectory()) {
				if (filePhoto.list().length > 0) {
					playPhotofiles = filePhoto.listFiles();// 获取图片
					if (playPhotofiles.length > 0) {
						photoAndTVTimer.postDelayed(PlayPhotoAndTVTimer, 0); // 1ms后执行
					}
				} else {
					Log.i("TPATT", "播放图片为空0");
				}
			} else {
				Log.i("TPATT", "播放图片为空1");
			}

		}

	}

	private void showSettingdlg() {
		if ((AttPlatformProto.getPlatformProtoStatus() == 1)
				|| (AttPlatformProto.getPlatformProtoStatus() == 2)) {
			StepFirstActivity.showMsgDialog(MainIdleActivity.this,
					"当前正在下载卡信息，不允许设置!", true);
		} else {
			pswDialog = new WifiPswDialog(MainIdleActivity.this,
					new OnCustomDialogListener() {
						public void back(String str) {
							if (str != null && !str.equals("")) {
								if (str.equals(settingPara.getAdminPassword())) {
									sqlTpatt.close();
									mAttUploadThread.close();
									isStop = true;
									pswDialog.dismiss();
									serialPortTimer
											.removeCallbacks(mReadSerialPortTimer);
									photoAndTVTimer
											.removeCallbacks(PlayPhotoAndTVTimer);
									didplayTimer
											.removeCallbacks(mainIdleRedrawDidplayTimer);
									final Intent intent = new Intent(
											MainIdleActivity.this,
											SettingParaActivity.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									getApplicationContext().startActivity(
											intent);// 跳转
									/*
									 * //关闭此线程 if ( playPhotoimage != null ) {
									 * playPhotoimage.setImageDrawable(null);//
									 * playPhotoimage.setImageResource(0);//
									 * playPhotoimage.destroyDrawingCache(); }
									 */
									finish();// 结束本MainIdleActivity
								} else {
									StepFirstActivity.showMsgDialog(
											MainIdleActivity.this, "管理员密码不正确!",
											false);
								}
							} else {
								StepFirstActivity
										.showMsgDialog(MainIdleActivity.this,
												"密码不能为空!", false);
							}
						}
					}, "管理员密码", "确认");
			pswDialog.setNoCancel();
			pswDialog.setPW(settingPara.getAdminPassword());
			pswDialog.show();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Log.i("csv", "重启videoview");
		if (settingPara.isIdle_vedio_switch()) {
			if (videoview != null) {
				// if (!videoview.isPlaying()) {
				Log.i("csv", "恢复视频播放");
				videoview.start();
				// }

			}

		}

	};

	protected void onPause() {
		super.onPause();
		if (settingPara.isIdle_vedio_switch()) {
			if (videoview != null) {
				videoview.pause();
			}

		}

	};

	/*
	 * 刷卡检测线程
	 *
	 * @param
	 *
	 * @return
	 */
	Runnable mReadSerialPortTimer = new Runnable() {
		public void run() {
			try {
				String strTemp;
				String cardid = null;
				int i = 0;
				try {
					cardid = SwingCard.AttChkIdCardNormal(
							settingPara.getCard_upload(),
							settingPara.isCard_reversal());
				} catch (Exception e) {
					Log.i("TPATT", "AttChkIdCardNormal出错：" + e.toString());
				}
				if (cardid != null) {
					if (isintent) {
						return;
					}
					isintent = true;
					Log.i("TPATT", "待机页面 接收到刷卡卡号：" + cardid);

					// 处理串口数据,正常触发SwingCardAttActivity
					midbytes = cardid.getBytes();
					if (settingPara.isIsenzero()) {

						if (settingPara.getCard_upload() == 0) {

							if (cardid.length() < 10) {
								cardid = "0000000000".substring(0,
										10 - cardid.length())
										+ cardid;
							}

						} else {

							if (cardid.length() < 8) {
								cardid = "00000000".substring(0,
										8 - cardid.length())
										+ cardid;
							}

						}

					} else {
						for (i = 0; i < cardid.length(); i++) {
							strTemp = new String(midbytes, i, 1);
							if (strTemp.equals("0")) {
							} else {
								cardid = new String(midbytes, i,
										cardid.length() - i);
								break;
							}
						}
					}
					if (issqlrun) {
						sqlTpatt.close();
						mAttUploadThread.close();
						// if (downloadCardInfoTipsDialog != null &&
						// downloadCardInfoTipsDialog.isShowing()) {
						// downloadCardInfoTipsDialog.dismiss();
						// }
						/*
						 * //关闭此线程 if ( playPhotoimage != null ) {
						 * playPhotoimage
						 * .setImageDrawable(null);//playPhotoimage.
						 * setImageResource
						 * (0);//playPhotoimage.destroyDrawingCache( ); }
						 */
						isStop = true;
						photoAndTVTimer.removeCallbacks(PlayPhotoAndTVTimer);
						didplayTimer
								.removeCallbacks(mainIdleRedrawDidplayTimer);
						// 从MainIdleActivity跳转到SwingCardAttActivity
						final Intent intent = new Intent(MainIdleActivity.this,
								SwingCardAttActivity.class);
						// 如果之前启动过这个SwingCardAttActivity，并还没有被destroy的话，而是无论是否存在，都重新启动新的MainIdleActivity
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						Bundle bundle = new Bundle();
						bundle.putString("swingcardid", cardid);
						intent.putExtras(bundle);
						// Log.i("TPATT", "start to intent");
						// 获取应用的上下文，生命周期是整个应用，应用结束才会结束
						getApplicationContext().startActivity(intent);// 跳转
						finish();// 结束本欢迎画面MainIdleActivity
					}

				} else {
					serialPortTimer.postDelayed(mReadSerialPortTimer, 200); // 100ms
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("TPATT", "定时刷卡线程: Exception");
				serialPortTimer.postDelayed(mReadSerialPortTimer, 200); //
				// 100ms
			}
		}
	};

	/*
	 * 考勤上报检测线程
	 *
	 * @param
	 *
	 * @return
	 */
	private class AttUploadThread extends Thread {

		@Override
		public void run() {
			try {
				Log.i("TPATT", "考勤上报线程");

				// 检测是否有未上报内容
				mRunning = true;
				while (mRunning) {

					if (issqlrun) {

						switch (AttPlatformProto.getPlatformProtoStatus()) {
							case 1:
								if (downloadCardInfoSendCount > 3) {
									downloadCardInfoSendCount = 0;
									AttPlatformProto.setPlatformProtoStatus(2);
								} else {
									downloadCardInfoSendCount++;
									Log.i("TPATT", "下载卡信息:准备");

									attPlatformProto
											.DownloadCardInfo(MainIdleActivity.this);

									AttUploadThread.sleep(4000);
									if (AttPlatformProto.getPlatformProtoStatus() == 2) {
										Log.i("TPATT", "下载卡信息完成,准备下载图片信息");
										downloadCardInfoSendCount = 0;
									}
								}
								break;

							case 2:
								if (downloadCardInfoSendCount > 3) {
									downloadCardInfoSendCount = 0;
									AttPlatformProto.setPlatformProtoStatus(3);
								} else {
									downloadCardInfoSendCount++;
									Log.i("TPATT", "下载卡家长图片信息:准备");

									attPlatformProto.DownloadCardPhotoInfo();

									AttUploadThread.sleep(4000);
									if (AttPlatformProto.getPlatformProtoStatus() == 3) {
										Log.i("TPATT", "下载卡家长图片信息:完成");
										downloadCardInfoSendCount = 0;
									}
								}
								break;

							case 3:
								int uploadAttCount;
								int uploadAttPhotoCount;

								uploadAttCount = 0;
								uploadAttPhotoCount = 0;
								if (settingPara.getAtt_pic_platform() == 0) {

									String platformID = settingPara.getPlatformId();

									// boolean Is_cloud_storage =
									// settingPara.isIs_cloud_storage();

									if (isover) {

										OkHttpPost request = new OkHttpPost(
												getApplicationContext()) {// TODO
											// 云储存上传
											@Override
											public void onSuccess(String resposeBody) {
												Log.i(OkHttpPost.TAG, "云存储上报完毕");
												sqlTpatt.saveUploadAttInfo(0, 1);
												isover = true;
											}

											@Override
											public void onFailure(
													String exceptionMsg) {
												try {
													AttUploadThread.sleep(2000);
												} catch (InterruptedException e) {
													Log.i(TAG, e.toString());
												}
												Log.i("TPATT", exceptionMsg);
												isover = true;
											}
										};

										String[] attinfoText = sqlTpatt
												.readAttInfo(settingPara);

										if (attinfoText[0] != null) {
											isover = false;
											uploadAttCount = request.uploadAttend(
													settingPara, sqlTpatt,
													platformID, null, null);

											if (uploadAttCount > 0) {

												sqlTpatt.saveUploadAttInfo(
														uploadAttCount, 0);

											}
											isover = true;
											AttUploadThread.sleep(1000);

										}
										// uploadWWAttend(platformID, platform,
										// fileName, photoPath, attInfo);

										if (uploadAttCount == 0) {
											if (attinfoText[1] != null) {
												isover = false;
												request.uploadAttendpic(
														settingPara, sqlTpatt,
														platformID);
												// isover=false;
											} else if (attinfoText[1] == null
													&& attinfoText[0] == null) {
												// Log.i("TPATT",
												// "考勤上报线程:sleep(2000)");
												AttUploadThread.sleep(2000);

												try {
													int num = (int) sqlTpatt
															.allCaseNum();

													if (num > 9900) {
														Message mb = new Message();
														mb.what = 7;
														tm.sendMessage(mb);
														AttUploadThread.sleep(2000);
													}

												} catch (Exception e) {
													// TODO: handle exception
													Log.i("tappo", "提取数据格式失败");
												}

											}
										}

										// request.uploadAttend(settingPara,
										// sqlTpatt, platformID);

									}

								}

								break;

							default:
								break;
						}

					}
				}
			} catch (Exception e) {
				Log.i(OkHttpPost.TAG, e.toString());

				System.out.println("exception...");
			}
		}

		public void close() {
			mRunning = false;
		}
	}

	private void initdata() {

		try {

			ad = sqlTpatt.finddategrou(new SimpleDateFormat("yyyyMMdd")
					.format(new java.util.Date()));
			Log.i("rizhi", "" + ad);
			String num = sp.getString("usernum", "" + 0);
			int nums = Integer.valueOf(num);

			// mk = sqlTpatt.getdatalist();
			// Log.i("data", mk.toString());
			//
			// int sumnum=0;
			// if (ad.size()>0) {
			//
			// for (int i = 0; i < ad.size(); i++) {
			//
			// int id=ad.get(i).getId();
			//
			// String bb=mk.get(""+id);
			//
			// if (bb!=null) {
			// sumnum++;
			// }
			//
			//
			// }
			//
			//
			// }

			Message msg = new Message();
			msg.what = 11;
			Bundle b = new Bundle();
			b.putString("tv", "出勤率:" + ad.size() + "/" + (nums));
			msg.setData(b);
			tm.sendMessage(msg);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/*
	 * 转播放图片和视频检测线程
	 *
	 * @param
	 *
	 * @return
	 */
	Runnable PlayPhotoAndTVTimer = new Runnable() {
		public void run() {
			if (!isStop) {
				try {

					if (playPhotofiles == null) {
						Log.i("TPATT", "轮播图片线程:无图片");
					} else {
						Log.i("TPATT", "轮播图片线程:待机页面");

						playPhotof = playPhotofiles[playPhotoIndex];
						if (playPhotof.isFile()) {
							// if (playPhotof.getTotalSpace() > 80 * 1024) {
							if (playPhotof.getName().endsWith(".jpg")
									|| playPhotof.getName().endsWith(".JPG")
									|| playPhotof.getName().endsWith(".png")
									|| playPhotof.getName().endsWith(".PNG")) // 判断扩展名
							{
								if (playPhotoimage != null) {
									// playPhotoimage.destroyDrawingCache();
									// playPhotoimage.setImageDrawable(Drawable.createFromPath(playPhotof.getPath()));
									// Bitmap bitmap =
									// appBaseFun.getLoacalBitmap(playPhotof.getPath());
									AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
										@Override
										protected String doInBackground(
												String... arg0) {
											playPhotobm = OKHttpUtils
													.getImage(playPhotof
															.getPath());
											if (playPhotobm == null) {
												File file = new File(
														playPhotof.getPath());
												if (file.exists()) {
													file.delete();
												}
											}
											return null;
										}

										protected void onPostExecute(
												String result) {
											if (playPhotobm != null) {
												playPhotoimage
														.setImageResource(0);
												playPhotoimage
														.setImageBitmap(playPhotobm);// 设置Bitmap
											}
											playPhotoIndex++;
											if (playPhotoIndex >= playPhotofiles.length) {
												playPhotoIndex = 0;
											}
											photoAndTVTimer
													.postDelayed(
															PlayPhotoAndTVTimer,
															settingPara
																	.getPlayPhotoTime() - 2000);
										};
									};
									asyncTask.execute("");
								}
							}
							// }
						}
					}
				} catch (Exception e) {

					Log.i("TPATT", "转播放图片线程: Exception");
					// appBaseFun.writeinfile( "转播放图片线程: Exception");
					WriteUnit.loadlist("转播放图片线程: Exception");
				}
			} else {
				photoAndTVTimer.removeCallbacks(PlayPhotoAndTVTimer);
			}
		}
	};

	/*
	 * 定时更新时间
	 *
	 * @param
	 *
	 * @return
	 */
	Runnable mainIdleRedrawDidplayTimer = new Runnable() {
		public void run() {
			if (!isStop) {
				try {
					Log.i("TPATT", "定时更新时间线程:待机页面");
					tvsc.setText(new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss")
							.format(new Date(System.currentTimeMillis()))
							+ "        "
							+ weeknum((cad.get(Calendar.DAY_OF_WEEK) - 1)));

					// 日期时间
					attinfoText = sqlTpatt.readAttInfo(settingPara);
					// String strTemp = attinfoText[2];
					//
					// // } else {
					// attInfo.setText(strTemp);

					// 刷屏定时器
					didplayTimer.postDelayed(mainIdleRedrawDidplayTimer, 1000); // 1s

				} catch (Exception e) {

					Log.i("TPATT", "定时更新时间线程: Exception");
					didplayTimer.postDelayed(mainIdleRedrawDidplayTimer, 1000); // 1s
				}
			} else {
				didplayTimer.removeCallbacks(mainIdleRedrawDidplayTimer);
				didplayTimer.postDelayed(mainIdleRedrawDidplayTimer, 1000); // 1s
			}
		}
	};

	Runnable runnable = new Runnable() {

		public void run() {
			// TODO Auto-generated method stub

			try {
				initdata();
				bh.postDelayed(runnable, 1000 * 60);
			} catch (Exception e) {
				// TODO: handle exception
				bh.postDelayed(runnable, 1000 * 60);
			}

		}
	};

	public void dialog_Exit(Context context) {
		final MsgDialog ad = new MsgDialog(context);
		ad.setTitle("提示");
		ad.setMessage("确定要退出吗?");
		ad.setPositiveButton("确定", new OnClickListener() {
			public void onClick(View v) {
				ad.dismiss();
				// android.os.Process.killProcess(android.os.Process.myPid());

				exit0();
			}
		});
		ad.setNegativeButton("取消", new OnClickListener() {
			public void onClick(View v) {
				ad.dismiss();
			}
		});

	}

	public void exit0() {

		StepFourthActivity.stopATTService(MainIdleActivity.this, settingPara);

		if (settingPara.isIsap()) {

			if (settingPara.getAtt_pic_platform() == 0
					|| settingPara.getAtt_pic_platform() == 2) {
				Intent intentst = new Intent(MainIdleActivity.this,
						WwpostServer.class);
				stopService(intentst);
			} else {
				Intent intentst = new Intent(MainIdleActivity.this,
						Nlpostast.class);
				stopService(intentst);
			}

		}

		if (settingPara.getAtt_pic_platform() == 0) {
			Intent intentst = new Intent(MainIdleActivity.this,
					TelpoService.class);
			stopService(intentst);
		}

		if (settingPara.getAtt_pic_platform() == 1) {
			Intent intentst = new Intent(MainIdleActivity.this, NlServer.class);
			stopService(intentst);
		}

		if (settingPara.isIstcap()) {

			Intent intentst = new Intent(MainIdleActivity.this,
					HttppostAst.class);
			stopService(intentst);

		}

		if (settingPara.isIsbleban()) {
			Intent intentst = new Intent(MainIdleActivity.this,
					TpBleService.class);
			stopService(intentst);
		}

		Intent intentst = new Intent(MainIdleActivity.this,
				OmcLoadService.class);
		stopService(intentst);

		MainIdleActivity.this.finish();
		System.exit(0);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			dialog_Exit(MainIdleActivity.this);
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(broadcastReceiver);
		mAttUploadThread.close();
		videoview.stopPlayback();
		bh.removeCallbacks(runnable);
		if (scanTimer != null) {
			scanTimer.cancel();
			scanTimer = null;
		}

		serialPortTimer.removeCallbacks(mReadSerialPortTimer);
	}

	private void showdlg() {
		Log.i("Tapp", "弹出界面");

		final MsgDialog ad = new MsgDialog(MainIdleActivity.this);
		ad.setTitle("版本升级");
		ad.setMessage("已经下载最新版的app，是否安装");
		ad.setPositiveButton("更新", new OnClickListener() {
			public void onClick(View v) {
				ad.dismiss();
				AppAction.install(Environment.getExternalStorageDirectory()
						.toString() + "/TAPP.apk", MainIdleActivity.this);
			}
		});
		ad.setNegativeButton("以后提醒", new OnClickListener() {
			public void onClick(View v) {
				ad.dismiss();
			}
		});
	}

	private List<String> getVideoFileNames(File[] files) {
		List<String> filelist = new ArrayList<String>();
		if (files != null) {// 先判断目录是否为空，否则会报空指针
			for (File file : files) {
				if (file.isDirectory()) {
					Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString()
							+ file.getPath().toString());
				} else {
					String fileName = file.getName();
					if (fileName.endsWith(".mp4") || fileName.endsWith(".avi")
							|| fileName.endsWith(".MP4")
							|| fileName.endsWith(".AVI")) {
						// String s = fileName.substring(0,
						// fileName.lastIndexOf(".")).toString();
						// Log.i("zeng", "文件名mp4：： " + s);
						filelist.add(fileName);
					}
				}
			}
		}
		return filelist;
	}

	// 0表示不在时间段内,1表示在时间段内
	public Boolean GetAttTimesStatus(String cTime, String sTime, String eTime) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		try {
			java.util.Date cDate = df.parse(cTime);
			java.util.Date sDate = df.parse(sTime);
			java.util.Date eDate = df.parse(eTime);
			if ((sDate.getTime() < cDate.getTime())
					&& (cDate.getTime() < eDate.getTime())) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		showSettingdlg();
		return false;// super.onCreateOptionsMenu(menu);
	}

	public static void updateStatusAP(Context context, SettingPara settingPara) {
		sendPlatformApBroadcast(context, settingPara, false,
				settingPara.statusAP());
	}

	public static void sendPlatformApBroadcast(Context context,
											   SettingPara settingPara, boolean is_platform_or_ap, boolean status) {
		Intent intent = new Intent();
		intent.setAction(MainIdleActivity.ACTION_UPDATE_PLATFORM_AP);
		intent.putExtra(MainIdleActivity.IS_PLATFORM_OR_AP, is_platform_or_ap);
		if (is_platform_or_ap) {
			AppBaseFun appBaseFun = new AppBaseFun();
			if (isNetworkAvailables(context, appBaseFun, settingPara)) {
				intent.putExtra(MainIdleActivity.STATUS_PLATFORM, status);
			} else {
				intent.putExtra(MainIdleActivity.STATUS_PLATFORM, false);
			}
		} else {
			intent.putExtra(MainIdleActivity.STATUS_AP, status);
			if (status) {
				settingPara.setApTime();
			}
		}
		context.sendBroadcast(intent);
	}

	public static boolean isNetworkAvailables(Context context,
											  AppBaseFun appBaseFun, SettingPara settingPara) {
		boolean ismobile = false;
		if (appBaseFun.isMobileAvailable(context)
				&& settingPara.isTake_internet()) {
			ismobile = true;
		} else {
			ismobile = false;
		}
		return appBaseFun.isWifiAvailable(context) || ismobile
				|| HttpConnect.isNetworkAvailables(context);
	}
}
