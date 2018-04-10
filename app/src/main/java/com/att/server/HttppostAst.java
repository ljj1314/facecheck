package com.att.server;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.att.AppBaseFun;
import com.att.MainIdleActivity;
import com.att.SettingPara;
import com.att.act.ApSocket;
import com.att.act.WriteUnit;
import com.att.sockest.TCPClient;
import com.att.sockest.TCPClient.OnDataReceiveListeners;
import com.baidu.android.common.logging.Log;
import com.serport.DataUtils;
import com.serport.SerialPortUtil;

import java.util.Timer;
import java.util.TimerTask;

public class HttppostAst extends Service {// bugly

	private SerialPortUtil serport = null;
	@SuppressWarnings("unused")
	private ApSocket aps = null;
	private TCPClient tcpClient = null;
	private Handler mhandle = null;
	private boolean isfull = false;
	private byte[] realtem = null;
	private int reallen = 0;
	private int pos = 0;
	private Handler handlem = new Handler();
	private Handler khandle = new Handler();
	private int inputsum = 0;
	private Handler sendhandle = new Handler();
	@SuppressWarnings("unused")
	private AppBaseFun appb = new AppBaseFun();
	@SuppressWarnings("unused")
	private Handler twhandle = new Handler();

	private String ap_version = "";
	private boolean status_ap = false;
	SettingPara settingPara;

	// 测试平台
	// private String remoteIp = "121.9.230.130";
	// private int port = 20482;

