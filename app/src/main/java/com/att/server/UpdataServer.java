package com.att.server;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.att.AppBaseFun;
import com.att.CurrentVersion;
import com.att.MainIdleActivity;
import com.att.SettingPara;
import com.att.act.WriteUnit;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UpdataServer extends Service {

	private String downPath = SettingPara.DOWN_PATH_TEST2;
	private String appName = "";
	private String verName = "";
	private String appVersion = "幼儿宝ver.json";
	private int newVerCode = 0;
	@SuppressWarnings({ "rawtypes", "unused" })
	private HttpHandler hh;
	@SuppressWarnings("unused")
	private List<String> filelist = null;
	private boolean isdownup = false;
	private AppBaseFun appBaseFun = new AppBaseFun();
	private Timer scanupTimer = null;
	private SharedPreferences sp = null;
	private SharedPreferences.Editor se = null;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	public void onCreate() {

		downPath = SettingPara.getDownPath();

		sp = getSharedPreferences("json", Context.MODE_PRIVATE);
		se = sp.edit();
		Log.v("TPATT", "启动升级: checkToUpdate");
		if ((appBaseFun.isMobileAvailable(UpdataServer.this) == true)
				|| (appBaseFun.isWifiAvailable(UpdataServer.this) == true)) {
			Log.v("TPATT", "升级: checkToUpdate");
			try {
				checkToUpdate();
			} catch (NameNotFoundException e) {

				e.printStackTrace();
			}
		}

		scanupTimer = new Timer();
		scanupTimer.schedule(new TimerTask() {

			@Override
			public void run() {

				if (isdownup) {

					if (getCurrentActivityName(getApplicationContext()).equals("com.att.MainIdleActivity")) {

						Message mes = new Message();
						mes.what = 1;
						MainIdleActivity.tm.sendMessage(mes);
						scanupTimer.cancel();
						scanupTimer = null;
					}

				}

			}
		}, 1000 * 60 * 2, 1000 * 60 * 2);

	}

	private void checkToUpdate() throws NameNotFoundException {

		if (getServerVersion()) {
			int currentCode = CurrentVersion.getVerCode(this);

			// Toast.makeText(UpdateAppActivity.this, "版本：" +
			// Integer.toString(newVerCode) + "," +
			// Integer.toString(currentCode), Toast.LENGTH_SHORT).show();

			if (newVerCode > currentCode) {
				// Current Version is old
				// 弹出更新提示对话框
				File del = new File(Environment.getExternalStorageDirectory().toString() + "/TAPP.apk");
				Log.i("Tapp", "判断下载");
				if (del.exists()) {
					Log.i("Tapp", "已下载");
					if (getCurrentActivityName(getApplicationContext()).equals("com.att.MainIdleActivity")) {
						Message mes = new Message();
						mes.what = 1;
						MainIdleActivity.tm.sendMessage(mes);
					}

				} else {

					HttpUtils hUtils = new HttpUtils();
					Log.i("Tapp", "进入下载");
					// appBaseFun.writeinfile(new SimpleDateFormat("yyyy年MM月dd日
					// HH:mm:ss").format(new
					// Date(System.currentTimeMillis()))+"进入下载");

					WriteUnit.loadlist(
							new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss").format(new Date(System.currentTimeMillis()))
									+ "进入下载");

					hh = hUtils.download(downPath + appName,
							Environment.getExternalStorageDirectory().toString() + "/TAPP.apk", true, true,
							new RequestCallBack<File>() {

								@Override
								public void onSuccess(ResponseInfo<File> arg0) {

									Log.i("Tapp", "下载成功");
									// appBaseFun.writeinfile(new
									// SimpleDateFormat("yyyy年MM月dd日
									// HH:mm:ss").format(new
									// Date(System.currentTimeMillis()))+"下载成功");
									WriteUnit.loadlist(new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss")
											.format(new Date(System.currentTimeMillis())) + "下载成功");
									isdownup = true;
									// AppAction.install(Environment.getExternalStorageDirectory().toString()+"/TAPP.apk",
									// TelpoService.this);

									se.putString("download", "yes");
									se.commit();

								}

								@Override
								public void onFailure(HttpException arg0, String arg1) {

									Log.i("Tapp", "下载失败");
									// appBaseFun.writeinfile(new
									// SimpleDateFormat("yyyy年MM月dd日
									// HH:mm:ss").format(new
									// Date(System.currentTimeMillis()))+"下载失败");
									WriteUnit.loadlist(new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss")
											.format(new Date(System.currentTimeMillis())) + "下载失败");

									File del = new File(
											Environment.getExternalStorageDirectory().toString() + "/TAPP.apk");
									if (del.exists()) {
										del.delete();
										try {
											checkToUpdate();
										} catch (NameNotFoundException e) {

											e.printStackTrace();
										}
									}
								}
							});
				}

			} else {
				File del = new File(Environment.getExternalStorageDirectory().toString() + "/TAPP.apk");
				if (del.exists()) {
					del.delete();
				}
			}
		}

	}

	// Get ServerVersion from GetUpdateInfo.getUpdateVerJSON
	public boolean getServerVersion() {

		try {
			String newVerJSON = CurrentVersion.getUpdataVerJSON(downPath + appVersion);
			JSONArray jsonArray = new JSONArray(newVerJSON);
			if (jsonArray.length() > 0) {
				Log.v("TPATT", "升级获取版本: " + jsonArray.toString());

				JSONObject obj = jsonArray.getJSONObject(0);
				try {
					newVerCode = Integer.parseInt(obj.getString("verCode"));
					verName = obj.getString("verName");
					appName = obj.getString("apkName");

					Log.v("TPATT", "下载升级文件名: " + appName + ",升级版本:" + verName + ",升级CODE:" + newVerCode);
				} catch (Exception e) {
					Log.e("UPPPPP", "升级获取版本出错: " + e.toString());
					newVerCode = -1;
					return false;
				}
			}
		} catch (Exception e) {
			Log.v("TPATT", "升级:获取版本异常 " + e.toString());
			return false;
		}
		return true;

	}

	public static String getCurrentActivityName(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);

		// get the info from the currently running task
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

		ComponentName componentInfo = taskInfo.get(0).topActivity;
		return componentInfo.getClassName();
	}

}
