/**************************************************************************
 Copyright (C) 广东天波教育科技有限公司　版权所有
 文 件 名：
 创 建 人：
 创建时间：2015.10.30
 功能描述：平台协议处理
 **************************************************************************/
package com.att;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.att.DBOpenHelper.AttInfo;
import com.att.act.PicCard;
import com.att.act.WriteUnit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

public class AttPlatformProto {
	private static int platformProtoStatus = 0;// 平台协议状态:0表示初始化
	private static int dCardInfoCount = 0;
	private static int dCardInfoErrorCount = 0;
	private static int dCardInfoTotalCount = 0;
	private SettingPara settingPara = new SettingPara();
	private HttpApp httpApp = new HttpApp();
	private AppBaseFun appBaseFun = new AppBaseFun();
	private SharedPreferences sp = null;
	private SharedPreferences.Editor se = null;

	/**
	 * 下载卡信息数
	 *
	 * @param
	 * @return
	 */
	public static int getDownloadCardInfoCount() {
		return (dCardInfoCount);
	}

	/**
	 * 下载卡信息数
	 *
	 * @param status
	 * @return
	 */
	public static void setDownloadCardInfoCount(int flag) {
		if (flag > 0) {
			dCardInfoCount++;
		} else {
			dCardInfoCount = 0;
		}
	}

	/**
	 * 下载卡信息数
	 *
	 * @param
	 * @return
	 */
	public static int getDownloadCardInfoErrorCount() {
		return (dCardInfoErrorCount);
	}

	/**
	 * 下载卡信息数
	 *
	 * @param status
	 * @return
	 */
	public static void setDownloadCardInfoErrorCount(int flag) {
		if (flag > 0) {
			dCardInfoErrorCount++;
		} else {
			dCardInfoErrorCount = 0;
		}
	}

	/**
	 * 下载卡信息数
	 *
	 * @param
	 * @return
	 */
	public static int getDownloadCardInfoTotalCount() {
		return (dCardInfoTotalCount);
	}

	/**
	 * 下载卡信息数
	 *
	 * @param status
	 * @return
	 */
	public static void setDownloadCardInfoTotalCount(int count) {
		dCardInfoTotalCount = count;
	}

	/**
	 * 平台协议状态
	 *
	 * @param
	 * @return
	 */
	public static int getPlatformProtoStatus() {
		return (platformProtoStatus);
	}

	/**
	 * 平台协议状态
	 *
	 * @param status
	 * @return
	 */
	public static void setPlatformProtoStatus(int status) {
		platformProtoStatus = status;
	}

	/**
	 * 上报考勤
	 *
	 * @param savetext
	 * @return
	 * @throws JSONException
	 */
	public synchronized int CheckNotUploadAtt(Context context, DBOpenHelper sqlTpatt) throws JSONException {
		// int maxUploadAttCount;// 每次上报最大考勤数
		int uploadCount = 0;
		if (MainIdleActivity.isNetworkAvailables(context, appBaseFun, settingPara)) {
			// maxUploadAttCount = settingPara.getMaxUploadAttCount();
			for (int i = 0; i < 1; i++) {
				AttInfo attInfo = sqlTpatt.findUploadAttInfo();
				if (attInfo != null) {
					switch (settingPara.getAttPlatformProto()) {
						case 0:
							// if(){//TODO 不管是否上报成功都算作成功，有漏报的可能
							uploadCount = uploadAtt(settingPara, attInfo, httpApp, null,null);
							//uploadCount = 1;
							// }
							break;

						default:
							Log.i("TPATT", "上报考勤:未知平台协议 ");
							break;
					}
				} else {
					// WriteUnit.loadlist("上报考勤:无上报考勤记录");
					Log.i("TPATT", "上报考勤:无上报考勤记录");
					break;
				}
			}
		} else {
			WriteUnit
					.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "上报考勤:未连接上网络");
			Log.i("TPATT", "上报考勤:未连接上网络");
		}

