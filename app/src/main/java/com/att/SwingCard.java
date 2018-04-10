/**************************************************************************
 Copyright (C) 广东天波教育科技有限公司　版权所有
 文 件 名：
 创 建 人：
 创建时间：2015.10.30
 功能描述：刷卡、串口
 **************************************************************************/
package com.att;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android_serialport_api.SerialPort;

public class SwingCard {

	protected static SerialPort mSerialPort = null;
	protected static OutputStream mOutputStream = null;
	private static InputStream mInputStream = null;
	private static byte aIdStep = 0;
	private static int uart_rec_len = 0;
	private static int Cidflash = 0;
	private static int CidflashTimeout = 0;
	private static String strcidBak = new String();
	private static String pintf_temp = new String();
	private static int uart_rec_max_len = 100;
	private static byte[] buffer = new byte[uart_rec_max_len];
	private static Boolean success = false; /// 0 还没初始化 1初始化成功

	// byte[] uart_rec = new byte[100];
	static char[] uart_rec = new char[uart_rec_max_len];
	static byte[] aIdBuf = new byte[4];
	static byte[] cid = new byte[4];
	static String longStr = new String();

	static Timer timer = null;
	static TimerTask task = null;
	static Timer timerFlashTimeout = null;
	static TimerTask timerFlashTimeouttask = null;
	// private static Mutex lock= null;
	private static Lock lock = new ReentrantLock();
	static ExecutorService transThread;
	@SuppressWarnings("rawtypes")

	static Future transPending;

	public enum radix { /// 进制
		decimal, hex
	}

	public enum rotating { /// 进制
		Normal, //// 正转
		Back/// 反转
	}

	private static void debug(String msg) {
		// Log.i("TPATT", "串口调试:" + msg);
	}

	private static void clean_uart_rec() {
		int i;
		for (i = 0; i < uart_rec_max_len; i++) {
			uart_rec[i] = 0;
		}
		uart_rec_len = 0;
	}

	private static void onDataReceived(byte[] buffer, int size) {
		int i, j = 0;
		int step = 0;

		if ((uart_rec_len + size) > uart_rec_max_len) {
			if (uart_rec_len >= uart_rec_max_len) {
				clean_uart_rec();
			} else {
				size = uart_rec_max_len - uart_rec_len;
			}
		}

		/// 复制到uart_rec 再分析
		for (i = 0; i < size; i++) {
			uart_rec[uart_rec_len] = (char) (buffer[i] & 0x00ff);
			uart_rec_len++;
		}

		pintf_temp = "";
		for (i = 0; i < uart_rec_len; i++) {
			pintf_temp += uart_rec[i];
		}
		debug("uart_rec:" + pintf_temp);

		pintf_temp = String.format("%d", uart_rec_len);
		debug("uart_rec_len:" + pintf_temp);

		if (uart_rec_len >= 10) {
			for (i = 0; i < uart_rec_len; i++) {
				if ((step == 0) && (i + 1 < uart_rec_len) && (uart_rec[i] == 0xd1) && (uart_rec[i + 1] == 0xd1)) {
					j = i;
					step = 1;
					debug("卡号头");
				} else if (step == 1 && (i + 1 < uart_rec_len) && uart_rec[i] == 0xd2 && uart_rec[i + 1] == 0xd2) {
					aIdBuf[0] = (byte) uart_rec[j + 4];
					aIdBuf[1] = (byte) uart_rec[j + 5];
					aIdBuf[2] = (byte) uart_rec[j + 6];
					aIdBuf[3] = (byte) uart_rec[j + 7];
					// aIdBuf[4] = 0;
					clean_uart_rec();
					aIdStep = 2;
					{

						debug("卡号");
						String hexString2 = HexString.encodeHexStr(aIdBuf);
						debug(hexString2);
					}
					break;
				}
			}
		}
		if (uart_rec_len >= uart_rec_max_len) {
			clean_uart_rec();
		}

	}

	public static void testCardid() {
		String str1 = "D1D1E00000010203D2D2";
		byte[] buffer = new byte[20];
		buffer = HexString.hexStringToBytes(str1);
		// byte buffer[] = {0xd1,0xd1,0xe0,0x00,0x10,0x50};

		onDataReceived(buffer, buffer.length);
	}

	// 没有过滤，
	public static String AttChkIdCard() {
		String strcid = null;

		if (aIdStep == 2) {
			// if ( GetCidReversion() )
			// {

			// cid[0] = aIdBuf[3];
			// cid[1] = aIdBuf[2];
			// cid[2] = aIdBuf[1];
			// cid[3] = aIdBuf[0];

			// }
			// else
			{
				cid[0] = aIdBuf[0];
				cid[1] = aIdBuf[1];
				cid[2] = aIdBuf[2];
				cid[3] = aIdBuf[3];

			}

			// IdCarInit();
			aIdStep = 0;
			aIdBuf[0] = 0;
			aIdBuf[1] = 0;
			aIdBuf[2] = 0;
			aIdBuf[3] = 0;
			// aIdBuf[4] = 0;
			strcid = new String();
			strcid = HexString.encodeHexStr(cid);
		}

		return (strcid);
	}

