package com.serport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android_serialport_api.SerialPort;

/**
 * 串口操作类
 */
public class SerialPortUtil {
	// private String TAG = SerialPortUtil.class.getSimpleName();
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;

	private String path = "/dev/ttyS3"; // 32寸大屏机
	private int baudrate = 115200; // 32寸大屏机

	// private String path = "/dev/ttyS5"; //21.5寸大屏机
	// private int baudrate = 9600; //21.5寸大屏机

	private static SerialPortUtil portUtil;
	private OnDataReceiveListener onDataReceiveListener = null;
	private boolean isStop = false;
	private boolean isfull = false;
	private int reallen = 0;
	private byte[] realtem = null;
	// private int position = 0;

	// private int Maxtime = 200;
	// private int Maxsize = 2048;

	private int pos = 0;

	public interface OnDataReceiveListener {
		public void onDataReceive(byte[] buffer, int size);
	}

	public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
		onDataReceiveListener = dataReceiveListener;
	}

	public static SerialPortUtil getInstance() {
		if (null == portUtil) {
			portUtil = new SerialPortUtil();
			portUtil.onCreate();
		}
		return portUtil;
	}

	/**
	 * 初始化串口信息
	 */
	public void onCreate() {
		try {
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			mReadThread = new ReadThread();
			// mReadThread.setOSPriority(Process.THREAD_PRIORITY_LOWEST); // 19
			mReadThread.setPriority(Thread.MAX_PRIORITY); // 10
			isStop = false;
			mReadThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// initBle();
	}

	/**
	 * 发送指令到串口
	 *
	 * @param cmd
	 * @return
	 */
	public boolean sendCmd() {// 查询蓝牙AP记录(0x06)
		boolean result = true;
		byte[] mBuffer = new byte[8];

		mBuffer[0] = 0x02;
		mBuffer[1] = 0x00;
		mBuffer[2] = 0x04;
		mBuffer[3] = 0x01;
		mBuffer[4] = 0x06;// 查询蓝牙AP记录
		mBuffer[5] = 0x55;// 报文体
		mBuffer[6] = 0x55;// 报文体

		int num = 0;

		for (int i = 0; i < 7; i++) {
			num ^= mBuffer[i];
		}
		mBuffer[7] = (byte) num;

		Log.i("AP", "查询蓝牙AP记录:" +  DataUtils.bytesToHexString(mBuffer));
		// mBuffer[7] = (byte)
		// (mBuffer[0]^0+mBuffer[2]^0+mBuffer[3]^0+mBuffer[4]^0+mBuffer[5]^0+mBuffer[6]^0);
		// Log.i("AP", "查询蓝牙AP记录:"
		// + (mBuffer[0] ^ 0 + mBuffer[2] ^ 0 + mBuffer[3] ^ 0 + mBuffer[4] ^ 0
		// + mBuffer[5] ^ 0 + mBuffer[6] ^ 0)
		// + "....num" + num);

		// 注意：我得项目中需要在每次发送后面加\r\n，大家根据项目项目做修改，也可以去掉，直接发送mBuffer
		try {
			if (mOutputStream != null) {
				mOutputStream.write(mBuffer);
			} else {
				result = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	/**
	 * 发送指令到串口
	 *
	 * @param cmd
	 * @return
	 */
	public boolean sendCmds() {
		boolean result = true;
		byte[] mBuffer = new byte[7];

		mBuffer[0] = 0x02;
		mBuffer[1] = 0x00;
		mBuffer[2] = 0x04;
		mBuffer[3] = 0x01;
		mBuffer[4] = 0x10;
		mBuffer[5] = (byte) 0xff;
		// mBuffer[6] = 0x55;

		int num = 0;

		for (int i = 0; i < 7; i++) {
			num ^= mBuffer[i];
		}
		mBuffer[6] = (byte) num;

		// mBuffer[7] = (byte)
		// (mBuffer[0]^0+mBuffer[2]^0+mBuffer[3]^0+mBuffer[4]^0+mBuffer[5]^0+mBuffer[6]^0);
		Log.i("AP", "" + (mBuffer[0] ^ 0 + mBuffer[2] ^ 0 + mBuffer[3] ^ 0 + mBuffer[4] ^ 0 + mBuffer[5] ^ 0)
				+ "....num" + num);

		// 注意：我得项目中需要在每次发送后面加\r\n，大家根据项目项目做修改，也可以去掉，直接发送mBuffer
		try {
			if (mOutputStream != null) {
				mOutputStream.write(mBuffer);
			} else {
				result = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public boolean sendBuffer(byte[] mBuffer) {
		boolean result = true;
		// String tail = "\r\n";
		// byte[] mBuffer = mk.getBytes();
		// byte[] mBufferTemp = new byte[mBuffer.length];
		// System.arraycopy(mBuffer, 0, mBufferTemp, 0, mBuffer.length);
		// System.arraycopy(tailBuffer, 0, mBufferTemp, mBuffer.length,
		// tailBuffer.length);
		// 注意：我得项目中需要在每次发送后面加\r\n，大家根据项目项目做修改，也可以去掉，直接发送mBuffer
		try {
			if (mOutputStream != null) {
				mOutputStream.write(mBuffer);
			} else {
				result = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	Handler imfo = new Handler() {

		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
				case 0:

					byte[] tt = msg.getData().getByteArray("data");
					int size = msg.getData().getInt("size");
					Log.e("AP", "receive:" + "...size" + size);
					if (isfull == false) {

						if (tt[0] == 0x02) {

							reallen = DataUtils.bytesToint2(tt[1], tt[2]);
							if (reallen > size) {

								isfull = true;
								realtem = new byte[reallen + 4];
								// byte[] data=new byte[size];
								System.arraycopy(tt, 0, realtem, 0, size);
								pos = size;

							} else {

								isfull = false;
								pos = 0;
								byte[] data = new byte[size];
								System.arraycopy(tt, 0, data, 0, size);
								Log.e("AP", DataUtils.bytesToHexString(data));
								// measure(data);
							}

						}

					} else {
						try {
							System.arraycopy(tt, 0, realtem, pos, size);
							pos += size;
							Log.e("AP", "size" + pos + "..len" + reallen);
							if (pos > reallen) {
								isfull = false;
								reallen = 0;
								pos = 0;

								// measure(realtem);

							}
						} catch (Exception e) {
						}

					}

					break;

				case 1:

					break;

				default:
					break;
			}

		}

	};

	@SuppressWarnings("unused")
	private class ReadThreads extends Thread {

		@Override
		public void run() {
			super.run();
			while (!isStop) {
				// Log.i("AP", "接受等待中");
				int size;
				int conu;
				try {
					if (mInputStream == null)
						return;
					// byte[] buffer = new byte[10240];
					// size = mInputStream.read(buffer);
					// int count = 0;
					// while (count == 0) {
					// count = mInputStream.available();
					// }
					// // int counts=DataUtils.bytesToint2(buffer[1],
					// buffer[2]);
					// byte[] b = new byte[count];
					// size=mInputStream.read(b);
					// // mInputStream.re
					// if (size==0) {
					// return;
					// }
					//
					//
					//
					//
					// // mInputStream.read(buffer);
					// Log.i("AP","length is:"+size+" count:"+count);
					// Log.i("AP", DataUtils.bytesToHexString(b));

					// size=mInputStream.available();

					// byte[] bb=new byte[64];
					// conu=mInputStream.read(bb);
					// Log.i("AP", "shoudao");
					// parse count
					byte[] bi = new byte[1];
					//
					// //new buffer by count
					//
					//
					//
					mInputStream.read(bi);
					if (bi[0] != 0x02) {
						continue;
					}
					//

					while (true) {

						if (isStop) {
							break;
						}

						continue;
					}

					byte[] bi1 = new byte[2];
					mInputStream.read(bi1);

					reallen = DataUtils.bytesToint2(bi1[0], bi1[1]);
					// Log.i("AP","data:"+
					// DataUtils.bytesToHexString(bi)+".reallen is:"+reallen);
					byte[] realtem = new byte[reallen + 4];
					int readCount = 0; // 已经成功读取的字节的个数
					if (mInputStream != null) {
						while (readCount < (reallen + 1)) {
							Log.i("AP", "readCount is:" + readCount);
							readCount += mInputStream.read(realtem, readCount + 3, (reallen + 1) - readCount);
						}
					}

					System.arraycopy(bi, 0, realtem, 0, bi.length);
					System.arraycopy(bi1, 0, realtem, 1, bi1.length);
					Log.i("AP", "reallen is:" + reallen);
					// position=0;
					// if (reallen>size) {
					// isfull=true;
					//
					// realtem=new byte[reallen+4];
					// System.arraycopy(b, 0, realtem, 0,b.length);
					// position=b.length;
					// }else {
					if (null != onDataReceiveListener) {
						onDataReceiveListener.onDataReceive(realtem, reallen + 4);
					}
					// }

					// else {
					//
					// System.arraycopy(b, 0, realtem, position,size);
					//
					// position=position+size;
					// Log.i("AP","position is:"+position);
					// if (position>reallen) {
					//
					// isfull=false;
					// if (null != onDataReceiveListener) {
					// onDataReceiveListener.onDataReceive(b, position);
					// }
					//
					//
					// }
					//
					//
					// }

					// while (true) {
					// if (size > (count-1)) {
					// break;
					// }
					//
					// size+=mInputStream.read(buffer);
					//
					// if (null != onDataReceiveListener) {
					// onDataReceiveListener.onDataReceive(buffer, size);
					// }
					// continue;
					// }
					// if (size > (count-1)) {
					//// if(MyLog.isDyeLevel()){
					//// MyLog.log(TAG, MyLog.DYE_LOG_LEVEL, "length
					// is:"+size+",data is:"+new String(buffer, 0, size));
					//// }
					// Log.i("AP","length is:"+size+",data is:"+new
					// String(buffer, 0, size));
					//// String mmString=DataUtils.bytesToHexString(buffer);
					//// android.util.Log.i("AP","Log......."+
					// mmString+"....."+onDataReceiveListener);

					// }
					// Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("AP", "错误信息" + e.toString());
					continue;
				}
			}
		}
	}

	/**
	 * 关闭串口
	 */
	public void closeSerialPort() {
		// sendShellCommond1();
		isStop = true;
		if (mReadThread != null) {
			mReadThread.interrupt();
		}
		if (mSerialPort != null) {
			mSerialPort.close();
		}
	}

	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();

			// 定义一个包的最大长度
			int maxLength = 2048;
			byte[] buffer = new byte[maxLength];
			// 每次收到实际长度
			int available = 0;
			// 当前已经收到包的总长度
			int currentLength = 0;
			// 协议头长度4个字节（开始符1，类型1，长度2）
			int headerLength = 4;
			Log.i("AP", "线程已启动，等待接收蓝牙AP数据");
			while (!isStop) {

				try {

					try {

						if (mInputStream == null) {
							continue;
						}

						available = mInputStream.available();
						if (available > 0) {
							// 防止超出数组最大长度导致溢出
							if (available > maxLength - currentLength) {
								available = maxLength - currentLength;
							}
							mInputStream.read(buffer, currentLength, available);
							currentLength += available;
						} else if (available == 0) {

							Thread.sleep(100);

						}

					} catch (Exception e) {
						e.printStackTrace();
					}

					// Log.i("AP", "接受完成"+currentLength);
					int cursor = 0;
					// 如果当前收到包大于头的长度，则解析当前包
					while (currentLength >= headerLength) {
						// 取到头部第一个字节
						if (buffer[0] != 0x02) {
							currentLength = 0;

							break;
						}

						int contentLenght = DataUtils.bytesToint2(buffer[1], buffer[2]);
						;
						// 如果内容包的长度大于最大内容长度或者小于等于0，则说明这个包有问题，丢弃
						if (contentLenght <= 0 || contentLenght > maxLength - 4) {
							currentLength = 0;
							Log.i("AP", "丢弃");
							break;
						}
						// 如果当前获取到长度小于整个包的长度，则跳出循环等待继续接收数据
						int factPackLen = contentLenght + 4;
						if (currentLength < contentLenght + 4) {
							break;
						}
						//Log.i("AP", "解析完成");
						// 一个完整包即产生
						// proceOnePacket(buffer,i,factPackLen);
						onDataReceiveListener.onDataReceive(buffer, factPackLen);
						currentLength -= factPackLen;
						cursor += factPackLen;
					}
					Thread.sleep(100);
					// 残留字节移到缓冲区首
					if (currentLength > 0 && cursor > 0) {
						System.arraycopy(buffer, cursor, buffer, 0, currentLength);
					}
				} catch (Exception e) {
					Log.e("AP", "蓝牙AP信息异常" + e.toString());
				}
			}
		}
	}

	/**
	 * 获取协议内容长度
	 *
	 * @param header
	 * @return
	 */
	public int parseLen(byte buffer[], int index, int headerLength) {

		// if (buffer.length - index < headerLength) { return 0; }
		byte a = buffer[index + 2];
		byte b = buffer[index + 3];
		int rlt = 0;
		if (((a >> 7) & 0x1) == 0x1) {
			rlt = (((a & 0x7f) << 8) | b);
		} else {
			char[] tmp = new char[2];
			tmp[0] = (char) a;
			tmp[1] = (char) b;
			String s = new String(tmp, 0, 2);
			rlt = Integer.parseInt(s, 16);
		}

		return rlt;
	}

}
