package com.att.server;



import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.att.AppBaseFun;
import com.att.act.AppAction;
import com.att.act.WriteUnit;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class OmcLoadService extends Service{



	private final static String ACTION = "android.hardware.usb.action.USB_STATE";
	private boolean isusb=false;
	private Handler mh=new Handler();
	private AppBaseFun appBaseFun=new AppBaseFun();
	private boolean ismouse=false;
	private Timer timer=null;
	private boolean isover=true;
	private boolean isdown=false;
	private int times=0;
	public static Handler tm=null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}



	public void onCreate(){




		if (!AppAction.isAvilible(getApplicationContext(), "com.telpoedu.omc")) {

//			IntentFilter filter = new IntentFilter();  
//			  
//			  
//			filter.addAction(ACTION);  
//			  
//			  
//			registerReceiver(usBroadcastReceiver, filter);  



			mh.postDelayed(runnable, 1000*60*10);

			timer=new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (isover&&isdown&&ismouse) {


						isover=false;

						File file = new File(Environment.getExternalStorageDirectory().toString() + "/omc.apk");

						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						intent.addCategory("android.intent.category.DEFAULT");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
						getApplicationContext().startActivity(intent);



					}




				}
			}, 1000*60*15, 1000*60*60);
		}






		tm=new Handler(){

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:


						Intent intent = new Intent();
						intent.setAction("com.telpoedu.omc.FROM_ATT_ACTION");
						intent.putExtra("type", "serial_port_error");
						sendBroadcast(intent);

						break;



					default:
						break;
				}



			}

		};

	}


	Runnable runnable=new Runnable() {

		public void run() {
			// TODO Auto-generated method stub

			boolean ismobile=false;

			getusbName();
			if (appBaseFun.isMobileAvailable(OmcLoadService.this)) {
				ismobile=true;
			}else {
				ismobile=false;
			}
			if ( appBaseFun.isWifiAvailable(OmcLoadService.this) || ismobile||HttpConnect.isNetworkAvailables(OmcLoadService.this))
			{

				Thread thread=new Thread(new Runnable() {

					public void run() {
						// TODO Auto-generated method stub
						download();
					}
				});
				thread.start();

			}else {

				mh.postDelayed(runnable, 1000*60*10);


			}



		}
	};



	public void onDestroy() {
		super.onDestroy();

		unregisterReceiver(usBroadcastReceiver);

	};


	BroadcastReceiver usBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			// TODO Auto-generated method stub

			String action = intent.getAction();



			if (action.equals(ACTION)) {

				boolean connected = intent.getExtras().getBoolean("connected");


				if (connected) {

					isusb=true;

				} else {

					isusb=false;

				}

			}

		}

	};


	private void download(){
		Log.i("Tapp", "进入下载");
		HttpUtils hUtils = new HttpUtils();

		hUtils.download("http://121.9.230.130:8105/APK/omc1/omc.apk",
				Environment.getExternalStorageDirectory().toString() + "/omc.apk", true, true,
				new RequestCallBack<File>() {

					@Override
					public void onSuccess(ResponseInfo<File> arg0) {

						Log.i("Tapp", "下载成功");
						isdown=true;
						WriteUnit.loadlist(new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss")
								.format(new Date(System.currentTimeMillis())) + "下载成功");


					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						Log.i("Tapp", "下载失败");

						WriteUnit.loadlist(new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss")
								.format(new Date(System.currentTimeMillis())) + "下载失败");

						File del = new File(
								Environment.getExternalStorageDirectory().toString() + "/omc.apk");
						if (del.exists()) {
							del.delete();

						}
						times++;
						if (times<5) {
							download();
						}








					}
				});






	}



	private void getusbName(){

		try {
			//获得外接USB输入设备的信息
			Process p=Runtime.getRuntime().exec("cat /proc/bus/input/devices");
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while((line = in.readLine())!= null){
				String deviceInfo = line.trim();
				//对获取的每行的设备信息进行过滤，获得自己想要的。

				//  deviceInfo=deviceInfo.replace(target, replacement)
				if (deviceInfo.startsWith("N:")) {

					Log.i("imfp", deviceInfo);

					if (deviceInfo.contains("Mouse")) {
						Log.i("imfp", "just is true");
						ismouse=true;
					}


				}



			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}


	}


}
