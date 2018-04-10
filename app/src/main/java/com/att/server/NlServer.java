package com.att.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;

import com.att.AppBaseFun;
import com.att.HttpApp;
import com.att.JsonValidator;
import com.att.MainIdleActivity;
import com.att.SettingPara;
import com.att.act.OKHttpUtils;
import com.att.act.WriteUnit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class NlServer extends Service {

	private SettingPara settingPara;
	private SharedPreferences sp;
	private SharedPreferences.Editor se;

	private boolean status_platform = false;

	private AppBaseFun appBaseFun=new AppBaseFun();
	private HttpApp httpApp=new HttpApp();


	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	public void onCreate() {

		settingPara = new SettingPara(getApplicationContext());

		sp = getSharedPreferences("json", Context.MODE_PRIVATE);
		se = sp.edit();
		Log.i("tappo", "进入服务");

		Timer tt=new Timer();
		tt.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("Pid", "64976641");
				hm.put("DeviceId", settingPara.getDevicID());
				hm.put("Timestamp", time);
				hm.put("Hash", getMD5("64976641" + settingPara.getDevicID() + time + "a8d5d481ecc14c3185704347884b20c7"));

				long nu = Nlpost.conttime(time);
				Log.i("tappo", "相差时间 差未:" + nu);

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
					WriteUnit.loadlist("获取到信息为token：" + nw);
					token res = gson.fromJson(nw, new TypeToken<token>() {
					}.getType());
					if (res != null && res.getResult() != null) {
						String too = res.getResult().getToken();
						if (too != null) {
							Log.i("tappo", "token的值为" + too);
							se.putString("token", too);
							se.commit();
						}
						status_platform = too != null;
						MainIdleActivity.sendPlatformApBroadcast(getApplicationContext(), settingPara, true, status_platform);
					}

				} catch (UnsupportedEncodingException e1) {

					e1.printStackTrace();
				} catch (ClientProtocolException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		},1000*30, 1000*60*60*24);


		Timer hearb = new Timer();
		hearb.schedule(new TimerTask() {
			//
			@Override
			public void run() {

				try {
					String too = sp.getString("token", null);
					// JSONObject jsonObj = new JSONObject();
					// jsonObj.put("username", username);
					// jsonObj.put("apikey", apikey);
					// Create the POST object and add the parameters
					if (too != null) {
						Log.i("tp","开始发送心跳，token是:"+too);
						HttpPost httpPost = new HttpPost("http://iot.jxt189.com/deviceapi/device/heartbeat");
						// StringEntity entity = new StringEntity("",
						// HTTP.UTF_8);
						// entity.setContentType("application/json");
						//
						// entity.setContentType("device_authorization:"+too);
						httpPost.setHeader("Content-Type", "application/json");
						httpPost.setHeader("Device-Authorization", too);
						// httpPost.setEntity(entity);
						String nw = Nlpost.httppost(httpPost);
						Log.i("csv", "收到的数据为" + nw);
						status_platform = nw != null && nw.length() > 0;
						MainIdleActivity.sendPlatformApBroadcast(getApplicationContext(), settingPara, true, status_platform);
						// Nlresult lp = Nlpost.contactre(nw);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("tp", "心跳发送失败");
				}

			}
		}, 1000 * 8, 1000 * 60);

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
					AppBaseFun appBaseFun = new AppBaseFun();
					long size = appBaseFun.getAutoFileOrFilesSize(appBaseFun.getPhoneCardPath() + "/tpatt/AttPhoto");
					Log.i("TPATT", "AttPhoto文件夹大小:" + String.valueOf(size));
					if (size > (1000 * 100 * 1024)) {
						appBaseFun.delAllFile(appBaseFun.getPhoneCardPath() + "/tpatt/AttPhoto", 1);
					}
				} catch (Exception e) {

				}

			}
		}, 0, 1000 * 60 * 60 * 2);

		Timer timecheck = new Timer();
		timecheck.schedule(new TimerTask() {

			@Override
			public void run() {

				HttpPost httpPost = new HttpPost("http://iot.jxt189.com/deviceapi/user/getUserList");
				@SuppressWarnings("unused")
				StringEntity entity;
				String too = sp.getString("token", null);
				if (too == null) {
					return;
				}
				try {
					// Gson gson=new Gson();
					// entity = new StringEntity("", HTTP.UTF_8);
					// entity.setContentType("application/json");
					//
					// entity.setContentType("device_authorization:"+too);
					//
					// httpPost.setEntity(entity);
					httpPost.setHeader("Content-Type", "application/json");
					httpPost.setHeader("Device-Authorization", too);
					String nw = Nlpost.httppost(httpPost);
					Log.i("lists", "返回结果:" + nw + too);
					// token res=gson.fromJson(nw, new TypeToken<token>()
					// {}.getType());
//					JSONObject jo = new JSONObject(nw);
//					String res = jo.getString("List");
					Log.i("time", nw);
					if (nw!=null) {
						JsonValidator jsonValidator = new JsonValidator();
						try {
							boolean isjson = jsonValidator.validate(nw);
							if (!isjson) {
								//	content="不是正确josn格式" + res;
								Log.i("tapp", "不是正确josn格式");
								// appBaseFun.writeinfile("不是正确josn格式" );
								// appBaseFun.writeinfile("不是正确josn格式为"+strRes);
								WriteUnit.loadlist("不是正确josn格式为" + nw);
								return ;
							}
						} catch (Exception e) {

						}

						loaddata(nw);

					}

				} catch (UnsupportedEncodingException e1) {

					e1.printStackTrace();
				} catch (ClientProtocolException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				} catch (JSONException e) {

					e.printStackTrace();
				}

			}
		}, 1000*60, 1000 * 60 * 60*3);

	}

	public String getMD5(String info) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(info.getBytes("UTF-8"));
			byte[] encryption = md5.digest();

			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < encryption.length; i++) {
				if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
					strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
				} else {
					strBuf.append(Integer.toHexString(0xff & encryption[i]));
				}
			}

			return strBuf.toString();
		} catch (NoSuchAlgorithmException e) {
			return "";
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public class token {

		private String Msg;
		private String Code;
		private Result Result;

		class Result {

			private String Token;

			public String getToken() {
				return Token;
			}

			public void setToken(String token) {
				Token = token;
			}

		}

		public String getMsg() {
			return Msg;
		}

		public void setMsg(String msg) {
			Msg = msg;
		}

		public String getCode() {
			return Code;
		}

		public void setCode(String code) {
			Code = code;
		}

		public Result getResult() {
			return Result;
		}

		public void setResult(Result result) {
			Result = result;
		}

	}

	private void loaddata(String res) throws JSONException{

		String cc = sp.getString("studentin", null);
		String code=null;
		try {
			code=new JSONObject(res).getString("Code");
			Log.i("code", code);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (code!=null&&code.equals("20000")) {

			String strdata = new JSONObject(res).getString("List");
			savesp("studentin", strdata);
			String rundata = sp.getString("rundata", "no");

			if (cc != null && cc.equals(strdata) && "ok".equals(rundata)) {

				Log.i("tapp", "数据一样，不更新");
				// appBaseFun.writeinfile("数据一样，不更新" );
				WriteUnit.loadlist("数据一样，不更新");

				return;

			}

			WriteUnit.loadlist("下载卡信息" + strdata);

			JSONArray jsonArray = new JSONArray(strdata);

			savesp("rundata", "no");

			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject jsonObject2 =
						(JSONObject)jsonArray.opt(i);

				String UserId=jsonObject2.getString("UserId");
				String Name=jsonObject2.getString("Name");
				String UserType=jsonObject2.getString("UserType");
				String ClassName=jsonObject2.getString("ClassName");
				String Logo=jsonObject2.getString("Logo");
				String Cards=jsonObject2.getString("Cards");
				String Relations=jsonObject2.getString("Relations");

				Log.i("TPATT", "创建文件夹:" + UserId);
				appBaseFun.makeRootDirectory(
						appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo/Photo/" + UserId);

				savesp(UserId+"class", ClassName);
				savesp(UserId + "userpic", Logo);
				loadpic(Logo, UserId);

				JSONArray js=new JSONArray(Cards);
				Log.i("jslen", js.length()+"....."+Cards+"....."+js.toString());
				if (js.length()>0) {
					for (int j = 0; j < js.length(); j++) {

						Log.i("js", js.optString(j));

						try {
							savesp(js.optString(j) + "name", Name);
							savesp(js.optString(j) + "idimfo", UserId);
						} catch (Exception e) {
							// TODO: handle exception
						}


					}
				}



				JSONArray ja=new JSONArray(Relations);
				Log.i(".........nl", ja+"...."+Relations);
				if (ja.length()>0) {
					for (int j = 0; j < ja.length(); j++) {

						JSONObject jsonObject3 =
								(JSONObject)ja.opt(j);
						String lg=jsonObject3.getString("Logo");
						if (lg!=null) {
							savesp(UserId + j, lg);

							loadpic(lg, UserId);
						}


					}
				}







			}





		}







	}

	private void savesp(String key, String value) {

		se.putString(key, value);
		se.commit();

	}


	private void loadpic(String photoUrl,String strPhotoPath){


		if (photoUrl.length() > 0) {
			//	if (photoUrl.endsWith(".jpg")||photoUrl.endsWith(".png")) {
			Log.i("TPATT", "下载卡图片信息请求0:学生=" + strPhotoPath + ";路径=" + photoUrl+"...."+filename(photoUrl));
			if (!appBaseFun.fileIsExists(appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo/Photo/"
					+ strPhotoPath + "/" + filename(photoUrl))) {
				Bitmap mBitmap = httpApp.getNetWorkBitmap(photoUrl);
				if (mBitmap != null) {
					boolean issu = appBaseFun.saveBitmapjpg(mBitmap,
							appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo/Photo/" + strPhotoPath
									+ "/" + filename(photoUrl)+".jpg");

				}
			}


//			} else {
//				Log.i("TPATT", "不符合格式" );
//			}
		} else {
			Log.i("TPATT", "图片链接不符合长度");
		}


	}


	private String filename(String url) {

		if (url == null) {
			return null;
		}

		return url.substring(url.lastIndexOf("=") + 1);
	}


}
