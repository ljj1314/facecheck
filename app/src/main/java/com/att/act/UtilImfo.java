package com.att.act;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import com.att.AppBaseFun;
import com.att.BootBroadcastReceiver;
import com.att.DBOpenHelper;
import com.att.MainIdleActivity;
import com.att.SettingPara;
import com.att.server.HttppostAst;
import com.att.server.NlServer;
import com.att.server.Nlpostast;
import com.att.server.TelpoService;
import com.att.server.WwpostServer;
import com.att.server.ZwService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UtilImfo {

	public static boolean iswork = false;

	public static boolean isupdateset = false;
	public static boolean ismedia = false;
	public static boolean issyndata = false;
	public static boolean iscleardata = false;
	public static boolean isreset=false;

	public static Context contextm=null;
	public static List<String> mlist=new ArrayList<String>();


	public static void start(final Context context) {

		Message msg = new Message();

		contextm=context;

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String cla = BootBroadcastReceiver
						.getCurrentActivityName(context);

				if (cla.equals("com.att.MainIdleActivity")&&iswork&&mlist.size()>0) {

					String meg=mlist.get(0);

					if (meg.equals("setting_update")) {
						Log.i("boot", "获取升级配置信息");
						update(context);
						mlist.remove(0);
						//isupdateset=false;
					} else if (meg.equals("media_update")) {
						Log.i("boot", "获取媒体切换信息");
						Message mg = Message.obtain();
						mg.what = 3;
						MainIdleActivity.tm.sendMessage(mg);
						//ismedia=false;
						mlist.remove(0);
					} else if (meg.equals("sync_data")) {
						Log.i("boot", "获取同步数据信息");
						syndata(context);
						//	issyndata=false;
						mlist.remove(0);
					} else if (meg.equals("clear_data")) {
						Log.i("boot", "获取清除数据信息");
						Message mg = Message.obtain();
						mg.what = 5;
						MainIdleActivity.tm.sendMessage(mg);

						clearsql(context);
						mlist.remove(0);

					}else if (meg.equals("reset_app")) {

						Log.i("boot", "获重启app信息");
						Message mg = Message.obtain();
						mg.what = 4;
						MainIdleActivity.tm.sendMessage(mg);
						//isreset=false;
						mlist.remove(0);


					}

					iswork = false;
					Log.i("boot", "操作完毕，返回成功");

				}

			}
		}, 1000 * 60 * 1, 1000 * 60 * 1);

	}

	public static void work(String message, String type) {

		iswork = true;

		if (mlist!=null) {
			if (type.equals("type")) {

				if (message.equals("setting_update")) {

					mlist.add(message);
					//	isupdateset = true;

				} else if (message.equals("media_update")) {
					mlist.add(message);
					//	ismedia = true;


				} else if (message.equals("notice")) {

				} else if (message.equals("sync_data")) {
					mlist.add(message);
					//	issyndata = true;

				} else if (message.equals("clear_data")) {
					mlist.add(message);
					//	iscleardata = true;

				}else if (message.equals("reset_app")) {
					mlist.add(message);
					//	isreset=true;

				}

			} else if (type.equals("content")) {

			}

			Intent intent = new Intent();
			intent.setAction("com.telpoedu.omc.FROM_ATT_ACTION");
			intent.putExtra("type", "down_back");
			contextm.sendBroadcast(intent);
		}else {
			mlist=new ArrayList<String>();
		}




	}

	public static void update(Context context) {

		Message mg = Message.obtain();
		mg.what =3;
		MainIdleActivity.tm.sendMessage(mg);

		SettingPara settingpara = new SettingPara(context);
		try {

			if (settingpara.isIsap()) {
				if (settingpara.getAtt_pic_platform() == 0
						|| settingpara.getAtt_pic_platform() == 2) {
					Intent intentst = new Intent(context, WwpostServer.class);
					context.stopService(intentst);
					context.startService(intentst);
				} else if (settingpara.getAtt_pic_platform() == 1) {
					Intent intentst = new Intent(context, Nlpostast.class);
					context.stopService(intentst);
					context.startService(intentst);
				}

			}
		} catch (Exception e) {

		}

		try {
			if (settingpara.isIstcap()
					&& settingpara.getAtt_pic_platform() == 0) {
				Intent intentst = new Intent(context, HttppostAst.class);
				context.stopService(intentst);
				context.startService(intentst);
			}
		} catch (Exception e) {

		}

		syndata(context);

	}



	public static void syndata(Context context){

		SettingPara settingpara = new SettingPara(context);
		try {

			if (settingpara.getAtt_pic_platform() == 0) {
				Intent intents = new Intent(context, TelpoService.class);
				context.stopService(intents);
				context.startService(intents);

			} else if (settingpara.getAtt_pic_platform() == 1) {

				Intent intents = new Intent(context, NlServer.class);
				context.stopService(intents);
				context.startService(intents);

			} else if (settingpara.getAtt_pic_platform() == 2) {
				Intent intents = new Intent(context, ZwService.class);
				context.stopService(intents);
				context.startService(intents);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static void clearsql(final Context context){

		AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				File file;
				AppBaseFun appBaseFun = new AppBaseFun();

				DBOpenHelper sqlTpatt = new DBOpenHelper(context);
				try {
					sqlTpatt.delDataBase();
					sqlTpatt.createDataBase();

					sqlTpatt.close();
				} catch (Exception e) {
					Log.i("TPATT", "createDataBase 失败:"+e.toString());
				}

				file = new File(appBaseFun.getPhoneCardPath() + "/tpatt/AttPhoto");
				appBaseFun.delete(file);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				appBaseFun.makeRootDirectory(appBaseFun.getPhoneCardPath() + "/tpatt/AttPhoto");
				return null;
			}

			protected void onPostExecute(String result) {
				//iscleardata=false;
				DBOpenHelper sqlTpatt = new DBOpenHelper(context);
				try {
					Log.i("TPATT", "createDataBase VersionActivity");

					sqlTpatt.createDataBase();

					Log.i("TPATT", "createDataBase VersionActivity0");
					sqlTpatt.close();
					Log.i("TPATT", "createDataBase VersionActivity1");
				} catch (Exception e) {
					Log.i("TPATT", "createDataBase 失败"+e.toString());
				}

				Message mg = Message.obtain();
				mg.what = 6;
				MainIdleActivity.tm.sendMessage(mg);


			};
		};
		asyncTask.execute("");





	}


}
