/**************************************************************************
 Copyright (C) 广东天波教育科技有限公司　版权所有
 文 件 名：
 创 建 人：
 创建时间：2015.10.30
 功能描述：
 **************************************************************************/
package com.att;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.att.act.WriteUnit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class AppBaseFun {
	public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
	public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
	public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
	public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值

	List<String> allExterSdcardPaths = null;

	/*
	 * 判断是否安装了SD卡
	 */
	public boolean isHaveSDCard() {
		// if
		// (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		if (getSDPath() != null) {
			// 有SD卡
			return true;
		} else {
			// 无SD卡
			return false;
		}
	}

	/*
	 * 判断wifi/lan连接状态
	 *
	 * @param ctx
	 *
	 * @return
	 */
	public boolean isWifiAvailable(Context ctx) {
		ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (State.CONNECTED == wifi) {
			return true;
		} else {
			State lan = conMan.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).getState();
			if (State.CONNECTED == lan) {
				return true;
			} else {
				return false;
			}
		}
	}

	/*
	 * 判断wifi/lan连接状态
	 *
	 * @param ctx
	 *
	 * @return
	 */
	public boolean isMobileAvailable(Context ctx) {
		ConnectivityManager conMan = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

		//	State status = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		NetworkInfo mMobileNetworkInfo = conMan
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mMobileNetworkInfo != null)
		{
			return mMobileNetworkInfo.isAvailable();
		}
		return false;
	}

	/**
	 * 加载本地图片
	 *
	 * @param url
	 * @return
	 */
	public String getLoacalBitmap(String url) {
		try {
			File f = new File(url);
			if (f.exists()) {
				FileInputStream fis = new FileInputStream(url);

				byte[] buffer = new byte[(int) f.length()];
				fis.read(buffer);
				fis.close();
				return Base64.encodeToString(buffer, Base64.DEFAULT);
			} else {
				return null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取手机自身内存路径
	 *
	 */
	public String getPhoneCardPath() {
		return Environment.getExternalStorageDirectory().getPath();

	}

	public String getFirstExterPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	public List<String> getAllExterSdcardPath() {
		List<String> SdList = new ArrayList<String>();

		String firstPath = getFirstExterPath();

		// 得到路径
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				// 将常见的linux分区过滤掉
				if (line.contains("secure"))
					continue;
				if (line.contains("asec"))
					continue;
				if (line.contains("media"))
					continue;
				if (line.contains("system") || line.contains("cache") || line.contains("sys") || line.contains("data")
						|| line.contains("tmpfs") || line.contains("shell") || line.contains("root")
						|| line.contains("acct") || line.contains("proc") || line.contains("misc")
						|| line.contains("obb")) {
					continue;
				}

				if (line.contains("fat") || line.contains("fuse") || (line.contains("ntfs"))) {

					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						String path = columns[1];
						if (path != null && !SdList.contains(path) && path.contains("sd"))
							SdList.add(columns[1]);
					}
				}
			}
			is.close();
			isr.close();
			br.close();
		} catch (ClassCastException e) {
			Log.e("TPATT", "getAllExterSdcardPath出错:" + e.toString());
		} catch (Exception e) {
			Log.e("TPATT", "getAllExterSdcardPath出错:" + e.toString());
		}

		if (!SdList.contains(firstPath)) {
			SdList.add(firstPath);
		}

		return SdList;
	}

	public List<String> getUsbPaths() {// 获得U盘路径
		List<String> SdList = new ArrayList<String>();
		// 得到路径
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				// 将常见的linux分区过滤掉
				if (line.contains("secure"))
					continue;
				if (line.contains("asec"))
					continue;
				if (line.contains("media"))
					continue;
				if (line.contains("system") || line.contains("cache") || line.contains("sys") || line.contains("data")
						|| line.contains("tmpfs") || line.contains("shell") || line.contains("root")
						|| line.contains("acct") || line.contains("proc") || line.contains("misc")
						|| line.contains("obb")) {
					continue;
				}

				if (line.contains("fat") || line.contains("fuse") || (line.contains("ntfs"))) {

					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						String path = columns[1];
						if (path != null && !SdList.contains(path) && path.contains("usb")) {
							SdList.add(columns[1]);
						}
					}
				}
			}
			is.close();
			isr.close();
			br.close();
		} catch (ClassCastException e) {
			Log.e("TPATT", "getUsbPaths出错:" + e.toString());
		} catch (Exception e) {
			Log.e("TPATT", "getUsbPaths出错:" + e.toString());
		}

		return SdList;
	}

	/**
	 * 获取外置SD路径
	 *
	 * @param url
	 * @return
	 */
	public String getSDPath() {
		if (allExterSdcardPaths == null || allExterSdcardPaths.size() != 2) {
			allExterSdcardPaths = getAllExterSdcardPath();
		}

		if (allExterSdcardPaths.size() == 2) {
			for (String path : allExterSdcardPaths) {
				if (path != null && !path.equals(getFirstExterPath())) {
					return path;
				}
			}
			return null;
		} else {
			return null;
		}
		//	 return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 * 创建文件夹
	 *
	 * @param url
	 * @return
	 */
	public void makeRootDirectory(String filePath) {
		boolean isok = true;
		File file = null;
		try {
			file = new File(filePath);
			// if (file.isFile()) {
			// file.delete();
			// }
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			isok = false;
			Log.i("error:", e + "");
		} finally {
			Log.i("success:", "创建文件夹" + isok + filePath);
		}
	}

	/**
	 * 创建文件
	 *
	 * @param url
	 * @return
	 */
	public File makeFilePath(String filePath, String fileName) {
		File file = null;

		makeRootDirectory(filePath);
		try {
			file = new File(filePath + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 读文件
	 *
	 * @param url
	 * @return
	 */
	public String readFileSdcard(String fileName) {
		String res = "";

		try {
			FileInputStream fin = new FileInputStream(fileName);

			int length = fin.available();

			byte[] buffer = new byte[length];

			fin.read(buffer);

			res = EncodingUtils.getString(buffer, "UTF-8");
			if (res.length() <= 0) {
				res = null;
			}

			fin.close();

		}catch (Exception e) {
			res = null;
			Log.i("TPATT", "readFileSdcard:异常" + e.toString());
		}catch (OutOfMemoryError e) {
			res = null;
			Log.i("TPATT", "readFileSdcard:异常" + e.toString());
		}

		return res;
	}

	/**
	 * 写文件
	 *
	 * @param url
	 * @return
	 */
	public boolean writeFileSdcard(String fileName, String message) {
		try {
			FileOutputStream fout = new FileOutputStream(fileName);

			byte[] bytes = message.getBytes();

			fout.write(bytes);

			fout.close();
			return true;
		} catch (Exception e) {
			Log.i("TPATT", "writeFileSdcard:异常");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 保存已上报考勤记录
	 *
	 * @param savetext
	 * @return
	 */
	public void saveUploadAttFile(String savetext) {

		String filePath;
		String text = "";

		filePath = getSDPath() + "/tpatt/UploadAtt.txt";
		File file = new File(filePath);
		if (file.exists()) {
			text = readFileSdcard(filePath);
			if (text != null) {
				if (text.length() > (27 * 5000)) // 考勤记录大于5000条，把文件重命名
				{
					SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss");
					Date curDate2 = new Date(System.currentTimeMillis());// 获取当前时间
					String timestamp = formatter2.format(curDate2);
					file.renameTo(new File(getSDPath() + "/UploadAttBak/" + timestamp + "UploadAtt.txt"));

					text = null;
					makeFilePath(getSDPath() + "/tpatt/", "UploadAtt.txt");
				}
			}
		} else {
			makeFilePath(getSDPath() + "/tpatt/", "UploadAtt.txt");
		}

		if (text == null) {
			text = savetext;
		} else {
			text += savetext;
		}
		writeFileSdcard(filePath, text);

		// Log.i("TPATT", "保存已上报考勤记录: " + text);
	}

	/**
	 * 保存未上报考勤记录
	 *
	 * @param savetext
	 * @return
	 */
	public void saveNotUploadAttFile(String savetext) {

		String filePath;
		String text = "";

		filePath = getSDPath() + "/tpatt/NotUploadAtt.txt";
		File file = new File(filePath);
		if (file.exists()) {
			text = readFileSdcard(filePath);
			if (text != null) {
				if (text.length() > (27 * 5000)) // 考勤记录大于5000条，把文件重命名
				{
					SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss");
					Date curDate2 = new Date(System.currentTimeMillis());// 获取当前时间
					String timestamp = formatter2.format(curDate2);
					file.renameTo(new File(getSDPath() + "/UploadAttBak/" + timestamp + "NotUploadAtt.txt"));

					text = null;
					makeFilePath(getSDPath() + "/tpatt/", "NotUploadAtt.txt");
				}
			}
		} else {
			makeFilePath(getSDPath() + "/tpatt/", "NotUploadAtt.txt");
		}

		if (text == null) {
			text = savetext;
		} else {
			text += savetext;
		}
		writeFileSdcard(filePath, text);
		// Log.i("TPATT", "保存未上报考勤记录: " + text);
	}

	/**
	 * 保存已上报考勤图片记录
	 *
	 * @param savetext
	 * @return
	 */
	public void saveUploadAttPhotoFile(String savetext) {

		String filePath;
		String text = "";

		filePath = getSDPath() + "/tpatt/UploadAttPhoto.txt";
		File file = new File(filePath);
		if (file.exists()) {
			text = readFileSdcard(filePath);
			if (text != null) {
				if (text.length() > (27 * 5000)) // 考勤记录大于5000条，把文件重命名
				{
					SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss");
					Date curDate2 = new Date(System.currentTimeMillis());// 获取当前时间
					String timestamp = formatter2.format(curDate2);
					file.renameTo(new File(getSDPath() + "/UploadAttBak/" + timestamp + "AttPhoto.txt"));

					text = null;
					makeFilePath(getSDPath() + "/tpatt/", "UploadAttPhoto.txt");
				}
			}
		} else {
			makeFilePath(getSDPath() + "/tpatt/", "UploadAttPhoto.txt");
		}

		if (text == null) {
			text = savetext;
		} else {
			text += savetext;
		}
		writeFileSdcard(filePath, text);

		// Log.i("TPATT", "保存已上报考勤图片记录: " + text);
	}

	/**
	 * 保存未上报考勤图片记录
	 *
	 * @param savetext
	 * @return
	 */
	public void saveNotUploadAttPhotoFile(String savetext) {

		String filePath;
		String text = "";

		filePath = getSDPath() + "/tpatt/NotUploadAttPhoto.txt";
		File file = new File(filePath);
		if (file.exists()) {
			text = readFileSdcard(filePath);
			if (text != null) {
				if (text.length() > (27 * 5000)) // 考勤记录大于5000条，把文件重命名
				{
					SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss");
					Date curDate2 = new Date(System.currentTimeMillis());// 获取当前时间
					String timestamp = formatter2.format(curDate2);
					file.renameTo(new File(getSDPath() + "/UploadAttBak/" + timestamp + "NotUploadAttPhoto.txt"));

					text = null;
					makeFilePath(getSDPath() + "/tpatt/", "NotUploadAttPhoto.txt");
				}
			}
		} else {
			makeFilePath(getSDPath() + "/tpatt/", "NotUploadAttPhoto.txt");
		}

		if (text == null) {
			text = savetext;
		} else {
			text += savetext;
		}
		writeFileSdcard(filePath, text);
		// Log.i("TPATT", "保存未上报考勤图片记录: " + text);
	}

	private void copy(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[2 * 1024];
		int read;

		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	/**
	 * 得到本地或者网络上的bitmap url - 网络或者本地图片的绝对路径,比如:
	 *
	 * A.网络路径: url="http://blog.foreverlove.us/girl2.png" ;
	 *
	 * B.本地路径:url="file://mnt/sdcard/photo/image.png";
	 *
	 * C.支持的图片格式 ,png, jpg,bmp,gif等等
	 *
	 * @param url
	 * @return
	 */
	public Bitmap GetLocalOrNetBitmap(String url) {
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;

		try {
			File file = new File(url);
			if (file.exists()) {
				in = new BufferedInputStream(new URL(url).openStream(), 2 * 1024);
				final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				out = new BufferedOutputStream(dataStream, 2 * 1024);
				copy(in, out);
				out.flush();
				byte[] data = dataStream.toByteArray();
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				data = null;
			} else {
				Log.i("TPATT", "考勤图片不存在!");
			}
			return bitmap;
		} catch (IOException e) {
			Log.i("TPATT", "考勤图片打开:异常");

			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将Bitmap转换成字符串
	 *
	 * @param savetext
	 * @return
	 */
	public String bitmaptoString(Bitmap bitmap) {
		String string = null;

		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		int options = 80;// 个人喜欢从80开始,
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		while (bStream.toByteArray().length / 1024 > 30) {
			bStream.reset();
			options -= 10;
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, bStream);
		}
		byte[] bytes = bStream.toByteArray();

		string = Base64.encodeToString(bytes, Base64.DEFAULT);

		return string;
	}

	/**
	 * 保存已考已报信息
	 *
	 * @param savetext
	 * @return
	 */
	public void saveAttInfoFile(int uploadAttCount, int uploadAttPhotoCount, int attCount, int attPhotoCount) {

		String filePath;
		String text = "";

		filePath = getSDPath() + "/tpatt/AttInfo.txt";
		File file = new File(filePath);
		if (file.exists()) {
			text = readFileSdcard(filePath);
			if (text != null) {
				String[] lines;

				lines = text.split("\r\n", text.length());
				List<String> list = Arrays.asList(lines);

				// 已考勤记录数
				text = String.valueOf(Integer.parseInt(list.get(0)) + attCount) + "\r\n";

				// 已考勤图片数
				text += String.valueOf(Integer.parseInt(list.get(1)) + attPhotoCount) + "\r\n";

				// 已上报考勤记录数
				text += String.valueOf(Integer.parseInt(list.get(2)) + uploadAttCount) + "\r\n";

				// 已上报考勤图片数
				text += String.valueOf(Integer.parseInt(list.get(3)) + uploadAttPhotoCount) + "\r\n";
			} else {
				// 已考勤记录数
				text = String.valueOf(attCount) + "\r\n";

				// 已考勤图片数
				text += String.valueOf(attPhotoCount) + "\r\n";

				// 已上报考勤记录数
				text += String.valueOf(uploadAttCount) + "\r\n";

				// 已上报考勤图片数
				text += String.valueOf(uploadAttPhotoCount) + "\r\n";
			}
		} else {
			makeFilePath(getSDPath() + "/tpatt/", "AttInfo.txt");
		}

		writeFileSdcard(filePath, text);
		// Log.i("TPATT", "保存已考已报信息: " + text);
	}

	/**
	 * 读已考已报信息
	 *
	 * @param savetext
	 * @return
	 */
	public String readAttInfoFile() {

		String filePath;
		String text = "";

		filePath = getSDPath() + "/tpatt/AttInfo.txt";
		File file = new File(filePath);
		if (file.exists()) {
			text = readFileSdcard(filePath);
			if (text != null) {
				String[] lines;

				lines = text.split("\r\n", text.length());
				List<String> list = Arrays.asList(lines);

				// 已考勤数/已上报考勤数
				if (Integer.parseInt(list.get(2)) > Integer.parseInt(list.get(0))) {
					text = list.get(0) + "/" + list.get(0) + "\r\n";
				} else {
					text = list.get(0) + "/" + list.get(2) + "\r\n";
				}

				// 已考勤图片数/已上报考勤图片数
				if (Integer.parseInt(list.get(3)) > Integer.parseInt(list.get(1))) {
					text += list.get(1) + "/" + list.get(1);
				} else {
					text += list.get(1) + "/" + list.get(3);
				}
			} else {
				text = "0/0\r\n0/0";
			}
		} else {
			text = "0/0\r\n0/0";
		}

		return text;
	}

	/**
	 * 保存卡信息文件
	 *
	 * @param
	 * @return
	 */
	public void saveCardInfoFile(String cardid, String text) {

		String filePath;

		filePath = getSDPath() + "/tpatt/CardInfo/" + cardid + ".txt";
		File file = new File(filePath);
		if (file.exists()) {
		} else {
			makeFilePath(getSDPath() + "/tpatt/CardInfo/", cardid + ".txt");
		}

		writeFileSdcard(filePath, text);
	}

	/**
	 * 读卡信息文件
	 *
	 * @param row
	 *            :0表示语音，1表示图片
	 * @return
	 * @throws IOException
	 */
	public String readCardInfoFile(String cardid, int row) {

		String filePath;
		String res = null;

		filePath = getSDPath() + "/tpatt/CardInfo/" + cardid + ".txt";
		File photofile = new File(filePath);
		if (photofile.exists()) {
			try {
				String str = "";
				FileInputStream fis = new FileInputStream(filePath);
				int size = fis.available();
				byte[] buffer = new byte[size];

				fis.read(buffer);
				fis.close();
				str = new String(buffer, "UTF-8");// 支持双字节字符
				if (str.length() > 0) {
					String[] lines;

					lines = str.split("\r\n", str.length());
					List<String> list = Arrays.asList(lines);
					if (list.size() > row) {
						res = list.get(row);
					}
					// list.clear();
					// lines=null;
				}
				// buffer=null;
				// str=null;
			} catch (IOException e) {
				Log.i("TPATT", "读卡信息文件:异常 " + e.toString() + filePath);
				e.printStackTrace();
			}
		} else {
			Log.i("TPATT", "读卡信息文件:文件不存在 " + filePath);
		}

		return res;
	}

	/**
	 * 保存卡信息文件
	 *
	 * @param
	 * @return
	 */
	public void savePhotoInfoFile(String childid, String text) {

		String filePath;

		filePath = getSDPath() + "/tpatt/CardInfo/Photo/" + childid + "/" + childid + ".txt";
		File file = new File(filePath);
		if (file.exists()) {
		} else {
			makeFilePath(getSDPath() + "/tpatt/CardInfo/Photo/", childid + "/" + childid + ".txt");
		}

		writeFileSdcard(filePath, text);
	}

	/**
	 * 保存已考已报信息
	 *
	 * @param row
	 *            :0表示学生姓名,1表示入园时间,2表示学校,3~7表示家长称呼
	 * @return
	 * @throws IOException
	 */
	public String readPhotoInfoFile(String childid, int row) {

		String filePath;
		String res = null;

		filePath = getSDPath() + "/tpatt/CardInfo/Photo/" + childid + "/" + childid + ".txt";
		File photofile = new File(filePath);
		if (photofile.exists()) {
			try {
				String str = "";
				FileInputStream fis = new FileInputStream(filePath);
				int size = fis.available();
				byte[] buffer = new byte[size];

				fis.read(buffer);
				fis.close();
				str = new String(buffer, "UTF-8");// 支持双字节字符
				if (str.length() > 0) {
					String[] lines;

					lines = str.split("\r\n", str.length());
					List<String> list = Arrays.asList(lines);
					if (list.size() > row) {
						res = list.get(row);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.i("TPATT", "读卡图片信息文件:文件不存在 ");
		}

		return res;
	}

	public void saveZZBitmap(Bitmap mBitmap, String bitPathName) {

		File f = new File(bitPathName);
		FileOutputStream fOut = null;

		try {
			fOut = new FileOutputStream(f);
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int options = 80;// 个人喜欢从80开始,
				mBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
				while (baos.toByteArray().length / 1024 > 30) {
					baos.reset();
					options -= 10;
					mBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
				}

				// mBitmap.compress(Bitmap.CompressFormat.PNG, 50, fOut);
				fOut.write(baos.toByteArray());
				fOut.flush();
				fOut.close();
				mBitmap.recycle();
			} catch (IOException e) {
				Log.i("TPATT", "保存异常: flush IOException ");
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			Log.i("TPATT", "保存异常:FileNotFoundException ");
			e.printStackTrace();
		}

	}

	/**
	 * 保存图片
	 *
	 * @param
	 * @return
	 */
	public void saveBitmap(Bitmap mBitmap, String bitPathName) {
		File f = new File(bitPathName);
		FileOutputStream fOut = null;

		try {
			fOut = new FileOutputStream(f);
			try {
				mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
				fOut.flush();
				fOut.close();
				mBitmap.recycle();
			} catch (IOException e) {
				Log.i("TPATT", "保存异常: flush IOException ");
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			Log.i("TPATT", "保存异常:FileNotFoundException ");
			e.printStackTrace();
		}
	}

	/**
	 * 保存图片
	 *
	 * @param
	 * @return
	 */
	@SuppressWarnings("finally")
	public boolean saveBitmapjpg(Bitmap mBitmap, String bitPathName) {
		File f = new File(bitPathName);
		FileOutputStream fOut = null;
		boolean issucc = true;

		try {
			fOut = new FileOutputStream(f);
			// mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			// fOut.flush();
			// fOut.close();

			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int options = 80;// 个人喜欢从80开始,
				mBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
				while (baos.toByteArray().length / 1024 > 30) {
					baos.reset();
					options -= 10;
					mBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
				}

				// mBitmap.compress(Bitmap.CompressFormat.PNG, 50, fOut);
				fOut.write(baos.toByteArray());
				fOut.flush();
				fOut.close();
				mBitmap.recycle();
			} catch (IOException e) {
				issucc = false;
				Log.i("TPATT", "保存异常: flush IOException ");
				e.printStackTrace();
			}

		} catch (IOException e) {
			Log.i("TPATT", "保存异常: flush IOException ");
			issucc = false;
			e.printStackTrace();
		} finally {
			Log.i("TPATT", "保存 " + issucc);
			mBitmap.recycle();
			return issucc;
		}

	}

	/**
	 * 保存图片
	 *
	 * @param
	 * @return
	 */
	public void saveBitmapdata(byte[] data, String bitPathName) {
		File f = new File(bitPathName);
		FileOutputStream fOut = null;

		try {
			fOut = new FileOutputStream(f);
			try {
				fOut.write(data);
				// mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
				fOut.flush();
				fOut.close();
			} catch (IOException e) {
				Log.i("TPATT", "保存异常: flush IOException ");
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			Log.i("TPATT", "保存异常:FileNotFoundException ");
			e.printStackTrace();
		}
	}

	/**
	 * 把图片byte流编程bitmap
	 *
	 * @param data
	 * @return
	 */
	public Bitmap byteToBitmap(byte[] data) {
		Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		int i = 0;
		while (true) {
			if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				b = BitmapFactory.decodeByteArray(data, 0, data.length, options);
				break;
			}
			i += 1;
		}
		return b;
	}

	/**
	 * 删除指定文件夹下几小时前的文件
	 *
	 * @param path
	 * @return
	 */
	public boolean delAllFile(String path, int hour) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				long lastModeify = temp.lastModified() / 1000;
				long current = System.currentTimeMillis() / 1000;

				Log.i("TPATT", "照片文件创建时间:" + "current=" + current + "  lastModeify=" + lastModeify);
				// if ( (current - lastModeify) > (60 * 60 * 24 * day) )
				if ((current - lastModeify) > (60 * 60 * hour)) {
					temp.delete();
				}
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i], hour);// 先删除文件夹里面的文件
				// writeinfile("开始删除图片，。。"+path + "/" +
				// tempList[i]+"。。。时间是："+day);
				flag = true;
			}

			if (getAutoFileOrFilesSize(getSDPath() + "/tpatt/AttPhoto") <= 1000 * 30 * 1024) {
				break;
			}

		}
		return flag;
	}

	/**
	 * 调用此方法自动计算指定文件或指定文件夹的大小
	 *
	 * @param filePath
	 *            文件路径
	 * @return 计算好的带B、KB、MB、GB的字符串
	 */
	public long getAutoFileOrFilesSize(String filePath) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("获取文件大小", "获取失败!");
		}
		return blockSize;
	}

	/**
	 * 获取指定文件大小
	 *
	 * @param f
	 * @return
	 * @throws Exception
	 */
	private static long getFileSize(File file) throws Exception {
		long size = 0;

		if (file.exists()) {
			FileInputStream fis = new FileInputStream(file);

			size = fis.available();
			fis.close();
		} else {
			file.createNewFile();
			Log.e("获取文件大小", "文件不存在!");
		}

		return size;
	}

	/**
	 * 获取指定文件夹大小
	 *
	 * @param f
	 * @return
	 * @throws Exception
	 */
	private static long getFileSizes(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSizes(flist[i]);
			} else {
				size = size + getFileSize(flist[i]);
			}
		}

		return size;
	}

	public void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;

		options.inPurgeable = true;

		options.inInputShareable = true;
		BitmapFactory.decodeResource(res, resId, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = 2;
		// 使用获取到的inSampleSize值再次解析图片

		return BitmapFactory.decodeResource(res, resId, options);
	}

	public boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {

			return false;
		}
		return true;
	}

	/**
	 *
	 * @param oldPath
	 *            String 原文件路径
	 * @param newPath
	 *            String 复制后路径
	 * @return boolean
	 */
	@SuppressWarnings({ "unused", "resource" })
	public void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	public static void execSuCmd(String cmd) {
		Process process = null;
		DataOutputStream os = null;
		DataInputStream is = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			int aa = process.waitFor();
			is = new DataInputStream(process.getInputStream());
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			String out = new String(buffer);
			Log.i("tag", out + aa);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
	}


	public static void watchdog(){

		try {
			/* Missing read/write permission, trying to chmod the file */
			Process su;
			// su = Runtime.getRuntime().exec("/system/bin/su");
			su = Runtime.getRuntime().exec("su");
			String cmd = "chmod 666 /dev/watchdog "+ "\n" + "exit\n";
			su.getOutputStream().write(cmd.getBytes());

			if ((su.waitFor() != 0)) {
				throw new SecurityException();
			}
			Log.i("TPATT", "看门狗初始化成功");
		} catch (Exception e) {
			Log.i("TPATT", "看门狗调试:看门狗异常");
			// appBaseFun.writeinfile("串口调试:串口驱动异常");
			WriteUnit.loadlist("看门狗调试:看门狗异常");
			e.printStackTrace();
			throw new SecurityException();
		}





	}


	/**
	 * 复制整个文件夹内容
	 * @param oldPath String 原文件路径 如：c:/fqf
	 * @param newPath String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
			File a=new File(oldPath);
			String[] file=a.list();
			File temp=null;
			for (int i = 0; i < file.length; i++) {
				Log.i(".....",""+ file.length);
				if(oldPath.endsWith(File.separator)){
					temp=new File(oldPath+file[i]);
				}
				else{
					temp=new File(oldPath+File.separator+file[i]);
				}

				if(temp.isFile()){
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" +
							(temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ( (len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if(temp.isDirectory()){//如果是子文件夹
					copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
				}
			}
		}
		catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();

		}

	}


	//控制电平
	public static void gpiocon(String com){

		try {

			Log.i("gpio", com);

			File file=new File("/sys/devices/virtual/adw/adwdev/adwgpio");

			BufferedWriter bw=new BufferedWriter(new FileWriter(file));

			bw.write(com);
			bw.flush();
			bw.close();


		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.i("gpio+error", e.getMessage());
		}


	}

	/**
	 * 旋转图片，使图片保持正确的方向。
	 * @param bitmap 原始图片
	 * @param degrees 原始图片的角度
	 * @return Bitmap 旋转后的图片
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
		if (degrees == 0 || null == bitmap) {
			return bitmap;
		}
		Matrix matrix = new Matrix();
		matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (null != bitmap) {
			bitmap.recycle();
		}
		return bmp;
	}



}
