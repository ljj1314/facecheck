package com.att.server;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.att.AppBaseFun;
import com.att.DBOpenHelper;
import com.att.DBOpenHelper.AttTem;
import com.att.HttpApp;
import com.att.MainIdleActivity;
import com.att.SettingPara;
import com.att.act.WriteUnit;
import com.telpo.BleSample.ACSUtility;
import com.telpo.BleSample.ACSUtility.blePort;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TpBleService extends Service {

	private ACSUtility util;
	private boolean isPortOpen = false;
	private ACSUtility.blePort mSelectedPort;
	private String add = null;
	private boolean isemum = false;
	private blePort mNewtPort = null;
	private Handler mhandle = null;
	private DBOpenHelper dbo = null;
	private Timer timer = null;
	private List<AttTem> la = null;
	private boolean isup = false;
	private ExecutorService uploadExecutorService = Executors
			.newFixedThreadPool(1);
	private String nowday = null;
	private List<AttTem> lanow = null;
	private List<AttTem> labeforce = null;
	private List<AttTem> loadlist = null;
	private HttpApp Httpp = new HttpApp();
	private AppBaseFun apb=new AppBaseFun();
	BluetoothDevice bluetoothDevice;
	private Handler han=new Handler();
	private String connecttime=null;
	private int scannum=0;
	private SettingPara set=new SettingPara();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {

		Log.i("twj", "go is on");

		dbo = new DBOpenHelper(getApplicationContext());

		//	getdivce();




		HandlerThread ble = new HandlerThread("ble");
		ble.start();

		mhandle = new Handler(ble.getLooper()) {

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:

						byte[] card = msg.getData().getByteArray("card");
						String tem = msg.getData().getString("tem", "0");
						String time = msg.getData().getString("time", "");

						float temf=Float.parseFloat(tem);

						if (temf>35.5&&temf<40) {
							dbo.saveloadTEMPERATURE(hex2dec(bytesToHexString(card)),
									time, "" + tem);
						}else {
							Log.i("twj", "当前体温不符合格式，删除:"+temf);
						}




						break;

					case 1:

						break;

					default:
						break;
				}

			}

		};

		Date date = new Date(System.currentTimeMillis());

		nowday = new SimpleDateFormat("yyyy-MM-dd").format(date);

		timer = new Timer();

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				gettemdata();
				onTWupload();

				if (isemum) {
					Log.i("ble", "ble链接状态:"+isemum+"....链接时间:"+connecttime);
				}

			}
		}, 1000 * 60 * 1, 1000 * 30 * 1);



		util = new ACSUtility(getApplicationContext(), userCallback);

		han.postDelayed(runnable, 1000*60);


	}


	private void getdivce(){


		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

		Set<BluetoothDevice> devices = adapter.getBondedDevices();

		for (BluetoothDevice bluetoothDevice : devices) {

			if (bluetoothDevice.getName().equals("TP")) {
				this.bluetoothDevice=bluetoothDevice;
			}


		}




	}



	private void gettemdata() {

		la = dbo.findByStatuInfo("0");
		Log.i("twj", "statu0:" + la + "...lenght:" + la.size());

		lanow = dbo.findByTWInfo("0", nowday);

		Log.i("twj", "now:" + lanow + "...lenght:" + lanow.size());

		labeforce = dbo.findByBefoceTWInfo("0", nowday);

		Log.i("twj", "beforce:" + labeforce + "...lenght:" + labeforce.size());
	}

	private char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);

		return cb.array();
	}

	public static String hex2dec(String hex) {
		String dec = new String();
		long num = Long.parseLong(hex, 16);
		dec = Long.toString(num, 10);
		return dec;
	}

	Runnable runnable=new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			util.enumAllPorts(10);

		}
	};


	/** */
	/**
	 * 把字节数组转换成16进制字符串
	 *
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	private final static String TAG = "ACSMainActivity";
	private ACSUtility.IACSUtilityCallback userCallback = new ACSUtility.IACSUtilityCallback() {

		// @Override
		public void didFoundPort(blePort newPort) {
			// TODO Auto-generated method stub

			util.stopEnum();
			mNewtPort = newPort;
			// runOnUiThread(new Runnable() {
			//
			// @Override
			// public void run() {
			// TODO Auto-generated method stub
			/*
			 * blePo= mNewtPort; ports.add(port._device.getName());
			 * devices.add(port); adapter.notifyDataSetChanged();
			 */

			if (mNewtPort._device.getName().equals("TPJY")&&!isemum) {

				connecttime= new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());

				mhandle.removeCallbacks(runnable);
				isemum = true;
				add = mNewtPort._device.getAddress();
				Log.i("ble", add);
				//util.stopEnum();

				util.openPort(mNewtPort);
				Intent intent = new Intent();
				intent.setAction("tp.ble");
				intent.putExtra("ble", true);
				sendBroadcast(intent);
			}else {
				han.postDelayed(runnable, 1000*20);
			}

			// }

			// });

		}

		// @Override
		public void didFinishedEnumPorts() {
			// TODO Auto-generated method stub
			Log.i("twj", "scan is finish");

			if (!isemum) {

				han.postDelayed(runnable, 1000*20);
			}

		}

		// @Override
		public void didOpenPort(blePort port, Boolean bSuccess) {
			// TODO Auto-generated method stub
			Log.d(TAG, "The port is open ? " + bSuccess);
			if (bSuccess) {
				isPortOpen = true;
				// runOnUiThread(new Runnable() {
				// @Override
				// public void run() {
				// TODO Auto-generated method stub
				// Toast.makeText(MainActivity.this,
				// "Connected from Peripheral", Toast.LENGTH_SHORT).show();
				// getProgressDialog().cancel();
				// }
				// });
			} else {
				// getProgressDialog().cancel();
				// Toast.makeText(MainActivity.this, "Fail Connected",
				// Toast.LENGTH_SHORT).show();
			}
		}

		// @Override
		public void didClosePort(blePort port) {
			// TODO Auto-generated method stub
			isPortOpen = false;
			// if (getProgressDialog().isShowing()) {
			// getProgressDialog().dismiss();
			// }
			isemum = false;
			Log.i("twj", "just close");
			Intent intent = new Intent();
			intent.setAction("tp.ble");
			intent.putExtra("ble", false);
			sendBroadcast(intent);
			han.postDelayed(runnable, 1000*30);
//			try {
//				Thread.sleep(1000 * 30);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			util.enumAllPorts(10);
			// Toast.makeText(MainActivity.this, "Disconnected from Peripheral",
			// Toast.LENGTH_SHORT).show();
			// runOnUiThread(new Runnable() {
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// // updateUiObject();
			// }
			// });
		}

		// @Override
		public void didPackageReceived(blePort port, byte[] packageToSend) {
			// TODO Auto-generated method stub
		}

		// @Override
		public void utilReadyForUse() {
			// TODO Auto-generated method stub



//			if (bluetoothDevice!=null) {
//				util.openport(bluetoothDevice);
//			}else {
			//	util.enumAllPorts(10);
			//	}




		}

		// @Override
		public void didPackageSended(boolean succeed) {
			// TODO Auto-generated method stub
		}

		public void didPackageAnalysis(byte[] cardid, float temperature,
									   float height, float weight, String time, byte[] status) {

			Message msgMessage = mhandle.obtainMessage();
			msgMessage.what = 0;
			Bundle bundle = new Bundle();
			bundle.putByteArray("card", cardid);
			bundle.putString("tem", "" + temperature);
			bundle.putString("time", time);
			msgMessage.setData(bundle);
			mhandle.sendMessage(msgMessage);

			Log.i(".....twj", temperature + ".." + height + "...." + time
					+ "...." + status);
			Intent intent = new Intent();
			intent.setAction("tp.ble");
			intent.putExtra("ble", true);
			sendBroadcast(intent);
			util.responeDevice();

		}
	};

	BluetoothAdapter mBluetoothAdapter;

	private int REQUEST_ENABLE_BT = 11;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		try {
			util.closePort();
			util.stopEnum();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}


	/*
	 *
	 * 体温上报进程
	 */
	private void onTWupload() {

		if (isup) {

			Log.i("twj", "上报未完成，等待");

			return;
		}

		uploadExecutorService.submit(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub

				isup = true;
				int upload = 0;
				loadlist = new ArrayList<DBOpenHelper.AttTem>();

				if (lanow.size() > 0 && lanow.size() <= 20) {

					loadlist = lanow;
					loadlist(lanow);

				} else if (lanow.size() > 20) {

					for (int i = 0; i < 20; i++) {

						AttTem at = lanow.get(i);

						loadlist.add(at);

					}

					loadlist(loadlist);

				} else if (lanow.size() == 0) {

					if (labeforce.size() > 0) {

						if (labeforce.size() <= 20) {
							loadlist = labeforce;
							loadlist(labeforce);
						} else if (labeforce.size() > 20) {

							for (int i = 0; i < 20; i++) {

								AttTem at = labeforce.get(i);

								loadlist.add(at);

							}

							loadlist(loadlist);
						}

					}

				}

				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				isup = false;

			}
		});

	}

	private int loadlist(List<AttTem> lt) {

//		int uploadflat = 0;
		SettingPara sp = new SettingPara();
		if (!MainIdleActivity.isNetworkAvailables(TpBleService.this, apb, sp)) {
			Log.i("twj", "未链接上网络");
			return 0;
		}

		onuploadtwxz(lt, sp);


		return 0;
	}


	//判断属于哪个平台的体温上传
	private void onuploadtwxz(List<AttTem> lt,SettingPara sp){

		String platformID = set.getPlatformId();
		String url="";
		if (platformID.equals("1")) {// 和宝贝
			//	platform = "hbb";
			url="http://hebb1.jiankangtongxue.cn/openAPI/BatchmorningNoonInspectionInfo.ashx";
			loadtmptp(url, lt, sp);
		} else if (platformID.equals("4")) {// 大地教育
			//	platform = "ddjy";
			url="http://ddjy1.jiankangtongxue.cn/openAPI/BatchmorningNoonInspectionInfo.ashx";
			loadtmptp(url, lt, sp);
		} else if (platformID.equals("5")) {// 健康童学
			//	platform = "jktx";
			url="http://jktx1.jiankangtongxue.cn/openAPI/BatchmorningNoonInspectionInfo.ashx";
			loadtmptp(url, lt, sp);
		} else if (platformID.equals("7")) {//福建幼学通
			//	platform = "fjyxt";
			url="http://fjyxt1.jiankangtongxue.cn/openAPI/BatchmorningNoonInspectionInfo.ashx";
			loadtmptp(url, lt, sp);
		}else  {
			url=set.getTempurl();

		}




	}

	private void loadtmptp(String url,List<AttTem> lt,SettingPara sp){

		int uploadflat = 0;
		//	SettingPara sp = new SettingPara();

		List<JSONObject> ls = new ArrayList<JSONObject>();

		// HashMap<String, Object> hs=null;
		for (int i = 0; i < lt.size(); i++) {

			JSONObject jsonObject = new JSONObject();

			try {
				jsonObject.put("CardID", lt.get(i).getTcard());
				jsonObject.put("Temperature", lt.get(i).getTem());
				jsonObject.put("CardTime", lt.get(i).getTtime());
				jsonObject.put("ProvinceCode", sp.getProvincCode());
				jsonObject.put("TransactionID", java.util.UUID.randomUUID()
						.toString().replaceAll("-", ""));
				jsonObject.put("SchoolID", sp.getSchoolID());
				jsonObject.put("DeviceID", sp.getDevicID());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ls.add(jsonObject);

		}

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("batch", "" + ls));

		Log.i("twj", "params:" + params.toString());

		String postRxText = null;

		for (int upload = 0; upload < 2; upload++) {

//			postRxText = Httpp
//					.postSendAndReceive(
//							"http://hebb1.jiankangtongxue.cn/openAPI/BatchmorningNoonInspectionInfo.ashx",
//							params);
			postRxText = Httpp
					.postSendAndReceive(
							url,
							params);
			Log.i("twj", "result:" + postRxText);
			if (postRxText != null) {

				try {

					JSONObject obj = new JSONObject(postRxText);

					String strTemp = obj.getString("ResultCode");

					if (strTemp.equals("200")) {
						WriteUnit.loadlist(new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss")
								.format(new java.util.Date())
								+ "上报考勤成功 ResultCode:" + strTemp);
						uploadflat = 1;
						Log.i("twj", "上传考勤成功 ResultCode:" + strTemp);
						break;
					} else {
						WriteUnit.loadlist(new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss")
								.format(new java.util.Date())
								+ "上报考勤数据返回:"
								+ postRxText
								+ "\n"
								+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(new java.util.Date())
								+ "上传考勤重发1");
						Log.i("twj", "上传考勤重发1");
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		}

		if (uploadflat == 1) {

			for (int i = 0; i < lt.size(); i++) {

				AttTem am = lt.get(i);

				am.setTstatu("1");

				dbo.updataTemInfo(am);

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}



	}


	private void loadtmplz(String url,List<AttTem> lt,SettingPara sp){















	}




}