	// 正式平台
	private String remoteIp = "115.29.200.219";
	private int port = 80;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	public void onCreate() {

		settingPara = new SettingPara(getApplicationContext());

		try {
			serport = SerialPortUtil.getInstance();
		} catch (Exception e) {

		}

		tcpClient = TCPClient.instance();
		if (tcpClient.isConnect()) {
			tcpClient.close();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

		tcpClient.connect(remoteIp, port);

		// aps=new ApSocket(getApplicationContext());
		// try {
		// aps.start();
		// // aps.sendObject("");
		// aps.setOnDataReceiveListeners(new ApSocket.OnDataReceiveListeners() {
		// public void onDataReceives(String buffer) {
		// if (buffer==null) {
		// return;
		// }
		// try {
		//// byte[] buf=buffer.getBytes();
		//// byte[] sen=new byte[buf.length+6];
		//// sen[0]=0x02;
		//// sen[1]=0x00;
		//// sen[2]=(byte) (buf.length+2);
		//// sen[3]=0x01;
		//// sen[4]=0x06;
		//// System.arraycopy(buf, 0, sen, 5, buf.length);
		//// int numsum=0;
		//// for (int i = 0; i < sen.length; i++) {
		//// numsum+=sen[i]^0;
		//// }
		//// sen[buf.length+5]=(byte) numsum;
		// Log.i("AP", "发送数组为"+buffer);
		// serport.sendBuffer(buffer.getBytes());
		// } catch (Exception e) {
		// }
		// }
		// });
		// } catch (UnknownHostException e) {
		// } catch (IOException e) {
		// }

		tcpClient.setOnDataReceiveListeners(new OnDataReceiveListeners() {

			public void onDataReceives(byte[] buffer, int len) {

				try {

					// int len = buffer.toString().length();
					Log.i("AP", "收到平台返回数据长度:" + len);
					Log.i("AP", "收到平台返回数据:" + bytesToHexString(buffer));
					// appb.writeinfile("平台返回数据是:"+buffer);
					WriteUnit.loadlist("平台返回数据是:" + bytesToHexString(buffer));
					byte[] mBuffer = new byte[6 + len];
					int kk = 2 + len;
					byte[] il = DataUtils.intTobyte2(kk);
					mBuffer[0] = 0x02;
					mBuffer[1] = il[0];
					mBuffer[2] = il[1];
					mBuffer[3] = 0x01;
					mBuffer[4] = 0x11;// 令牌使用中透传消息(0x11)
					// mBuffer[5] = (byte) 0xff;
					// mBuffer[6] = 0x55;

					// System.arraycopy(buffer.getBytes("utf-8"), 0, mBuffer,5 ,
					// buffer.length());
					System.arraycopy(buffer, 0, mBuffer, 5, len);
					int num = 0;

					for (int i = 0; i < (6 + len); i++) {
						num ^= mBuffer[i];
					}
					mBuffer[5 + len] = (byte) num;

					boolean isend = serport.sendBuffer(mBuffer);
					// serport.sendBuffer(buffer.getBytes());
					Log.i("AP", "发送透传数据给AP是否成功:" + isend + " 长度：" + mBuffer.length);
				} catch (Exception e) {

					Log.i("AP", "发送透传数据出错:" + e.toString());
				}
			}

			public void onDataReceives(String buffer) {

			}

			public void onDataReceives(byte[] byteArray) {

			}
		});

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
						Log.i("AP", "收到AP数据长度:" + size + " 内容:" + DataUtils.bytesToHexString(tt));
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
									Log.i("AP", "继续收数据：" + DataUtils.bytesToHexString(data));
									measure(data);
								}

							}

						} else {
							try {
								System.arraycopy(tt, 0, realtem, pos, size);
								pos += size;
								Log.i("AP", "size:" + pos + " len:" + reallen);
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
						Log.i("AP", "接收数据是:" + DataUtils.bytesToHexString(data));
						// Log.i("AP", "收到AP数据:" +
						// DataUtils.bytesToHexString(data));
						// appb.writeinfile("接收数据是:"+
						// DataUtils.bytesToHexString(data));
						WriteUnit.loadlist("接收数据是:" + DataUtils.bytesToHexString(data));
						measure(data);
						break;

					default:
						break;
				}

			}

		};

		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e1) {
		// }
		// sendsm();
		// try {
		// Thread.sleep(4000);
		// } catch (InterruptedException e1) {
		// }
		// sendin();

		handlem.postDelayed(rn, 5000);
		khandle.postDelayed(rn1, 8000);
		// twhandle.postDelayed(tw, 10000);

		sendhandle.postDelayed(rr, 1000 * 60 * 2);

		handlem.postDelayed(getStatusRunnable, 1000 * 10);

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

			Log.i("AP", "查询状态出错："+e.toString());
		}

	}

	Runnable rn = new Runnable() {
		public void run() {

			sendsm();

		}
	};

	Runnable rn1 = new Runnable() {
		public void run() {

			sendin();// 开启透传

		}
	};

	Runnable rr = new Runnable() {

		public void run() {

			sendin();// 开启透传
			MainIdleActivity.updateStatusAP(getApplicationContext(), settingPara);

			sendhandle.postDelayed(rr, 1000 * 60);
		}
	};

	Runnable tw = new Runnable() {

		public void run() {

			try {

				byte[] mBuffer = new byte[7];

				mBuffer[0] = 0x02;
				mBuffer[1] = 0x00;
				mBuffer[2] = 0x03;
				mBuffer[3] = 0x01;
				mBuffer[4] = 0x09;// 卡号反转(0x09)

				SettingPara spPara = new SettingPara();
				if (spPara.isCard_reversal()) {
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
				// Log.i("AP", "下达卡号是否反转:" + issuc);

			} catch (Exception e) {

				Log.i("AP", "下达卡号是否反转出错:" + e.toString());
			}

		}
	};

	private void measure(byte[] buffer) {

		sendhandle.removeCallbacks(rr);
		sendhandle.postDelayed(rr, 1000 * 60);

		try {

			if (buffer[0] != 0x02) {

				return;
			}

			switch (buffer[4]) {

				case 0x11:// 令牌使用中透传消息(0x11)
					status_ap = true;

					inputsum++;
					Log.i("AP", "收到的数据总数:" + inputsum);
					tcpClient.close();
					byte[] len = new byte[2];
					len[0] = buffer[1];
					len[1] = buffer[2];

					int le = DataUtils.bytesToint2(len[0], len[1]);

					@SuppressWarnings("unused")
					byte[] mp = new byte[le];

					Log.i("AP", "收到的AP命令长度：" + le + " 第5个字节:" + buffer[5]);
					if (le > 3) {
						byte[] con = new byte[le - 2];

						System.arraycopy(buffer, 5, con, 0, le - 2);

						Log.i("AP", "需往平台发送的数据长度：" + convertHexToString(DataUtils.bytesToHexString(con)).length());
						Log.i("AP", "需往平台发送的数据：" + convertHexToString(DataUtils.bytesToHexString(con)));
						WriteUnit.loadlist("往平台发送数据:" + convertHexToString(DataUtils.bytesToHexString(con)));
						// appb.writeinfile("send:往平台发送数据"+convertHexToString(DataUtils.bytesToHexString(con)));

						if (tcpClient.isConnect()) {

							// while (!tcpClient.getconnect()) {
							// Log.i("AP", "finishConnect:" +
							// tcpClient.getconnect());
							// }

							boolean sendok = tcpClient.sendMsg(con);
							// appb.writeinfile("往平台发送"+sendok);
							WriteUnit.loadlist("往平台发送" + sendok);

							if (!sendok) {
								// tp.close();
								tcpClient.reConnect();
								// tp=TCPClient.instance();
								// tp.connect("115.29.200.219", 80);
								tcpClient.sendMsg(con);
							}

							// tp.sendMsg(convertHexToString(DataUtils.bytesToHexString(con)));
						} else {

							tcpClient.connect(remoteIp, port);

							Thread.sleep(1000);
							// tp.reConnect();
							boolean sendok = tcpClient.sendMsg(con);

							if (!sendok) {
								tcpClient.reConnect();
								// tp.close();
								// tp=TCPClient.instance();
								// tp.connect("115.29.200.219", 80);
								tcpClient.sendMsg(con);
							}
							// tp.sendMsg(convertHexToString(DataUtils.bytesToHexString(con)));
						}
						// Log.i("AP", "复制后数组为"+DataUtils.bytesToHexString(con));
					}
					// if (aps.getrun()) {
					// aps.sendObject(convertHexToString(DataUtils.bytesToHexString(con)));
					// }else {
					// aps.start();
					// aps.sendObject(convertHexToString(DataUtils.bytesToHexString(con)));
					// }

					break;

				case 0x04:// 心跳
					status_ap = true;
					break;

				case 0x08:// 下达终端ID，密码，MD5验证(0x08)

					if (buffer[5] == 0x01) {// 0x01：表示下达成功
						status_ap = true;
						break;

					} else {// 0x00：表示下达失败
						status_ap = false;
						sendsm();

					}

					break;

				case 0x12:// 令牌使用完返还消息(0x12)

					sendin();// 开启透传

					break;

				case 0x10:// 令牌到达消息

					if (buffer[5] == 0x4F && buffer[6] == 0x4B) {
						sendhandle.removeCallbacks(rr);
					}

					break;

				case 0x0C:// 查询状态(0x0C)
					// Log.i("AP", "收到查询版本号的返回");
					status_ap = true;
					byte[] len2 = new byte[2];
					len2[0] = buffer[1];
					len2[1] = buffer[2];
					int le2 = DataUtils.bytesToint2(len2[0], len2[1]);
					if (le2 > 2) {
						byte[] con = new byte[le2 - 2];
						System.arraycopy(buffer, 5, con, 0, le2 - 2);
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

				default:

					break;
			}
			MainIdleActivity.sendPlatformApBroadcast(getApplicationContext(), settingPara, false, status_ap);

		} catch (Exception e) {

			Log.i("AP", e.toString());
		}

	}

	@Override
	public void onDestroy() {

		super.onDestroy();

		serport.closeSerialPort();

	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	private void sendsm() {

		try {

			String id = "04050000000000000008";// 终端ID: 04050000000000000007
			String pw = "a4F06cKAil8=";// 密码：a4F06cKAil8=
			String md5 = "6E0C2BC650D2810B";// MD5码：6E0C2BC650D2810B
			String xd = id + "-" + pw + "-" + md5;// 下达内容
			// String xd = "04050000000000000008-a4F06cKAil8=-6E0C2BC650D2810B";
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
			Log.i("AP", "下达终端连接指令:" + DataUtils.bytesToHexString(xm));
			@SuppressWarnings("unused")
			boolean issuc = serport.sendBuffer(xm);
			// Log.i("AP", "下达指令:" + issuc);
		} catch (Exception e) {

			Log.i("AP", "下达终端连接指令出错:" + e.toString());
		}
	}

	// 进入透传模式

	private void sendin() {// 开启透传

		try {

			byte[] mBuffer = new byte[7];

			mBuffer[0] = 0x02;
			mBuffer[1] = 0x00;
			mBuffer[2] = 0x03;
			mBuffer[3] = 0x01;
			mBuffer[4] = 0x10;// 令牌到达消息
			mBuffer[5] = (byte) 0xff;
			// mBuffer[6] = 0x55;

			int num = 0;

			for (int i = 0; i < 7; i++) {
				num ^= mBuffer[i];
			}
			mBuffer[6] = (byte) num;

			// xm[6]=(byte) sum;

			Log.i("AP", "尝试获取AP的透传消息:" + DataUtils.bytesToHexString(mBuffer));
			@SuppressWarnings("unused")
			boolean issuc = serport.sendBuffer(mBuffer);
			// Log.i("AP", "开启透传是否成功：" + issuc);

		} catch (Exception e) {

			Log.i("AP", "尝试获取AP的透传消息出错:" + e.toString());
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

}
