package com.att.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.att.AppBaseFun;
import com.att.HttpApp;
import com.att.SettingPara;
import com.google.gson.Gson;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class Zjpostast extends Service{


	private SettingPara settingPara;
	private SharedPreferences sp;
	private SharedPreferences.Editor se;

	private boolean status_platform = false;

	private AppBaseFun appBaseFun=new AppBaseFun();
	private HttpApp httpApp=new HttpApp();
	private Timer hearttm;
	private Timer uptm;



	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();


		settingPara = new SettingPara(getApplicationContext());

		sp = getSharedPreferences("json", Context.MODE_PRIVATE);
		se = sp.edit();
		Log.i("tappo", "进入服务");

		Timer ttTimer=new Timer();
		ttTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub


				JSONObject hm = new JSONObject();
				try {

					hm.put("devSn", "64976641");

					hm.put("DeviceId", settingPara.getDevicID());

					hm.put("versionInfo", "64976641");
					hm.put("Reserve1", "");
					hm.put("Reserve2", "");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				HttpPost httpPost = new HttpPost("http://iot.jxt189.com/deviceapi/auth/verify");
				StringEntity entity;
				try {
					Gson gson = new Gson();
					entity = new StringEntity(gson.toJson(hm), HTTP.UTF_8);
					entity.setContentType("application/json");

					// entity.setContentType("device_authorization: asdfasdf");

					httpPost.setEntity(entity);
					String nw = Nlpost.httppost(httpPost);
					Log.i("tappo", "获取到信息为：" + nw);









				} catch (Exception e) {
					// TODO: handle exception
				}


			}
		}, 1000*5, 1000*60*60*24);


		hearttm=new Timer();
		hearttm.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub




				JSONObject hm = new JSONObject();
				try {

					hm.put("devSn", "64976641");

					hm.put("DeviceId", settingPara.getDevicID());

					hm.put("versionInfo", "64976641");
					hm.put("Reserve1", "");
					hm.put("Reserve2", "");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}


				HttpPost httpPost = new HttpPost("http://iot.jxt189.com/deviceapi/auth/verify");
				StringEntity entity;
				try {
					Gson gson = new Gson();
					entity = new StringEntity(gson.toJson(hm), HTTP.UTF_8);
					entity.setContentType("application/json");

					// entity.setContentType("device_authorization: asdfasdf");

					httpPost.setEntity(entity);
					String nw = Nlpost.httppost(httpPost);
					Log.i("tappo", "获取到信息为：" + nw);
				} catch (Exception e) {
					// TODO: handle exception
				}










			}
		}, 1000*60, 1000*60);





		uptm=new Timer();
		uptm.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub












			}
		}, 1000*60, 1000*60*30);










	}





	private void getdata(){


		JSONObject hm = new JSONObject();
		try {

			hm.put("devSn", "64976641");

			hm.put("DeviceId", settingPara.getDevicID());

			hm.put("versionInfo", "64976641");
			hm.put("Reserve1", "");
			hm.put("Reserve2", "");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}













	}
















}
