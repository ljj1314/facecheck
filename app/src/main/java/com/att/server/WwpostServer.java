package com.att.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

import com.att.AppBaseFun;
import com.att.DBOpenHelper;
import com.att.DBOpenHelper.AttMeasure;
import com.att.HttpApp;
import com.att.MainIdleActivity;
import com.att.SettingPara;
import com.att.act.ApSocket;
import com.att.act.OKHttpUtils;
import com.att.act.WriteUnit;
import com.baidu.android.common.logging.Log;
import com.google.gson.Gson;
import com.serport.DataUtils;
import com.serport.SerialPortUtil;
import com.telpo.dataprocess.DataProcess;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WwpostServer extends Service {

	private SerialPortUtil serport = null;
	@SuppressWarnings("unused")
	private ApSocket aps = null;
	// private TCPClient tp = null;
	private Handler mhandle = null;
	private boolean isfull = false;
	private byte[] realtem = null;
	private int reallen = 0;
	private int pos = 0;
	private Handler handlem = new Handler();
	// private Handler khandle = new Handler();
	private int inputsum = 0;
	private Handler sendhandle = new Handler();
	// private SharedPreferences sp;
	// private AppBaseFun appbean = new AppBaseFun();

	OkHttpClient client = new OkHttpClient();
	SettingPara settingPara;
	HttpApp httpApp = new HttpApp();

	private DBOpenHelper sqlAP;
	private Handler thandle=null;

	private String ap_version = "";
	private boolean status_ap = false;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {

		settingPara = new SettingPara(getApplicationContext());

		try {
			serport = SerialPortUtil.getInstance();
		} catch (Exception e) {

		}

		// sp = getSharedPreferences("json", Context.MODE_PRIVATE);

		sqlAP = new DBOpenHelper(WwpostServer.this);
		try {
			// Log.i("AP", "createDataBase WwpostServer");
			sqlAP.createDataBase();
		} catch (Exception e) {
			Log.i("AP", "createDataBase 失败" + e.toString());
		}

		serport.setOnDataReceiveListener(new SerialPortUtil.OnDataReceiveListener() {

			public void onDataReceive(byte[] buffer, int size) {
				// Log.i("AP", "收到AP数据：" + buffer.toString());

				// String mmString=DataUtils.bytesToHexString(buffer);

				Message ms = new Message();
				ms.what = 1;
				Bundle bd = new Bundle();
				bd.putByteArray("data", buffer);
				bd.putInt("size", size);
				ms.setData(bd);
				mhandle.sendMessage(ms);

			}
		});

		HandlerThread handlerThread = new HandlerThread("ww");
		handlerThread.start();

		mhandle = new Handler(handlerThread.getLooper()) {

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:

						byte[] tt = msg.getData().getByteArray("data");
						int size = msg.getData().getInt("size");
						Log.i("AP", "receive:" + "...size" + size + DataUtils.bytesToHexString(tt));
						if (isfull == false) {

							if (tt[0] == 0x02) {// 帧头

								reallen = DataUtils.bytesToint2(tt[1], tt[2]);
								if (reallen > size) {

									isfull = true;
									realtem = new byte[reallen + 4];
									// byte[] data=new byte[size];
									System.arraycopy(tt, 0, realtem, 0, size);
									pos = size;

								} else {

									isfull = false;
									pos = 0;
									byte[] data = new byte[size];
									System.arraycopy(tt, 0, data, 0, size);
									Log.i("AP", DataUtils.bytesToHexString(data));
									//	measureParse(data);

									Message mg=new Message();
									mg.what=0;
									Bundle b=new Bundle();
									b.putByteArray("data", data);
									mg.setData(b);
									thandle.sendMessage(mg);
								}

							}

						} else {
							try {
								System.arraycopy(tt, 0, realtem, pos, size);
								pos += size;
								Log.i("AP", "size" + pos + "..len" + reallen);
								if (pos > reallen) {
									isfull = false;
									reallen = 0;
									pos = 0;

									//measureParse(realtem);
									Message mg=new Message();
									mg.what=0;
									Bundle b=new Bundle();
									b.putByteArray("data", realtem);
									mg.setData(b);
									thandle.sendMessage(mg);
								}
							} catch (Exception e) {

							}

						}

						break;

					case 1:

						byte[] tt1 = msg.getData().getByteArray("data");
						int size1 = msg.getData().getInt("size");
						byte[] data = new byte[size1];
						System.arraycopy(tt1, 0, data, 0, size1);
						Log.i("AP", "收到AP数据:" + DataUtils.bytesToHexString(data));
						//	measureParse(data);
						Message mg=new Message();
						mg.what=0;
						Bundle b=new Bundle();
						b.putByteArray("data", data);
						mg.setData(b);
						thandle.sendMessage(mg);
						break;

					default:
						break;
				}

			}

		};


		HandlerThread handlerThread1 = new HandlerThread("post");
		handlerThread1.start();
		thandle=new Handler(handlerThread1.getLooper()){

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:

						byte[] tt = msg.getData().getByteArray("data");
						measureParse(tt);

				}
			}
		};


		handlem.postDelayed(hbRunnable, 1000 * 60);// 心跳
		// khandle.postDelayed(twRunnable, 1000 * 61);// 卡号反转(0x09)

		Timer send = new Timer();
		send.schedule(new TimerTask() {

			@Override
			public void run() {
				serport.sendCmd();// 查询蓝牙AP记录(0x06)
			}
		}, 1000 * 60 * 2, 1000 * 60);

		handlem.postDelayed(getStatusRunnable, 1000 * 10);
		// khandle.postDelayed(rn1, 8000);
		// sendhandle.postDelayed(rr, 1000 * 60 * 2);


		Timer ap=new Timer();
		ap.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!status_ap) {
					Intent intent = new Intent();
					intent.setAction("com.telpoedu.omc.FROM_ATT_ACTION");
					intent.putExtra("type", "ap_error");
					sendBroadcast(intent);
				}
			}
		}, 1000*60*8, 1000*60*5);



	}

	Runnable getStatusRunnable = new Runnable() {
		public void run() {
			sendGetStatus();
			// sendGetVersion();
			handlem.postDelayed(getStatusRunnable, 1000 * 60 * 2);
		}
	};

	Runnable rn1 = new Runnable() {
		public void run() {

			sendin();

		}
	};

	Runnable rr = new Runnable() {

		public void run() {

			// sendin();
			sendhandle.postDelayed(hbRunnable, 1000 * 60);
		}
	};

	Runnable twRunnable = new Runnable() {// 卡号反转(0x09)

		public void run() {

			try {

				byte[] mBuffer = new byte[7];

				mBuffer[0] = 0x02;
				mBuffer[1] = 0x00;
				mBuffer[2] = 0x03;
				mBuffer[3] = 0x01;
				mBuffer[4] = 0x09;// 卡号反转(0x09)

				SettingPara spPara = new SettingPara();
				if (spPara.isCard_reversal()) {// 0x00:关闭,0x01:开启
					mBuffer[5] = (byte) 0x01;
				} else {
					mBuffer[5] = (byte) 0x00;
				}

				// mBuffer[5] = (byte) 0xff;
				// mBuffer[6] = 0x55;

				int num = 0;

				for (int i = 0; i < 7; i++) {
					num ^= mBuffer[i];
				}
				mBuffer[6] = (byte) num;

				// xm[6]=(byte) sum;

				Log.i("AP", "下达卡号是否反转:" + DataUtils.bytesToHexString(mBuffer));
				@SuppressWarnings("unused")
				boolean issuc = serport.sendBuffer(mBuffer);
				// Log.i("AP", "下达卡号是否反转是否成功：" + issuc);

			} catch (Exception e) {

				Log.i("AP", "下达卡号是否反转出错:" + e.toString());
			}

		}
	};

	private void measureParse(byte[] buffer) {

		// sendhandle.removeCallbacks(rr);
		// sendhandle.postDelayed(rr, 1000 * 60);

		try {

			if (buffer[0] != 0x02) {

				return;
			}

			int reversal = 0;
			if (settingPara.isCard_reversal()) {
				reversal = 1;
			} else {
				reversal = 0;
			}

			DataProcess lib = new DataProcess();

			switch (buffer[4]) {

				case 0x11:// 令牌使用中透传消息(0x11)

					inputsum++;
					Log.i("postsum", "收到的数据条目:" + inputsum);

					break;

				case 0x04:// 心跳(0x04)
					Log.i("AP", "收到心跳的返回");
					status_ap = true;
					break;

				case 0x08:// 下达终端ID，密码，MD5验证(0x08)

					Log.i("AP", "收到下达终端ID，密码，MD5验证的返回");

					if (buffer[5] == 0x01) {// 0x01：表示下达成功
						status_ap = true;
						break;

					} else {// 0x00：表示下达失败
						status_ap = false;
						sendsm();

					}

					break;

				case 0x4F:

					handlem.postDelayed(hbRunnable, 0);
					break;

				case 0x12:// 令牌使用完返还消息(0x12)

					sendin();

					break;

				case 0x10:// 令牌到达消息

					// if (buffer[5] == 0x4F && buffer[6] == 0x4B) {
					// sendhandle.removeCallbacks(rr);
					// }

					break;

				case 0x07:// 查询AP接收的从机版本号(0x07)
					Log.i("AP", "收到查询AP接收的从机版本号的返回");

					break;

				case 0x0C:// 查询状态(0x0C)
					// Log.i("AP", "收到查询版本号的返回");
					status_ap = true;
					byte[] len = new byte[2];
					len[0] = buffer[1];
					len[1] = buffer[2];
					int le = DataUtils.bytesToint2(len[0], len[1]);
					if (le > 2) {
						byte[] con = new byte[le - 2];
						System.arraycopy(buffer, 5, con, 0, le - 2);
						String result = convertHexToString(DataUtils.bytesToHexString(con));
						String line = "";
						String keyword = "Version:";
						int index = result.indexOf(keyword);
						line = result.substring(index + keyword.length());
						index = line.indexOf("Time:");
						ap_version = line.substring(0, index);
						Log.i("AP", "收到蓝牙AP版本号: " + ap_version);
						if (!ap_version.equals(settingPara.getAp_version())) {
							settingPara.setAp_version(ap_version);
							settingPara.save_settingpara();
						}
					}
					break;

				case 0x06:// 查询蓝牙AP记录(0x06)
					Log.i("AP", "收到查询蓝牙AP记录的返回");
					status_ap = true;
					handlem.removeCallbacks(hbRunnable);
					handlem.postDelayed(hbRunnable, 1000 * 60 * 2);
					if (buffer[5] == 0x00) {
						break;// 收到记录为空
					}
					inputsum++;
					Log.i("postsum", "收到的数据条目:" + inputsum);
					String data = lib.loadap(buffer, buffer.length, reversal);

					String[] arg = data.split("#");

					int num = Integer.valueOf(arg[0]).intValue();
					if (num > 0) {
						Log.i("AP", "收到的数据条目:" + num + "解析后数据是:" + data);
						for (int i = 0; i < num; i++) {

							try {

								String[] coin = arg[i + 1].split("-");
								String caid = coin[0];
								String tem = coin[1];// 体温
								String heig = coin[2];// 体重
								String tall = coin[3];// 高度
								String time = coin[4];// 时间

								if (settingPara.getCard_upload()==1) {
									caid=dec2hex(caid);
								}

								Log.i("AP", "体温:" + tem + " 卡号:" + caid);
								WriteUnit.loadlist("体温:" + tem + " 卡号:" + caid + " 时间：" + time);
								// appbean.writeinfile("体温:"+tem+"卡号是:"+caid+"时间是："+time);
								if (tem == null || tem.equals("0") || tem.equals("FF")) {

									tem = "0";

								}

								if (heig == null || heig.equals("0") || heig.equals("FF")) {

									heig = "0";

								}

								if (tall == null || tall.equals("0") || tall.equals("FF")) {

									tall = "0";

								}

								if (time == null || time.equals("0") || time.equals("FF")) {

									time = "0";

								}

								double temd = Double.valueOf(tem);
								double talld = Double.valueOf(tall);
								double heigd = Double.valueOf(heig);

								if (0 < temd && temd < 35.5) {
									continue;
								}

								if (temd > 40) {
									continue;
								}

								if (0 < talld && talld < 50) {
									continue;
								}

								if (talld > 180) {
									continue;

								}

								if (heigd > 100) {
									continue;
								}

								// CheckNotUploadAtt(getApplicationContext(), caid,
								// time, tem, tall, heig);
								sqlAP.saveAttMeasure(caid, time, tem, tall, heig);
								int uploadCount = 0;
								if (settingPara.getAttPlatformProto() == 0) {
									uploadCount = uploadAttMeasureWW(settingPara, getApplicationContext(), caid, time, tem,
											tall, heig);
								} else if (settingPara.getAttPlatformProto() == 2) {// 中维平台体温数据上传
									uploadCount = uploadAttMeasureZW(getApplicationContext(), caid, time, tem);
								}
								sqlAP.saveUploadAttMeasure(uploadCount);

							} catch (Exception e) {
							}


							Thread.sleep(300);


						}

					}

					// String message="";

					break;

				default:

					break;
			}
			MainIdleActivity.sendPlatformApBroadcast(getApplicationContext(), settingPara, false, status_ap);

		} catch (Exception e) {

			Log.i("AP", e.toString());
		}

	}

	public static String dec2hex(String dec) {
		String hex = new String();
		long num = Long.parseLong(dec, 10);
		hex = Long.toString(num, 16);
		return hex;
	}


	@Override
	public void onDestroy() {

		super.onDestroy();

		serport.closeSerialPort();

	}

	private void sendsm() {// 下达终端ID，密码，MD5验证(0x08)

		try {
			String id = "04050000000000000008";// 终端ID: 04050000000000000007
			String pw = "a4F06cKAil8=";// 密码：a4F06cKAil8=
			String md5 = "6E0C2BC650D2810B";// MD5码：6E0C2BC650D2810B
			String xd = id + "-" + pw + "-" + md5;// 下达内容

			int len = xd.getBytes().length;

			byte[] xm = new byte[len + 6];

			xm[0] = 0x02;// 帧头
			xm[1] = 0x00;// 长度
			xm[2] = (byte) (len + 2);// 长度,从地址到报文体(包括报文体)，高位在前
			xm[3] = 0x01;// 地址
			xm[4] = 0x08;// 功能号
			// xm[4]=(byte) len;
			System.arraycopy(xd.getBytes(), 0, xm, 5, len);// 报文体

			int sn = 0;

			for (int i = 0; i < (xm.length - 1); i++) {
				sn ^= xm[i];
			}
			xm[len + 5] = (byte) sn;// 校验码
			Log.i("AP", "下达终端连接指令:" + DataUtils.bytesToHexString(xm));
			@SuppressWarnings("unused")
			boolean issuc = serport.sendBuffer(xm);
			// Log.i("AP", "下达指令:" + issuc);
		} catch (Exception e) {
			Log.i("AP", "下达终端连接指令出错:" + e.toString());
		}
	}

	// 进入透传模式

	private void sendin() {// 令牌到达消息

		try {

			byte[] mBuffer = new byte[7];

			mBuffer[0] = 0x02;// 帧头
			mBuffer[1] = 0x00;// 长度
			mBuffer[2] = 0x03;// 长度
			mBuffer[3] = 0x01;// 地址
			mBuffer[4] = 0x10;// 功能号
			mBuffer[5] = (byte) 0xff;// 报文体
			// mBuffer[6] = 0x55;

			int num = 0;

			for (int i = 0; i < 7; i++) {
				num ^= mBuffer[i];
			}
			mBuffer[6] = (byte) num;// 校验码

			// xm[6]=(byte) sum;

			Log.i("AP", "开启透传:" + DataUtils.bytesToHexString(mBuffer));
			@SuppressWarnings("unused")
			boolean issuc = serport.sendBuffer(mBuffer);
			// Log.i("AP", "开启透传：++" + issuc);

		} catch (Exception e) {

			Log.i("AP", "开启透传出错:" + e.toString());
		}

	}

	private void sendGetStatus() {// 查询状态(0x0C)

		try {

			byte[] mBuffer = new byte[8];

			mBuffer[0] = 0x02;
			mBuffer[1] = 0x00;
			mBuffer[2] = 0x04;
			mBuffer[3] = 0x01;
			mBuffer[4] = 0x0C;// 查询状态
			mBuffer[5] = 0x55;// 报文体
			mBuffer[6] = 0x55;// 报文体

			int num = 0;

			for (int i = 0; i < 7; i++) {
				num ^= mBuffer[i];
			}
			mBuffer[7] = (byte) num;

			Log.i("AP", "查询AP版本号:" + DataUtils.bytesToHexString(mBuffer));
			boolean issuc = serport.sendBuffer(mBuffer);

			if (issuc) {
				ap_version = "";
				handlem.postDelayed(new Runnable() {
					public void run() {
						if (ap_version != null && ap_version.equals("")) {
							Log.i("AP", "获取AP版本号超时，清除AP版本号");
							if (settingPara.getAp_version() != null && settingPara.getAp_version().length() > 0) {
								settingPara.setAp_version("");
								settingPara.save_settingpara();
							}
						}
					}
				}, 1000 * 20);
			}

		} catch (Exception e) {

			Log.i("AP", "查询AP版本号出错:" + e.toString());
		}

	}

	@SuppressWarnings("unused")
	private void sendGetVersion() {// 查询AP接收的从机版本号(0x07)

		try {

			byte[] mBuffer = new byte[8];

			mBuffer[0] = 0x02;
			mBuffer[1] = 0x00;
			mBuffer[2] = 0x04;
			mBuffer[3] = 0x01;
			mBuffer[4] = 0x07;// 查询AP接收的从机版本号
			mBuffer[5] = 0x55;// 报文体
			mBuffer[6] = 0x55;// 报文体

			int num = 0;

			for (int i = 0; i < 7; i++) {
				num ^= mBuffer[i];
			}
			mBuffer[7] = (byte) num;

			Log.i("AP", "查询AP接收的从机版本号:" + DataUtils.bytesToHexString(mBuffer));
			boolean issuc = serport.sendBuffer(mBuffer);

		} catch (Exception e) {

			Log.i("AP", "查询AP接收的从机版本号出错:" + e.toString());
		}

	}

	public String convertHexToString(String hex) {

		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		// 49204c6f7665204a617661 split into two characters 49, 20, 4c...
		for (int i = 0; i < hex.length() - 1; i += 2) {

			// grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			// convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			// convert the decimal to character
			sb.append((char) decimal);

			temp.append(decimal);
		}

		return sb.toString();
	}

	Runnable hbRunnable = new Runnable() {// 心跳(0x04)

		public void run() {

			try {

				Calendar calendar = Calendar.getInstance();
				int timey = calendar.get(Calendar.YEAR) - 2000;
				// Log.i("AP", "year" + timey);
				int mon = calendar.get(Calendar.MONTH) + 1;
				int day = calendar.get(Calendar.DATE);
				int hh = calendar.get(Calendar.HOUR_OF_DAY);
				int mm = calendar.get(Calendar.MINUTE);
				int ss = calendar.get(Calendar.SECOND);

				// int
				// fy=Integer.valueOf(Integer.toHexString(timey)).intValue();
				// int fm=Integer.valueOf(Integer.toHexString(mon)).intValue();
				// int fd=Integer.valueOf(Integer.toHexString(day)).intValue();
				// int fh=Integer.valueOf(Integer.toHexString(hh)).intValue();
				// int fmm=Integer.valueOf(Integer.toHexString(mm)).intValue();
				// int fs=Integer.valueOf(Integer.toHexString(ss)).intValue();
				//
				// Log.i("AP",
				// "befor:"+timey+"-"+mon+"-"+day+"-"+hh+"-"+mm+"-"+ss+"||after:"+fy+"-"+fm+"-"+fd+"-"+fh+"-"+fmm+"-"+fs);

				// SettingPara abf=new SettingPara();
				int len = 7 + 6;
				byte[] heartbear = new byte[len];

				// byte[] deviceid= abf.getDevicID().getBytes();

				heartbear[0] = 0x02;
				heartbear[1] = 0x00;
				heartbear[2] = 0x09;
				heartbear[3] = 0x01;
				heartbear[4] = 0x04;// 心跳(0x04)
				heartbear[5] = (byte) timey;
				heartbear[6] = (byte) mon;
				heartbear[7] = (byte) day;
				heartbear[8] = (byte) hh;
				heartbear[9] = (byte) mm;
				heartbear[10] = (byte) ss;
				heartbear[11] = 0x01;
				// heartbear[11]=0x01;
				// System.arraycopy(deviceid, 0, heartbear, 12,
				// deviceid.length);

				int sum = 0;

				for (int i = 0; i < len; i++) {

					sum ^= heartbear[i];

				}
				heartbear[heartbear.length - 1] = (byte) sum;

				Log.i("AP", "发送AP时间心跳包:" + DataUtils.bytesToHexString(heartbear));

				serport.sendBuffer(heartbear);

			} catch (Exception e) {

				Log.i("AP", e.toString());
			}

			MainIdleActivity.updateStatusAP(getApplicationContext(), settingPara);
			handlem.postDelayed(hbRunnable, 1000 * 60 * 2);

		}
	};

	// 上报体温数据（体温接口）
	private static synchronized int CheckNotUploadAtt(SettingPara settingPara, Context context, String cardid,
													  String cardtime, String temp, String tall, String height) throws JSONException {
		// int maxUploadAttCount;// 每次上报最大考勤数
		int uploadCount = 0;
		// if (MainIdleActivity.isNetworkAvailables(context, appbean,
		// settingPara)) {
		// maxUploadAttCount = settingPara.getMaxUploadAttCount();

		switch (settingPara.getAttPlatformProto()) {
			case 0: {
				String postRxText = "";
				// List<NameValuePair> params = new ArrayList<NameValuePair>();

				// TimeStamp
				SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date curDate2 = new Date(System.currentTimeMillis());// 获取当前时间
				String timestamp = formatter2.format(curDate2);

				// 考勤时间
				// String attdtime = attInfo.getDtime();
				// byte[] midbytes = attdtime.getBytes();
				// String cardtime = new String(midbytes, 0, 4) + "-" + new
				// String(midbytes, 4, 2) + "-" + new String(midbytes, 6, 2)
				// + " " + new String(midbytes, 8, 2) + ":" + new
				// String(midbytes, 10, 2) + ":" + new String(midbytes, 12, 2);
				// TransactionID
				String transactionid = java.util.UUID.randomUUID().toString().replaceAll("-", "");

				// 进出校
				String cardtype = "0";
				cardtime = "20" + cardtime.replaceAll("/", "-");

				HashMap<String, Object> params = new HashMap<String, Object>();

				params.put("ProvinceCode", settingPara.getProvincCode());
				// params.add(new BasicNameValuePair("ProvinceCode",
				// settingPara.getProvincCode()));
				params.put("TransactionID", transactionid);
				params.put("SchoolID", settingPara.getSchoolID());
				params.put("DeviceID", settingPara.getDevicID());
				params.put("CardID", cardid);
				params.put("CardTime", cardtime);
				params.put("CardType", cardtype);
				params.put("TimeStamp", timestamp);
				// params.put("Extension",java.util.UUID.randomUUID().toString());

				params.put("Temperature", temp);
				params.put("Height", tall);
				params.put("BodyWeight", height);

				HashMap<String, Object> jh = new HashMap<String, Object>();
				jh.put("JsonInfo", params);

				List<HashMap<String, Object>> lb = new ArrayList<HashMap<String, Object>>();

				lb.add(jh);

				Gson gson = new Gson();
				String str = gson.toJson(params);

				// 上报平台
				Log.i("AP", "万维上报测量数据:" + params.toString());
				WriteUnit.loadlist(timestamp + "万维上报测量数据:" + params.toString());
				int uploadFlag = 0;
				for (int upload = 0; upload < 1; upload++) {
					// postRxText =httpPost(settingPara.getTempurl(),
					// "JsonInfo="+params.toString());
					// postRxText =
					// httpApp.postSendAndReceive(settingPara.getTempurl(),
					// params);

					String url = settingPara.getTempurl();

					postRxText = getPost(url, "JsonInfo=" + str);

					Log.i("AP", "url:" + url + "\r\n万维上报测量数据返回:" + postRxText);
					WriteUnit.loadlist(formatter2.format(new java.util.Date()) + "上报测量数据返回:" + params.toString());

					if (postRxText != null) {
						// / JSONArray jsonArray = new JSONArray(postRxText);
						// if ( jsonArray.length() > 0 )
						// {
						// JSONObject obj = jsonArray.getJSONObject(0);
						try {
							// JSONTokener jsonParser = new JSONTokener(postRxText);
							// JSONObject jsonObject = (JSONObject)
							// jsonParser.nextValue();
							JSONObject jsonObject = new JSONObject(postRxText);
							String strTemp = jsonObject.getString("code");

							if (strTemp.equals("200") || strTemp.equals("400") || strTemp.equals("success")
									|| strTemp.equals("-500")) {
								uploadFlag = 1;
								Log.i("AP", "上传测量成功");
								break;
							} else {
								WriteUnit.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
										+ "上报测量重发1");
								// appBaseFun.writeinfile(new
								// SimpleDateFormat("yyyy-MM-ddW
								// HH:mm:ss").format(new
								// java.util.Date())+"上传考勤重发1");
								Log.i("AP", "上传测量重发1");
							}
						} catch (Exception e) {
							if (postRxText.contains("\"code\":\"200\"")) {
								uploadFlag = 1;
								Log.i("AP", "上传测量成功");
							} else {
								WriteUnit.loadlist(formatter2.format(new java.util.Date()) + "上报测量重发2" + e.toString());
								Log.i("AP", "上传测量重发2:" + e.toString());
								uploadFlag=0;
							}
						}

					} else {
						WriteUnit.loadlist(
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "上传测量无应答2");
						// appBaseFun.writeinfile(new
						// SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new
						// java.util.Date())+"上传测量无应答2");
						Log.i("AP", "上传测量无应答2");
						uploadFlag = 0;
					}

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}

				}
				if (uploadFlag == 1) {
					uploadCount++;
				}
			}
			break;

			default:
				Log.i("AP", "上报测量:未知平台协议 ");
				break;
		}

		// } else {
		// WriteUnit
		// .loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new
		// java.util.Date()) + "上报考勤:未连接上网络");
		// // appBaseFun.writeinfile(new SimpleDateFormat("yyyy-MM-dd
		// // HH:mm:ss").format(new java.util.Date())+"上报考勤:未连接上网络");
		// Log.i("AP", "上报测量:未连接上网络");
		// }

		return uploadCount;
	}

	private static String getPost(String path, String mess) {

		URL url;
		// String res = null;
		try {
			url = new URL(path);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(3 * 1000);
			// 如果通过post提交数据，必须设置允许对外输出数据
			conn.setDoOutput(true);
			// 此两参数必须设置
			// Content-Type: application/x-www-form-urlencoded
			// Content-Length: 38
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// conn.setRequestProperty("Content-Length",
			// String.valueOf(entitydata.length));
			OutputStream outStream = conn.getOutputStream();

			outStream.write(mess.getBytes("utf-8"));
			outStream.flush();
			outStream.close();

			int response = conn.getResponseCode();
			Log.i("AP", "返回到响应结果" + response);
			// 获得服务器的响应码
			if (response == HttpURLConnection.HTTP_OK) {
				InputStream inptStream = conn.getInputStream();
				// inptStream.close();
				return dealResponseResult(inptStream); // 处理服务器的响应结果
			}
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return null;
	}

	/*
	 * Function : 处理服务器的响应结果（将输入流转化成字符串） Param : inputStream服务器的响应输入流
	 */
	public static String dealResponseResult(InputStream inputStream) throws IOException {
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
		inputStream.close();
		resultData = new String(byteArrayOutputStream.toByteArray());
		byteArrayOutputStream.close();
		return resultData;
	}

	// 上报考勤测量数据（万维）
	public static synchronized int uploadAttMeasureWW(SettingPara settingPara, Context context, String cardid,
													  String cardtime, String temp, String tall, String height) throws JSONException {
		String platformID = settingPara.getPlatformId();
		if (platformID == "1") {// 和宝贝
			return CheckNotUploadAtt(settingPara, context, cardid, cardtime, temp, tall, height);
		} else {
			return CheckNotUploadAtt(settingPara, context, cardid, cardtime, temp, tall, height);
		}
	}

	// 上报考勤测量数据（中维）
	public static synchronized int uploadAttMeasureZW(Context context, String cardid, String cardtime, String temp)
			throws JSONException {
		int uploadCount = 0;
		SharedPreferences sp = context.getSharedPreferences("json", Context.MODE_PRIVATE);
		String uuid = sp.getString(cardid + "idimfo", "");
		if (uuid != null && uuid.length() > 0) {
			OKHttpUtils oKHttpUtils = new OKHttpUtils(context);
			boolean status = oKHttpUtils.addTemperature(uuid, cardid, temp, cardtime);
			if (status) {
				uploadCount = 1;
			}
		} else {
			uploadCount = 1;
		}
		return uploadCount;
	}

	// 上报考勤测量数据（使用考勤接口）
	public static synchronized int CheckNotUploadAttMeasure(SettingPara settingPara, Context context, String cardid,
															String cardtime, String temp, String tall, String height) throws JSONException {
		AppBaseFun appbean = new AppBaseFun();
		// SettingPara settingPara = new SettingPara();
		@SuppressWarnings("unused")
		int maxUploadAttCount;// 每次上报最大考勤数
		int uploadCount = 0;
		if (MainIdleActivity.isNetworkAvailables(context, appbean, settingPara)) {
			maxUploadAttCount = settingPara.getMaxUploadAttCount();

			switch (settingPara.getAttPlatformProto()) {
				case 0: {
					String postRxText = "";

					int uploadFlag = 1;
					for (int upload = 0; upload < 2; upload++) {
						try {
							postRxText = uploadAttMeasure(settingPara.getAttPlatformUrl(), cardid, cardtime, temp, tall,
									height);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						Log.i("AP", "上报测量数据返回 " + postRxText);
						WriteUnit.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
								+ "上报测量数据返回:" + postRxText);
						if (postRxText != null) {
							JSONObject obj = new JSONObject(postRxText);
							try {
								String strTemp = obj.getString("ResultCode");

								if (strTemp.equals("200") || strTemp.equals("400") || strTemp.equals("success")
										|| strTemp.equals("500")) {
									Log.i("AP", "上传测量成功");
									break;
								} else {
									WriteUnit.loadlist(
											new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
													+ "上传测量重发1");
									Log.i("AP", "上传测量重发1");
								}
							} catch (Exception e) {
								WriteUnit.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
										+ "上传测量重发2");
								Log.i("AP", "上传测量重发2");
							}

						} else {
							WriteUnit.loadlist(
									new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "上传测量无应答2");
							Log.i("AP", "上传测量无应答2");
							uploadFlag = 0;
						}
					}
					if (uploadFlag == 1) {
						uploadCount++;
					}
				}
				break;

				default:
					Log.i("AP", "上报测量:未知平台协议 ");
					break;
			}

		} else {
			WriteUnit
					.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "上报考勤:未连接上网络");
			Log.i("AP", "上报测量:未连接上网络");
		}

		return uploadCount;
	}

	// 上报考勤测量数据
	public static String uploadAttMeasure(String url, String cardid, String cardtime, String temp, String tall,
										  String height) throws IOException {
		OkHttpClient client = new OkHttpClient();
		SettingPara settingPara = new SettingPara();

		cardtime=("20"+cardtime).replaceAll("/", "-");


		// TimeStamp
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate2 = new Date(System.currentTimeMillis());// 获取当前时间
		String timestamp = formatter2.format(curDate2);

		// TransactionID
		String transactionid = java.util.UUID.randomUUID().toString().replaceAll("-", "");

		// 进出校
		String cardtype = "0";

		FormBody formBody = new FormBody.Builder()

				.add("ProvinceCode", settingPara.getProvincCode()).add("TransactionID", transactionid)
				.add("SchoolID", settingPara.getSchoolID()).add("DeviceID", settingPara.getDevicID())
				.add("CardID", cardid).add("CardTime", cardtime).add("CardType", cardtype).add("TimeStamp", timestamp)
				.add("Extension", java.util.UUID.randomUUID().toString()).add("Temperature", temp).add("Height", tall)
				.add("Weight", height).build();
		android.util.Log.i("tiwen", formBody.toString());
		// RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(formBody).build();

		Response response = client.newCall(request).execute();
		if (response.isSuccessful()) {
			return response.body().string();
		} else {
			throw new IOException("Unexpected code " + response);
		}
	}

	/**
	 * 上报测量数据
	 */
	public static synchronized int CheckNotUploadAttMeasure(SettingPara settingPara, Context context,
															DBOpenHelper sqlAP) throws JSONException {
		AppBaseFun appBaseFun = new AppBaseFun();
		// SettingPara settingPara = new SettingPara();
		// int maxUploadAttCount;// 每次上报最大考勤数
		int uploadCount = 0;
		if (MainIdleActivity.isNetworkAvailables(context, appBaseFun, settingPara)) {
			// maxUploadAttCount = settingPara.getMaxUploadAttCount();
			for (int i = 0; i < 1; i++) {
				AttMeasure attMeasure = sqlAP.findUploadAttMeasure();
				if (attMeasure != null) {
					switch (settingPara.getAttPhotoPlatformProto()) {
						case 0: {
							uploadCount = uploadAttMeasureWW(settingPara, context, attMeasure.getCaid(),
									attMeasure.getTime(), attMeasure.getTem(), attMeasure.getTall(), attMeasure.getHeig());
						}
						break;
						case 2: {// 中维平台体温数据上传
							uploadCount = uploadAttMeasureZW(context, attMeasure.getCaid(), attMeasure.getTime(),
									attMeasure.getTem());
						}
						break;
						default:
							Log.i("AP", "上报测量:未知平台协议 ");
							break;
					}
				} else {
					// Log.i("AP", "上报测量:无上报测量记录");
					break;
				}
			}
		} else {
			WriteUnit
					.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "上报测量:未连接上网络");
			Log.i("AP", "上报测量:未连接上网络");
		}

		return uploadCount;
	}

}
