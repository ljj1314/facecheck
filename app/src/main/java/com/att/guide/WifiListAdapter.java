package com.att.guide;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.att.R;

import java.util.List;

public class WifiListAdapter extends BaseAdapter {

	Context context;
	LayoutInflater inflater;
	List<ScanResult> list;
	WiFiUtils wifiUtils;

	public WifiListAdapter(Context context, List<ScanResult> list, WiFiUtils wifiUtils) {

		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.wifiUtils = wifiUtils;
	}

	public void setDate(List<ScanResult> list) {
		this.list = list;
	}

	public int getCount() {

		return list.size();
	}

	public ScanResult getItem(int position) {

		return list.get(position);
	}

	public long getItemId(int position) {

		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.row_wifi, null);
		}

		view.setOnHoverListener(new OnHoverListener() {
			public boolean onHover(View arg0, MotionEvent arg1) {
				return false;
			}
		});

		ScanResult scanResult = list.get(position);
		TextView wifi_ssid = (TextView) view.findViewById(R.id.wifi_ssid);
		wifi_ssid.setText(scanResult.SSID);

		ImageView lock_icon = (ImageView) view.findViewById(R.id.lock_icon);
		if (scanResult.capabilities.contains("WPA")) {
			lock_icon.setVisibility(View.VISIBLE);
		} else {
			lock_icon.setVisibility(View.GONE);
		}


		ConnectivityManager mConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiInfo = mConnMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		ImageView connected_icon = (ImageView) view.findViewById(R.id.connected_icon);
		int wifiItemId = wifiUtils.IsConfiguration("\"" + scanResult.SSID + "\"");
		if (wifiItemId != -1) {
			String connectedSSID = wifiUtils.getConnectedSSID();
			String itemSSID = "\"" + scanResult.SSID + "\"";
			if (wifiInfo != null && wifiInfo.isConnectedOrConnecting()
					&&connectedSSID.equals(itemSSID)) {
				if(wifiInfo.isConnected()){//已连接
					connected_icon.setVisibility(View.VISIBLE);
					hideProgressAnim(view);
				}else{//正在连接
					connected_icon.setVisibility(View.INVISIBLE);
					showProgressAnim(view);
				}
			} else {//已配置
				connected_icon.setVisibility(View.INVISIBLE);
				hideProgressAnim(view);
			}
		}else {
			connected_icon.setVisibility(View.INVISIBLE);
			hideProgressAnim(view);
		}

		ImageView wifi_icon = (ImageView) view.findViewById(R.id.wifi_icon);
		// 判断信号强度，显示对应的指示图标
		if (Math.abs(scanResult.level) >= 100) {
			wifi_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.wifi_icon_3));
		} else if (Math.abs(scanResult.level) >= 80) {
			wifi_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.wifi_icon_3));
		} else if (Math.abs(scanResult.level) >= 70) {
			wifi_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.wifi_icon_3));
		} else if (Math.abs(scanResult.level) >= 60) {
			wifi_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.wifi_icon_2));
		} else if (Math.abs(scanResult.level) >= 40) {
			wifi_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.wifi_icon_1));
		} else {
			wifi_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.wifi_icon_0));
		}
		return view;
	}

	public static void showProgressAnim(View view) {
		ImageView iv_loading_anim = (ImageView) view.findViewById(R.id.progress_anim);
		if(iv_loading_anim!=null){
			iv_loading_anim.setVisibility(View.VISIBLE);
			iv_loading_anim.setBackgroundResource(R.anim.progress_anim);
			AnimationDrawable anim = (AnimationDrawable) iv_loading_anim.getBackground();
			anim.setOneShot(false);
			if (anim.isRunning()){// 是否正在运行？
				anim.stop();// 停止
			}
			anim.start();// 启动
		}
	}

	public static void hideProgressAnim(View view) {
		ImageView iv_loading_anim = (ImageView) view.findViewById(R.id.progress_anim);
		if(iv_loading_anim!=null){
			iv_loading_anim.setVisibility(View.GONE);
		}
	}
}
