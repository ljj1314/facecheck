package com.att;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arcsoft.sdk.ArcFaceHelper;
import com.att.act.UtilImfo;

import java.util.List;

public class BootBroadcastReceiver extends BroadcastReceiver
{
	// 系统启动完成
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	static final String OME_DATA = "com.telpoedu.omc.FROM_ATT_ACTION";//APP发给OMC的信息

	static final String APP_DATA = "com.telpoedu.omc.TO_ATT_ACTION";//OMC发给APP的信息
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

	public static final String TAGIN = "android.hardware.usb.action.USB_DEVICE_ATTACHED";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		//Log.v("TPATT", "开机启动的Activity0");

		// 当收听到的事件是“BOOT_COMPLETED”时，就创建并启动相应的Activity和Service
		if (intent.getAction().equals(ACTION))
		{
			String cla=getCurrentActivityName(context);

			if (cla.equals("com.att.MainIdleActivity")||cla.equals("com.att.VersionActivity")||cla.equals("com.att.SwingCardAttActivity")) {
				return;
			}



			// 开机启动的Activity
			Log.i("TPATT", "大屏机APP开机启动");
			Intent activityIntent = new Intent(context, VersionActivity.class);
			activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//启动应用，参数为需要自动启动的应用的包名
			activityIntent.setPackage("幼儿宝");
			// 启动Activity
			context.startActivity(activityIntent);

//			   // 开机启动的Service
//			   Intent serviceIntent = new Intent(context, StartOnBootService.class);
//			   // 启动Service
//			   context.startService(serviceIntent);
		}else if (intent.getAction().equals(APP_DATA)) {

			Log.i("TPATT", "收到来自OMC的信息广播");
			String type=intent.getStringExtra("type");
			String content=intent.getStringExtra("content");

			if (type!=null&&!"".equals(type)) {
				try {
					UtilImfo.work(type, "type");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}



			}else if (content!=null&&!"".equals(content)) {


				UtilImfo.work(content, "content");


			}



		}else if (intent.getAction().equals(TAGIN)) {
			String cla=getCurrentActivityName(context);

			if (cla.equals("com.att.MainIdleActivity")||cla.equals("com.att.VersionActivity")||cla.equals("com.att.SwingCardAttActivity")) {
				return;
			}



			// 开机启动的Activity
			Log.i("TPATT", "大屏机APP开机启动");

			Intent activityIntent = new Intent(context, VersionActivity.class);
			activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//启动应用，参数为需要自动启动的应用的包名
			activityIntent.setPackage("幼儿宝");
			// 启动Activity
			context.startActivity(activityIntent);
		}




	}

	public static String getCurrentActivityName(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);


		// get the info from the currently running task
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);


		ComponentName componentInfo = taskInfo.get(0).topActivity;
		return componentInfo.getClassName();
	}



}