		return uploadCount;
	}

	public static int uploadAtt(SettingPara settingPara, AttInfo attInfo, HttpApp httpApp, String picUrl,String status)
			throws JSONException {

		// TimeStamp
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate2 = new Date(System.currentTimeMillis());// 获取当前时间
		String timestamp = formatter2.format(curDate2);

		// 考勤时间
		String attdtime = attInfo.getDtime();
		byte[] midbytes = attdtime.getBytes();
		String cardtime = new String(midbytes, 0, 4) + "-" + new String(midbytes, 4, 2) + "-"
				+ new String(midbytes, 6, 2) + " " + new String(midbytes, 8, 2) + ":" + new String(midbytes, 10, 2)
				+ ":" + new String(midbytes, 12, 2);
		// TransactionID
		String transactionid = attInfo.getTranid();

		// 进出校
		String cardtype ="";

		if (status==null) {
			cardtype = attInfo.getStatus();
		}else {
			cardtype=status;
		}

		// 卡号
		String cardid = attInfo.getCardId();

		String postRxText = "";
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("ProvinceCode", settingPara.getProvincCode()));
		params.add(new BasicNameValuePair("TransactionID", transactionid));
		params.add(new BasicNameValuePair("SchoolID", settingPara.getSchoolID()));
		params.add(new BasicNameValuePair("DeviceID", settingPara.getDevicID()));
		params.add(new BasicNameValuePair("CardID", cardid));
		params.add(new BasicNameValuePair("CardTime", cardtime));
		params.add(new BasicNameValuePair("CardType", cardtype));
		params.add(new BasicNameValuePair("TimeStamp", timestamp));
		params.add(new BasicNameValuePair("Extension", java.util.UUID.randomUUID().toString()));

		if (picUrl != null && picUrl.length() > 0) {
			params.add(new BasicNameValuePair("PicUrl", picUrl));// 图片url
		}

		// 上报平台
		// Log.i("TPATT", "上报考勤数据:" + params.toString());
		WriteUnit.loadlist(timestamp + "上报考勤数据:" + params.toString());
		int uploadFlag = 0;
		for (int upload = 0; upload < 2; upload++) {
			String att_url = settingPara.getAttPlatformUrl();
			postRxText = httpApp.postSendAndReceive(att_url, params);
			WriteUnit.debugLog("att_url:" + att_url + "\r\n上报考勤返回:" + postRxText);
			// WriteUnit.loadlist(formatter2.format(new java.util.Date()) +
			// "上报考勤数据返回:" + postRxText);
			if (postRxText != null) {
				/// JSONArray jsonArray = new
				/// JSONArray(postRxText);
				// if ( jsonArray.length() > 0 )
				// {
				// JSONObject obj = jsonArray.getJSONObject(0);
				JSONObject obj = new JSONObject(postRxText);
				try {
					String strTemp = obj.getString("ResultCode");

					// try {
					// if (obj.getString("ProvinceServer") != null
					// && obj.getString("ProvinceServer").length() > 0) {
					// String stp = obj.getString("ProvinceServer");
					// Log.i("ps", stp);
					// String[] ms = stp.split(";");
					// String mk = ms[0].toString();
					// Log.i("mmlll", ms + "...." + mk);
					// if (mk != null && !mk.equals("")) {
					// SharedPreferences sp =
					// context.getSharedPreferences("json",
					// Activity.MODE_PRIVATE);
					// SharedPreferences.Editor se = sp.edit();
					// se.putString("port", mk);
					// se.putString("allport", stp);
					// se.commit();
					// }
					// }
					// } catch (Exception e) {
					// //Log.i("TPATT", "ProvinceServer信息为空");
					// }

					if (strTemp.equals("200")||strTemp.equals("400")|| strTemp.equals("success") ) {
						WriteUnit.loadlist(formatter2.format(new java.util.Date()) + "上报考勤成功 ResultCode:" + strTemp);
						uploadFlag=1;
						Log.i("TPATT", "上传考勤成功 ResultCode:" + strTemp);
						break;
					} else {
						WriteUnit.loadlist(formatter2.format(new java.util.Date()) + "上报考勤数据返回:" + postRxText + "\n"
								+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "上传考勤重发1");
						Log.i("TPATT", "上传考勤重发1");
					}
				} catch (Exception e) {
					WriteUnit.loadlist(
							new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "上传考勤重发2");
					Log.i("TPATT", "上传考勤重发2");
				}

			} else {
				WriteUnit.loadlist(
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "上传考勤无应答2");
				Log.i("TPATT", "上传考勤无应答2");
				uploadFlag = 0;
			}
		}
		//uploadFlag=1;
		return uploadFlag;
	}

	/**
	 * 上报考勤图片
	 *
	 * @throws JSONException
	 */
	public synchronized int CheckNotUploadAttPhoto(Context context, DBOpenHelper sqlTpatt) throws JSONException {
		@SuppressWarnings("unused")
		int maxUploadAttCount;// 每次上报最大考勤数
		int uploadCount = 0;
		if (MainIdleActivity.isNetworkAvailables(context, appBaseFun, settingPara)) {
			maxUploadAttCount = settingPara.getMaxUploadAttCount();
			for (int i = 0; i < 1; i++) {
				AttInfo attInfo = sqlTpatt.findUploadAttPhotoInfo();
				if (attInfo != null) {
					switch (settingPara.getAttPhotoPlatformProto()) {
						case 0: {

							// 考勤时间
							String attdtime = attInfo.getDtime();
							byte[] midbytes = attdtime.getBytes();
							String cardtime = new String(midbytes, 0, 4) + "-" + new String(midbytes, 4, 2) + "-"
									+ new String(midbytes, 6, 2) + " " + new String(midbytes, 8, 2) + ":"
									+ new String(midbytes, 10, 2) + ":" + new String(midbytes, 12, 2);
							// TransactionID
							String transactionid = attInfo.getTranid();

							// 进出校
							String cardtype = attInfo.getStatus();

							// 卡号
							String cardid = attInfo.getCardId();

							// 读考勤图片
							String photoPath = appBaseFun.getPhoneCardPath() + "/tpatttp/AttPhoto/" +attInfo.getPhoto()
									+ ".jpg";
							Log.i("TPATT", "考勤图片地址:" + photoPath);

							uploadCount = uploadPic(photoPath, transactionid, cardid, cardtime);
						}
						break;

						default:
							Log.i("TPATT", "上报考勤:未知平台协议 ");
							break;
					}
				} else {
					// WriteUnit.loadlist("上传图片无上报考勤记录");
					Log.i("TPATT", "上报考勤:无上报考勤记录");
					break;
				}
			}

		} else {
			WriteUnit
					.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "上报图片:未连接上网络");
			Log.i("TPATT", "上报考勤:未连接上网络");
		}

		return uploadCount;
	}

	// 上传图片
	private int uploadPic(String photoPath, String transactionid, String cardid, String cardtime) throws JSONException {

		boolean isresult = false;
		int uploadCount = 0;
		String postRxText = "";
		String bm = appBaseFun.getLoacalBitmap(photoPath);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ProvinceCode", settingPara.getProvincCode()));
		params.add(new BasicNameValuePair("TransactionID", transactionid));
		params.add(new BasicNameValuePair("SchoolID", settingPara.getSchoolID()));
		params.add(new BasicNameValuePair("DeviceID", settingPara.getDevicID()));
		params.add(new BasicNameValuePair("CardID", cardid));
		params.add(new BasicNameValuePair("CardTime", cardtime));

		Log.i("TPATT", "上报考勤图片数据:" + params.toString());
		WriteUnit.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "上报考勤图片数据:"
				+ params.toString());
		if (bm != null) {
			params.add(new BasicNameValuePair("PhotoImg", bm));
			// bm.recycle();//回收bitmap空间

			// 上报平台
			int uploadFlag = 0;

			for (int upload = 0; upload < 2; upload++) {
				if (settingPara.getCardInfoUrl().startsWith("https")) {
					postRxText = httpApp.posthttpps(settingPara.getAttPhotoPlatformUrl(), params);
				}else {
					postRxText = httpApp.postSendAndReceive(settingPara.getAttPhotoPlatformUrl(), params);
				}


				WriteUnit.debugLog("上报考勤图片返回:" + postRxText);
				// WriteUnit.loadlist(new SimpleDateFormat("yyyy-MM-dd
				// HH:mm:ss").format(new java.util.Date())
				// + "上报考勤图片返回:" + postRxText.toString());

				if (postRxText != null) {
					isresult = true;
					JSONObject obj = new JSONObject(postRxText);
					try {
						String strTemp = obj.getString("ResultCode");
						if (strTemp.equals("200") || strTemp.equals("success") || strTemp.equals("400")) {
							WriteUnit.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
									+ "上报图片成功 photoPath:" + photoPath);
							Log.i("TPATT", "上传图片成功 photoPath:" + photoPath);
							uploadFlag=1;
							break;
						} else {
							WriteUnit.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
									+ "上报考勤图片返回:" + postRxText.toString() + "\n"
									+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
									+ "上传图片重发1");
							Log.i("TPATT", "上传图片重发1");
						}
					} catch (Exception e) {
						WriteUnit.loadlist(
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "上传图片重发2");
						Log.i("TPATT", "上传图片重发2");
					}

				}

			}
			if (uploadFlag == 1) {
				uploadCount = 1;
			}
		} else {
			isresult = true;
			WriteUnit.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
					+ "考勤图片不存在 photoPath:" + photoPath);
			uploadCount = 1;
			Log.i("TPATT", "考勤图片不存在 photoPath:" + photoPath);
		}

		if (!isresult) {
			Log.i("TPATT", "上传图片无应答2");
			WriteUnit.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "上传图片无应答2");
			// uploadCount = 0;
		}
		return uploadCount;
	}

	/**
	 * 下载卡信息
	 *
	 * @param
	 * @return
	 */
	public AlertDialog DownloadCardInfoTips(Context context) {
		String strTips;
		AlertDialog dialog;
		boolean ismobile = false;
		if (appBaseFun.isMobileAvailable(context) && settingPara.isTake_internet()) {
			ismobile = true;
		} else {
			ismobile = false;
		}

		// 更新本地学生数据
		if (appBaseFun.isWifiAvailable(context) || ismobile) {
			strTips = "是否更新本地学生信息？";
		} else {
			strTips = "当前无WIFI网络,是否更新卡和图片信息？";
		}
		Log.i("TPATT", "下载卡和图片信息请求:");

		dialog = new AlertDialog.Builder(context).setTitle("提示").setMessage(strTips)
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface, int paramInt) {
						Log.i("TPATT", "下载卡信息请求1:=" + String.valueOf(paramInt));
						paramDialogInterface.dismiss();
						setPlatformProtoStatus(1); // 下载卡和图片信息
					}
				}).setNegativeButton("否", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface paramDialogInterface, int paramInt) {
						setPlatformProtoStatus(3);// 上传考勤信息
					}
				}).show();

		return (dialog);
	}

	/**
	 * 下载卡信息
	 *
	 * @param
	 * @return
	 *
	 * 		{ "_metadata": {"code":"200"}, "data": [ {"child_id":
	 *         "9b56fe3e-e7d8-43b7-9b5a-604796084198","child_name":
	 *         "何琪","card_id": "797A87A2","card_id1": "79C4ED5","card_id2":
	 *         "","card_id3": ""}, {"child_id":
	 *         "ddb410d5-da43-4066-9ed9-09dc3a10db23","child_name":
	 *         "贝贝","card_id": "718C7422","card_id1": "","card_id2":
	 *         "","card_id3": ""} ] }
	 */
	@SuppressWarnings("unused")
	public void DownloadCardInfo(Context context) {
		sp = context.getSharedPreferences("json", Activity.MODE_PRIVATE);
		se = sp.edit();

		switch (settingPara.getAttPlatformProto()) {
			case 0: {
				String strUrl;
				String strRes;

				strUrl = settingPara.getCardInfoUrl() + "?SchoolID=" + settingPara.getSchoolID() + "&ProvinceCode="
						+ settingPara.getProvincCode();

				Log.i("TPATT", "下载卡信息请求:" + strUrl);
				strRes = httpApp.getSendAndReceive(strUrl);
				Log.i("TPATT", "下载卡信息返回:" + strRes);

				if (strRes != null) {
					JSONObject jsonObject;

					try {
						setDownloadCardInfoCount(0);
						setDownloadCardInfoTotalCount(0);
						jsonObject = new JSONObject(strRes).getJSONObject("_metadata");
						if (jsonObject.getString("code").equals("200")) {
							String name;
							String strPhotoPath;
							String cardid = null;

							Log.i("TPATT", "下载卡信息请求:" + "code 200");
							String strdata = new JSONObject(strRes).getString("data");
							JSONArray jsonArray = new JSONArray(strdata);
							Log.i("TPATT", "下载卡信息请求:jsonArray.length=" + String.valueOf(jsonArray.length()));
							setDownloadCardInfoTotalCount(jsonArray.length());
							for (int i = 0; i < jsonArray.length(); i++) {
								setDownloadCardInfoCount(1);

								// JSONObject jsonObject2 =
								// (JSONObject)jsonArray.opt(i);
								// //家长图片保存文件夹
								// strPhotoPath = jsonObject2.getString("child_id");
								// Log.i("TPATT", "图片路径:" + strPhotoPath);
								// appBaseFun.makeRootDirectory(appBaseFun.getSDPath()
								// + "/tpatt/CardInfo/Photo/" + strPhotoPath);
								// //学生姓名
								// name = jsonObject2.getString("child_name");
								// Log.i("TPATT", "学生姓名:" + name);
								// //卡号1
								// cardid = jsonObject2.getString("card_id");
								// Log.i("TPATT", "卡号1:" + cardid);
								// if ( cardid.length() > 0 )
								// {
								// appBaseFun.saveCardInfoFile(cardid,name + "\r\n"
								// + strPhotoPath + "\r\n");
								// }
								// //卡号2
								// cardid = jsonObject2.getString("card_id1");
								// Log.i("TPATT", "卡号2:" + cardid);
								// if ( cardid.length() > 0 )
								// {
								// appBaseFun.saveCardInfoFile(cardid,name + "\r\n"
								// + strPhotoPath + "\r\n");
								// }
								// //卡号3
								// cardid = jsonObject2.getString("card_id2");
								// Log.i("TPATT", "卡号3:" + cardid);
								// if ( cardid.length() > 0 )
								// {
								// appBaseFun.saveCardInfoFile(cardid,name + "\r\n"
								// + strPhotoPath + "\r\n");
								// }
								// //卡号4
								// cardid = jsonObject2.getString("card_id3");
								// Log.i("TPATT", "卡号4:" + cardid);
								// if ( cardid.length() > 0 )
								// {
								// appBaseFun.saveCardInfoFile(cardid,name + "\r\n"
								// + strPhotoPath + "\r\n");
								// }

								JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
								name = jsonObject2.getString("child_name");
								Log.i("TPATT", "学生姓名:" + name);

								// cardid = jsonObject2.getString("card_id");
								// 家长图片保存文件夹
								strPhotoPath = jsonObject2.getString("child_id");
								savesp(jsonObject2.getString("card_id") + "idimfo", strPhotoPath);
								savesp(jsonObject2.getString("card_id1") + "idimfo", strPhotoPath);
								savesp(jsonObject2.getString("card_id2") + "idimfo", strPhotoPath);
								savesp(jsonObject2.getString("card_id3") + "idimfo", strPhotoPath);

								savesp(jsonObject2.getString("card_id") + "name", name);
								savesp(jsonObject2.getString("card_id1") + "name", name);
								savesp(jsonObject2.getString("card_id2") + "name", name);
								savesp(jsonObject2.getString("card_id3") + "name", name);
								Log.i("TPATT", "创建文件夹:" + strPhotoPath);
								appBaseFun.makeRootDirectory(
										appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo/Photo/" + strPhotoPath);

							}
							setPlatformProtoStatus(2);
							setDownloadCardInfoCount(0);
							setDownloadCardInfoTotalCount(0);
						}
					} catch (JSONException e) {

						Log.i("TPATT", "下载卡信息异常");

						e.printStackTrace();
					}
				}
			}
			break;

			default:
				Log.i("TPATT", "下载卡信息请求2:");
				break;
		}
	}

	/**
	 * 下载家长图片
	 *
	 * @param
	 * @return
	 */
	@SuppressWarnings("unused")
	public void DownloadCardPhotoInfo() {
		switch (settingPara.getAttPlatformProto()) {
			case 0: {
				String strUrl;
				String strRes;

				String timeStamp = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss")
						.format(new Date(System.currentTimeMillis()));
				strUrl = settingPara.getCardPhotoInfoUrl() + "?provinceCode=" + settingPara.getProvincCode() + "&deviceID="
						+ settingPara.getDevicID() + "&timeStamp=" + timeStamp;

				Log.i("TPATT", "下载卡图片信息请求:" + strUrl);
				strRes = httpApp.getSendAndReceive(strUrl);
				Log.i("TPATT", "下载卡图片信息返回:" + strRes);

				if (strRes != null) {
					try {
						String strdata;
						String strPhotoPath;
						String userName;
						String photoUrl;
						String strparentsPhotos;
						String parentsName;
						String parentsPhotoUrl;

						strdata = new JSONObject(strRes).getString("code");
						if (strdata.equals("200")) {
							Log.i("TPATT", "下载卡图片信息请求:" + "code 200");

							strdata = new JSONObject(strRes).getString("msg");
							if (strdata.equals("交易成功")) {
								Log.i("TPATT", "下载卡图片信息请求:" + "交易成功");

								strdata = new JSONObject(strRes).getString("result");
								Gson gson = new Gson();
								List<PicCard> pi = gson.fromJson(strdata, new TypeToken<ArrayList<PicCard>>() {
								}.getType());
								JSONArray jsonArray = new JSONArray(strdata);

								Log.i("TPATT", "下载卡图片信息请求:jsonArray.length=" + String.valueOf(jsonArray.length()));
								setDownloadCardInfoTotalCount(jsonArray.length());
								setDownloadCardInfoErrorCount(0);
								for (int i = 0; i < jsonArray.length(); i++) {
									setDownloadCardInfoCount(1);

									JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);

									// 家长图片保存文件夹
									strPhotoPath = jsonObject2.getString("userId");// 与卡信息中的child_id一致

									// 学生姓名
									userName = jsonObject2.getString("userName");

									// 学生图片下载路径
									photoUrl = jsonObject2.getString("photoUrl");
									savesp(strPhotoPath + "userpic", photoUrl);
									if (photoUrl.length() > 0) {
										if (photoUrl.endsWith(".jpg")) {
											Log.i("TPATT", "下载卡图片信息请求0:学生=" + userName + ";路径=" + photoUrl);
											if (!appBaseFun.fileIsExists(appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo/Photo/"
													+ strPhotoPath + "/" + filename(photoUrl))) {
												Bitmap mBitmap = httpApp.getNetWorkBitmap(photoUrl);
												if (mBitmap != null) {
													boolean issu = appBaseFun.saveBitmapjpg(mBitmap,
															appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo/Photo/" + strPhotoPath
																	+ "/" + filename(photoUrl));
													pi.get(i).setIssucc(issu);
												} else {
													setDownloadCardInfoErrorCount(1);

												}
											}

											else {
												pi.get(i).setIssucc(true);
											}
											// }

										} else {
											Log.i("TPATT", "下载卡图片信息请求1:学生=" + userName + ";路径=" + photoUrl);
										}
									} else {
										Log.i("TPATT", "下载卡图片信息请求2:学生=" + userName + ";路径=" + photoUrl);
									}

									// 学校名称
									String schoolName = jsonObject2.getString("schoolName");

									// 添加时间
									String addTime = jsonObject2.getString("addTime");

									// 家长图片parentsPhotos
									strparentsPhotos = jsonObject2.getString("parentsPhotos");
									JSONArray jsonArrayPhotos = new JSONArray(strparentsPhotos);
									Log.i("TPATT", "下载卡家长图片信息请求:jsonArrayPhotos.length="
											+ String.valueOf(jsonArrayPhotos.length()));
									for (int j = 0; j < jsonArrayPhotos.length(); j++) {
										JSONObject jsonObjectparents = (JSONObject) jsonArrayPhotos.opt(j);

										// studentId
										// String studentId =
										// jsonObjectparents.getString("studentId");//与卡信息中的child_id/userId一致

										// 家长名称
										parentsName = jsonObjectparents.getString("userName");

										// 家长图片下载路径
										parentsPhotoUrl = jsonObjectparents.getString("photoUrl");
										savesp(strPhotoPath + j, parentsPhotoUrl);
										if (parentsPhotoUrl.length() > 0) {
											if (parentsPhotoUrl.endsWith(".jpg")) {
												Log.i("TPATT", "下载卡图片信息请求0:家长=" + parentsName + ";路径=" + parentsPhotoUrl);

												if (!appBaseFun
														.fileIsExists(appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo/Photo/"
																+ strPhotoPath + "/" + filename(parentsPhotoUrl))) {
													Bitmap mBitmap = httpApp.getNetWorkBitmap(parentsPhotoUrl);
													if (mBitmap != null) {
														boolean isok = appBaseFun.saveBitmapjpg(mBitmap,
																appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo/Photo/"
																		+ strPhotoPath + "/" + filename(parentsPhotoUrl));

														pi.get(i).getParentsPhotos().get(j).setIssucc(isok);
													}
												} else {
													pi.get(i).getParentsPhotos().get(j).setIssucc(true);
												}
											} else {
												Log.i("TPATT", "下载卡图片信息请求1:家长=" + parentsName + ";路径=" + parentsPhotoUrl);
											}
										} else {
											Log.i("TPATT", "下载卡图片信息请求2:家长=" + parentsName + ";路径=" + parentsPhotoUrl);
										}

										// addTime
										// String parentsAddTime =
										// jsonObjectparents.getString("addTime");

										// 保存图片信息:0表示学生姓名,1表示入园时间,2表示学校,3~7表示家长称呼
										// appBaseFun.savePhotoInfoFile(strPhotoPath,userName
										// + "\r\n" + addTime + "\r\n" +
										// schoolName);
									}
								}
							}
							setPlatformProtoStatus(3);
						}
					} catch (JSONException e) {

						Log.i("TPATT", "下载卡图片信息异常");

						e.printStackTrace();
					}
				}
			}
			break;

			default:
				break;
		}
	}

	private void savesp(String key, String value) {

		se.putString(key, value);
		se.commit();

	}

	private String filename(String url) {

		if (url == null) {
			return null;
		}

		return url.substring(url.lastIndexOf("/") + 1);
	}

}
