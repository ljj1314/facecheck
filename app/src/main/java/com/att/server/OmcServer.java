package com.att.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.att.AppBaseFun;
import com.att.HttpApp;
import com.att.SettingPara;
import com.att.act.AppAction;
import com.att.act.ClientOMC;
import com.att.act.ClientOMC.OnDataReceiveListeners;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.serport.DataUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OmcServer extends Service {

	private ClientOMC omc = null;
	private List<String> ml = null;
	private AppBaseFun appb = new AppBaseFun();
	private List<String> filelist = null;
	private boolean isread = false;
	private boolean iswrite = false;
	private String topline = "";
	private boolean isup = false;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	public void onCreate() {

		sp = getSharedPreferences("omc", Context.MODE_PRIVATE);
		omc = ClientOMC.instance(sp);
		SettingPara sp = new SettingPara();
		omc.connect(sp.getOmcurl(), 8895);

		Timer heartTimer = new Timer();
		heartTimer.schedule(new TimerTask() {
			@Override
			public void run() {

			}
		}, 1000 * 60, 1000 * 30);

		omc.setOnDataReceiveListeners(new OnDataReceiveListeners() {

			public void onDataReceives(String buffer) {

				if (buffer == null) {
					return;
				}
				Log.i("tappo", "获取后的OMC信息为：" + buffer);

				if (buffer.startsWith("JTP")) {

					String ff = buffer.substring(29, buffer.length());
					Log.i("tappo", "截取后的OMC信息为ff：" + ff);
					if (ff.startsWith("F1")) {

						isread = true;
						iswrite = false;
						isup = false;

					} else if (ff.startsWith("F2")) {

						isread = false;
						iswrite = true;
						isup = false;

					} else if (ff.startsWith("10")) {
						return;
					} else if (ff.startsWith("F3")) {
						isread = false;
						iswrite = false;
						isup = true;
					}

					String bd = buffer.substring(31, (buffer.length() - 2));
					Log.i("tappo", "截取后的OMC信息为bd：" + bd);
					topline = buffer.substring(3, 25);
					if (isread) {
						String[] afk = bd.split("=&");
						ml = new ArrayList<String>();
						readline(afk);
					} else if (iswrite) {

						String[] afk = bd.split("&");
						ml = new ArrayList<String>();
						wirteline(afk);

					} else if (isup) {

						String[] afk = bd.split("=");
						ml = new ArrayList<String>();

						try {
							upline(afk);
						} catch (Exception e) {

							e.printStackTrace();
						}

					}

				}

			}
		});

		Timer hb = new Timer();
		hb.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				boolean isnet = omc.sendMsgbyte(omc.send().getBytes());
				if (!isnet) {
					omc.close();
					omc.connect("172.16.68.113", 8895);
				}
			}
		}, 1000 * 45, 1000 * 45);

	}

	private void upline(String[] afk) throws Exception {

		// for (int i = 0; i < afk.length; i++) {
		String sendms = "TPJ" + topline;
		String md = "";
		int conlen = 0;
		if (afk[0].startsWith("5.1")) {

			Intent intent = new Intent(getApplicationContext(), UpdataServer.class);
			getApplication().startService(intent);

			String rr = "ResultCode=00&ResultDesc=Success";
			String rrlen = "" + rr.length();
			String yz = getlistb((new String().valueOf(243 + rr.length())).getBytes());
			byte[] mk = ("TPJ" + topline + "0000".substring(0, 4 - rrlen.trim().length()) + rrlen + "F3" + rr + yz)
					.getBytes();
			omc.sendMsg(mk);

		} else if (afk[0].startsWith("5.2")) {
			// String url=afk[1];
			File file = new File(appb.getPhoneCardPath() + "/logtext");
			File file2 = new File(appb.getPhoneCardPath() + "/lgz.zip");

			try {
				AppAction.zipFiles(file, file2);
			} catch (IOException e) {

				e.printStackTrace();
			}
			InputStream input = null;
			// byte[] adc=null;
			try {
				input = new FileInputStream(file2); // 从文件中读取
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte data[] = new byte[1024];
				int len = 0;
				while ((len = input.read(data)) != -1) {
					bos.write(data, 0, len);
				}
				// adc=new byte[bos.toByteArray().length];
				byte[] adc = bos.toByteArray();
				// omc.sendMsgbyte(bos.toByteArray());

				String up = "5.2=lgz.zip";
				conlen = up.length() + 2;
				String ll = "" + conlen;
				// String bw=""+conlen;
				HttpApp ha = new HttpApp();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				SettingPara settingPara = new SettingPara();
				params.add(new BasicNameValuePair("DeviceID", settingPara.getDevicID()));
				params.add(new BasicNameValuePair("content", DataUtils.bytesToHexString(adc)));
				String res = ha.postSendAndReceive("http://192.168.0.247:10112/openapi/UploadLog.ashx", params);
				Log.i("tappo", "返回结果" + res);
				if (res.equals("ok")) {
					String yz = getlistb((new String().valueOf(243 + conlen)).getBytes());

					byte[] mk = ("TPJ" + topline + "0000".substring(0, 4 - ll.trim().length()) + conlen + "F3" + up
							+ yz).getBytes();
					omc.sendMsg(mk);
				}

				// byte[] ff=new byte[adc.length+mk.length];
				// System.arraycopy(mk, 0, ff,0, mk.length);
				// System.arraycopy(adc, 0, ff, mk.length, adc.length);

				Log.i("tappo", "发送成功");
			} catch (Exception e) {
				throw e;
			} finally {
				input.close();
			}

		}

		// }

	}

	private static String getlistb(byte[] b) {

		int num = 0;

		for (int i = 0; i < b.length; i++) {

			num = (num + b[i]) % 0xffff;

		}
		b = String.valueOf(num).getBytes();
		String mi = DataUtils.bytesToHexString(b);

		return mi.substring(0, 2);
	}

	private void getFileName(File[] files) {
		if (files != null) {// 先判断目录是否为空，否则会报空指针
			for (File file : files) {
				if (file.isDirectory()) {
					Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString() + file.getPath().toString());

				} else {
					String fileName = file.getName();
					if (fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".3gp")) {

						// String s = fileName.substring(0,
						// fileName.lastIndexOf(".")).toString();
						Log.i("zeng", "文件名mp4：：   " + fileName);
						filelist.add(fileName);
					}
				}
			}
		}
	}

	private void getFileNamepic(File[] files) {
		if (files != null) {// 先判断目录是否为空，否则会报空指针
			for (File file : files) {
				if (file.isDirectory()) {
					Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString() + file.getPath().toString());

				} else {
					String fileName = file.getName();
					if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {

						// String s = fileName.substring(0,
						// fileName.lastIndexOf(".")).toString();
						Log.i("zeng", "文件名jpg：：   " + fileName);
						filelist.add(fileName);
					}
				}
			}
		}
	}

	private void readline(String[] afk) {
		SettingPara st = new SettingPara();
		for (int i = 0; i < afk.length; i++) {
			Log.i("tappo", afk[i]);
			String md = "";

			if (afk[i].equals("1.1")) {
				if (st.isIdle_vedio_switch()) {
					md = "1.1=2";
				} else {
					md = "1.1=1";
				}

			} else if (afk[i].equals("1.2")) {
				md = "1.2=" + st.getIdle_pic_duration();
			} else if (afk[i].equals("1.3")) {
				md = "1.3=0";
			} else if (afk[i].equals("2.1")) {

				String[] start = new String[3];
				String[] end = new String[3];
				String[] time = new String[2];

				start = st.getGo_school_start();

				end = st.getGo_school_end();

				md = "2.1=" + start[0].split(":")[0] + ":" + start[0].split(":")[1] + "-" + end[0].split(":")[0] + ":"
						+ end[0].split(":")[1] + "," + start[1].split(":")[0] + ":" + start[1].split(":")[1] + "-"
						+ end[1].split(":")[0] + ":" + end[1].split(":")[1] + "," + start[2].split(":")[0] + ":"
						+ start[2].split(":")[1] + "-" + end[2].split(":")[0] + ":" + end[2].split(":")[1];
			} else if (afk[i].equals("2.2")) {

				String[] start = new String[3];
				String[] end = new String[3];
				String[] time = new String[2];

				start = st.getOut_school_start();

				end = st.getOut_school_end();

				md = "2.2=" + start[0].split(":")[0] + ":" + start[0].split(":")[1] + "-" + end[0].split(":")[0] + ":"
						+ end[0].split(":")[1] + "," + start[1].split(":")[0] + ":" + start[1].split(":")[1] + "-"
						+ end[1].split(":")[0] + ":" + end[1].split(":")[1] + "," + start[2].split(":")[0] + ":"
						+ start[2].split(":")[1] + "-" + end[2].split(":")[0] + ":" + end[2].split(":")[1];
			} else if (afk[i].equals("2.3")) {

				if (st.isTake_photo()) {
					md = "2.3=1";
				} else {
					md = "2.3=0";
				}

			} else if (afk[i].equals("4.1")) {

				// md="4.1="+st.getIdle_pic_duration();

				// if (st.isIdle_vedio_switch()) {
				File file = new File(appb.getSDPath() + File.separator + "tp");
				File[] files = file.listFiles();// 读取

				File filepic = new File(appb.getSDPath() + "/tpatt/PlayPhoto");
				File[] filespic = filepic.listFiles();// 读取

				filelist = new ArrayList<String>();
				getFileName(files);
				getFileNamepic(filespic);

				// }
				if (filelist.size() > 0) {
					for (int j = 0; j < filelist.size(); j++) {

						if (j == 0) {
							md = "4.1=" + filelist.get(j) + ",";
						} else if (j == (filelist.size() - 1)) {
							md += filelist.get(j);
						} else {
							md += filelist.get(j) + ",";
						}

					}
				} else {
					md = "4.1=";
				}

				Log.i("tappo", "" + filelist);

			} else if (afk[i].equals("4.2")) {

			} else if (afk[i].equals("4.4")) {

			} else {
				continue;
			}

			ml.add(md);
		}

		String ln = "";
		if (isread) {
			for (int i = 0; i < ml.size(); i++) {
				if (i != (ml.size() - 1)) {
					ln += ml.get(i) + "&";
				} else {
					ln += ml.get(i);
				}

			}
			String jd = "" + ln.length();
			String yz = getlistb((new String().valueOf(241 + ln.length() + 2)).getBytes());
			String sendms = "TPJ" + topline + "0000".substring(0, 4 - jd.trim().length()) + jd + "F1" + ln + yz;
			Log.i("tappo", "整理后的输出信息为" + sendms);
			omc.sendMsg(sendms.getBytes());
		}

	}

	private void wirteline(String[] afk) {

		SettingPara st = new SettingPara();
		for (int i = 0; i < afk.length; i++) {

			String md = "";
			String[] bp = afk[i].split("=");
			if (bp[0].equals("1.1")) {
				try {

					String[] a1 = afk[i].split("=");

					if (a1[1].equals("1")) {
						st.setIdle_vedio_switch(false);
					} else {
						st.setIdle_vedio_switch(true);
					}
				} catch (Exception e) {

				}
			} else if (bp[0].equals("1.2")) {
				try {

					st.setIdle_pic_duration(Integer.valueOf(afk[i].split("=")[1]).intValue());
					md = "1.2=" + st.getIdle_pic_duration();
				} catch (Exception e) {

				}
			} else if (bp[0].equals("1.3")) {
				md = "1.3=0";
			} else if (bp[0].equals("2.1")) {
				try {

					String[] start = new String[3];
					String[] end = new String[3];
					String[] time = new String[2];

					String[] start1 = new String[3];
					String[] end1 = new String[3];

					String[] mt = bp[1].split(",");
					String[] time1 = mt[0].split("-");
					String[] time2 = mt[1].split("-");
					String[] time3 = mt[2].split("-");

					start[0] = time1[0].split(":")[0] + ":" + time1[0].split(":")[1];
					start[1] = time2[0].split(":")[0] + ":" + time2[0].split(":")[1];
					start[2] = time3[0].split(":")[0] + ":" + time3[0].split(":")[1];

					end[0] = time1[1].split(":")[0] + ":" + time1[1].split(":")[1];
					end[1] = time2[1].split(":")[0] + ":" + time2[1].split(":")[1];
					end[2] = time3[1].split(":")[0] + ":" + time3[1].split(":")[1];

					st.setGo_school_start(start);
					st.setGo_school_end(end);
				} catch (Exception e) {

				}
			} else if (bp[0].equals("2.2")) {

				try {

					String[] start = new String[3];
					String[] end = new String[3];
					String[] time = new String[2];

					String[] mt = bp[1].split(",");
					String[] time1 = mt[0].split("-");
					String[] time2 = mt[1].split("-");
					String[] time3 = mt[2].split("-");

					start[0] = time1[0].split(":")[0] + ":" + time1[0].split(":")[1];
					start[1] = time2[0].split(":")[0] + ":" + time2[0].split(":")[1];
					start[2] = time3[0].split(":")[0] + ":" + time3[0].split(":")[1];

					end[0] = time1[1].split(":")[0] + ":" + time1[1].split(":")[1];
					end[1] = time2[1].split(":")[0] + ":" + time2[1].split(":")[1];
					end[2] = time3[1].split(":")[0] + ":" + time3[1].split(":")[1];

					st.setOut_school_start(start);
					st.setOut_school_end(end);

				} catch (Exception e) {

				}

			} else if (bp[0].equals("2.3")) {
				try {

					if (bp[1].equals("0")) {
						st.setTake_photo(false);
					} else {
						st.setTake_photo(true);
					}
				} catch (Exception e) {

				}

			} else if (bp[0].equals("4.2")) {
				try {

					String[] mi = bp[1].split(",");
					// String[]
					Log.i("tappo", "分离信息" + mi[0] + "...." + mi[1]);
					if (mi[0].equals("A")) {

						for (int j = 1; j < mi.length; j++) {
							Log.i("tappo", "进入下载" + mi[j] + "....." + filename(mi[j]));
							if (mi[j].endsWith(".jpg") || mi[j].endsWith(".png")) {

								HttpUtils hUtils = new HttpUtils();
								hUtils.download(mi[j], appb.getSDPath() + "/tpatt/PlayPhoto/" + filename(mi[j]), true,
										true, new RequestCallBack<File>() {

											@Override
											public void onSuccess(ResponseInfo<File> arg0) {

												Log.i("Tapp", "下载成功");
												String lm = "4.2=A&ResultCode=00&ResultDesc=Success";
												String ll = "" + lm.length();
												String yz = getlistb(
														(new String().valueOf(242 + lm.length() + 2)).getBytes());
												String sendms = "TPJ" + topline
														+ "0000".substring(0, 4 - ll.trim().length()) + ll + "F2" + lm
														+ yz;

												omc.sendMsg(sendms.getBytes());
												// AppAction.install(Environment.getExternalStorageDirectory().toString()+"/TAPP.apk",
												// TelpoService.this);

											}

											@Override
											public void onFailure(HttpException arg0, String arg1) {

												Log.i("Tapp", "下载失败");

											}
										});

							} else {

								HttpUtils hUtils = new HttpUtils();
								hUtils.download(mi[j], appb.getSDPath() + "/tp/" + filename(mi[j]), true, true,
										new RequestCallBack<File>() {

											@Override
											public void onSuccess(ResponseInfo<File> arg0) {

												Log.i("Tapp", "下载成功");

												// AppAction.install(Environment.getExternalStorageDirectory().toString()+"/TAPP.apk",
												// TelpoService.this);
												String lm = "4.2=A&ResultCode=00&ResultDesc=Success";
												String ll = "" + lm.length();
												String yz = getlistb(
														(new String().valueOf(242 + lm.length() + 2)).getBytes());
												String sendms = "TPJ" + topline
														+ "0000".substring(0, 4 - ll.trim().length()) + ll + "F2" + lm
														+ yz;

												omc.sendMsg(sendms.getBytes());

											}

											@Override
											public void onFailure(HttpException arg0, String arg1) {

												Log.i("Tapp", "下载失败");

											}
										});

							}
						}

					} else if (bp[1].startsWith("D")) {

						Log.i("tappo", "删除图片名为" + filename(mi[1]));
						for (int j = 1; j < mi.length; j++) {
							try {
								if (mi[j].endsWith(".jpg") || mi[j].endsWith(".png")) {
									Log.i("tappo", "删除图片名为" + filename(mi[j]));
									File file = new File(appb.getSDPath() + "/tpatt/PlayPhoto/" + mi[j]);

									file.delete();

								} else {

									File file = new File(appb.getSDPath() + "/tp/" + mi[j]);

									file.delete();

								}

							} catch (Exception e) {

							}

						}

					}

				} catch (Exception e) {

				}

			}

			ml.add(md);
		}

		st.save_settingpara();
		String lm = "ResultCode=00&ResultDesc=Success";
		String ll = "" + lm.length();
		String yz = getlistb((new String().valueOf(242 + lm.length() + 2)).getBytes());
		String sendms = "TPJ" + topline + "0000".substring(0, 4 - ll.trim().length()) + ll + "F2" + lm + yz;
		if (iswrite) {
			// for (int i = 0; i < ml.size(); i++) {
			//
			// sendms+=ml.get(i)+"&";
			//
			// }
			omc.sendMsg(sendms.getBytes());
			Log.i("tappo", "整理后的输出信息为" + sendms);

		}

	}

	private String filename(String url) {

		if (url == null) {
			return null;
		}

		return url.substring(url.lastIndexOf("/") + 1);
	}

	private String getimei() {

		return ((TelephonyManager) this.getSystemService(TELEPHONY_SERVICE)).getDeviceId();

	}

}
