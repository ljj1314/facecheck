package com.att.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.att.SettingPara;
import com.att.act.WriteUnit;
import com.baidu.android.common.logging.Log;
import com.serport.DataUtils;
import com.serport.SerialPortUtil;
import com.telpo.dataprocess.DataProcess;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Nlpostast extends Service {

	private SerialPortUtil serport = null;
	// private ApSocket aps = null;
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
	private SharedPreferences sp;
	// private AppBaseFun appbean=new AppBaseFun();

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	public void onCreate() {

		try {
			serport = SerialPortUtil.getInstance();
		} catch (Exception e) {

		}

		sp = getSharedPreferences("json", Context.MODE_PRIVATE);

		serport.setOnDataReceiveListener(new SerialPortUtil.OnDataReceiveListener() {

			public void onDataReceive(byte[] buffer, int size) {

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

		mhandle = new Handler() {

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:

						byte[] tt = msg.getData().getByteArray("data");
						int size = msg.getData().getInt("size");
						Log.e("tappo", "receive:" + "...size" + size + DataUtils.bytesToHexString(tt));
						if (isfull == false) {

							if (tt[0] == 0x02) {

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
									Log.e("tappo", DataUtils.bytesToHexString(data));
									measure(data);
								}

							}

						} else {
							try {
								System.arraycopy(tt, 0, realtem, pos, size);
								pos += size;
								Log.e("tappo", "size" + pos + "..len" + reallen);
								if (pos > reallen) {
									isfull = false;
									reallen = 0;
									pos = 0;

									measure(realtem);

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
						Log.e("tappo", "数据是:" + DataUtils.bytesToHexString(data));
						measure(data);
						break;

					default:
						break;
				}

			}

		};

		handlem.postDelayed(runnable, 1000 * 60);

		Timer send = new Timer();
		send.schedule(new TimerTask() {

			@Override
			public void run() {
				//Log.i("AP", "查询蓝牙AP记录");
				serport.sendCmd();
			}
		}, 1000 * 60 * 2, 1000 * 15);

		// handlem.postDelayed(rn, 5000);
		// khandle.postDelayed(rn1, 8000);
		//
		//
		// sendhandle.postDelayed(rr, 1000 * 60 * 2);

	}

	Runnable rn = new Runnable() {
		public void run() {

			sendsm();

		}
	};

	Runnable rn1 = new Runnable() {
		public void run() {

			sendin();

		}
	};

	Runnable rr = new Runnable() {

		public void run() {

			sendin();
			sendhandle.postDelayed(rr, 1000 * 60);
		}
	};

	private void measure(byte[] buffer) {

		// sendhandle.removeCallbacks(rr);
		// sendhandle.postDelayed(rr, 1000 * 60);

		try {

			if (buffer[0] != 0x02) {

				return;
			}

			switch (buffer[4]) {

				case 0x11:

					inputsum++;
					Log.e("postsum", "收到的数据条目未:" + inputsum);

					break;

				case 0x04:

					break;

				case 0x08:

					if (buffer[5] == 0x01) {

						break;

					} else {

						sendsm();

					}

					break;

				case 0x4F:

					handlem.postDelayed(runnable, 0);
					break;

				case 0x12:

					sendin();

					break;

				case 0x10:

					// if (buffer[5] == 0x4F && buffer[6] == 0x4B) {
					// sendhandle.removeCallbacks(rr);
					// }

					break;

				case 0x06:

					handlem.removeCallbacks(runnable);
					handlem.postDelayed(runnable, 1000 * 60 * 2);

					if (buffer[5] == 0x00) {
						break;
					}
					inputsum++;
					Log.e("postsum", "收到的数据条目未:" + inputsum);
					SettingPara settingPara = new SettingPara();
					int reversal = 0;
					if (settingPara.isCard_reversal()) {
						reversal = 1;
					} else {
						reversal = 0;
					}

					DataProcess lib = new DataProcess();
					String data = lib.loadap(buffer, buffer.length, reversal);

					String[] arg = data.split("#");

					int num = Integer.valueOf(arg[0]).intValue();
					if (num > 0) {
						Log.e("tappo", "收到的数据条目未:" + num + "解析后数据是:" + data);
						for (int i = 0; i < num; i++) {

							String[] coin = arg[i + 1].split("-");
							String caid = coin[0];
							String tem = coin[1];
							String heig = coin[2];
							String tall = coin[3];
							String time = coin[4];

							Log.e("tappo", "体温:" + tem + "卡号是:" + caid);
							// appbean.writeinfile("体温:"+tem+"卡号是:"+caid+"时间是："+time);
							WriteUnit.loadlist("体温:" + tem + "卡号是:" + caid + "时间是：" + time);
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

							String too = sp.getString("token", null);
							int rss = Nlpost.postwarm(getApplicationContext(), too, caid, tem, heig, tall, time);
							Log.e("tappo", "返回是:" + rss);

						}

					}

					// String message="";

					break;

				default:

					break;
			}

		} catch (Exception e) {

			Log.i("tappo", e.toString());
		}

	}

	@Override
	public void onDestroy() {

		super.onDestroy();

		serport.closeSerialPort();

	}

	private void sendsm() {

		try {

			String xd = "04050000000000000008-a4F06cKAil8=-6E0C2BC650D2810B";
			int len = xd.getBytes().length;

			byte[] xm = new byte[len + 6];

			xm[0] = 0x02;
			xm[1] = 0x00;
			xm[2] = (byte) (len + 2);
			xm[3] = 0x01;
			xm[4] = 0x08;
			// xm[4]=(byte) len;
			System.arraycopy(xd.getBytes(), 0, xm, 5, len);

			int sn = 0;

			for (int i = 0; i < (xm.length - 1); i++) {
				sn ^= xm[i];
			}
			xm[len + 5] = (byte) sn;
			Log.e("tappo", "下达指令：" + DataUtils.bytesToHexString(xm));
			boolean issuc = serport.sendBuffer(xm);
			Log.e("tappo", "下达指令：" + issuc);
		} catch (Exception e) {
			Log.i("tappo", "下达指令出错：" + e.toString());
		}
	}

	// 进入透传模式

	private void sendin() {

		try {

			byte[] mBuffer = new byte[7];

			mBuffer[0] = 0x02;
			mBuffer[1] = 0x00;
			mBuffer[2] = 0x03;
			mBuffer[3] = 0x01;
			mBuffer[4] = 0x10;
			mBuffer[5] = (byte) 0xff;
			// mBuffer[6] = 0x55;

			int num = 0;

			for (int i = 0; i < 7; i++) {
				num ^= mBuffer[i];
			}
			mBuffer[6] = (byte) num;

			// xm[6]=(byte) sum;

			Log.e("tappo", "开启透传：" + DataUtils.bytesToHexString(mBuffer));
			boolean issuc = serport.sendBuffer(mBuffer);
			Log.e("tappo", "开启透传：" + issuc);

		} catch (Exception e) {

			Log.i("tappo", "开启透传出错：" + e.toString());
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

	Runnable runnable = new Runnable() {

		public void run() {

			try {

				Calendar calendar = Calendar.getInstance();
				int timey = calendar.get(Calendar.YEAR) - 2000;
				Log.i("tappo", "year" + timey);
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
				// Log.i("tappo",
				// "befor:"+timey+"-"+mon+"-"+day+"-"+hh+"-"+mm+"-"+ss+"||after:"+fy+"-"+fm+"-"+fd+"-"+fh+"-"+fmm+"-"+fs);

				// SettingPara abf=new SettingPara();
				int len = 7 + 6;
				byte[] heartbear = new byte[len];

				// byte[] deviceid= abf.getDevicID().getBytes();

				heartbear[0] = 0x02;
				heartbear[1] = 0x00;
				heartbear[2] = 0x09;
				heartbear[3] = 0x01;
				heartbear[4] = 0x04;
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

				Log.i("tappo", "时间心跳包" + DataUtils.bytesToHexString(heartbear));

				serport.sendBuffer(heartbear);

			} catch (Exception e) {

				Log.i("tappo", e.toString());
			}

		}
	};

}