	// 增加过滤，相同卡号连续时只发一次
	public static String AttChkIdCardEx() {
		String strcid = null;

		if (aIdStep == 2) {
			cid[0] = aIdBuf[0];
			cid[1] = aIdBuf[1];
			cid[2] = aIdBuf[2];
			cid[3] = aIdBuf[3];

			aIdStep = 0;
			aIdBuf[0] = 0;
			aIdBuf[1] = 0;
			aIdBuf[2] = 0;
			aIdBuf[3] = 0;

			strcid = new String();
			strcid = HexString.encodeHexStr(cid);

			CidflashTimeout = 0; // 开始计时
			Cidflash = 1;

			if (strcidBak.equals(strcid) == false)// 不相等
			{
				strcidBak = strcid;//// 保存
			} else // 相等不更新
			{
				return null;
			}

		}

		return (strcid);
	}

	public static int bytesToInt(byte[] bytes) {
		int number = bytes[0] & 0xFF;
		number |= ((bytes[1] << 8) & 0xFF00);
		number |= ((bytes[2] << 16) & 0xFF0000);
		number |= ((bytes[3] << 24) & 0xFF000000);
		return number;
	}

	public static long bytesToLong(byte[] b) {
		long l = 0;
		l = b[0];
		l |= ((long) b[1] << 8);
		l |= ((long) b[2] << 16);
		l |= ((long) b[3] << 24);
		l |= ((long) b[4] << 32);
		l |= ((long) b[5] << 40);
		l |= ((long) b[6] << 48);
		l |= ((long) b[7] << 56);
		return l;
	}

	public static String hex2dec(String hex) {
		String dec = new String();
		long num = Long.parseLong(hex, 16);
		dec = Long.toString(num, 10);
		return dec;
	}

	public static String dec2hex(String dec) {
		String hex = new String();
		long num = Long.parseLong(dec, 10);
		hex = Long.toString(num, 16);
		return hex;
	}

	// 增加过滤，相同卡号连续时只发一次
	public static String AttChkIdCardNormal(int Eradix, boolean Erotating) {
		String strcid = null;
		// Log.i("TAPP","AttChkIdCardNormal begin");
		lock.lock();
		try {

			if (aIdStep == 2) {
				if (Erotating == true) {
					cid[0] = aIdBuf[3];
					cid[1] = aIdBuf[2];
					cid[2] = aIdBuf[1];
					cid[3] = aIdBuf[0];
				} else {
					cid[0] = aIdBuf[0];
					cid[1] = aIdBuf[1];
					cid[2] = aIdBuf[2];
					cid[3] = aIdBuf[3];
				}

				aIdStep = 0;
				aIdBuf[0] = 0;
				aIdBuf[1] = 0;
				aIdBuf[2] = 0;
				aIdBuf[3] = 0;

				strcid = new String();
				strcid = HexString.encodeHexStr(cid);

				CidflashTimeout = 0; // 开始计时
				Cidflash = 1;

				if (strcidBak.equals(strcid) == false)// 不相等
				{
					strcidBak = strcid;//// 保存
				} else // 相等不更新
				{

					return null;
				}

				if (Eradix == 0) {
					if (strcid != null) {

						return hex2dec(strcid);
					} else {

						return null;
					}
				} else {

					return (strcid);
				}
			}

			return null;
		} finally {
			lock.unlock();
			// Log.i("TAPP","AttChkIdCardNormal end");
		}

	}

	private static int runtimes = 0;
	private static String outcard = null;
	//private static long outtime = 0;
	private static int swingcardnums = 0;

	private static void timetask() {
		// Log.i("TAPP","timetask begin");
		lock.lock();
		try {

			if (aIdStep == 0) {
				int size;
				try {
					if (mInputStream == null) {

						return;
					}
					for (size = 0; size < uart_rec_max_len; size++) {
						buffer[size] = 0;
					}

					while (true) {
						size = mInputStream.available();

						if (size < 10) {

							break;
						}

						size = mInputStream.read(buffer, 0, 10);
						String hexString2 = null;
						{
							debug("串口接收");
							pintf_temp = String.format("size = %d", size);
							debug(pintf_temp);
							hexString2 = HexString.encodeHexStr(buffer);
							debug(hexString2);
							// Log.i("TPATT","接收卡号0："+hexString2);
						}

						if (outcard == null) {

							outcard = hexString2;
							swingcardnums = 0;
						} else {
							if (outcard.equals(hexString2) == false) {
								outcard = hexString2;
								swingcardnums = 0;
							} else {

								if (swingcardnums <= 25) {
									// Log.i("TPATT","卡号重复，丢弃");
									return;
								} else {
									outcard = hexString2;
									swingcardnums = 0;
								}
							}
						}

						// Log.i("TPATT","卡号被接收");
						onDataReceived(buffer, size);

					}

					runtimes++;
					if (runtimes >= 300) {
						onresumes();
						runtimes = 0;
					}
					swingcardnums++;

				} catch (IOException e) {
					Log.i("TAPP", "串口接收出错："+e.toString());
					onresumes();
					return;
				}
			}
		} finally {
			lock.unlock();
			// Log.i("TAPP","timetask end");
		}

	}

