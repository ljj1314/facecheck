package com.att.act;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.att.AppBaseFun;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OKHttpUtils {

	public static final String TAG = "qiniu";

	private Context context;

	public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

	public static String JOVISION_ATTEND_URL = "http://xwapi.jovision.com/";// 中维平台服务器地址
	public static String JOVISION_URL = "http://manager.jovision.com/anon/qiniu/imgUpload.do?key=";// 中维平台

	public static String STRONG_CLOUD_URL = "http://hebb1.jiankangtongxue.cn/OpenAPI/StrongCloud.ashx";// 云存储

	private volatile boolean isCancelled = false;

	public AppBaseFun appBaseFun = new AppBaseFun();

	private OkHttpClient client;

	public OKHttpUtils(Context _context) {
		context = _context;
		client = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.SECONDS).writeTimeout(1, TimeUnit.SECONDS)
				.readTimeout(1, TimeUnit.SECONDS).build();
	}

	public String getResponse(FormBody formBody, String post) {
		try {
			Request request = new Request.Builder().url(JOVISION_ATTEND_URL + post).post(formBody).build();
			Response response = client.newCall(request).execute();
			String responseString = response.body().string();
			Log.i(TAG, post + "->" + responseString);
			if (response.isSuccessful()) {
				return responseString;
			}
		} catch (Exception e) {
			Log.i(TAG, post + "->" + e.toString());
		}
		return null;
	}

	public String getResponse(String url, FormBody formBody) {
		try {
			Request request = new Request.Builder().url(url).post(formBody).build();
			Response response = client.newCall(request).execute();
			String responseString = response.body().string();
			//Log.i(TAG, url + "->" + responseString);
			if (response.isSuccessful()) {
				return responseString;
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
		return null;
	}

	// 获取Token
	public String[] getCloudStorageTokens(String platform) {
		FormBody formBody = new FormBody.Builder().add("Action", "getToken").add("platform", platform).build();
		String responseString = getResponse(STRONG_CLOUD_URL, formBody);
		//Log.i(TAG, responseString);
		try {
			JSONTokener jsonParser = new JSONTokener(responseString);
			JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
			int status_code = jsonObject.getInt("status");// 返回状态 （1-成功/0-失败）
			String message = jsonObject.getString("msg");// 返回信息
			if (status_code == 1) {
				JSONObject result = jsonObject.getJSONObject("item");
				String token = result.getString("token");
				String prefix = result.getString("prefix");
				String host = result.getString("host");
				//Log.i(TAG, "token:" + token);
				//Log.i(TAG, "prefix:" + prefix + "   host:" + host);
				String tokens[] = { token, prefix, host };
				return tokens;
			}else{
				Log.i(TAG, "status_code:" + status_code + " msg:" + message);
			}
		} catch (Exception ex) {
			Log.i(TAG, "获取云存储Token出错："+ex.toString());
		}
		return null;
	}

	// 考勤信息上传接口
	public boolean addAttendInfo(String uuid, String cardNo, String picPath, String time) {
		FormBody formBody = new FormBody.Builder().add("uuid", uuid).add("cardNo", cardNo).add("picPath", picPath)
				.add("time", time).build();
		String responseString = getResponse(formBody, "attend/addAttendInfo");
		if (responseString != null && responseString.length() > 0) {
			try {
				JSONTokener jsonParser = new JSONTokener(responseString);
				if (jsonParser != null) {
					JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
					int status_code = jsonObject.getInt("status_code");// 返回状态（200-成功/400-失败）
					int code = jsonObject.getInt("code");// 返回码 0
					String message = jsonObject.getString("message");// 返回信息
					if (status_code == 200) {
						Log.i(TAG, "考勤信息上传成功：" + status_code + "," + code + "," + message);
						return true;
					}
				}
			} catch (Exception ex) {
				if(responseString.toString().contains("\"status_code\":200")){
					return true;
				}else{
					Log.i(TAG, "中维考勤信息上传出错：" + ex.toString());
				}
			}
		} else {
			Log.i(TAG, "考勤信息上传没有返回");
		}
		return false;
	}

	// 获取宝宝考勤卡信息接口
	public ZwCardInfoOne getCardInfo(String no, String cardNo) {
		FormBody formBody = new FormBody.Builder().add("no", no).add("cardNo", cardNo).build();
		String responseString = getResponse(formBody, "attend/getCardInfo");
		if (responseString != null && responseString.length() > 0) {
			Log.i(TAG, responseString);
			try {
				JSONTokener jsonParser = new JSONTokener(responseString);
				JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
				int status_code = jsonObject.getInt("status_code");// 返回状态
				// （200-成功/400-失败）
				int code = jsonObject.getInt("code");// 返回码 0
				String message = jsonObject.getString("message");// 返回信息
				Log.i(TAG, status_code + " " + code + " " + message);
				JSONObject result = jsonObject.getJSONArray("result").getJSONObject(0);
				String s = result.toString();
				Gson gson = new Gson();
				ZwCardInfoOne ca = gson.fromJson(s, new TypeToken<ZwCardInfoOne>() {
				}.getType());
				return ca;
			} catch (JSONException ex) {
				Log.i(TAG, ex.toString());
			}
		}
		return null;
	}

	// 获取全部宝宝考勤卡信息接口

	// 获取教师考勤卡信息
	public TeacherInfo getTeacherCardInfo(String no, String cardNo) {
		FormBody formBody = new FormBody.Builder().add("no", no).add("cardNo", cardNo).build();
		String responseString = getResponse(formBody, "attend/getTeacherCardInfo");
		if (responseString != null && responseString.length() > 0) {
			Log.i(TAG, responseString);
			try {
				JSONTokener jsonParser = new JSONTokener(responseString);
				JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
				int status_code = jsonObject.getInt("status_code");// 返回状态
				// （200-成功/400-失败）
				int code = jsonObject.getInt("code");// 返回码 0
				String message = jsonObject.getString("message");// 返回信息
				Log.i(TAG, status_code + " " + code + " " + message);
				JSONObject result = jsonObject.getJSONArray("result").getJSONObject(0);
				String s = result.toString();
				Gson gson = new Gson();
				TeacherInfo ca = gson.fromJson(s, new TypeToken<TeacherInfo>() {
				}.getType());
				return ca;
			} catch (JSONException ex) {
				Log.i(TAG, ex.toString());
			}
		}
		return null;
	}

	// 获取全部教师考勤卡信息

	// 获取考勤机显示图片地址
	public void getAttendDisplay(String no) {
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
						int status_code = jsonObject.getInt("status_code");// 返回状态
						// （200-成功/400-失败）
						int code = jsonObject.getInt("code");// 返回码 0
						String message = jsonObject.getString("message");// 返回信息
						Log.i(TAG, status_code + " " + code + " " + message);
						JSONObject result = jsonObject.getJSONObject("result");
						String kid = result.getString("kid");// 所属幼儿园ID
						JSONArray pics = result.getJSONArray("pics");// 须显示图片下载地址数组
						for (int i = 0; i < pics.length(); i++) {
							String pic = pics.getString(i);
							Log.i(TAG, pic);
						}
					} catch (JSONException ex) {
						Log.i(TAG, ex.toString());
					}
				}

				@Override
				public void onFailure(String exceptionMsg) {
					Log.i(TAG, exceptionMsg);
				}
			};
			request.execute(json, "attend/getAttendDisplay");
		} catch (JSONException ex) {
			throw new RuntimeException(ex);
		}
	}

	// 测温结果上报
	public boolean addTemperature(String uuid, String cardNo, String temperature, String time) {
		FormBody formBody = new FormBody.Builder().add("uuid", uuid).add("cardNo", cardNo)
				.add("temperature", temperature).add("time", time).build();
		String responseString = getResponse(formBody, "attend/addTemperature");
		if (responseString != null && responseString.length() > 0) {
			try {
				JSONTokener jsonParser = new JSONTokener(responseString);
				JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
				int status_code = jsonObject.getInt("status_code");// 返回状态（200-成功/400-失败）
				int code = jsonObject.getInt("code");// 返回码 0
				String message = jsonObject.getString("message");// 返回信息
				if (status_code == 200) {
					Log.i(TAG, "测温结果上报成功：" + status_code + "," + code + "," + message);
					return true;
				}
			} catch (JSONException ex) {
				Log.i(TAG, "测温结果上报出错：" + ex.toString());
			}
		} else {
			Log.i(TAG, "测温结果上报没有返回");
		}
		return false;
	}

	// 七牛云中维平台考勤照片上传
	public void jovisionAttPhotoUpload(final String dataPath, final String fileKey) {
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
					String token = person.getString("token");
					qiniuUpload(dataPath, fileKey, token);
				} catch (JSONException ex) {
					Log.i(TAG, "考勤照片上传出错：" + ex.toString());
				}
			}

			public void onFailure(Call request, IOException e) {
				Log.i(TAG, "考勤照片上传出错：" + e.toString());
			}
		});
	}

	public void qiniuUpload(String dataPath, String key, String token) {
		String dirPath = appBaseFun.getPhoneCardPath() + "/365HTTP";// <断点记录文件保存的文件夹位置>
		try {
			Recorder recorder = new FileRecorder(dirPath);
			// 默认使用key的url_safe_base64编码字符串作为断点记录文件的文件名
			// 避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
			KeyGenerator keyGen = new KeyGenerator() {
				public String gen(String key, File file) {
					// 不必使用url_safe_base64转换，uploadManager内部会处理
					// 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
					return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
				}
			};
			Configuration config = new Configuration.Builder().chunkSize(256 * 1024) // 分片上传时，每片的大小。
					// 默认256K
					.putThreshhold(512 * 1024) // 启用分片上传阀值。默认512K
					.connectTimeout(10) // 链接超时。默认10秒
					.responseTimeout(60) // 服务器响应超时。默认60秒
					// .recorder(recorder) // recorder分片上传时，已上传片记录器。默认null
					.recorder(recorder, keyGen) // keyGen
					// 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
					.zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。默认
					// Zone.zone0
					.build();
			// 重用uploadManager。一般地，只需要创建一个uploadManager对象
			UploadManager uploadManager = new UploadManager(config);

			File data = new File(dataPath);// <File对象、或 文件路径、或 字节数组>
			uploadManager.put(data, key, token, new UpCompletionHandler() {
				public void complete(String key, ResponseInfo info, JSONObject res) {
					// res包含hash、key等信息，具体字段取决于上传策略的设置。
					Log.i(TAG, "考勤照片上传完成：" + key + ",\r\n " + info + ",\r\n " + res);
				}
			}, new UploadOptions(null, null, false, new UpProgressHandler() {
				public void progress(String key, double percent) {
					Log.i(TAG, "考勤照片上传进度：" + key + ": " + percent);
				}
			}, new UpCancellationSignal() {
				public boolean isCancelled() {
					return isCancelled;
				}
			}));
		} catch (IOException e) {
			Log.i(TAG, "考勤照片上传出错：" + e.toString());
		}
	}

	// 点击取消按钮，让UpCancellationSignal##isCancelled()方法返回true，以停止上传
	public void cancell() {
		isCancelled = true;
	}

	public static Bitmap getImage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 1920f;// 这里设置高度为800f
		float ww = 1080f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		if (bitmap != null) {
			return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
		} else {
			return null;
		}
	}

	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
			return bitmap;
		} catch (OutOfMemoryError e) {
			Log.i(TAG, e.toString());
		}
		return image;
	}

	public static long CHECK_OMC_STATUS_TIME_PERIOD = 1000 * 60 * 1;

	public static void checkOmcStatus(Context context) {
		int verCode = -1;
		String appPackName = "com.telpoedu.omc";
		try {
			verCode = context.getPackageManager().getPackageInfo(appPackName, 0).versionCode;
			if (verCode != -1) {
				//Log.i("OMC", "OMC已安装，版本号：" + verCode);
				ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
				boolean isOpen = false;
				int importance = 0;
				if (appProcesses != null) {
					for (RunningAppProcessInfo appProcess : appProcesses) {
						if (appProcess.processName.equals(appPackName)) {
							importance = appProcess.importance;
							if (importance <= RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
								Log.i("OMC", "OMC已安装(版本号：" + verCode +"),后台服务已运行(状态值:" + importance+")");
								isOpen = true;
								break;
							}else{

							}
						}
					}
				}
				if (!isOpen) {
					Intent intent = new Intent("com.telpoedu.omc.USER_ACTION");
					// intent.addFlags(Intent. FLAG_INCLUDE_STOPPED_PACKAGES);
					if (android.os.Build.VERSION.SDK_INT >= 12) {// >=android.os.Build.VERSION_CODES.HONEYCOMB_MR1
						intent.setFlags(32);// 3.1以后的版本需要设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES
					}
					// Intent intent =
					// context.getPackageManager().getLaunchIntentForPackage(appPackName);
					if (intent != null) {
						Log.i("OMC", "OMC已安装(版本号：" + verCode +"),后台服务未运行(状态值:" + importance+"),发送启动广播");
						context.sendBroadcast(intent);
						// context.startActivity(intent);
					}
				}
			} else {
				Log.i("OMC", "OMC未安装");
			}
		} catch (Exception e) {
			Log.i("OMC", "OMC检查安装出错：" + e.toString());
		}
	}
}
