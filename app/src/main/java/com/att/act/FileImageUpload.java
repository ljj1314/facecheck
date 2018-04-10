package com.att.act;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

//文件上传类
public class FileImageUpload {
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10 * 1000; // 超时时间
	@SuppressWarnings("unused")
	private static final String CHARSET = "utf-8"; // 设置编码
	public static final String SUCCESS = "1";
	public static final String FAILURE = "0";

	/**
	 * * android上传文件到服务器
	 *
	 * @param file
	 *            需要上传的文件
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 */
	public static String uploadFile(File file, String RequestURL, HashMap<String, Object> param, String token,
									String filename) {

		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		StringBuffer sb = new StringBuffer();
		sb.append(PREFIX);
		sb.append(BOUNDARY);
		sb.append(LINE_END);

		Gson gson = new Gson();
		sb.append("Content-Disposition: form-data; name=\"data\"" + LINE_END + LINE_END);
		String ll = gson.toJson(param);

		Log.i("tappo", "json数据是：" + ll);

		sb.append(ll);
		sb.append(LINE_END + LINE_END);

		StringBuffer sb1 = new StringBuffer();
		if (file != null) {

			// sb.append(LINE_END);
			sb1.append(PREFIX);
			sb1.append(BOUNDARY);
			sb1.append(LINE_END);

			Log.i("tappo", "文件名是:" + filename);
			sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"" + LINE_END);
			sb1.append("Content-Type: image/jpeg" + LINE_END + LINE_END);

		}

		byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志

		long contentLenght = sb.toString().getBytes().length + end_data.length

				+ sb1.toString().getBytes().length + file.length() + LINE_END.getBytes().length
				+ LINE_END.getBytes().length;

		try {

			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			// conn.setRequestProperty("Charset", CHARSET);
			// 设置编码

			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Device-Authorization", token);
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

			conn.setRequestProperty("Content-Length", Long.toString(contentLenght));

			// conn.setRequestProperty("Device-Authorization", token+
			// ";boundary=" + BOUNDARY);
			// conn.setRequestProperty("connection", "keep-alive");

			/** * 当文件不为空，把文件包装并且上传 */
			// OutputStream outputSteams=conn.getOutputStream();
			DataOutputStream outputSteam = new DataOutputStream(conn.getOutputStream());

			outputSteam.write(sb.toString().getBytes());

			if (file != null && file.exists()) {
				try {
					outputSteam.write(sb1.toString().getBytes());

					InputStream is = new FileInputStream(file);
					byte[] bytes = new byte[1024];
					int len = 0;
					while ((len = is.read(bytes)) != -1) {
						outputSteam.write(bytes, 0, len);
					}
					is.close();
				} catch (Exception e) {

				}
			}
			outputSteam.write((LINE_END + LINE_END).getBytes());
			// byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
			outputSteam.write(end_data);

			outputSteam.flush();
			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int res = conn.getResponseCode();

			Log.e(TAG, "response code:" + res + "||message:" + conn.getResponseMessage());
			if (res == 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
				String line = "";
				String result = "";
				while (null != (line = br.readLine())) {
					result += line;
				}

				Log.i("tappo", "获取到数据：" + result);
				br.close();
				return result;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return FAILURE;
	}

	/**
	 * * android上传文件到服务器
	 *
	 * @param file
	 *            需要上传的文件
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 */
	public static String uploadtem(File file, String RequestURL, HashMap<String, Object> param, String token,
								   String filename) {

		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		StringBuffer sb = new StringBuffer();
		sb.append(PREFIX);
		sb.append(BOUNDARY);
		sb.append(LINE_END);

		Gson gson = new Gson();
		sb.append("Content-Disposition: form-data; name=\"data\"" + LINE_END + LINE_END);
		String ll = gson.toJson(param);

		Log.i("tappo", "json数据是：" + ll);

		sb.append(ll);
		sb.append(LINE_END + LINE_END);

		byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志

		long contentLenght = sb.toString().getBytes().length + end_data.length

				+ LINE_END.getBytes().length + LINE_END.getBytes().length;

		try {

			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			// conn.setRequestProperty("Charset", CHARSET);
			// 设置编码

			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Device-Authorization", token);
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

			conn.setRequestProperty("Content-Length", Long.toString(contentLenght));

			// conn.setRequestProperty("Device-Authorization", token+
			// ";boundary=" + BOUNDARY);
			// conn.setRequestProperty("connection", "keep-alive");

			/** * 当文件不为空，把文件包装并且上传 */
			// OutputStream outputSteams=conn.getOutputStream();
			DataOutputStream outputSteam = new DataOutputStream(conn.getOutputStream());

			outputSteam.write(sb.toString().getBytes());

			outputSteam.write((LINE_END + LINE_END).getBytes());
			// byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
			outputSteam.write(end_data);

			outputSteam.flush();
			/**
			 * 获取响应码 200=成功 当响应成功，获取响应的流
			 */
			int res = conn.getResponseCode();

			Log.e(TAG, "response code:" + res + "||message:" + conn.getResponseMessage());
			if (res == 200) {
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
				String line = "";
				String result = "";
				while (null != (line = br.readLine())) {
					result += line;
				}

				Log.i("tappo", "获取到数据：" + result);
				br.close();
				return result;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return FAILURE;
	}

}