	public static Thread thread = new Thread(new Runnable() {

		public void run() {
			try {

				while (true) {

					if (transThread.isShutdown()) {
						return;
					}
					try {
						timetask();
					} catch (Exception e) {
						// TODO: handle exception
						lock.unlock();
					}


					Thread.sleep(200);

					continue;
				}
			} catch (Exception e) {
				Log.i("TAPP", "transThread.isShutdown出错："+e.toString());
			}

		}
	});

	private static void FlashTimeouttask() {
		// debug("timerFlashTimeouttask 100ms timer");

		if (Cidflash > 0) {
			CidflashTimeout++;

			if (CidflashTimeout > 1) // 1S
			{
				Cidflash = 0; // 清空
				strcidBak = "";
				debug("清空，超时");
			}
		}
	}

	public static void start() {
		if (success == true) {
			return;
		}
		try {
			debug("串口初始化");

			if (mSerialPort == null) {
				if (android.os.Build.MODEL.startsWith("rk")||android.os.Build.MODEL.startsWith("TPS")) {

					mSerialPort = new SerialPort(new File("/dev/ttyS3"), 4800, 0);

				}

				else {
					mSerialPort = new SerialPort(new File("/dev/ttyS2"), 4800, 0);
				}
			}
			if (mOutputStream == null) {
				mOutputStream = mSerialPort.getOutputStream();
			}

			if (mInputStream == null) {
				mInputStream = mSerialPort.getInputStream();
			}

			///// 定时器1
			// if(timer == null)
			// {
			// timer = new Timer();
			// }

			// if(task == null)
			// {
			// task = new TimerTask()
			// {
			//
			// @Override
			// public void run()
			// {
			//
			//
			// timetask();
			//
			// }
			// };
			// }
			//
			// if(task != null && timer != null )
			// {
			// timer.schedule(task, 100, 200);
			// }
			transThread = Executors.newSingleThreadExecutor();
			transPending = transThread.submit(thread);
			// thread.start();
			///// 刷新定时器2
			if (timerFlashTimeouttask == null) {
				timerFlashTimeouttask = new TimerTask() {

					@Override
					public void run() {
						FlashTimeouttask();
					}
				};
			}
			if (timerFlashTimeout == null) {
				timerFlashTimeout = new Timer();
			}

			if (timerFlashTimeouttask != null && timerFlashTimeout != null) {
				timerFlashTimeout.schedule(timerFlashTimeouttask, 100, 500);
			}

			success = true;

			aIdStep = 0;
			Cidflash = 0;
			CidflashTimeout = 0;
			strcidBak = "";

			clean_uart_rec();
		} catch (SecurityException e) {
			debug("打开失败");
		} catch (IOException e) {
			debug("打开失败");
		}

	}

	public static void stop() throws InterruptedException {
		if (success == false) {
			return;
		}
		success = false;

		if (timer != null) {
			timer.cancel();
			task.cancel();
			task = null;
			timer = null;
		}

		transThread.shutdown();
		if (timerFlashTimeout != null) {
			timerFlashTimeout.cancel();
			timerFlashTimeouttask.cancel();
			timerFlashTimeout = null;
			timerFlashTimeouttask = null;
		}
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
			mOutputStream = null;
			mInputStream = null;

			debug("关闭");
		}

		debug("串口关闭");

	}

	public static void onresumes() {
		try {
			if (mSerialPort != null) {
				mSerialPort.close();
				mSerialPort = null;
				mOutputStream = null;
				mInputStream = null;
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				debug("关闭");
			}

			if (mSerialPort == null) {
				if (android.os.Build.MODEL.startsWith("rk")||android.os.Build.MODEL.startsWith("TPS")) {

					mSerialPort = new SerialPort(new File("/dev/ttyS3"), 4800, 0);

				} else {
					mSerialPort = new SerialPort(new File("/dev/ttyS2"), 4800, 0);
				}
				// mSerialPort = new SerialPort(new File("/dev/ttyS1"), 4800,
				// 0);

			}
			if (mOutputStream == null) {
				mOutputStream = mSerialPort.getOutputStream();
			}

			if (mInputStream == null) {
				mInputStream = mSerialPort.getInputStream();
			}

		} catch (SecurityException e) {
			Log.i("TAPP", "onresumes出错："+e.toString());
			onresumes();
		} catch (IOException e) {
			Log.i("TAPP", "onresumes出错："+e.toString());
			onresumes();
		}

	}

}
