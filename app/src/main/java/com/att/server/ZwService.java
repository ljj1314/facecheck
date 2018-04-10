package com.att.server;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.att.AppBaseFun;
import com.att.MainIdleActivity;
import com.att.SettingPara;
import com.att.act.OKHttpUtils;
import com.att.act.WriteUnit;
import com.att.guide.StepFourthActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ZwService extends Service {

	private Timer mtimer = null;
	private SettingPara settingPara;
	// private HttpApp httpApp = new HttpApp();
	private SharedPreferences sp = null;
	// private SharedPreferences.Editor se = null;
	private AppBaseFun appBaseFun = new AppBaseFun();
	// private String hearttext = null;
	// private Handler handle = null;
	// private static final String TAG = "Update";
	// private ProgressDialog pBar;
	// private String downPath = "http://121.9.230.130:8105/APK/mobile/";//移动服务器
	// private String downPath = "http://121.9.230.130:8105/APK/teplo/";// 测试服务器
	// private String downPath =
	// "http://121.9.230.130:8105/APK/telcom/";//电信发布服务器
	// private String appName = "";
	// private String verName = "";
	// private String appVersion = "幼儿宝ver.json";
	// private int newVerCode = 0;
	// private HttpHandler hh;
	// private boolean isdownup = false;
	// private long beattime = 0;
	// private boolean isbeat = true;
	// private List<ZwCardInfo> listca = null;
	// private List<ZwCardInfo> lc = null;
	// private boolean isnet = false;
	private Handler mhandle = new Handler();
	private int times = 1800;
	public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
	public static String JOVISION_ATTEND_URL = "http://xwapi.jovision.com/";// 中维平台服务器地址
	// private OkHttpClient client = new OkHttpClient();
	// private List<TeacherInfo> listtc = null;
	// private List<TeacherInfo> ltc = null;

	private boolean status_platform = false;

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
		}, 1000 * 8 , 1000 * 60 * 10);

		try {

			sp = getSharedPreferences("json", Context.MODE_PRIVATE);
			// se = sp.edit();
		} catch (Exception e) {

		}

		if (settingPara.getUpdatetime() != 0) {
			times = settingPara.getUpdatetime();
		}

		mtimer = new Timer();
		mtimer.schedule(new TimerTask() {
			@Override
			public void run() {
				String imei = ((TelephonyManager) getApplication().getSystemService(TELEPHONY_SERVICE)).getDeviceId();
				status_platform = StepFourthActivity.loadDataZW(null, sp, imei);
				MainIdleActivity.sendPlatformApBroadcast(getApplicationContext(), settingPara, true, status_platform);
			}
		}, 0, times * 1000);

		Timer checkOmcStatusTime = new Timer();
		checkOmcStatusTime.schedule(new TimerTask() {
			@Override
			public void run() {
				OKHttpUtils.checkOmcStatus(getApplicationContext());
				MainIdleActivity.sendPlatformApBroadcast(getApplicationContext(), settingPara, true, status_platform);
			}
		}, 1000 * 30, OKHttpUtils.CHECK_OMC_STATUS_TIME_PERIOD);

		Timer clearMoreLogTime = new Timer();
		clearMoreLogTime.schedule(new TimerTask() {
			@Override
			public void run() {
				WriteUnit.clearMoreLog();
			}
		}, 10000, 1000 * 60 * 60 * 2);

		Timer del = new Timer();
		del.schedule(new TimerTask() {

			@Override
			public void run() {

				try {

					File file = new File(appBaseFun.getPhoneCardPath() + File.separator + "tpatt/AttPhoto");
					File[] files = file.listFiles();// 读取
					List<String> filelist = new ArrayList<String>();

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

				}

			}
			// }, 1000*60*30, 1000*60*30);
		}, 0, 1000 * 60 * 30);
	}

	Runnable net = new Runnable() {

		public void run() {

			if (MainIdleActivity.isNetworkAvailables(getApplicationContext(), appBaseFun, settingPara)) {

				String imei = ((TelephonyManager) getApplication().getSystemService(TELEPHONY_SERVICE)).getDeviceId();
				StepFourthActivity.loadDataZW(null, sp, imei);

			} else {

				mhandle.postDelayed(net, 1000 * 30);

			}

		}
	};

	@Override
	public void onDestroy() {

		super.onDestroy();
		// hh.cancel();
		// ZwService.this.stopService(name)
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

	public static String Attinfo(String imei, String form) {
		try {

			FormBody formBody = new FormBody.Builder()

					.add("no", imei)
//					.add("no", "869751021321326")

					.build();
			Request request = new Request.Builder().url(JOVISION_ATTEND_URL + form).post(formBody).build();
			OkHttpClient client = new OkHttpClient();
			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				throw new IOException("Unexpected code " + response);
			}
		} catch (Exception e) {
			Log.i(OKHttpUtils.TAG, e.toString());
		}
		return null;
	}

}
