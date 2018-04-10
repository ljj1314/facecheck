package com.att.act;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WriteUnit {

	private static List<String> content = new ArrayList<String>();
	private static int contentlen = 0;
	private static String text = null;
	private static boolean isNotloading = true;

	public static void debugLog(String log) {
		// Log.i("TPATT", log);//TODO 发布时注释掉不必要的日志输出
	}

	public static void start() {

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {

				try {

					if (content.size() > 0 && isNotloading) {
						isNotloading = false;
						for (int i = 0; i < content.size(); i++) {

							text += content.get(i) + "\n";

						}

						content.clear();
						contentlen = 0;
						writeInFile(text);
						isNotloading = true;
						text = null;
					}
				} catch (Exception e) {

					content.clear();
					contentlen = 0;
					isNotloading = true;
					text = null;

				}

			}
		}, 1000 * 60, 1000 * 60);

	}

	/**
	 * 加载文本数据 当con字节大于4096后自动加载
	 *
	 */
	public static void loadlist(String con) {

		if (con != null) {
			contentlen += con.length();
			content.add(con);
		}

		if (contentlen > 4096 && isNotloading) {
			contentlen = 0;
			isNotloading = false;

			for (int i = 0; i < content.size(); i++) {
				text += content.get(i) + "\n";
			}

			writeInFile(text);

			content.clear();
			isNotloading = true;
			text = null;

		}

	}

	// 写入SD卡txt文件
	public static void writeInFile(String text) {

		try {

			// String strswingCardDtimeDisplay = new
			// SimpleDateFormat("yyyyMMdd").format(new
			// Date(System.currentTimeMillis()));
			String strswingCardDtime = new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()));

			String aaa = Environment.getExternalStorageDirectory() + File.separator + "logtext/" + strswingCardDtime
					+ ".txt";
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
		} catch (Exception e) {
		}
	}

	public static void clearMoreLog() {
		List<String> filelist = null;

		try {

			Calendar calendar = Calendar.getInstance();

			@SuppressWarnings("unused")
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");

			@SuppressWarnings("unused")
			int day = calendar.get(Calendar.DAY_OF_MONTH);

			Date nowtimes = new Date(System.currentTimeMillis());

			File file = new File(Environment.getExternalStorageDirectory() + File.separator + "logtext");
			File[] files = file.listFiles();// 读取
			filelist = getFileName(files);
			if (filelist.size() > 1) {
				for (int i = 0; i < filelist.size(); i++) {

					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					java.util.Date updatetime = null;
					try {
						updatetime = sdf.parse(filelist.get(i));
						long timeouts = twoDateDistance(updatetime, nowtimes);
						if (timeouts > 7) {
							File delfile = new File(Environment.getExternalStorageDirectory() + File.separator
									+ "logtext/" + filelist.get(i) + ".txt");
							delfile.delete();
							// appBaseFun.delete(new
							// File(Environment.getExternalStorageDirectory()
							// + File.separator
							// +"logtext/"+filelist.get(i)+".txt"));
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}

				}
			}

		} catch (Exception e) {

		}
	}

	private static List<String> getFileName(File[] files) {
		List<String> filelist = new ArrayList<String>();
		if (files != null) {// 先判断目录是否为空，否则会报空指针
			for (File file : files) {
				if (file.isDirectory()) {
					// Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString() +
					// file.getPath().toString());

				} else {
					String fileName = file.getName();
					if (fileName.endsWith(".txt")) {

						String s = fileName.substring(0, fileName.lastIndexOf(".")).toString();
						// Log.i("zeng", "文件名txt：： " + s);
						filelist.add(s);
					}
				}
			}
		}
		return filelist;
	}

	/**
	 * 计算两个日期型的时间相差多少时间
	 *
	 * @param updatetime
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static long twoDateDistance(java.util.Date updatetime, java.util.Date endDate) {

		if (updatetime == null || endDate == null) {
			return 0;
		}
		long timeLong = endDate.getTime() - updatetime.getTime();
		// if (timeLong<60*60*24*1000*7){
		timeLong = timeLong / 1000 / 60 / 60 / 24;
		return timeLong;
		// }
		// return 0;
	}

}
