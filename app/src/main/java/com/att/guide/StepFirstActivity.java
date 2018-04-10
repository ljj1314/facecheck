package com.att.guide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.att.R;
import com.att.SettingParaActivity;
import com.att.guide.WifiPswDialog.OnCustomDialogListener;

import java.util.List;

public class StepFirstActivity extends Activity {

	private ImageView back_button;
	boolean isSet = false;
	private String wifiPassword = null;
	private WifiManager wifiManager;
	private WiFiUtils wifiUtils;
	private List<ScanResult> wifiResultList;
	private WifiListAdapter arrayWifiAdapter;
	private WifiInfo currentWifiInfo;// 当前所连接的wifi

	private Handler didplayTimer = new Handler();

	private TextView state_1;
	private TextView state_2;
	private TextView state_3;
	private ImageView round_icon_1;
	private ImageView round_icon_2;
	private ImageView arrow_icon;
	private LinearLayout ll_ethernet;
	private LinearLayout ll_mobile;
	private LinearLayout ll_wifi;
	private ListView lv_wifi;
	private Button next_step;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.step_first_seven);
		init();
	}

	private void init() {
		isSet = getIntent().getBooleanExtra("isSet", false);
		back_button = (ImageView) findViewById(R.id.back_button);
		if (isSet) {
			back_button.setVisibility(View.VISIBLE);
			back_button.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					onBackPressed();
				}
			});
		} else {
			back_button.setVisibility(View.INVISIBLE);
		}

		getNetworkInfo();

		showWiFiList();

		didplayTimer.postDelayed(wifiDidplayTimer, 5000);
		didplayTimer.postDelayed(networkInfoDidplayTimer, 2000);
	}

	@Override
	public void onBackPressed() {
		if (isSet) {
			Intent intent = new Intent();
			intent.setClass(StepFirstActivity.this, SettingParaActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.back_left_in, R.anim.back_right_out);
		}
		super.onBackPressed();
	}

	private void showWiFiList() {
		wifiUtils = new WiFiUtils(StepFirstActivity.this);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		wifiResultList = wifiManager.getScanResults();
		lv_wifi = (ListView) findViewById(R.id.lv_wifi);
		if (wifiResultList != null) {
			arrayWifiAdapter = new WifiListAdapter(this, wifiResultList, wifiUtils);
			lv_wifi.setAdapter(arrayWifiAdapter);
			ListOnItemClickListener wifiListListener = new ListOnItemClickListener();
			lv_wifi.setOnItemClickListener(wifiListListener);
		}
	}

	class ListOnItemClickListener implements OnItemClickListener {
		String wifiItemSSID = null;
		@SuppressWarnings("unused")
		private View selectedItemView;

		public void onItemClick(AdapterView<?> arg0, final View arg1, final int arg2, long arg3) {

			// wifiIndex = which;
			// handler.sendEmptyMessage(3);
			Log.i("ListOnItemClickListener", "start");
			selectedItemView = arg1;
			// arg1.setBackgroundResource(R.color.gray);//点击的Item项背景设置
			ScanResult wifiItem = arrayWifiAdapter.getItem(arg2);// 获得选中的设备
			// String []ItemValue = wifiItem.split("--");
			// wifiItemSSID = ItemValue[0];
			wifiItemSSID = wifiItem.SSID;
			Log.i("ListOnItemClickListener", wifiItemSSID);
			int wifiItemId = wifiUtils.IsConfiguration("\"" + wifiItemSSID + "\"");
			Log.i("ListOnItemClickListener", String.valueOf(wifiItemId));
			if (wifiItemId != -1) {
				if (wifiUtils.ConnectWifi(wifiItemId)) {// 连接指定WIFI
					// arg1.setBackgroundResource(R.color.green);
					WifiListAdapter.showProgressAnim(arg1);
				}
			} else {// 没有配置好信息，配置
				if (wifiItem.capabilities.contains("WPA")) {
					WifiPswDialog pswDialog = new WifiPswDialog(StepFirstActivity.this, new OnCustomDialogListener() {
						public void back(String str) {
							wifiPassword = str;
							if (wifiPassword != null) {
								connetionConfiguration(arg2, wifiPassword);
								WifiListAdapter.showProgressAnim(arg1);
								// int netId =
								// wifiUtils.AddWifiConfig(wifiResultList,
								// wifiItemSSID, wifiPassword);
								// Log.i("WifiPswDialog",
								// String.valueOf(netId));
								// if (netId != -1) {
								// wifiUtils.getConfiguration();//
								// 添加了配置信息，要重新得到配置信息
								// if (wifiUtils.ConnectWifi(netId)) {
								// //
								// selectedItem.setBackgroundResource(R.color.green);
								// }
								// } else {
								// Toast.makeText(StepFirstActivity.this,
								// "网络连接错误", Toast.LENGTH_SHORT).show();
								// //
								// selectedItem.setBackgroundResource(R.color.burlywood);
								// }
							} else {
								// selectedItem.setBackgroundResource(R.color.burlywood);
							}
						}
					}, "密码", "连接");
					pswDialog.show();
				} else {
					// connetionConfiguration(arg2, "");
					wifiUtils.ConnectWifi(wifiItemId);// 连接指定WIFI
					WifiListAdapter.showProgressAnim(arg1);
				}
			}
		}
	}

	Runnable wifiDidplayTimer = new Runnable() {
		public void run() {
			long delayMillis = 2000;
			try {
				wifiUtils.WifiOpen();
				wifiUtils.WifiStartScan();
				wifiResultList = wifiUtils.getScanResults();
				wifiUtils.getConfiguration();
				arrayWifiAdapter.setDate(wifiResultList);
				arrayWifiAdapter.notifyDataSetChanged();
				didplayTimer.postDelayed(wifiDidplayTimer, delayMillis);
			} catch (Exception e) {
				didplayTimer.postDelayed(wifiDidplayTimer, delayMillis);
			}
		}
	};

	Runnable networkInfoDidplayTimer = new Runnable() {
		public void run() {
			long delayMillis = 2000;
			try {
				getNetworkInfo();
				didplayTimer.postDelayed(networkInfoDidplayTimer, delayMillis);
			} catch (Exception e) {
				didplayTimer.postDelayed(networkInfoDidplayTimer, delayMillis);
			}
		}
	};

	boolean isEthernetConnected = false;
	boolean isMobelConnected = false;
	boolean isWiFiConnected = false;

	public void getNetworkInfo() {
		state_1 = (TextView) findViewById(R.id.state_1);
		state_2 = (TextView) findViewById(R.id.state_2);
		state_3 = (TextView) findViewById(R.id.state_3);
		round_icon_1 = (ImageView) findViewById(R.id.round_icon_1);
		round_icon_2 = (ImageView) findViewById(R.id.round_icon_2);
		arrow_icon = (ImageView) findViewById(R.id.arrow_icon);
		ll_ethernet = (LinearLayout) findViewById(R.id.ll_ethernet);
		ll_mobile = (LinearLayout) findViewById(R.id.ll_mobile);
		if(ll_wifi==null){
			ll_wifi = (LinearLayout) findViewById(R.id.ll_wifi);
			ll_ethernet.setFocusable(true);
			ll_mobile.setFocusable(true);
			ll_wifi.setFocusable(true);
			ll_wifi.requestFocus();
			//ll_wifi.setFocusableInTouchMode(true);
			//ll_wifi.requestFocusFromTouch();
		}
		next_step = (Button) findViewById(R.id.next_step);

		ConnectivityManager mConnMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// NetworkInfo aActiveInfo = mConnMgr.getActiveNetworkInfo(); //
		// 获取活动网络连接信息
		NetworkInfo ethernetInfo = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		isEthernetConnected = false;
		if (ethernetInfo != null && ethernetInfo.isAvailable()) {
			if (ethernetInfo.isConnected()) {
				isEthernetConnected = true;
				state_1.setText("可用");
				state_1.setTextColor(getResources().getColor(R.color.blue_45b1ea));
				round_icon_1.setImageResource(R.drawable.round_icon_blue);
			} else {
				state_1.setText("不可用");
				state_1.setTextColor(getResources().getColor(R.color.gray_c4c4c4));
				round_icon_1.setImageResource(R.drawable.round_icon_gray);
			}
		} else {
			state_1.setText("不可用");
			state_1.setTextColor(getResources().getColor(R.color.gray_c4c4c4));
			round_icon_1.setImageResource(R.drawable.round_icon_gray);
		}
		ll_ethernet.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (!isEthernetConnected) {
					showMsgDialog(StepFirstActivity.this, "以太网络不可用，请检查并重新插入网线。", false);
				}
			}
		});

		NetworkInfo mobileInfo = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		isMobelConnected = false;
		if (mobileInfo != null && mobileInfo.isAvailable()) {
			if (mobileInfo != null && mobileInfo.isConnected()) {
				isMobelConnected = true;
				state_2.setText("可用");
				state_2.setTextColor(getResources().getColor(R.color.blue_45b1ea));
				round_icon_2.setImageResource(R.drawable.round_icon_blue);
			} else {
				state_2.setText("不可用");
				state_2.setTextColor(getResources().getColor(R.color.gray_c4c4c4));
				round_icon_2.setImageResource(R.drawable.round_icon_gray);
			}
		} else {
			state_2.setText("不可用");
			state_2.setTextColor(getResources().getColor(R.color.gray_c4c4c4));
			round_icon_2.setImageResource(R.drawable.round_icon_gray);
		}
		ll_mobile.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (!isMobelConnected) {
					showMsgDialog(StepFirstActivity.this, "移动网络不可用，请检查并重新插入SIM卡。", false);
				}
			}
		});

		NetworkInfo wifiInfo = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		isWiFiConnected = false;
		if (wifiInfo != null && wifiInfo.isAvailable()) {
			if (wifiInfo.isConnected()) {
				isWiFiConnected = true;
			}
			state_3.setText("可用");
			state_3.setTextColor(getResources().getColor(R.color.blue_45b1ea));
		} else {
			state_3.setText("不可用");
			state_3.setTextColor(getResources().getColor(R.color.gray_c4c4c4));
		}
		ll_wifi.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (lv_wifi.getVisibility() == View.VISIBLE) {
					lv_wifi.setVisibility(View.GONE);
					arrow_icon.setImageResource(R.drawable.arrow_down);
				} else {
					lv_wifi.setVisibility(View.VISIBLE);
					arrow_icon.setImageResource(R.drawable.arrow_up);
				}
			}
		});
		if (isEthernetConnected || isMobelConnected || isWiFiConnected) {
			next_step.setEnabled(true);
			next_step.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setClass(StepFirstActivity.this, StepSecondActivity.class);
					intent.putExtra("isSet", isSet);
					startActivity(intent);
					overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
					StepFirstActivity.this.finish();
				}
			});
		} else {
			next_step.setEnabled(false);
		}
	}

	public void connetionConfiguration(int index, String password) {
		new ConnectWifiThread().execute(index + "", password);
	}

	class ConnectWifiThread extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			int index = Integer.parseInt(params[0]);
			if (index > wifiResultList.size()) {
				return null;
			}
			// 连接配置好指定ID的网络
			WifiConfiguration config = WiFiUtils.createWifiInfo(wifiResultList.get(index).SSID, params[1], 3,
					wifiManager);

			int networkId = wifiManager.addNetwork(config);
			if (null != config) {
				wifiManager.enableNetwork(networkId, true);
				return wifiResultList.get(index).SSID;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (null != result) {
				handler.sendEmptyMessage(0);
			} else {
				handler.sendEmptyMessage(1);
			}
			super.onPostExecute(result);
		}

	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					// wifi_result_textview.setText("正在获取ip地址...");
					new RefreshSsidThread().start();
					break;
				case 1:
					Toast.makeText(StepFirstActivity.this, "连接失败！", Toast.LENGTH_SHORT).show();
					break;
				case 3:
					// View layout =
					// LayoutInflater.from(StepFirstActivity.this).inflate(R.layout.custom_dialog_layout,
					// null);
					// Builder builder = new Builder(StepFirstActivity.this);
					// builder.setTitle("请输入密码").setView(layout);
					// final EditText passowrdText = (EditText)
					// layout.findViewById(R.id.password_edittext);
					// builder.setPositiveButton("连接", new
					// DialogInterface.OnClickListener() {
					//
					// @Override
					// public void onClick(DialogInterface dialog, int which) {
					// connetionConfiguration(wifiIndex,
					// passowrdText.getText().toString());
					// }
					// }).show();
					break;
				case 4:
					// Toast.makeText(StepFirstActivity.this, "连接成功！",
					// Toast.LENGTH_SHORT).show();
					// wifi_result_textview.setText("当前网络：" +
					// currentWifiInfo.getSSID() + " ip:"
					// + WifiUtil.intToIp(currentWifiInfo.getIpAddress()));
					break;
			}
			super.handleMessage(msg);
		}
	};

	class RefreshSsidThread extends Thread {

		@Override
		public void run() {
			boolean flag = true;
			while (flag) {
				currentWifiInfo = wifiManager.getConnectionInfo();
				if (null != currentWifiInfo.getSSID() && 0 != currentWifiInfo.getIpAddress()) {
					flag = false;
				}
			}
			handler.sendEmptyMessage(4);
			super.run();
		}
	}

	public static void showMsgDialog(Context context, String msg, boolean canCancel) {
		final MsgDialog msgDialog = new MsgDialog(context);
		msgDialog.setTitle("提示");
		msgDialog.setMessage(msg);
		msgDialog.setPositiveButton("确定", new OnClickListener() {
			public void onClick(View v) {
				msgDialog.dismiss();
			}
		});
		if (canCancel) {
			msgDialog.setNegativeButton("取消", new OnClickListener() {
				public void onClick(View v) {
					msgDialog.dismiss();
				}
			});
		}
	}
}
