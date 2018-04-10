package com.att.server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
import android.util.Log;

public class HttpConnect extends Activity {

	private static HashMap<String, Object> video = null;
	private static List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	// public static String urlhead="http://121.9.230.130:20482";

	public static String urlhead = "http://service.lovicoco.com";

	public static boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();

		return (info != null && info.isConnected());
	}

	public static boolean isNetworkAvailables(Context ctx) {
		// 得到网络连接信息
		ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 去进行判断网络是否连接
		if (manager.getActiveNetworkInfo() != null) {
			return manager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	public static List<Map<String, Object>> getWlwzServerData(int start, int end) {

		String response = "";
		JSONArray jsonArray = null;

		// List params = new ArrayList();

		// params.add(new BasicNameValuePair("m", "html"));

		response = httpPost(urlhead, null);
		// System.out.println("=============>>>>>>>>>1111" + response);
		try {

			jsonArray = new JSONArray(response);

			for (int i = 0; i < jsonArray.length(); i++) {
				// JSONObject jsonObject = (JSONObject) jsonArray.get(i);

				video = new HashMap<String, Object>();

				mData.add(video);
			}

		} catch (JSONException e) {
			e.printStackTrace();

		}
		return mData;

	}

	public static String httpPost(String url, String params) {
		String response = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		// new UrlEncodedFormEntity()
		// httpPost.setEntity(entity);
		httpPost.addHeader("Content-Type", "multipart/form-data; boundary=----------ThIs_Is_tHe_bouNdaRY_$");

		try {
			if (params != null) {
				// Log.i("params:",params);
				httpPost.setEntity(new StringEntity(params, HTTP.UTF_8));
				// httpPost.setEntity(new UrlEncodedFormEntity(params,
				// HTTP.UTF_8));
				// Log.i("param",""+ httpPost.toString());
			}
			HttpResponse httpResponse = httpClient.execute(httpPost);
			// System.out.println("......................."+httpResponse);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				response = EntityUtils.toString(httpResponse.getEntity());
			} else {
				response = "" + statusCode;
			}
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			return "false";
		} catch (ClientProtocolException e) {

			// e.printStackTrace();
			return "false";
		} catch (IOException e) {

			// e.printStackTrace();
			return "false";
		}
		return response;

	}

	public static String httpPost(String url, String params, String token) {
		String response = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		// new UrlEncodedFormEntity()
		// httpPost.setEntity(entity);
		httpPost.addHeader("device_authorization", token);
		httpPost.addHeader("Content-Type", "multipart/form-data; boundary=----------ThIs_Is_tHe_bouNdaRY_$");

		try {
			if (params != null) {
				// Log.i("params:",params);
				httpPost.setEntity(new StringEntity(params, HTTP.UTF_8));
				// httpPost.setEntity(new UrlEncodedFormEntity(params,
				// HTTP.UTF_8));
				// Log.i("param",""+ httpPost.toString());
			}
			HttpResponse httpResponse = httpClient.execute(httpPost);
			// System.out.println("......................."+httpResponse);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				response = EntityUtils.toString(httpResponse.getEntity());
			} else {
				response = "" + statusCode;
			}
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			return "false";
		} catch (ClientProtocolException e) {

			// e.printStackTrace();
			return "false";
		} catch (IOException e) {

			// e.printStackTrace();
			return "false";
		}
		return response;

	}

	public static String getHexString(String str) {
		// char to hex.
		// char character = 'a';
		// String str = String.valueOf(character);

		// String to hex.
		// String str = "aA";
		StringBuilder sb = new StringBuilder();
		char[] charArray = str.toCharArray();
		if (charArray != null) {
			for (char c : charArray) {
				byte byteHigh = (byte) (c >> 8 & 0xff);
				byte byteLow = (byte) (c & 0xff);
				sb.append(convertByteToHex(byteHigh));
				sb.append(convertByteToHex(byteLow));
			}
		}
		return sb.toString();
	}

	private static String convertByteToHex(byte b) {
		int high = b / 16;
		int low = b % 16;
		return getHex(high) + getHex(low);
	}

	private static String getHex(int nibble) {
		switch (nibble) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
				return String.valueOf(nibble);
			case 10:
				return "A";
			case 11:
				return "B";
			case 12:
				return "C";
			case 13:
				return "D";
			case 14:
				return "E";
			case 15:
				return "F";
		}
		return "";
	}

	public int formatting(String a) {
		int i = 0;
		for (int u = 0; u < 10; u++) {
			if (a.equals(String.valueOf(u))) {
				i = u;
			}
		}
		if (a.equals("a")) {
			i = 10;
		}
		if (a.equals("b")) {
			i = 11;
		}
		if (a.equals("c")) {
			i = 12;
		}
		if (a.equals("d")) {
			i = 13;
		}
		if (a.equals("e")) {
			i = 14;
		}
		if (a.equals("f")) {
			i = 15;
		}
		return i;
	}

	public static String getContactNameFromPhoneBook(Context context, String phoneNum) {

		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.NUMBER };
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNum));
		Cursor pCur = context.getContentResolver().query(uri, projection, null, null, null);

		String contactName = "";
		// ContentResolver cr = context.getContentResolver();
		// Cursor pCur = cr.query(
		// ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
		// ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
		// new String[] { phoneNum }, null);
		if (pCur.moveToFirst()) {
			contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			pCur.close();
		}
		return contactName;
	}

	@SuppressWarnings("deprecation")
	public static String getContactName(Context context, String number) {
		if (TextUtils.isEmpty(number)) {
			return null;
		}

		final ContentResolver resolver = context.getContentResolver();

		Uri lookupUri = null;
		String[] projection = new String[] { PhoneLookup._ID, PhoneLookup.DISPLAY_NAME };
		Cursor cursor = null;
		try {
			lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
			cursor = resolver.query(lookupUri, projection, null, null, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				lookupUri = Uri.withAppendedPath(android.provider.Contacts.Phones.CONTENT_FILTER_URL,
						Uri.encode(number));
				cursor = resolver.query(lookupUri, projection, null, null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String ret = null;
		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
			ret = cursor.getString(1);
		}

		cursor.close();
		return ret;
	}

	public static final byte[] HexStringTobytes(String Hexstr) {

		byte[] values = Hexstr.getBytes();
		int length = values.length / 2;
		byte[] ret = new byte[length];
		int index = 0;
		byte value = 0;

		while (index < length) {
			value = 0;

			if (values[index * 2] >= 0x30 && values[index * 2] <= 0x39) {
				value |= values[index * 2] - 0x30;

			} else if (values[index * 2] >= 'a' && values[index * 2] <= 'f') {

				value |= values[index * 2] - 'a' + 0x0A;
			} else if (values[index * 2] >= 'A' && values[index * 2] <= 'F') {

				value |= values[index * 2] - 'A' + 0x0A;
			} else {
				return null;
			}

			value <<= 4;
			if (values[index * 2 + 1] >= 0x30 && values[index * 2 + 1] <= 0x39) {
				value |= values[index * 2 + 1] - 0x30;

			} else if (values[index * 2 + 1] >= 'a' && values[index * 2 + 1] <= 'f') {

				value |= values[index * 2 + 1] - 'a' + 0x0A;
			} else if (values[index * 2 + 1] >= 'A' && values[index * 2 + 1] <= 'F') {

				value |= values[index * 2 + 1] - 'A' + 0x0A;
			} else {
				return null;
			}
			ret[index] = value;
			index++;
		}

		return ret;

	}

	/**
	 * 瀛楃涓茶浆鎹㈡垚鍗佸叚杩涘埗瀛楃涓?
	 *
	 * @param String
	 *            str 寰呰浆鎹㈢殑ASCII瀛楃涓?
	 * @return String 姣忎釜Byte涔嬮棿绌烘牸鍒嗛殧锛屽: [61 6C 6B]
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] str2HexStr(String str) throws UnsupportedEncodingException {
		// byte[] bs1 = str.getBytes();
		// String ai=new String(bs1, "ANSI");
		// char[] chars = "0123456789ABCDEF".toCharArray();
		// StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes("GBK");
		// int bit;
		//
		// for (int i = 0; i < bs.length; i++)
		// {
		// bit = (bs[i] & 0x0f0) >> 4;
		// sb.append(chars[bit]);
		// bit = bs[i] & 0x0f;
		// sb.append(chars[bit]);
		// sb.append(' ');
		// }
		return bs;
	}

	public static boolean isNumeric(String str) {

		Pattern pattern = Pattern.compile("[0-9]*");

		Matcher isNum = pattern.matcher(str);

		if (!isNum.matches()) {

			return false;

		}

		return true;

	}

	public static boolean isEnlic(String str) {

		Pattern pattern = Pattern.compile("[a-zA-Z]");

		Matcher isNum = pattern.matcher(str);

		if (!isNum.matches()) {

			return false;

		}

		return true;

	}

	public static boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					// Log.i("鍚庡彴", appProcess.processName);
					return true;
				} else {
					// Log.i("鍓嶅彴", appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}

	public static void writeinfile(String text, String name) throws IOException {

		String aaa = Environment.getExternalStorageDirectory() + File.separator + name;
		File file = new File(aaa);
		FileWriter pw = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			pw = new FileWriter(file, true);
			pw.write(text + "\n");
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	/** * 娓呴櫎/data/data/com.xxx.xxx/files涓嬬殑鍐呭 * * @param context */
	public static void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	/**
	 * * 娓呴櫎澶栭儴cache涓嬬殑鍐呭(/mnt/sdcard/android/data/com.xxx.xxx/cache) *
	 * * @param context
	 */
	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	/**
	 * * 鍒犻櫎鏂规硶 杩欓噷鍙細鍒犻櫎鏌愪釜鏂囦欢澶逛笅鐨勬枃浠讹紝濡傛灉浼犲叆鐨刣irectory鏄釜鏂囦欢锛屽皢涓嶅仛澶勭悊 *
	 * * @param directory
	 */
	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

	public static boolean isRunningForeground(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
			return true;
		}

		return false;
	}

	public static boolean isRuningAct(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		String MY_PKG_NAME = "com.msqsoft.kidwatchapp";
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(MY_PKG_NAME)
					|| info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
				isAppRunning = true;
				Log.i("djj", info.topActivity.getPackageName() + " info.baseActivity.getPackageName()="
						+ info.baseActivity.getPackageName());
				break;
			}
		}

		return isAppRunning;
	}

	public static String getCurrentActivityName(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);

		// get the info from the currently running task
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

		ComponentName componentInfo = taskInfo.get(0).topActivity;
		return componentInfo.getClassName();
	}

	public String getRunningActivityName() {
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
		return runningActivity;
	}

	public static void onput(String urls, String path) {

		try {
			URL url = new URL(urls);
			HttpURLConnection con;
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(1000 * 60);
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + "");

			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			FileInputStream fStream = new FileInputStream(path);

			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			fStream.close();
			ds.flush();
			ds.close();

		} catch (Exception e) {
		}

	}

	/**
	 * 获取网落图片资源
	 *
	 * @param url
	 * @return
	 */
	public static Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
			// 获得连接
			HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
			// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(6000);
			// 连接设置获得数据流
			conn.setDoInput(true);
			// 不使用缓存
			conn.setUseCaches(false);
			// 这句可有可无，没有影响
			// conn.connect();
			// 得到数据流
			InputStream is = conn.getInputStream();
			// 解析得到图片
			bitmap = BitmapFactory.decodeStream(is);
			// 关闭数据流
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;

	}

	/**
	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
	 *
	 * @return 应用程序是/否获取Root权限
	 */
	public static boolean upgradeRootPermission(String pkgCodePath) {
		Process process = null;
		DataOutputStream os = null;
		try {
			String cmd = "chmod 777 " + pkgCodePath;
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}

}
