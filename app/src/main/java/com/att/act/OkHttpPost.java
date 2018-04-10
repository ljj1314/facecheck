package com.att.act;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.att.AppBaseFun;
import com.att.AttPlatformProto;
import com.att.DBOpenHelper;
import com.att.HttpApp;
import com.att.MainIdleActivity;
import com.att.SwingCardAttActivity;
import com.att.DBOpenHelper.AttInfo;
import com.att.SettingPara;
import com.att.server.HttpConnect;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.persistent.FileRecorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class OkHttpPost {

	private Context context;

	public static final String TAG = "qiniu";
	public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
	public static String JOVISION_ATTEND_URL = "http://xwapi.jovision.com/";//
	public static String JOVISION_PHOTO_URL = "http://7xr5vb.com1.z0.glb.clouddn.com/";//
	public static String JOVISION_URL = "http://manager.jovision.com/anon/qiniu/imgUpload.do?key=";//

	OkHttpClient okHttpClient = new OkHttpClient();

	public AppBaseFun appBaseFun = new AppBaseFun();

	// String ALBUM_PATH;

	public abstract void onSuccess(String resposeBody);

	public abstract void onFailure(String exceptionMsg);

	boolean isAddAttendInfo = false;

	private HttpApp httpApp = new HttpApp();

	public static String token = null;
	private String prefix = null;
	private String host = null;
	private int tryTime = 0;
	private SettingPara settingPara = null;
	private AttPlatformProto attPlatformProto=new AttPlatformProto();
	private int loadupcount=0;

	public OkHttpPost(Context _context) {
		context = _context;
	}

	public void execute(final String json, final String post) {
		new Thread(new Runnable() {
			public void run() {
				RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, json);
				Log.i(TAG, post + " request:" + json);
				Request request = new Request.Builder().url(JOVISION_ATTEND_URL + post).post(requestBody).build();
				try {
					okhttp3.Response response = okHttpClient.newCall(request).execute();
					if (response.isSuccessful()) {
						String json = response.body().string();
						Log.i(TAG, post + " response:" + json);
						onSuccess(json);
					} else {
						onFailure(response.toString());
					}
				} catch (IOException e) {
					onFailure(e.toString());
				}
			}
		}).start();
	}

	public void onResponse(int code) {
		if (code == 200) {
			onSuccess("��������ɹ���");
		} else {
			onFailure("��������ʧ�ܣ�������룺" + code);
		}
	}

	// ��ȡ���ڻ���ʾͼƬ��ַ
	public void getAttendDisplay(String no, final String albumPath) {
		try {
			JSONObject attendInfo = new JSONObject();
			attendInfo.put("no", no);
			final String json = attendInfo.toString();
			OkHttpPost request = new OkHttpPost(context) {
				@SuppressWarnings("unused")
				@Override
				public void onSuccess(String resposeBody) {
					try {
						JSONTokener jsonParser = new JSONTokener(resposeBody);
						JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
						int status_code = jsonObject.getInt("status_code");// ����״̬
						// ��200-�ɹ�/400-ʧ�ܣ�
						int code = jsonObject.getInt("code");// ������ 0
						String message = jsonObject.getString("message");// ������Ϣ
						Log.i(TAG, status_code + " " + code + " " + message);
						JSONObject result = jsonObject.getJSONObject("result");
						String kid = result.getString("kid");// �����׶�԰ID
						JSONArray pics = result.getJSONArray("pics");// ����ʾͼƬ���ص�ַ����
						File file = new File(albumPath);
						if (file.exists() && file.isDirectory()) {
							if (file.list().length > 0) {
								File[] playPhotofiles = file.listFiles();// ��ȡͼƬ
								if (playPhotofiles.length > 0) {
									for (int i = 0; i < playPhotofiles.length; i++) {
										String playPhotoName = playPhotofiles[i].getName();
										boolean isIn = false;
										for (int j = 0; j < pics.length(); j++) {
											String picUrl = pics.getString(j);
											String picName = picUrl.substring(picUrl.lastIndexOf("/") + 1);
											if (playPhotoName.equals(picName)) {
												isIn = true;
												// Log.i(TAG,
												// "ͼƬ������ɾ��"+playPhotoName);
												break;
											}
										}
										if (!isIn) {
											boolean s = playPhotofiles[i].delete();
											Log.i(TAG, "�h��" + (s ? "�ɹ�" : "ʧ��") + playPhotoName);
										}
									}

								}
							} else {
								Log.v("TPATT", "����ͼƬΪ��0");
							}
						} else {
							file.mkdirs();
						}
						if (pics.length() > 0) {
							for (int i = 0; i < pics.length(); i++) {
								String picUrl = pics.getString(i);
								String fileName = picUrl.substring(picUrl.lastIndexOf("/") + 1);
								if (new File(albumPath + fileName).exists()) {
									Log.i(TAG, "ͼƬ�Ѵ��ڲ�����" + albumPath + fileName);
								} else {
									boolean s = false;
									while (!s) {
										s = loadImage(picUrl, albumPath, fileName);
									}
									Log.i(TAG, "����" + (s ? "�ɹ�" : "ʧ��") + picUrl);
								}
							}
							OkHttpPost.this.onSuccess(json);
						} else {
							OkHttpPost.this.onFailure(json);
						}
					} catch (JSONException ex) {
						OkHttpPost.this.onFailure(ex.toString());
					}
				}

				@Override
				public void onFailure(String exceptionMsg) {
					OkHttpPost.this.onFailure(exceptionMsg.toString());
				}
			};
			request.execute(json, "attend/getAttendDisplay");
		} catch (JSONException ex) {
			throw new RuntimeException(ex);
		}
	}

	private boolean loadImage(final String url, String albumPath, String fileName) {
		OkHttpClient client = new OkHttpClient();
		try {
			Request request = new Request.Builder().url(url).build();
			Response response = client.newCall(request).execute();
			InputStream inStream = response.body().byteStream();
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(inStream);
				if (bitmap != null) {
					saveFile(bitmap, fileName, albumPath);
					return true;
				}
			} catch (OutOfMemoryError e) {
				Log.i(TAG, e.toString());
			}
			return false;
		} catch (IOException e) {
			Log.i(TAG, e.toString());
			return false;
		}
	}

	public void saveFile(Bitmap bm, String fileName, String albumPath) throws IOException {
		if (bm != null) {
			File dirFile = new File(albumPath);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
			File myCaptureFile = new File(albumPath + fileName);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
			bos.close();
		}
	}

	public int uploadAttend(SettingPara sp, DBOpenHelper sqlTpatt, String platformID,String att,String status) {

		this.settingPara = sp;

		boolean ismobile = false;
		if (appBaseFun.isMobileAvailable(context) && settingPara.isTake_internet()) {
			ismobile = true;
		} else {
			ismobile = false;
		}


		if (appBaseFun.isWifiAvailable(context) || ismobile || HttpConnect.isNetworkAvailable(context)) {
			for (int i = 0; i < 1; i++) {
				AttInfo attInfo = sqlTpatt.findUploadAttInfo();
				if (attInfo != null) {
					// ����ʱ��
					String attdtime = attInfo.getDtime();
					byte[] midbytes = attdtime.getBytes();
					String cardtime = new String(midbytes, 0, 4) + "-" + new String(midbytes, 4, 2) + "-"
							+ new String(midbytes, 6, 2) + " " + new String(midbytes, 8, 2) + ":"
							+ new String(midbytes, 10, 2) + ":" + new String(midbytes, 12, 2);

					// ����У
					String cardtype = attInfo.getStatus();

					// ����
					String cardid = attInfo.getCardId();
					// ������ͼƬ
					String fileName = attInfo.getPhoto() + ".jpg";
					// String fileName = "201609231918282195736306.jpg";
					String photoPath = appBaseFun.getPhoneCardPath() + "/tpatt/AttPhoto/" + fileName;

					// �ϱ�ƽ̨
					// String platformID = SettingPara.getPlatformID();
					if (platformID.equals("8")) {// ��ά
						uploadZWAttend(platformID, cardid, attdtime, cardtime, photoPath);
					} else {
						String platform = null;
						if (platformID.equals("1")||settingPara.getAtt_pic_url().equals("http://172.16.255.78:6666/openAPI/CreditCardPhotos.ashx")) {// �ͱ���
							platform = "hbb";
						} else if (platformID.equals("4")) {// ��ؽ���
							platform = "ddjy";
						} else if (platformID.equals("5")) {// ����ͯѧ
							platform = "jktx";
						} else if (platformID.equals("7")) {//������ѧͨ
							platform = "fjyxt";
						}
						else {
							platform = "jktx";
							//	OkHttpPost.this.onFailure("�ϱ�����:ƽ̨��֧���ƴ洢");
						}
						if (platform != null) {

							int upload=	uploadWWAttend(platformID, platform, fileName, photoPath, attInfo,status);

							return upload;
						}
					}
				} else {
					return 0;
				}
			}
		} else {
			Log.i("tappo", "�ϱ�����:δ����������");
			//	OkHttpPost.this.onFailure("�ϱ�����:δ����������");
			return 0;
		}
		return 0;
	}

	public void uploadAttendpic(SettingPara sp, DBOpenHelper sqlTpatt, String platformID) {

		this.settingPara = sp;

		boolean ismobile = false;
		if (appBaseFun.isMobileAvailable(context) && settingPara.isTake_internet()) {
			ismobile = true;
		} else {
			ismobile = false;
		}


		if (appBaseFun.isWifiAvailable(context) || ismobile || HttpConnect.isNetworkAvailable(context)) {
			//for (int i = 0; i < 1; i++) {
			AttInfo attInfo = sqlTpatt.findUploadAttPhotoInfo();
			if (attInfo != null) {
				// ����ʱ��
				//	AttInfo attInfo = sqlTpatt.findUploadAttPhotoInfo();	

				String attdtime = attInfo.getDtime();
				byte[] midbytes = attdtime.getBytes();
				String cardtime = new String(midbytes, 0, 4) + "-" + new String(midbytes, 4, 2) + "-"
						+ new String(midbytes, 6, 2) + " " + new String(midbytes, 8, 2) + ":"
						+ new String(midbytes, 10, 2) + ":" + new String(midbytes, 12, 2);

				// ����У
				String cardtype = attInfo.getStatus();

				// ����
				String cardid = attInfo.getCardId();
				// ������ͼƬ
				String fileName = attInfo.getPhoto() + ".jpg";
				// String fileName = "201609231918282195736306.jpg";
				String photoPath = appBaseFun.getPhoneCardPath() + "/tpatttp/AttPhoto/" + fileName;



				// �ϱ�ƽ̨
				// String platformID = SettingPara.getPlatformID();
				if (platformID.equals("8")) {// ��ά
					uploadZWAttend(platformID, cardid, attdtime, cardtime, photoPath);
				} else {
					String platform = null;
					if (platformID.equals("1")||settingPara.getAtt_pic_url().equals("http://172.16.255.78:6666/openAPI/CreditCardPhotos.ashx")) {// �ͱ���
						platform = "hbb";
					} else if (platformID.equals("4")) {// ��ؽ���
						platform = "ddjy";
					} else if (platformID.equals("5")) {// ����ͯѧ
						platform = "jktx";
					}else if (platformID.equals("7")) {//������ѧͨ
						platform = "fjyxt";
					}
					else {
						platform = "jktx";
						//	OkHttpPost.this.onFailure("�ϱ�����:ƽ̨��֧���ƴ洢");
					}
					if (platform != null) {

						getTokens(platform);


						String fileKey = prefix + "/" + fileName;
						// Log.i(TAG, "fileKey:" + fileKey);
						String picUrl = host + fileKey;

						uploadQiNiuPic(platform, platformID, photoPath, fileKey);


					}
				}
			} else {
				OkHttpPost.this.onFailure("�ϱ�����:���ϱ����ڼ�¼");
				//	break;
			}
			//	}
		} else {
			OkHttpPost.this.onFailure("�ϱ�����:δ����������");
		}


	}




	{





	}

	private boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	private synchronized void getTokens(String platform) {
		//	token = settingPara.getCloudStorageToken();
		prefix = settingPara.getCloudStoragePrefix();
		host = settingPara.getCloudStorageHost();

		if (isEmpty(token) || isEmpty(prefix) || isEmpty(host)) {
			OKHttpUtils oKHttpUtils = new OKHttpUtils(context);
			String[] tokens = oKHttpUtils.getCloudStorageTokens(platform);
			if (tokens != null && tokens.length == 3) {
				token = tokens[0];
				Log.i(TAG, "token�����ڣ��ӷ�������ȡ��" + token);
				settingPara.setCloudStorageToken(token);
				prefix = tokens[1];
				settingPara.setCloudStoragePrefix(prefix);
				host = tokens[2];
				settingPara.setCloudStorageHost(host);
			}
		} else {
			Log.i(TAG, "token�Ѵ���,����: " + token.length());
		}
	}

	private int uploadWWAttend(String platformID, String platform, String fileName, final String photoPath,
							   AttInfo attInfo,String status) {

		Log.i(OkHttpPost.TAG, "-----------�ƴ洢�ϱ���ʼ------------");

		try {
			getTokens(platform);

			String fileKey = prefix + "/" + fileName;
			// Log.i(TAG, "fileKey:" + fileKey);
			String picUrl = host + fileKey;
			// if(host.endsWith("/")&&fileKey.startsWith("/")){
			// picUrl = host + fileKey.substring(1);
			// }

			Log.i(TAG, "picUrl: " + picUrl);
			if (MainIdleActivity.isNetworkAvailables(context, appBaseFun, settingPara)) {
				loadupcount = AttPlatformProto.uploadAtt(settingPara, attInfo, httpApp, picUrl,status);
			}else {
				Log.i(TAG, "�ϴ�����δ����" );
			}
//			if (isAddAttendInfo) {
//				uploadQiNiuPic(platform, platformID, photoPath, fileKey);
//			}
			return loadupcount;
		} catch (Exception e) {
			Log.i(TAG, "�ϴ�������Ϣʧ��:" + e.toString());
		}
		return 0;
	}

	private void uploadZWAttend(final String platformID, String cardid, String attdtime, String cardtime,
								final String photoPath) {

		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String attNo = tm.getDeviceId();// ���ڻ����
		String fileName = attdtime + ".jpg";// [���ھ�ȷ���루yyyyMMddhhmmss��].jpg
		final String fileKey = "attendance/babypic/" + attNo + "/" + fileName;
		// Log.i(TAG, "fileKey:" + fileKey);
		OKHttpUtils oKHttpUtils = new OKHttpUtils(context);
		SharedPreferences sp = context.getSharedPreferences("json", Context.MODE_PRIVATE);
		String uuid = sp.getString(cardid + "idimfo", "");
		if (uuid == null || uuid.length() == 0
				|| oKHttpUtils.addAttendInfo(uuid, cardid, JOVISION_PHOTO_URL + fileKey, cardtime)) {
			isAddAttendInfo = true;
			if (uuid != null && uuid.length() > 0) {
				Log.i(TAG, "�ϴ�������Ϣ�ɹ�");
				String token_url = JOVISION_URL + fileKey;
				OkHttpClient mOkHttpClient = new OkHttpClient();
				final Request request = new Request.Builder().url(token_url).build();
				Call call = mOkHttpClient.newCall(request);
				call.enqueue(new Callback() {
					public void onResponse(Call arg0, final okhttp3.Response response) throws IOException {
						String json = response.body().string();
						try {
							JSONTokener jsonParser = new JSONTokener(json);
							JSONObject person = (JSONObject) jsonParser.nextValue();
							token = person.getString("token");
							uploadQiNiuPic(null, platformID, photoPath, fileKey);
						} catch (JSONException ex) {
							OkHttpPost.this.onFailure(ex.toString());
						}
					}

					public void onFailure(Call request, IOException e) {
						OkHttpPost.this.onFailure(e.toString());
					}
				});
			} else {
				Log.i(TAG, "uuid������");
				OkHttpPost.this.onSuccess("success");
			}
		} else {
			isAddAttendInfo = false;
			Log.i(TAG, "�ϴ�������Ϣʧ��");
			OkHttpPost.this.onFailure("�ϴ�ʧ��");
		}
	}


	/**
	 * ��ȡָ���ļ���С
	 *
	 * @param f
	 * @return
	 * @throws Exception
	 */
	private static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			//file.createNewFile();
			Log.e("��ȡ�ļ���С", "�ļ�������!");
		}
		return size;
	}


	private void uploadQiNiuPic(final String platform, final String platformID, final String photoPath,
								final String fileKey) {
		String dirPath = appBaseFun.getPhoneCardPath() + "/365HTTP";// <�ϵ��¼�ļ�������ļ���λ��>
		try {
			Recorder recorder = new FileRecorder(dirPath);
			KeyGenerator keyGen = new KeyGenerator() {
				public String gen(String key, File file) {
					return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
				}
			};
			Configuration config = new Configuration.Builder().chunkSize(256 * 1024) // ��Ƭ�ϴ�ʱ��ÿƬ�Ĵ�СĬ��256K
					.putThreshhold(512 * 1024) // ���÷�Ƭ�ϴ���ֵ��Ĭ��512K
					.connectTimeout(10) // ���ӳ�ʱ��Ĭ��10��
					.responseTimeout(60) // ��������Ӧ��ʱ��Ĭ��60��
					.recorder(recorder, keyGen) // keyGen
					.retryMax(0)		//�ش�����
					// ��Ƭ�ϴ�ʱ�����ɱ�ʶ��������Ƭ��¼���������Ǹ��ļ����ϴ���¼
					.zone(Zone.zone0) // ��������ָ����ͬ������ϴ���������������������IP��Ĭ��
					// Zone.zone0
					.build();
			UploadManager uploadManager = new UploadManager(config);
			File data = new File(photoPath);// <File���󡢻� �ļ�·������ �ֽ�����>
			if (data.exists()&&getFileSize(data)>0) {
				uploadManager.put(data, fileKey, token, new UpCompletionHandler() {
					public void complete(String key, ResponseInfo info, JSONObject res) {
						boolean status = info.toString().contains("status:200");
						Log.i(TAG, "��ţ�ϴ����-> status:" + status + ",\r\nkey: " + key + ",\r\nres: " + res + ",\r\ninfo: "
								+ info);
						if (status) {
							OkHttpPost.this.onSuccess("success");
						} else {
							if (status) {
								OkHttpPost.this.onFailure("���������ϴ�ʧ��");
							} else {
								try{//TODO ȷ���Ƿ������߳�
									if (platformID.equals("1") || platformID.equals("4") || platformID.equals("5")) {
										if (res != null && res.length() > 0 && res.toString().contains("expired token")
												&& tryTime < 3) {
											Log.i(TAG, "�ƴ洢ͼƬ�ϴ�ʧ��,��������tryTime��" + tryTime);
											tryTime++;
											settingPara.setCloudStorageToken("");
											settingPara.setCloudStoragePrefix("");
											settingPara.setCloudStorageHost("");
											getTokens(platform);
											uploadQiNiuPic(platform, platformID, photoPath, fileKey);
										} else {
											OkHttpPost.this.onFailure("�ƴ洢ͼƬ�ϴ�ʧ��");
										}
									} else {
										OkHttpPost.this.onFailure("�ƴ洢����ͼƬ�ϴ�ʧ��");
									}
								}catch (Exception e) {
									OkHttpPost.this.onFailure("�ƴ洢����ͼƬ�ϴ�ʧ�ܣ�"+e.toString());
								}
							}
						}
					}
				}, null);
			} else {
				Log.i(TAG, "ͼƬ�����ڣ�photoPath:" + photoPath);
				OkHttpPost.this.onSuccess("success");
			}
		} catch (IOException e) {
			OkHttpPost.this.onFailure("�ƴ洢����ͼƬ�ϴ�����"+e.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}