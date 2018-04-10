package com.att.act;

import android.content.SharedPreferences;
import android.util.Log;

import com.att.SettingPara;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * C/S架构的客户端对象，持有该对象，可以随时向服务端发送消息。
 */
public class Client {

	// private static String msg = null;
	private static SettingPara settingPara = new SettingPara();
	private static SharedPreferences.Editor se = null;
	private static SharedPreferences sp = null;
	@SuppressWarnings("unused")
	private static SharedPreferences.Editor se1 = null;
	private static SharedPreferences sp1 = null;
	// private static Handler handler=new Handler();

	/**
	 * 处理服务端发回的对象，可实现该接口。
	 */
	public static interface ObjectAction {
		void doAction(Object obj, Client client);
	}

	public static final class DefaultObjectAction implements ObjectAction {
		public void doAction(Object obj, Client client) {
			System.out.println("处理：\t" + obj.toString());
		}
	}

	public static void main(SharedPreferences.Editor ses, SharedPreferences sps, SharedPreferences.Editor ses1,
							SharedPreferences sps1) throws UnknownHostException, IOException {
		// msg=args;
		sp = sps;
		se = ses;
		sp1 = sps1;
		se1 = ses1;
		String ttp = sp1.getString("port", null);
		Log.i("http", "ttp:" + ttp);
		if (ttp != null && ttp.length() > 0) {
			String[] tp = ttp.split(":");
			if (tp.length > 0) {
				String serverIp = tp[0];
				int port = Integer.valueOf(tp[1]).intValue();
				Log.i("http", serverIp + port);
				if (running) {
					return;
				}
				Client client = new Client(serverIp, port);
				client.start();
			}

		}

	}

	private String serverIp;
	private int port;
	private Socket socket;
	private static boolean running = false;
	@SuppressWarnings("unused")
	private long lastSendTime;
	@SuppressWarnings("rawtypes")
	private ConcurrentHashMap<Class, ObjectAction> actionMapping = new ConcurrentHashMap<Class, ObjectAction>();

	public Client(String serverIp, int port) {

		this.serverIp = serverIp;
		this.port = port;
	}

	public boolean getrun()

	{

		return running;

	}

	public void start() throws UnknownHostException, IOException {

		if (running) {
			return;
		}
		socket = new Socket(serverIp, port);
		System.out.println("本地端口：" + socket.getLocalPort());
		lastSendTime = System.currentTimeMillis();
		running = true;
		// new Thread(new KeepAliveWatchDog()).start();
		thread.start();
		// new Thread(new ReceiveWatchDog()).start();
		// handler.postDelayed(runnable, 30000);
	}

	public void stop() {
		if (running) {
			running = false;
			try {
				socket.close();

			} catch (IOException e) {

				e.printStackTrace();
			}
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
		OutputStream os = socket.getOutputStream();
		String message = obj.toString();
		os.write(message.getBytes());
		os.flush();
		// ObjectOutputStream oos = new
		// ObjectOutputStream(socket.getOutputStream());
		// oos.writeObject(obj);
		System.out.println("发送：\t" + obj);
		// oos.flush();
	}

	Thread thread = new Thread(new Runnable() {

		public void run() {

			while (running) {

				try {

					String msg = send();
					Client.this.sendObject(msg);
					lastSendTime = System.currentTimeMillis();
				} catch (IOException e) {
					e.printStackTrace();
					Client.this.stop();
				} finally {
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						e.printStackTrace();

					}
				}

			}

		}
	});

	class KeepAliveWatchDog implements Runnable {
		long checkDelay = 10;
		long keepAliveDelay = 30000;

		public void run() {
			while (running) {
				// if(System.currentTimeMillis()-lastSendTime>keepAliveDelay){
				try {

					String msg = send();
					Client.this.sendObject(msg);
					lastSendTime = System.currentTimeMillis();
				} catch (IOException e) {
					e.printStackTrace();
					// Client.this.stop();
					// try {
					// socket.close();
					// Client.this.start();
					// lastSendTime = System.currentTimeMillis();
					// } catch (IOException e1) {
					//
					// e1.printStackTrace();
					lastSendTime = System.currentTimeMillis();
					// }
				} finally {
					try {
						Thread.sleep(keepAliveDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
						// Client.this.stop();
					}
				}

				// }else{

				// }
			}
		}
	}

	private synchronized String send() {
		String liunum = "";
		int vernum = sp.getInt("rivernum", 0);
		if (vernum >= 0 && vernum < 65536) {

			liunum = HeartBeats.getrivercount(vernum);
			vernum++;
			if (vernum > 65535) {
				vernum = 0;
			}
			se.putInt("rivernum", vernum);
			se.commit();
		}

		String deviceid = settingPara.getDevice_id();
		deviceid = deviceid + "                  ".substring(0, 18 - deviceid.length());

		String lens = "0002";

		return "TPJ" + liunum + deviceid + lens + "10" + "61";

	}

	class ReceiveWatchDog implements Runnable {
		public void run() {
			while (running) {
				try {
					// InputStream in = socket.getInputStream();
					// if(in.available()>0){
					// // System.out.println("接收：\t"+in.toString());
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
					////// ObjectInputStream ois = new ObjectInputStream(in);
					////// Object obj = ois.readObject();
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
					//// oa.doAction(obj, Client.this);
					// }else{
					// Thread.sleep(10);
					// }

					// Thread.sleep(10000);

				} catch (Exception e) {
					e.printStackTrace();
					Client.this.stop();
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
}
