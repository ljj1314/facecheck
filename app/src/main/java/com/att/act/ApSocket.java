package com.att.act;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * C/S架构的客户端对象，持有该对象，可以随时向服务端发送消息。
 */
public class ApSocket {

	// private static String msg = null;
	// private static SettingPara settingPara = new SettingPara();
	// private static SharedPreferences.Editor se = null;
	// private static SharedPreferences sp = null;
	// private static SharedPreferences.Editor se1 = null;
	// private static SharedPreferences sp1 = null;
	// private static Handler handler=new Handler();
	// private static String httpurl = null;
	// private static int ports = 900;
	@SuppressWarnings("unused")
	private Context context;
	PrintWriter out;
	BufferedReader in;
	InputStream sin;
	private OnDataReceiveListeners onDataReceiveListener = null;
	// private boolean isStop = false;

	public interface OnDataReceiveListeners {
		public void onDataReceives(String buffer);
	}

	public void setOnDataReceiveListeners(OnDataReceiveListeners dataReceiveListener) {
		onDataReceiveListener = dataReceiveListener;
	}

	/**
	 * 处理服务端发回的对象，可实现该接口。
	 */
	public static interface ObjectAction {
		void doAction(Object obj, ApSocket client);
	}

	public static final class DefaultObjectAction implements ObjectAction {
		public void doAction(Object obj, ApSocket client) {
			System.out.println("处理：\t" + obj.toString());
		}
	}

	public static void main() throws UnknownHostException, IOException {

		// ApSocket client = new ApSocket();
		// client.start();

	}

	private String serverIp = "115.29.200.219";
	private int port = 80;
	private Socket socket;
	private static boolean running = false;
	@SuppressWarnings("unused")
	private long lastSendTime;
	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<Class, ObjectAction> actionMapping = new ConcurrentHashMap<Class, ObjectAction>();
	Selector mSelector = null;

	public ApSocket(Context context) {

		this.context = context;
		// this.serverIp=serverIp;this.port=port;
		// main();

	}

	public boolean getrun()

	{

		return running;

	}

	public void start() throws UnknownHostException, IOException {

		if (running) {
			return;
		}
		socket = new Socket();
		SocketAddress socAddress = new InetSocketAddress(serverIp, port);
		socket.connect(socAddress, 4000);
		Log.i("tappo", "初始化soket成功");
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		// sin=socket.getInputStream();
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		System.out.println("本地端口：" + socket.getLocalPort());
		lastSendTime = System.currentTimeMillis();
		running = true;
		// new Thread(new KeepAliveWatchDog()).start();

		new Thread(new ReceiveWatchDog()).start();
		// handler.postDelayed(runnable, 30000);
	}

	public void stop() {
		if (running)
			running = false;

		try {
			socket.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 添加接收对象的处理对象。
	 *
	 * @param cls
	 *            待处理的对象，其所属的类。
	 * @param action
	 *            处理过程对象。
	 */
	public void addActionMap(Class<Object> cls, ObjectAction action) {
		actionMapping.put(cls, action);
	}

	public void sendObject(Object obj) throws IOException {

		// OutputStream os = socket.getOutputStream();
		// String message = obj.toString() ;
		// os.write(message.getBytes());
		// os.flush();
		out.println(obj.toString() + "\r\n");
		out.flush();
		// ObjectOutputStream oos = new
		// ObjectOutputStream(socket.getOutputStream());
		// oos.writeObject(obj);
		// System.out.println("发送：\t"+obj);
		Log.i("tappo", "发送" + obj.toString());
		// oos.flush();
	}

	class ReceiveWatchDog implements Runnable {
		@SuppressWarnings("unused")
		public void run() {
			while (running) {
				try {
					// Log.i("tappo", "查询数据");
					String line = "";
					String msg = "";
					// int sum=0;
					// while (sum==0) {
					//
					// sum=in.read();
					//
					// }
					//
					// char[] mn=new char[sum];
					// int readCount = 0; // 已经成功读取的字节的个数
					// while (readCount < sum) {
					// readCount += in.read(mn, readCount, sum - readCount);
					// }
					//
					// Log.i("tappo", "接受数据成功1"+mn.toString());
					// int size=in.read();
					// InputStream in = socket.getInputStream();
					// if (socket.isInputShutdown()) {

					int r = -1;
					// <Byte> l = new LinkedList<Byte>();
					while ((r = in.read()) > 0) {

						Log.i("tappo", "接受数据成功" + (char) r);
						msg += (char) r;
						Log.i("tappo", msg);
						// l.add(Byte.valueOf((byte) r));

					}

					Log.i("tappo", "接受完毕：" + msg);
					// while ((line= in.readLine()) != null){
					// msg+=line+"\n";
					// // System.out.println("接收：\t"+in.toString());
					// Log.i("tappo", "接受数据成功"+in.toString());
					//// BufferedReader br = new BufferedReader(new
					// InputStreamReader(in));
					//// String receiveMsg = br.readLine();
					//
					//
					//// byte len[] = new byte[1024];
					//// int count = in.read(len);
					//// byte[] temp = new byte[count];
					//// for (int i = 0; i < count; i++) {
					//// temp[i] = len[i];
					//// }
					////
					//// ObjectInputStream ois = new ObjectInputStream(in);
					//// Object obj = ois.readObject();
					// Log.i("tappo","line:"+ line);
					// Log.i("tappo", msg);
					// if ("0".equals(line)) {
					// break;
					// }
					//
					// // }
					//
					//// // System.out.println("接收：\t"+temp.toString());
					//// String mm=bytesToHexString(temp);
					//// // System.out.println("接收：\t"+mm);
					//// if
					// (mm.equals("4A5450303033443632303130303034202020202020202020203030303231303631"))
					// {
					////
					//// }
					//// ObjectAction oa = actionMapping.get(obj.getClass());
					//// oa = oa==null?new DefaultObjectAction():oa;
					//// oa.doAction(obj, ApSocket.this);
					// }
					// else{
					// Thread.sleep(10);
					// }
					if (null != onDataReceiveListener) {

						onDataReceiveListener.onDataReceives(msg);
						// socket.close();
						stop();
					}
					// Thread.sleep(100);

				} catch (Exception e) {
					e.printStackTrace();
					ApSocket.this.stop();
					// try {
					// socket.close();
					// Client.this.start();
					// } catch (IOException e1) {
					//
					// e1.printStackTrace();
					// }
				}
			}
		}
	}

	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	public String convertHexToString(String hex) {

		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		// 49204c6f7665204a617661 split into two characters 49, 20, 4c...
		for (int i = 0; i < hex.length() - 1; i += 2) {

			// grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			// convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			// convert the decimal to character
			sb.append((char) decimal);

			temp.append(decimal);
		}

		return sb.toString();
	}

	public void close() {
		try {
			out.close();
			in.close();
			sin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
