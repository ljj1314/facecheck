/**************************************************************************
 Copyright (C) 广东天波教育科技有限公司　版权所有
 文 件 名：
 创 建 人：
 创建时间：2015.10.30
 功能描述：
 **************************************************************************/
package com.att;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CurrentVersion {
	private static final String TAG = "Config";
	public static final String appPackName = "com.att";

	public static int getVerCode(Context context) {
		int verCode = -1;

		try {
			verCode = context.getPackageManager().getPackageInfo(appPackName, 0).versionCode;
		} catch (Exception e) {
			Log.e(TAG, "getVerCode出错："+e.toString());
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(appPackName, 0).versionName;
		} catch (Exception e) {
			Log.e(TAG, "getVerName出错："+e.toString());
		}
		return verName;
	}

	public static String getAppName(Context context) {
		String appName = context.getResources().getText(R.string.app_name).toString();

		return appName;
	}

	public static String getUpdataVerJSON(String serverPath) throws Exception {
		StringBuilder newVerJSON = new StringBuilder();
		HttpClient client = new DefaultHttpClient();// 新建http客户端
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);// 设置连接超时范围
		HttpConnectionParams.setSoTimeout(httpParams, 5000);

		// serverPath是ver.json的路径
		HttpResponse response = client.execute(new HttpGet(serverPath));
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 8192);
				String line = null;

				while ((line = reader.readLine()) != null) {
					newVerJSON.append(line + "\n");// 按行读取放入StringBuilder中
				}
				reader.close();
				return newVerJSON.toString();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
