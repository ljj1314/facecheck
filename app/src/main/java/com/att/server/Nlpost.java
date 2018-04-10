package com.att.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.att.AppBaseFun;
import com.att.DBOpenHelper;
import com.att.DBOpenHelper.AttInfo;
import com.att.SettingPara;
import com.att.SwingCardAttActivity;
import com.att.act.FileImageUpload;
import com.att.act.WriteUnit;
import com.att.usecase.Nlproson;
import com.att.usecase.Nlresult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Nlpost {

	public static String ipurl = "";

	public static String httppost(HttpPost httpPost) throws ClientProtocolException, IOException {

		String response = null;
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse = client.execute(httpPost);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			response = EntityUtils.toString(httpResponse.getEntity());
		} else {
			response = "" + statusCode;
		}

		if (response == null) {
			return "false";
		}

		return response;
	}

	@SuppressWarnings("unused")
	public static String conpost(String address, String token) throws ClientProtocolException, IOException {

		String response = null;
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(3000);
		// 这是请求方式为POST
		conn.setRequestMethod("POST");
		// 设置post请求必要的请求头
		conn.setRequestProperty("Content-Type", "application/json");// 请求头, 必须设置
		conn.setRequestProperty("Content-Type", "device_authorization:" + token);// 请求头,
		// 必须设置
		// conn.setRequestProperty("Content-Length", data.length + "");//
		// 注意是字节长度, 不是字符长度

		conn.setDoOutput(true);// 准备写出
		// conn.connect();
		conn.getOutputStream();// 写出数据

		if (response == null) {
			return "false";
		}

		return conn.getResponseMessage();
	}

	public static Nlproson contactres(String mes) throws JSONException {

		try{
			Gson gson = new Gson();

			// if (type==0) {

			Nlproson ln = gson.fromJson(mes, new TypeToken<Nlproson>() {
			}.getType());

			return ln;

		}catch (IllegalStateException e) {
			Log.i("tappo", "能龙contactres出错:" + e.toString());
			return null;
		}

	}

	public static Nlresult contactre(String mes) throws JSONException {

		Gson gson = new Gson();

		Nlresult ln = gson.fromJson(mes, new TypeToken<Nlresult>() {
		}.getType());
		return ln;

	}

	public static int postform(Context context, DBOpenHelper sqlTpatt, String token) {

		HashMap<String, Object> hs = new HashMap<String, Object>();

		// hs.put("SwipeData", m1);
		// hs.put("HealthData", m2);
		int uploadCount = 0;

		Gson gson = new Gson();
		// String paramStr = gson.toJson(hs);

		String postpathString = "http://iot.jxt189.com/deviceapi/data/updateData";
		// String postpathString =
		// "http://172.16.255.78:8088/openapi/GetStudentInfos.ashx";

		//

		AppBaseFun appBaseFun = new AppBaseFun();
		SettingPara settingPara = new SettingPara();

		boolean ismobile = false;
		if (appBaseFun.isMobileAvailable(context) && settingPara.isTake_internet()) {
			ismobile = true;
		} else {
			ismobile = false;
		}
		if (appBaseFun.isWifiAvailable(context) || ismobile || HttpConnect.isNetworkAvailable(context)) {
			// maxUploadAttCount = settingPara.getMaxUploadAttCount();
			for (int i = 0; i < 1; i++) {
				AttInfo attInfo = sqlTpatt.findUploadAttPhotoInfo();
				if (attInfo != null) {
					switch (settingPara.getAttPhotoPlatformProto()) {
						case 1: {
							String postRxText = "";
							// List<NameValuePair> params = new
							// ArrayList<NameValuePair>();

							// 考勤时间
							String attdtime = attInfo.getDtime();
							byte[] midbytes = attdtime.getBytes();
							String cardtime = new String(midbytes, 0, 4) + "-" + new String(midbytes, 4, 2) + "-"
									+ new String(midbytes, 6, 2) + " " + new String(midbytes, 8, 2) + ":"
									+ new String(midbytes, 10, 2) + ":" + new String(midbytes, 12, 2);
							// TransactionID
							// String transactionid = attInfo.getTranid();

							// 进出校
							String cardtype = attInfo.getStatus();

							// 卡号
							String cardid = attInfo.getCardId();
							hs.put("CardNo", cardid);
							// 读考勤图片
							String photoPath = appBaseFun.getPhoneCardPath() + "/tpatt/AttPhoto/" + attInfo.getPhoto()
									+ ".jpg";
							String filename = attInfo.getPhoto() + ".jpg";
							Log.v("TPATT", "考勤图片名:" + photoPath);
							// Bitmap bm = appBaseFun.getLoacalBitmap(photoPath);

							long moretime = conttime(cardtime) - oldtime();

							HashMap<String, Object> SwipeData = new HashMap<String, Object>();
							SwipeData.put("SwipeTime", moretime);
							SwipeData.put("InOutType", cardtype);
							SwipeData.put("Location", "1");

							// HashMap<String, Object> HealthData=new
							// HashMap<String, Object>();
							// HealthData.put("CheckTime", "");
							// HealthData.put("Temperature", "");
							// HealthData.put("Flag1", "0");
							// HealthData.put("Flag2", "0");
							// HealthData.put("Flag3", "0");
							// HealthData.put("Flag4", "0");
							// HealthData.put("Flag5", "0");
							// HealthData.put("Flag6", "0");

							hs.put("SwipeData", SwipeData);
							// hs.put("HealthData", HealthData);

							// writeinfile(new SimpleDateFormat("yyyy-MM-dd
							// HH:mm:ss").format(new java.util.Date())+"上报考勤图片数据:"
							// +params.toString());
							if (hs != null) {
								// hs.put("filename",appBaseFun.bitmaptoString(bm));
								// params.add(new
								// BasicNameValuePair("PhotoImg",appBaseFun.bitmaptoString(bm)));
								// bm.recycle();//回收bitmap空间

								// 上报平台
								int uploadFlag = 1;
								for (int upload = 0; upload < 2; upload++) {
									// HttpApp httpApp = new HttpApp();

									// postRxText =
									// HttpConnect.httpPost(postpathString.trim(),
									// paramStr,token);
									File file = new File(appBaseFun.getPhoneCardPath() + "/tpatt/AttPhoto/" + attInfo.getPhoto()+ ".jpg");
									postRxText = FileImageUpload.uploadFile(file, postpathString, hs, token, filename);
									// postRxText =
									// httpApp.postSendAndReceive(settingPara.getAttPhotoPlatformUrl(),
									// params);
									Log.v("TPATT", "上报考勤图片返回 " + postRxText);
									// writeinfile( new SimpleDateFormat("yyyy-MM-dd
									// HH:mm:ss").format(new
									// java.util.Date())+"上报考勤图片返回:"
									// +postRxText.toString());
									if (postRxText != null) {

										try {
											Data data = gson.fromJson(postRxText, new TypeToken<Data>() {
											}.getType());

											// JSONObject obj = new
											// JSONObject(postRxText);
											String strTemp = data.getCode();
											if (strTemp.equals("20000")||strTemp.equals("50002")||strTemp.equals("40000")||strTemp.equals("40300")||strTemp.equals("40303")
													||strTemp.equals("40305")||strTemp.equals("40306")||strTemp.equals("40307")||strTemp.equals("40400")
													||strTemp.equals("41302")||strTemp.equals("41502")||strTemp.equals("50000")||strTemp.equals("40304")) {
												uploadFlag = 1;
												Log.v("TPATT", "上传图片成功 file:" + file.getAbsolutePath());
												SwingCardAttActivity.isnet=true;
												break;
											} else {
												// writeinfile(new
												// SimpleDateFormat("yyyy-MM-dd
												// HH:mm:ss").format(new
												// java.util.Date())+"上传图片重发1");
												SwingCardAttActivity.isnet=true;
												uploadFlag=0;
												Log.v("TPATT", "上传图片重发1");
											}
										} catch (Exception e) {
											// writeinfile(new
											// SimpleDateFormat("yyyy-MM-dd
											// HH:mm:ss").format(new
											// java.util.Date())+"上传图片重发2");
											uploadFlag=0;
											SwingCardAttActivity.isnet=false;
											Log.v("TPATT", "上传图片重发2");
										}
										// }
										// else
										// {
										// Log.v("TPATT", "上传图片无应答1" );
										// uploadFlag = 0;
										// }
									} else {
										SwingCardAttActivity.isnet=false;
										Log.v("TPATT", "上传图片无应答2");
										uploadFlag = 0;
									}
								}
								if (uploadFlag == 1) {
									uploadCount = 1;
								}
							}
							// else {
							// // writeinfile(new SimpleDateFormat("yyyy-MM-dd
							// // HH:mm:ss").format(new
							// // java.util.Date())+"考勤图片不存在!!!");
							// uploadCount = -1;
							// Log.v("TPATT", "考勤图片不存在!!!");
							// }
						}
						break;

						default:
							Log.v("TPATT", "上报考勤:未知平台协议 ");
							break;
					}
				} else {
					// writeinfile("上传图片无上报考勤记录");
					Log.v("TPATT", "上报考勤:无上报考勤记录");
					break;
				}
			}
		} else {
			// writeinfile(new SimpleDateFormat("yyyy-MM-dd
			// HH:mm:ss").format(new java.util.Date())+"上报图片:未连接上网络");
			Log.v("TPATT", "上报考勤:未连接上网络");
		}

		return uploadCount;
	}

	public static int postwarm(Context context, String token, String caid, String temp, String heig, String tall,
							   String time) {

		HashMap<String, Object> hs = new HashMap<String, Object>();

		// hs.put("SwipeData", m1);
		// hs.put("HealthData", m2);
		int uploadCount = 0;

		Gson gson = new Gson();
		// String paramStr = gson.toJson(hs);

		String postpathString = "http://iot.jxt189.com/deviceapi/data/updateData";
		// String postpathString =
		// "http://172.16.255.78:8088/openapi/GetStudentInfos.ashx";

		//

		AppBaseFun appBaseFun = new AppBaseFun();
		SettingPara settingPara = new SettingPara();

		boolean ismobile = false;
		if (appBaseFun.isMobileAvailable(context) && settingPara.isTake_internet()) {
			ismobile = true;
		} else {
			ismobile = false;
		}
		if (appBaseFun.isWifiAvailable(context) || ismobile || HttpConnect.isNetworkAvailable(context)) {
			// maxUploadAttCount = settingPara.getMaxUploadAttCount();
			for (int i = 0; i < 1; i++) {

				switch (settingPara.getAttPhotoPlatformProto()) {
					case 1: {
						String postRxText = "";
						// List<NameValuePair> params = new
						// ArrayList<NameValuePair>();

						// Bitmap bm = appBaseFun.getLoacalBitmap(photoPath);

						long moretime = conttimes(time) - oldtime();
						hs.put("CardNo", caid);

						// HashMap<String, Object> SwipeData=new HashMap<String,
						// Object>();
						// SwipeData.put("SwipeTime", moretime);
						// SwipeData.put("InOutType", "0");
						// SwipeData.put("Location", "1");

						HashMap<String, Object> HealthData = new HashMap<String, Object>();
						HealthData.put("CheckTime", moretime);
						HealthData.put("Temperature", temp);
						HealthData.put("Flag1", "0");
						HealthData.put("Flag2", "0");
						HealthData.put("Flag3", "0");
						HealthData.put("Flag4", "0");
						HealthData.put("Flag5", "0");
						HealthData.put("Flag6", "0");

						// hs.put("SwipeData", SwipeData);
						hs.put("HealthData", HealthData);

						// writeinfile(new SimpleDateFormat("yyyy-MM-dd
						// HH:mm:ss").format(new java.util.Date())+"上报考勤图片数据:"
						// +params.toString());
						if (hs != null) {
							// hs.put("filename",appBaseFun.bitmaptoString(bm));
							// params.add(new
							// BasicNameValuePair("PhotoImg",appBaseFun.bitmaptoString(bm)));
							// bm.recycle();//回收bitmap空间

							// 上报平台
							int uploadFlag = 1;
							for (int upload = 0; upload < 2; upload++) {
								// HttpApp httpApp=new HttpApp();

								// postRxText =
								// HttpConnect.httpPost(postpathString.trim(),
								// paramStr,token);
								// File file=new
								// File(appBaseFun.getSDPath()+"/tpatt/AttPhoto/" +
								// attdtime + cardtype + cardid + ".jpg");
								postRxText = FileImageUpload.uploadtem(null, postpathString, hs, token, null);
								// postRxText =
								// httpApp.postSendAndReceive(settingPara.getAttPhotoPlatformUrl(),
								// params);
								Log.v("TPATT", "上报考勤图片返回:" + postRxText);
								// appBaseFun.writeinfile("上报体温数据返回: " +
								// postRxText);
								WriteUnit.loadlist("上报体温数据返回:" + postRxText);
								// writeinfile( new SimpleDateFormat("yyyy-MM-dd
								// HH:mm:ss").format(new
								// java.util.Date())+"上报考勤图片返回:"
								// +postRxText.toString());
								if (postRxText != null) {

									try {
										Data data = gson.fromJson(postRxText, new TypeToken<Data>() {
										}.getType());

										// JSONObject obj = new
										// JSONObject(postRxText);
										String strTemp = data.getCode();
										if (strTemp.equals("20000")) {
											uploadFlag = 1;
											Log.v("TPATT", "上传图片成功:" + postRxText);
											break;
										} else {
											// writeinfile(new
											// SimpleDateFormat("yyyy-MM-dd
											// HH:mm:ss").format(new
											// java.util.Date())+"上传图片重发1");
											Log.v("TPATT", "上传图片重发1");
										}
									} catch (Exception e) {
										// writeinfile(new
										// SimpleDateFormat("yyyy-MM-dd
										// HH:mm:ss").format(new
										// java.util.Date())+"上传图片重发2");
										Log.v("TPATT", "上传图片重发2");
									}
									// }
									// else
									// {
									// Log.v("TPATT", "上传图片无应答1" );
									// uploadFlag = 0;
									// }
								} else {
									Log.v("TPATT", "上传图片无应答2");
									uploadFlag = 0;
								}
							}
							if (uploadFlag == 1) {
								uploadCount = 1;
							}
						}

					}
					break;

					default:
						Log.v("TPATT", "上报考勤:未知平台协议 ");
						break;
				}
			}

		} else {
			// writeinfile(new SimpleDateFormat("yyyy-MM-dd
			// HH:mm:ss").format(new java.util.Date())+"上报图片:未连接上网络");
			Log.v("TPATT", "上报考勤:未连接上网络");
		}

		return uploadCount;
	}

	public static long conttime(String cardtime) {
		cardtime = cardtime.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
		// cardtime.replaceAll("-", "");
		// cardtime.replaceAll(":", "");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date dt2 = null;
		try {
			dt2 = sdf.parse(cardtime);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		// 继续转换得到秒数的long型
		long startDateLong = dt2.getTime();

		long nowtime = oldtime();

		return startDateLong - nowtime;
	}

	public static long conttimes(String cardtime) {
		cardtime = cardtime.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "").replaceAll("/", "");
		// cardtime.replaceAll("-", "");
		// cardtime.replaceAll(":", "");
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		Date dt2 = null;
		try {
			dt2 = sdf.parse(cardtime);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		// 继续转换得到秒数的long型
		long startDateLong = dt2.getTime();

		long nowtime = oldtime();

		return startDateLong - nowtime;
	}

	public static long oldtime() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date dt2 = null;
		try {
			dt2 = sdf.parse("19700101080000");
		} catch (ParseException e) {

			e.printStackTrace();
		}
		// 继续转换得到秒数的long型
		long startDateLong = dt2.getTime();

		return startDateLong;
	}

	public static String gettime(long tt) {

		Date date = new Date(tt);
		String strs = "";
		try {
			// yyyy表示年MM表示月dd表示日
			// yyyy-MM-dd是日期的格式，比如2015-12-12如果你要得到2015年12月12日就换成yyyy年MM月dd日
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// 进行格式化
			strs = sdf.format(date);
			System.out.println(strs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strs;
	}

	/*
	 * Function : 发送Post请求到服务器 Param : params请求体内容，encode编码格式
	 */
	public static String submitPostData(String strUrlPath, HashMap<String, String> params, String encode) {

		// byte[] data = getRequestData(params,
		// HTTP.UTF_8).toString().getBytes();//获得请求体
		try {

			URL url = new URL(strUrlPath);

			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(2000); // 设置连接超时时间
			httpURLConnection.setReadTimeout(2000);

			httpURLConnection.setDoInput(true); // 打开输入流，以便从服务器获取数据
			httpURLConnection.setDoOutput(true); // 打开输出流，以便向服务器提交数据
			httpURLConnection.setRequestMethod("POST"); // 设置以Post方式提交数据
			httpURLConnection.setUseCaches(false); // 使用Post方式不能使用缓存
			// 设置请求体的类型是文本类型
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			httpURLConnection.setRequestProperty("Device-Authorization", encode);
			// 设置请求体的长度
			// httpURLConnection.setRequestProperty("Content-Length",
			// String.valueOf(data.length));
			// 获得输出流，向服务器写入数据
			OutputStream outputStream = httpURLConnection.getOutputStream();
			Gson gson = new Gson();
			outputStream.write(gson.toJson(params).getBytes());

			int response = httpURLConnection.getResponseCode();
			Log.i("tappo", "返回到响应结果" + response);
			// 获得服务器的响应码
			if (response == HttpURLConnection.HTTP_OK) {
				SwingCardAttActivity.isnet=true;
				InputStream inptStream = httpURLConnection.getInputStream();
				return dealResponseResult(inptStream); // 处理服务器的响应结果
			}
		} catch (IOException e) {
			Log.i("tappo", "能龙请求出错: " + e.toString());
			SwingCardAttActivity.isnet=false;
			return "-1";
		}
		return "-1";
	}

	/*
	 * Function : 封装请求体信息 Param : params请求体内容，encode编码格式
	 */
	public static StringBuffer getRequestData(HashMap<String, String> params, String encode) {
		StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
		try {
			for (HashMap.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	/*
	 * Function : 处理服务器的响应结果（将输入流转化成字符串） Param : inputStream服务器的响应输入流
	 */
	public static String dealResponseResult(InputStream inputStream) {
		String resultData = null; // 存储处理结果
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		try {
			while ((len = inputStream.read(data)) != -1) {
				byteArrayOutputStream.write(data, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		resultData = new String(byteArrayOutputStream.toByteArray());
		return resultData;
	}

	// public static String fortime(){
	//
	// // long yi=1457676314000;
	//
	// long nu = oldtime()+1457676314000;
	//
	// return null;
	// }
	//

	public static boolean checkInternet(Context context)
	{
		ConnectivityManager cm=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo info=cm.getActiveNetworkInfo();
		if(info!=null&&info.isConnected())
		{
			//能连接Internet
			Log.i("tpyy", "msg:"+info);
			if (info.getState() == NetworkInfo.State.CONNECTED)
			{
				Log.i("tpyy1", "msg:"+info.getState());
				// 当前所连接的网络可用
				return true;
			}
		}


		return false;
	}



	class Data {

		private String Code;
		private String Msg;

		public String getCode() {
			return Code;
		}

		public void setCode(String code) {
			Code = code;
		}

		public String getMsg() {
			return Msg;
		}

		public void setMsg(String msg) {
			Msg = msg;
		}

		class Result {

			private String Location;
			private String InOutType;
			private String SwipeTime;
			private String ScreenShot;

			public String getLocation() {
				return Location;
			}

			public void setLocation(String location) {
				Location = location;
			}

			public String getInOutType() {
				return InOutType;
			}

			public void setInOutType(String inOutType) {
				InOutType = inOutType;
			}

			public String getSwipeTime() {
				return SwipeTime;
			}

			public void setSwipeTime(String swipeTime) {
				SwipeTime = swipeTime;
			}

			public String getScreenShot() {
				return ScreenShot;
			}

			public void setScreenShot(String screenShot) {
				ScreenShot = screenShot;
			}

		}

	}

}
