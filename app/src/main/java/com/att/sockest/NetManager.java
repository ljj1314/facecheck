package com.att.sockest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *   鍒ゆ柇缃戠粶杩炴帴鐘舵??
 * @author wjh
 *
 */
public class NetManager
{
	static NetManager s_m = null;

	private Context context;

	private NetManager()
	{

	}

	public void init(Context ctx)
	{
		context = ctx;
	}

	public static synchronized NetManager instance()
	{
		if (s_m == null)
		{
			s_m = new NetManager();
		}
		return s_m;
	}

	/**
	 * 鍒ゆ柇鏄惁鏈夌綉缁滆繛鎺?
	 * @return
	 */
	public boolean isNetworkConnected()
	{
		if (context == null)
		{
			return false;
		}
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null)
		{
			return false;
		} else
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
			{
				for (int i = 0; i < info.length; i++)
				{
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * 鍒ゆ柇WIFI缃戠粶鏄惁鍙敤
	 * @return
	 */
	public boolean isWifiConnected()
	{
		if (context != null)
		{
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null)
			{
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	/**
	 * 鍒ゆ柇MOBILE缃戠粶鏄惁鍙敤
	 * @return
	 */
	public boolean isMobileConnected()
	{
		if (context != null)
		{
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null)
			{
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public int getConnectedType()
	{
		if (context != null)
		{
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable())
			{
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

}
