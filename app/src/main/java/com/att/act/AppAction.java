package com.att.act;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AppAction {

	public static boolean install(String apkPath, Context context) {
		// 先判断手机是否有root权限
		if (hasRootPerssion()) {
			// 有root权限，利用静默安装实现
			return clientInstall(apkPath);
		} else {
			// 没有root权限，利用意图进行安装
			File file = new File(apkPath);
			if (!file.exists())
				return false;
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			context.startActivity(intent);
			return true;
		}
	}

	/**
	 * 判断手机是否有root权限
	 */
	private static boolean hasRootPerssion() {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return false;
	}

	/**
	 * 静默安装
	 */
	private static boolean clientInstall(String apkPath) {
		PrintWriter PrintWriter = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su");
			PrintWriter = new PrintWriter(process.getOutputStream());
			PrintWriter.println("chmod 777 " + apkPath);
			PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
			PrintWriter.println("pm install -r " + apkPath);
			// PrintWriter.println("exit");
			PrintWriter.flush();
			PrintWriter.close();
			int value = process.waitFor();
			return returnResult(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return false;
	}

	private static boolean returnResult(int value) {
		// 代表成功
		if (value == 0) {
			return true;
		} else if (value == 1) { // 失败
			return false;
		} else { // 未知情况
			return false;
		}
	}

	public static void main(String[] args) throws IOException {
		File outfile = new File("D://new_car.csv");// 存储到新文件的路径
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream("D://car.csv"));// 待处理数据的文件路径
			BufferedReader reader = new BufferedReader(isr);
			BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				@SuppressWarnings("unused")
				String item[] = line.split(",");
				/*
				 * 信息处理块
				 */
				bw.newLine();// 新起一行
				bw.write("" + "," + "");// 写到新文件中
			}
			isr.close();
			reader.close();
			bw.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 读取csv
	 *
	 * @param csvFilePath
	 * @throws Exception
	 */
	public static void readerCsv(String csvFilePath, SharedPreferences.Editor se, List<ChildCard> lc) throws Exception {

		// File file = new File(csvFilePath);
		// FileReader fReader = new FileReader(file);

		DataInputStream in = new DataInputStream(new FileInputStream(new File(csvFilePath)));//
		CSVReader csvReader = new CSVReader(new InputStreamReader(in, "gbk"), ',', '\'', 0);

		// CSVReader csvReader = new CSVReader(fReader);
		String[] strs = csvReader.readNext();
		if (strs != null && strs.length > 0) {
			// for(String str : strs)
			if (null != strs && !strs.equals(""))
				// Log.i("csv", str. + " , ");
				Log.i("csv", strs[0] + strs[1]);
			se.putString(strs[0] + "name", strs[1]);
			se.putString(strs[0] + "local", "yes");
			se.commit();
			// System.out.print(str + " , ");
			// System.out.println("\n---------------");
		}
		List<String[]> list = csvReader.readAll();
		for (String[] ss : list) {
			// for(String s : ss)
			// if(null != s && !s.equals(""))
			// Log.i("csv","1"+ s + " , ");

			Log.i("csv", "" + ss);
			if (ss != null && ss.length > 0) {
				Log.i("csv", ss[0] + ss[1]);
				ChildCard childCard = new ChildCard();
				childCard.setCard_id(ss[0]);
				childCard.setChild_name(ss[1]);
				lc.add(childCard);
				se.putString(ss[0] + "name", ss[1]);
				se.putString(ss[0] + "local", "yes");
				se.commit();
			}

			// System.out.print(s + " , ");
			// System.out.println();
		}
		csvReader.close();
	}


	/**
	 * 读取多音字csv
	 *
	 * @param csvFilePath
	 * @throws Exception
	 */
	public static void readerCsvduoy(String csvFilePath, SharedPreferences.Editor se, List<ChildCard> lc) throws Exception {

		// File file = new File(csvFilePath);
		// FileReader fReader = new FileReader(file);

		DataInputStream in = new DataInputStream(new FileInputStream(new File(csvFilePath)));//
		CSVReader csvReader = new CSVReader(new InputStreamReader(in, "gbk"), ',', '\'', 0);

		// CSVReader csvReader = new CSVReader(fReader);
		String[] strs = csvReader.readNext();
		if (strs != null && strs.length > 0) {
			// for(String str : strs)
			if (null != strs && !strs.equals(""))
				// Log.i("csv", str. + " , ");
				Log.i("csv", strs[0] + strs[1]);
			se.putString(strs[0] + "dyname", strs[1]);
			//	se.putString(strs[0] + "dylocal", "yes");
			se.commit();
			// System.out.print(str + " , ");
			// System.out.println("\n---------------");
		}
		List<String[]> list = csvReader.readAll();
		for (String[] ss : list) {
			// for(String s : ss)
			// if(null != s && !s.equals(""))
			// Log.i("csv","1"+ s + " , ");

			Log.i("csv", "" + ss);
			if (ss != null && ss.length > 0) {
				Log.i("csv", ss[0] + ss[1]);
				ChildCard childCard = new ChildCard();
				childCard.setCard_id(ss[0]);
				childCard.setChild_name(ss[1]);
				lc.add(childCard);
				se.putString(ss[0] + "dyname", ss[1]);
				//	se.putString(ss[0] + "dylocal", "yes");
				se.commit();
			}

			// System.out.print(s + " , ");
			// System.out.println();
		}
		csvReader.close();
	}

	/**
	 * 批量压缩文件（夹）
	 *
	 * @param resFileList
	 *            要压缩的文件（夹）列表
	 * @param zipFile
	 *            生成的压缩文件
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */

	public static void zipFiles(File resFileList, File zipFile) throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(
				new BufferedOutputStream(new FileOutputStream(zipFile), 1024 * 1024));
		// for (File resFile : resFileList) {
		zipFile(resFileList, zipout, "");
		// }
		zipout.close();
	}

	/**
	 * 压缩文件
	 *
	 * @param resFile
	 *            需要压缩的文件（夹）
	 * @param zipout
	 *            压缩的目的文件
	 * @param rootpath
	 *            压缩的文件路径
	 * @throws FileNotFoundException
	 *             找不到文件时抛出
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	public static void zipFile(File resFile, ZipOutputStream zipout, String rootpath)
			throws FileNotFoundException, IOException {
		rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator) + resFile.getName();
		rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
		if (resFile.isDirectory()) {
			File[] fileList = resFile.listFiles();
			for (File file : fileList) {
				zipFile(file, zipout, rootpath);
			}
		} else {
			byte buffer[] = new byte[1024 * 1024];
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile), 1024 * 1024);
			zipout.putNextEntry(new ZipEntry(rootpath));
			int realLength;
			while ((realLength = in.read(buffer)) != -1) {
				zipout.write(buffer, 0, realLength);
			}
			in.close();
			zipout.flush();
			zipout.closeEntry();
		}
	}

	public void toDate(long time) {
		try {
			Process process = Runtime.getRuntime().exec("su");
			String datetime = "20131023.112800"; // 测试的设置的时间【时间格式
			// yyyyMMdd.HHmmss】
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("setprop persist.sys.timezone GMT\n");
			os.writeBytes("/system/bin/date -s " + datetime + "\n");
			os.writeBytes("clock -w\n");
			os.writeBytes("exit\n");
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static boolean isAvilible( Context context, String packageName )
	{
		final PackageManager packageManager = context.getPackageManager();
		// 获取所有已安装程序的包信息
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		for ( int i = 0; i < pinfo.size(); i++ )
		{
			if(pinfo.get(i).packageName.equalsIgnoreCase(packageName))
				return true;
		}
		return false;
	}



}
