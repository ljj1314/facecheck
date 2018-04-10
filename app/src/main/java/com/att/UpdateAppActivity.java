/**************************************************************************
 Copyright (C) 广东天波教育科技有限公司　版权所有
 文 件 名：
 创 建 人：
 创建时间：2015.10.30
 功能描述：升级
 **************************************************************************/
package com.att;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.att.guide.MsgDialog;
import com.att.server.UpdataServer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UpdateAppActivity extends Activity {
	/** Called when the activity is first created. */
	private static final String TAG = "Update";
	private ProgressDialog pBar;
	// private String downPath =
	// "http://121.9.230.130:8105/APK/mobile/";//移动服务器//和宝贝
	// private String downPath = "http://121.9.230.130:8105/APK/test/";//测试服务器
	// private String downPath =
	// "http://121.9.230.130:8105/APK/telcom/";//电信发布服务器
	// private String downPath =
	// "http://121.9.230.130:8105/APK/teplo/";//天波测试服务器
	private String downPath = SettingPara.DOWN_PATH_TEST2;//"http://121.9.230.130:8105/APK/mobile2/";// 移动服务器//和宝贝
	// private String downPath = "http://121.9.230.130:8105/APK/test2/";//测试服务器
	// private String downPath =
	// "http://121.9.230.130:8105/APK/telcom2/";//电信发布服务器
	// private String downPath =
	// "http://121.9.230.130:8105/APK/teplo2/";//天波测试服务器
	// private String downPath = "http://121.9.230.130:8105/APK/dadi/";//大地服务器

	private String appName = "";
	private String verName = "";
	private String appVersion = "幼儿宝ver.json";
	private int newVerCode = 0;
	private Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		downPath = SettingPara.getDownPath();
		// 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
		// 设置你申请的应用appid
		// StringBuffer param = new StringBuffer();
		// param.append("appid="+getString(R.string.app_id));
		// param.append(",");
		// 设置使用v5+
		// param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
		// SpeechUtility.createUtility(UpdateAppActivity.this,
		// param.toString());

		super.onCreate(savedInstanceState);

		// 加入bugly
		// CrashReport.initCrashReport(UpdateAppActivity.this,
		// getString(R.string.app_id), false);

		setContentView(R.layout.version);

		try {
			AppBaseFun appBaseFun = new AppBaseFun();
			if ((appBaseFun.isMobileAvailable(UpdateAppActivity.this) == true)
					|| (UpdateAppActivity.isNetworkAvailable(UpdateAppActivity.this) == true)) {
				Log.v("TPATT", "升级: checkToUpdate");
				checkToUpdate();
			} else {
				// Toast.makeText(UpdateAppActivity.this, "isNetworkAvailable",
				// Toast.LENGTH_SHORT).show();

				// 启动intent对应的Activity
				Intent intent = new Intent(UpdateAppActivity.this, MainIdleActivity.class);
				startActivity(intent);

				finish();
			}
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}
	}

	// check the Network is available
	private static boolean isNetworkAvailable(Context context) {

		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netWorkInfo = cm.getActiveNetworkInfo();
			return (netWorkInfo != null && netWorkInfo.isAvailable());// 检测网络是否可用
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public int getNewVersion() {
		return (newVerCode);
	}

	// check new version and update
	private void checkToUpdate() throws NameNotFoundException {

		if (getServerVersion()) {
			int currentCode = CurrentVersion.getVerCode(this);

			// Toast.makeText(UpdateAppActivity.this, "版本：" +
			// Integer.toString(newVerCode) + "," +
			// Integer.toString(currentCode), Toast.LENGTH_SHORT).show();

			if (newVerCode > currentCode) {
				File del = new File(Environment.getExternalStorageDirectory().toString() + "/TAPP.apk");
				if (del.exists()) {
					// AppAction.install(Environment.getExternalStorageDirectory().toString()+"/TAPP.apk",
					// UpdateAppActivity.this);
					del.delete();
				}
				showUpdateDialog();

				// Current Version is old
				// 弹出更新提示对话框

			} else {
				File del = new File(Environment.getExternalStorageDirectory().toString() + "/TAPP.apk");
				if (del.exists()) {
					del.delete();
				}
				// 启动intent对应的Activity
				Intent intent = new Intent(UpdateAppActivity.this, MainIdleActivity.class);
				startActivity(intent);

				finish();
			}
		} else {
			// 启动intent对应的Activity
			Intent intent = new Intent(UpdateAppActivity.this, MainIdleActivity.class);
			startActivity(intent);

			finish();
		}
	}

	// show Update Dialog
	private void showUpdateDialog() throws NameNotFoundException {
		final MsgDialog ad = new MsgDialog(UpdateAppActivity.this);
		ad.setTitle("版本升级");
		ad.setMessage("检测到最新版本，请及时更新!");
		ad.setCancelable(false);
		ad.setPositiveButton("更新", new OnClickListener() {
			public void onClick(View v) {
				ad.dismiss();
				Intent intents = new Intent(getApplicationContext(), UpdataServer.class);
				getApplication().startService(intents);

				Intent intent = new Intent(UpdateAppActivity.this, MainIdleActivity.class);
				startActivity(intent);
				UpdateAppActivity.this.finish();
				/*
				 * //数据库的创建，及调用 DBOpenHelper helper; helper = new
				 * DBOpenHelper(getApplicationContext()); helper.delDataBase();
				 */
				// showProgressBar();//更新当前版本
			}
		});
		ad.setNegativeButton("以后提醒", new OnClickListener() {
			public void onClick(View v) {
				ad.dismiss();
				Intent intent = new Intent(UpdateAppActivity.this, MainIdleActivity.class);
				startActivity(intent);
				finish();
			}
		});

	}

	protected void showProgressBar() {

		pBar = new ProgressDialog(UpdateAppActivity.this);
		pBar.setTitle("正在下载最新版本");
		pBar.setMessage("请稍后...");
		pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		downAppFile(downPath + appName);
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
					Log.e(TAG, "升级获取版本出错: " + e.toString());
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

	protected void downAppFile(final String url) {
		pBar.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					Log.isLoggable("DownTag", (int) length);
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is == null) {
						throw new RuntimeException("isStream is null");
					}
					File file = new File(Environment.getExternalStorageDirectory(), appName);
					fileOutputStream = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					int ch = -1;
					do {
						ch = is.read(buf);
						if (ch <= 0)
							break;
						fileOutputStream.write(buf, 0, ch);
					} while (true);
					is.close();
					fileOutputStream.close();
					haveDownLoad();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	// cancel progressBar and start new App
	protected void haveDownLoad() {

		handler.post(new Runnable() {
			public void run() {
				pBar.cancel();
				// 弹出警告框 提示是否安装新的版本
				final MsgDialog ad = new MsgDialog(UpdateAppActivity.this);
				ad.setTitle("下载完成");
				ad.setMessage("是否安装新的应用");
				ad.setCancelable(false);
				ad.setPositiveButton("确定", new OnClickListener() {
					public void onClick(View v) {
						ad.dismiss();
						installNewApk();
						finish();
					}
				});
				ad.setNegativeButton("取消", new OnClickListener() {
					public void onClick(View v) {
						ad.dismiss();
						Intent intent = new Intent(UpdateAppActivity.this, MainIdleActivity.class);
						startActivity(intent);
						finish();
					}
				});
			}
		});
	}

	// 安装新的应用
	protected void installNewApk() {

		Intent intent = new Intent(Intent.ACTION_VIEW);

		intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), appName)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(UpdateAppActivity.this, MainIdleActivity.class);
		startActivity(intent);
		super.onBackPressed();
	}
}
