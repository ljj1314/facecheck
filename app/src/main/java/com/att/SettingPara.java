/**************************************************************************
 Copyright (C) 广东天波教育科技有限公司　版权所有
 文 件 名：
 创 建 人：
 创建时间：2015.10.30
 功能描述：参数设置、查询
 **************************************************************************/
package com.att;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//参数设置类
public class SettingPara {

	public static String PATH_SETTING_FILE = "/tpatttp/";
	public static String PATH_SETTING_NAME = "Setting.txt";
	public static String PATH_SETTING_NAME_TEMP = "SettingTemp.txt";
	public static String PATH_SETTING_TXT = PATH_SETTING_FILE + PATH_SETTING_NAME;
	public static String PATH_SETTING_TXT_TEMP = PATH_SETTING_FILE + PATH_SETTING_NAME_TEMP;

	private static String DEFAULT_OMC_URL = "183.238.195.77:10099";// 192.168.0.170

	// 旧的升级地址，已不用
	public static String DOWN_PATH_TEST = "http://121.9.230.130:8105/APK/test/";// 电信测试服务器，电信幼学通
	public static String DOWN_PATH_TEPLO = "http://121.9.230.130:8105/APK/teplo/";// 天波服务器，和宝贝
	public static String DOWN_PATH_MOBILE = "http://121.9.230.130:8105/APK/mobile/";// 移动幼儿宝，和宝贝
	public static String DOWN_PATH_TELCOM = "http://121.9.230.130:8105/APK/telcom/";// 电信发布服务器，幼学通

	// 新的升级地址，正在使用
	public static String DOWN_PATH_TEST2 = "http://121.9.230.130:8105/APK/test2/";// 移动测试服务器，暂无apk
	public static String DOWN_PATH_TEPLO2 = "http://121.9.230.130:8105/APK/teplo2/";// 天波测试服务器，健康童学
	public static String DOWN_PATH_MOBILE2 = "http://121.9.230.130:8105/APK/mobile2/";// 移动服务器，和宝贝
	public static String DOWN_PATH_TELCOM2 = "http://121.9.230.130:8105/APK/telcom2/";// 电信发布服务器，幼学通
	public static String DOWN_PATH_DADI = "http://121.9.230.130:8105/APK/dadi/";// 大地服务器
	public static String DOWN_PATH_ZW = "http://121.9.230.130:8105/APK/zhongwei/";// 中维服务器
	public static String DOWN_PATH_HDBB = "http://121.9.230.130:8105/APK/hdbb/";// 互动宝宝
	public static String DOWN_PATH_OMC = "http://121.9.230.130:8105/APK/OMC/";// OMC升级地址
	public static String DOWN_PATH_FJYXT = "http://121.9.230.130:8105/APK/fjyxt/";// 福建幼学通升级地址
	public static String DOWN_PATH_HET = "http://121.9.230.130:8105/APK/het/";// 和而泰升级地址

	// 万维协议幼学通（全国）
	public static String ATT_URL_CTXY_ALL = "http://yjifc.ctxy.cn/CreditCardRecords.ashx";
	public static String ATT_PIC_URL_CTXY_ALL = "http://yjifc.ctxy.cn/CreditCardPhotos.ashx";
	public static String PARENTS_PIC_URL_CTXY_ALL = "http://jk.ctxy.cn:8080/babySafely/getBabyPhotosList";
	public static String CARD_INFO_URL_CTXY_ALL = "http://yjifc.ctxy.cn/GetStudentInfos.ashx";
	public static String TEMP_URL_CTXY_ALL = "http://uxt.ctxy.cn:8081/morningNoonInspection/morningNoonInspectionInfo";

	// 万维协议幼学通（甘肃乐智）
	public static String ATT_URL_CTXY_GANSU = "http://yjifc1.ctxy.cn/CreditCardRecords.ashx";
	public static String ATT_PIC_URL_CTXY_GANSU = "http://yjifc1.ctxy.cn/CreditCardPhotos.ashx";
	public static String PARENTS_PIC_URL_CTXY_GANSU = "http://jk2.ctxy.cn:8080/getBabyPhotosList";
	public static String CARD_INFO_URL_CTXY_GANSU = "http://yjifc1.ctxy.cn/GetStudentInfos.ashx";
	public static String TEMP_URL_CTXY_GANSU = "http://jk2.ctxy.cn:8080/uxt//morningNoonInspection/morningNoonInspectionInfo";

	// URL后缀
	public static String ATT_URL = "/openAPI/CreditCardRecords.ashx";

	// 万维协议和宝贝平台
	public static String IP_TEST = "http://192.168.0.247:7987";// TODO 测试地址
	public static String IP_HEBB = "http://hebb1.jiankangtongxue.cn";
	public static String ATT_URL_HEBB = IP_HEBB + ATT_URL;
	public static String ATT_URL_HEBB_TEST = IP_TEST + ATT_URL;
	public static String ATT_PIC_URL_HEBB = IP_HEBB + "/openAPI/CreditCardPhotos.ashx";
	public static String PARENTS_PIC_URL_HEBB = IP_HEBB + "/openAPI/getBabyPhotosList.ashx";
	public static String CARD_INFO_URL_HEBB = IP_HEBB + "/openAPI/GetStudentInfos.ashx";
	public static String TEMP_URL_HEBB = IP_HEBB + "/openapi/morningNoonInspectionInfo.ashx";

	// 万维协议健康同学
	public static String ATT_URL_JKTX = "http://jktx1.jiankangtongxue.cn/openAPI/CreditCardRecords.ashx";
	public static String ATT_PIC_URL_JKTX = "http://jktx1.jiankangtongxue.cn/openAPI/CreditCardPhotos.ashx";
	public static String PARENTS_PIC_URL_JKTX = "http://jktx1.jiankangtongxue.cn/openAPI/getBabyPhotosList.ashx";
	public static String CARD_INFO_URL_JKTX = "http://jktx1.jiankangtongxue.cn/openAPI/GetStudentInfos.ashx";
	public static String TEMP_URL_JKTX = "http://jktx1.jiankangtongxue.cn/openapi/morningNoonInspectionInfo.ashx";

	// 万维协议大地教育
	public static String ATT_URL_DDJY = "http://ddjy1.jiankangtongxue.cn/openAPI/CreditCardRecords.ashx";
	public static String ATT_PIC_URL_DDJY = "http://ddjy1.jiankangtongxue.cn/openAPI/CreditCardPhotos.ashx";
	public static String CARD_INFO_URL_DDJY = "http://ddjy1.jiankangtongxue.cn/openAPI/GetStudentInfos.ashx";
	public static String PARENTS_PIC_URL_DDJY = "http://ddjy1.jiankangtongxue.cn/openAPI/getBabyPhotosList.ashx";
	public static String TEMP_URL_DDJY = "http://ddjy1.jiankangtongxue.cn/openAPI/morningNoonInspection/morningNoonInspectionInfo";

	// 万维协议和教育
	public static String ATT_URL_HEJY = "http://hejy1.jiankangtongxue.cn/openAPI/CreditCardRecords.ashx";
	public static String ATT_PIC_URL_HEJY = "http://hejy1.jiankangtongxue.cn/openAPI/CreditCardPhotos.ashx";
	public static String PARENTS_PIC_URL_HEJY = "http://hejy1.jiankangtongxue.cn/openAPI/getBabyPhotosList.ashx";
	public static String CARD_INFO_URL_HEJY = "http://hejy1.jiankangtongxue.cn/openAPI/GetStudentInfos.ashx";
	public static String TEMP_URL_HEJY = "http://hejy1.jiankangtongxue.cn/openapi/morningNoonInspectionInfo.ashx";

	// 万维协议互动宝宝
	public static String ATT_URL_HDBB = "http://hdbb1.jiankangtongxue.cn/openapi/CreditCardRecords.ashx";
	public static String ATT_PIC_URL_HDBB = "http://hdbb1.jiankangtongxue.cn/openapi/CreditCardPhotos.ashx";
	public static String PARENTS_PIC_URL_HDBB = "http://hdbb1.jiankangtongxue.cn/openAPI/getBabyPhotosList.ashx";
	public static String CARD_INFO_URL_HDBB = "http://hdbb1.jiankangtongxue.cn/openapi/GetStudentInfos.ashx";


	// 万维协议福建幼学通
	public static String ATT_URL_FJ = "http://wx.landray.com:81/yeyytj/CreditCardRecords.ashx";
	public static String ATT_PIC_URL_FJ = "http://wx.landray.com:81/yeyytj/CreditCardPhotos.ashx";
	public static String PARENTS_PIC_URL_FJ = "http://wx.landray.com:81/yeyytj/getBabyPhotosList.ashx";
	public static String CARD_INFO_URL_FJ = "http://wx.landray.com:81/yeyytj/GetStudentInfos.ashx";
	public static String TEMP_URL_FJ = "http://wx.landray.com:81/yeyytj/morningNoonInspectionInfo.ashx";

	//万维协议和尔泰
	public static String ATT_URL_het = "https://dp.clife.net/v1/web/campus/device/attendance/getStudentInfos";
	public static String ATT_PIC_URL_het = "https://dp.clife.net/v1/web/campus/device/attendance/creditCardPhotos";
	//	public static String PARENTS_PIC_URL_FJ = "http://wx.landray.com:81/yeyytj/getBabyPhotosList.ashx";
	public static String CARD_INFO_URL_het = "https://test.cms.clife.cn/v1/web/campus/device/attendance/getStudentInfos";
//	public static String TEMP_URL_FJ = "http://wx.landray.com:81/yeyytj/morningNoonInspectionInfo.ashx";
//

	public static String[] ATT_URLS = { ATT_URL_CTXY_ALL, ATT_URL_CTXY_GANSU, ATT_URL_HEBB, ATT_URL_JKTX, ATT_URL_DDJY,
			ATT_URL_HEJY,ATT_URL_FJ ,ATT_URL_het};// 考勤上报
	public static String[] ATT_PIC_URLS = { ATT_PIC_URL_CTXY_ALL, ATT_PIC_URL_CTXY_GANSU, ATT_PIC_URL_HEBB,
			ATT_PIC_URL_JKTX, ATT_PIC_URL_DDJY, ATT_PIC_URL_HEJY,ATT_PIC_URL_FJ ,ATT_PIC_URL_het};// 考勤图片上报
	public static String[] PARENTS_PIC_URLS = { PARENTS_PIC_URL_CTXY_ALL, PARENTS_PIC_URL_CTXY_GANSU,
			PARENTS_PIC_URL_HEBB, PARENTS_PIC_URL_JKTX, PARENTS_PIC_URL_DDJY, PARENTS_PIC_URL_HEJY,PARENTS_PIC_URL_FJ,"" };// 家长图片下载
	public static String[] CARD_INFO_URLS = { CARD_INFO_URL_CTXY_ALL, CARD_INFO_URL_CTXY_GANSU, CARD_INFO_URL_HEBB,
			CARD_INFO_URL_JKTX, CARD_INFO_URL_DDJY, CARD_INFO_URL_HEJY ,CARD_INFO_URL_FJ,CARD_INFO_URL_het};// 卡信息下载
	public static String[] TEMP_URLS = { TEMP_URL_CTXY_ALL, TEMP_URL_CTXY_GANSU, TEMP_URL_HEBB, TEMP_URL_JKTX,
			TEMP_URL_DDJY, TEMP_URL_HEJY ,TEMP_URL_FJ,""};// 体温上报


	public static int PLATFORM_REGION = 3;// 0万维协议幼学通（全国） 2和宝贝 3健康同学4大地教育5和教育6福建幼学通7和尔泰
	private String ap_version; // 蓝牙AP版本号

	private String omcurl; // OMC网管URL
	private String omc_update_topline; // OMC自身更新指令号
	private int omc_update_vercode;// OMC新版本号
	private int omc_heartbeat; // OMC心跳时间

	private SharedPreferences sp;
	private SharedPreferences.Editor se;
	public static String OMC_SP_NAME = "omc";
	public static String OMC_SP_RIVER_NUM = "rivernum";
	public static String OMC_SP_IMEI = "imei";
	public static String OMC_SP_DEVICE_ID = "deviceid";
	public static String OMC_SP_IP = "ip";
	public static String OMC_SP_PORT = "port";
	public static String OMC_SP_AP_TIME = "aptime";// AP最近一次返回数据时间
	public static String OMC_SP_HEART_BEAT = "heartbeat";
	public static String OMC_SP_RE_BOOT_COUNT = "rebootcount";
	public static String OMC_SP_CLOUD_STORAGE_TOKEN = "cloud_storage_token";
	public static String OMC_SP_CLOUD_STORAGE_PREFIX = "cloud_storage_prefix";
	public static String OMC_SP_CLOUD_STORAGE_HOST = "cloud_storage_host";

	private String att_url; // 考勤上报URL
	private String att_pic_url; // 考勤图片上报URL
	private String parents_pic_url; // 家长图片下载URL
	private String card_info_url; // 卡信息下载URL
	private String tempurl; // 体温上报url

	private String device_id; // 终端ID
	private String school_id; // 学校ID
	private String province_id; // 省份编码

	private int att_platform = -1; // 考勤平台协议
	private int att_pic_platform; // 考勤图片平台协议
	private int card_buma; // 卡号补码
	private int card_disp; // 卡号显示格式
	private int card_upload; // 卡号上报格式
	private int att_upload_sum; // 卡批量上报数量
	private int att_upload_space; // 重复刷卡限制时间
	private static int DEFAULT_ATT_UPLOAD_SPACE = 0;// 1800

	private String[] go_school_start; // 上学时间段开始时间
	private String[] go_school_end; // 上学时间段结束时间
	private String[] out_school_start; // 放学时间段开始时间
	private String[] out_school_end; // 放学时间段结束时间

	private int go_school_t1_voice; // 上学时间段1语音提示
	private int go_school_t2_voice; // 上学时间段2语音提示
	private int go_school_t3_voice; // 上学时间段3语音提示
	private int out_school_t1_voice; // 放学时间段1语音提示
	private int out_school_t2_voice; // 放学时间段2语音提示
	private int out_school_t3_voice; // 放学时间段3语音提示

	private int heartbeat; // 心跳时间
	private int att_timeouts; // 考勤界面超时时间
	private int idle_pic_duration; // 待机轮播图片间隔
	private int updatetime; // 升级间隔

	private boolean card_reversal; // 卡号反转
	private boolean idle_vedio_switch; // 待机轮播视频开关
	private boolean carderr_voice_tips; // 无效卡语音提示
	private boolean isallscreen; // 是否全屏播放
	private boolean is_cloud_storage; // 是否启用云存储

	private boolean take_photo; // 抓拍开关
	private boolean take_internet; // 数据开关
	private boolean take_voice; // 语音开关
	private boolean isap; // 蓝牙AP开关设置
	private boolean iscsv; // 导入本地CSV文件开关设置
	private boolean islg; // 厂家logo开关设置
	private boolean issgid; // 学校ID开关设置
	private boolean istcap; // 透传蓝牙AP开关设置
	private boolean isenzero; // 卡号补0
	private boolean isuninstall;// 是否被主动卸载

	private String admin_passwd; // 管理员密码
	private String account; // 账号设置
	private String password; // 密码设置
	private String schoolname; // 学校名称设置

	private boolean isallvideo;//完整播报
	private boolean isclassvideo;//班级播报

	private boolean isbleban;//板载蓝牙，和宝贝

	private String volume;  //容量，默认是1g
	private boolean issportatt;  //运动考勤

	public static String getDownPath() {
		String downPath = DOWN_PATH_TEST2;
		SettingPara settingPara = new SettingPara();
		if (settingPara.getAtt_pic_platform() == 0) {
			if (settingPara.getAtt_url() != null && settingPara.getAtt_url().length() > 0) {
				int index = -1;
				for (int i = 0; i < SettingPara.ATT_URLS.length; i++) {
					if (SettingPara.ATT_URLS[i].equals(settingPara.getAtt_url())) {
						index = i;
					}
				}
				if (index >= 0) {
					switch (index) {
						case 0:
							downPath = DOWN_PATH_TELCOM2;// 幼学通
							break;
						case 1:
							downPath = DOWN_PATH_TELCOM2;// 幼学通
							break;
						case 2:
							downPath = DOWN_PATH_MOBILE2;// 和宝贝
							break;
						case 3:
							downPath = DOWN_PATH_TEPLO2;// 健康童学
							break;
						case 4:
							downPath = DOWN_PATH_DADI;// 大地教育
							break;
						case 5:
							downPath = DOWN_PATH_TEPLO2;// 和教育
							break;
						case 6:
							downPath = DOWN_PATH_FJYXT;//福建幼学通

							break;
						case 7:
							downPath = DOWN_PATH_HET;//和而泰

							break;

						default:
							break;
					}
				} else if (ATT_URL_HDBB.equals(settingPara.getAtt_url())) {
					downPath = DOWN_PATH_HDBB;// 互动宝宝
				}
			}
		} else if (settingPara.getAtt_pic_platform() == 1) {
			return DOWN_PATH_TELCOM2;// 能龙平台
		} else if (settingPara.getAtt_pic_platform() == 2) {
			return DOWN_PATH_ZW;// 中维平台
		}
		return downPath;
	}

	// 考勤上报URL
	public String getAtt_url() {
		return att_url;
	}

	public void setAtt_url(String att_url) {
		this.att_url = att_url;
	}

	// 考勤图片上报URL
	public String getAtt_pic_url() {
		return att_pic_url;
	}

	public void setAtt_pic_url(String att_pic_url) {
		this.att_pic_url = att_pic_url;
	}

	// 家长图片下载URL
	public String getParents_pic_url() {
		return parents_pic_url;
	}

	public void setParents_pic_url(String parents_pic_url) {
		this.parents_pic_url = parents_pic_url;
	}

	// 卡信息下载URL
	public String getCard_info_url() {
		return card_info_url;
	}

	public void setCard_info_url(String card_info_url) {
		this.card_info_url = card_info_url;
	}

	// 终端ID
	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	// 学校ID
	public String getSchool_id() {
		return school_id;
	}

	public void setSchool_id(String school_id) {
		this.school_id = school_id;
	}

	// 省份编码
	public String getProvince_id() {
		return province_id;
	}

	public void setProvince_id(String province_id) {
		this.province_id = province_id;
	}

	// 考勤平台
	public int getAtt_platform() {
		// return att_platform;
		return getAtt_pic_platform();
	}

	public void setAtt_platform(int att_platform) {
		this.att_platform = att_platform;
	}

	// 考勤图片平台
	public int getAtt_pic_platform() {
		return att_pic_platform;
	}

	public void setAtt_pic_platform(int att_pic_platform) {
		this.att_pic_platform = att_pic_platform;
	}

	// 卡显示格式
	public int getCard_disp() {
		return card_disp;
	}

	public void setCard_disp(int card_disp) {
		this.card_disp = card_disp;
	}

	// 卡上报格式
	public int getCard_upload() {
		return card_upload;
	}

	public void setCard_upload(int card_upload) {
		this.card_upload = card_upload;
	}

	// 每包最大上传条数
	public int getAtt_upload_sum() {
		return att_upload_sum;
	}

	public void setAtt_upload_sum(int att_upload_sum) {
		this.att_upload_sum = att_upload_sum;
	}

	// 卡号补码
	public int getCard_buma() {
		return card_buma;
	}

	public void setCard_buma(int card_buma) {
		this.card_buma = card_buma;
	}

	// 卡号反转
	public boolean isCard_reversal() {
		return card_reversal;
	}

	public void setCard_reversal(boolean card_reversal) {
		this.card_reversal = card_reversal;
	}

	// 上学时间段开始时间
	public String[] getGo_school_start() {
		return go_school_start;
	}

	public void setGo_school_start(String[] go_school_start) {
		this.go_school_start = go_school_start;
	}

	// 上学时间段结束时间
	public String[] getGo_school_end() {
		return go_school_end;
	}

	public void setGo_school_end(String[] go_school_end) {
		this.go_school_end = go_school_end;
	}

	// 放学时间段开始时间
	public String[] getOut_school_start() {
		return out_school_start;
	}

	public void setOut_school_start(String[] out_school_start) {
		this.out_school_start = out_school_start;
	}

	// 放学时间段结束时间
	public String[] getOut_school_end() {
		return out_school_end;
	}

	public void setOut_school_end(String[] out_school_end) {
		this.out_school_end = out_school_end;
	}

	// 上学时间段1语音提示
	public int getGo_school_t1_voice() {
		return go_school_t1_voice;
	}

	public void setGo_school_t1_voice(int go_school_t1_voice) {
		this.go_school_t1_voice = go_school_t1_voice;
	}

	// 上学时间段2语音提示
	public int getGo_school_t2_voice() {
		return go_school_t2_voice;
	}

	public void setGo_school_t2_voice(int go_school_t2_voice) {
		this.go_school_t2_voice = go_school_t2_voice;
	}

	// 上学时间段3语音提示
	public int getGo_school_t3_voice() {
		return go_school_t3_voice;
	}

	public void setGo_school_t3_voice(int go_school_t3_voice) {
		this.go_school_t3_voice = go_school_t3_voice;
	}

	// 放学时间段1语音提示
	public int getOut_school_t1_voice() {
		return out_school_t1_voice;
	}

	public void setOut_school_t1_voice(int out_school_t1_voice) {
		this.out_school_t1_voice = out_school_t1_voice;
	}

	// 放学时间段2语音提示
	public int getOut_school_t2_voice() {
		return out_school_t2_voice;
	}

	public void setOut_school_t2_voice(int out_school_t2_voice) {
		this.out_school_t2_voice = out_school_t2_voice;
	}

	// 放学时间段3语音提示
	public int getOut_school_t3_voice() {
		return out_school_t3_voice;
	}

	public void setOut_school_t3_voice(int out_school_t3_voice) {
		this.out_school_t3_voice = out_school_t3_voice;
	}

	// 心跳包
	public int getHeartbeat() {
		return heartbeat;
	}

	public void setHeartbeat(int heartbeat) {
		this.heartbeat = heartbeat;
	}

	// 考勤界面超时时间
	public int getAtt_timeouts() {
		return att_timeouts;
	}

	public void setAtt_timeouts(int att_timeouts) {
		this.att_timeouts = att_timeouts;
	}

	// 轮播图片间隔
	public int getIdle_pic_duration() {
		return idle_pic_duration;
	}

	public void setIdle_pic_duration(int idle_pic_duration) {
		this.idle_pic_duration = idle_pic_duration;
	}

	// 轮播视频开关
	public boolean isIdle_vedio_switch() {
		return idle_vedio_switch;
	}

	public void setIdle_vedio_switch(boolean idle_vedio_switch) {
		this.idle_vedio_switch = idle_vedio_switch;
	}

	// 无效卡语音提示
	public boolean isCarderr_voice_tips() {
		return carderr_voice_tips;
	}

	public void setCarderr_voice_tips(boolean carderr_voice_tips) {
		this.carderr_voice_tips = carderr_voice_tips;
	}

	// 抓拍开关
	public boolean isTake_photo() {
		return take_photo;
	}

	public void setTake_photo(boolean take_photo) {
		this.take_photo = take_photo;
	}

	// 管理员密码
	public String getAdmin_passwd() {
		return admin_passwd;
	}

	public void setAdmin_passwd(String admin_passwd) {
		this.admin_passwd = admin_passwd;
	}

	// 图片轮播放时间
	public long getPlayPhotoTime() {
		int temp = getIdle_pic_duration();
		if (temp < 8) {
			return 8000;
		} else {
			return 1000 * temp;
		}
	}

	// 获取管理员密码
	public String getAdminPassword() {

		String temp = getAdmin_passwd();
		if (temp.length() == 0) {
			return "1234";
		} else {
			return temp;
		}
	}

	// 获取每包最大考勤条数
	public int getMaxUploadAttCount() {

		int temp = getAtt_upload_sum();
		if (temp < 1) {
			return 1;
		} else {
			return temp;
		}
	}

	// 普通考勤平台协议
	public int getAttPlatformProto() {

		// return getAtt_platform();
		return getAtt_pic_platform();
	}

	// 考勤图片平台协议
	public int getAttPhotoPlatformProto() {

		return getAtt_pic_platform();
	}

	// 获取省份编码
	public String getProvincCode() {

		return getProvince_id();// "62000000";
	}

	// 获取学校ID
	public String getSchoolID() {

		return getSchool_id();// "ae5341d0-2dfd-4633-868d-dbdecf0382f2";//
	}

	// 获取终端
	public String getDevicID() {

		return getDevice_id();// "62010004";//
	}

	// 获取考勤平台URL
	public String getAttPlatformUrl() {
		return getAtt_url();// "http://yjifc1.ctxy.cn/CreditCardRecords.ashx";//
	}

	// 获取考勤图片平台URL
	public String getAttPhotoPlatformUrl() {

		return getAtt_pic_url();// "http://yjifc1.ctxy.cn/CreditCardPhotos.ashx";//
	}

	// 获取卡信息URL
	public String getCardInfoUrl() {

		return getCard_info_url();// "http://yjifc1.ctxy.cn/GetStudentInfos.ashx";//
	}

	// 获取卡接送家长图片信息URL
	public String getCardPhotoInfoUrl() {

		return getParents_pic_url();// "http://yjifc1.ctxy.cn:8080/babySafely/getBabyPhotosList";//
	}

	public long getSwingCardDisplayTimeout() {

		int temp = getAtt_timeouts();
		if (temp < 10) {
			return 10000;
		} else {
			return 1000 * temp;
		}
	}

	public void reset_settingparam() {

		att_url = ATT_URLS[PLATFORM_REGION];
		att_pic_url = ATT_PIC_URLS[PLATFORM_REGION];
		//	parents_pic_url = CARD_INFO_URLS[PLATFORM_REGION];
		parents_pic_url = PARENTS_PIC_URLS[PLATFORM_REGION];
		card_info_url = CARD_INFO_URLS[PLATFORM_REGION];
		tempurl = TEMP_URLS[PLATFORM_REGION];

		// device_id = "62010004";
		// school_id = "ae5341d0-2dfd-4633-868d-dbdecf0382f2";

		// device_id = "41010453";
		// school_id = "FA769957-C4E5-4711-A939-936C261F9378";

		device_id = "";
		school_id = "";

		// province_id = "62000000";

		// province_id = "41000000";

		province_id = "62000000";

		att_platform = 0;
		att_pic_platform = 0;
		card_disp = 0;
		card_upload = 0;
		att_upload_sum = 1;
		att_upload_space = DEFAULT_ATT_UPLOAD_SPACE;
		card_buma = 0;
		card_reversal = false;

		String values1[] = { "6:30", "6:30", "6:30" };
		go_school_start = values1;
		String values2[] = { "9:30", "9:30", "9:30" };
		go_school_end = values2;
		String values3[] = { "15:30", "15:30", "15:30" };
		out_school_start = values3;
		String values4[] = { "19:30", "19:30", "19:30" };
		out_school_end = values4;

		go_school_t1_voice = 0;
		go_school_t2_voice = 0;
		go_school_t3_voice = 0;
		out_school_t1_voice = 0;
		out_school_t2_voice = 0;
		out_school_t3_voice = 0;

		heartbeat = 60;
		att_timeouts = 30;
		idle_pic_duration = 10;
		idle_vedio_switch = false;
		carderr_voice_tips = true;
		take_photo = true;
		admin_passwd = "1234";
		take_internet = true;
		take_voice = true;
		account = "";
		password = "";
		schoolname = "";
		isap = false;
		iscsv = false;
		islg = true;
		issgid = false;
		istcap = false;
		isenzero = false;
		updatetime = 1800;
		isallscreen = false;
		is_cloud_storage = true;

		isuninstall = false;
		ap_version = "";
		omcurl = DEFAULT_OMC_URL;
		omc_update_topline = "";
		omc_update_vercode = 0;
		omc_heartbeat = 1800;


		isallvideo=false;
		isclassvideo=false;

		isbleban=false;

		volume="1";

		issportatt=false;

	}

	public boolean save_settingpara() {
		try {
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("att_url", att_url);
			jsonObject.put("att_pic_url", att_pic_url);
			jsonObject.put("parents_pic_url", parents_pic_url);
			jsonObject.put("card_info_url", card_info_url);
			jsonObject.put("device_id", device_id);
			jsonObject.put("school_id", school_id);
			jsonObject.put("province_id", province_id);
			jsonObject.put("att_platform", att_platform);
			jsonObject.put("att_pic_platform", att_pic_platform);
			jsonObject.put("card_disp", card_disp);
			jsonObject.put("card_upload", card_upload);
			jsonObject.put("att_upload_sum", att_upload_sum);
			jsonObject.put("att_upload_space", att_upload_space);
			jsonObject.put("card_buma", card_buma);
			jsonObject.put("card_reversal", card_reversal);

			String str = new String();
			for (String value : go_school_start) {
				str += value;
				str += "#";
			}
			jsonObject.put("go_school_start", str);

			str = "";
			for (String value : go_school_end) {
				str += value;
				str += "#";
			}
			jsonObject.put("go_school_end", str);

			str = "";
			for (String value : out_school_start) {
				str += value;
				str += "#";
			}
			jsonObject.put("out_school_start", str);

			str = "";
			for (String value : out_school_end) {
				str += value;
				str += "#";
			}
			jsonObject.put("out_school_end", str);

			jsonObject.put("go_school_t1_voice", go_school_t1_voice);
			jsonObject.put("go_school_t2_voice", go_school_t2_voice);
			jsonObject.put("go_school_t3_voice", go_school_t3_voice);
			jsonObject.put("out_school_t1_voice", out_school_t1_voice);
			jsonObject.put("out_school_t2_voice", out_school_t2_voice);
			jsonObject.put("out_school_t3_voice", out_school_t3_voice);

			jsonObject.put("heartbeat", heartbeat);
			jsonObject.put("att_timeouts", att_timeouts);
			jsonObject.put("idle_pic_duration", idle_pic_duration);
			jsonObject.put("idle_vedio_switch", idle_vedio_switch);
			jsonObject.put("carderr_voice_tips", carderr_voice_tips);
			jsonObject.put("take_photo", take_photo);
			jsonObject.put("admin_passwd", admin_passwd);
			jsonObject.put("take_internet", take_internet);
			jsonObject.put("take_voice", take_voice);
			jsonObject.put("account", account);
			jsonObject.put("password", password);
			jsonObject.put("isap", isap);
			jsonObject.put("iscsv", iscsv);
			jsonObject.put("schoolname", schoolname);
			jsonObject.put("islg", islg);
			jsonObject.put("issgid", issgid);
			jsonObject.put("istcap", istcap);
			jsonObject.put("isenzero", isenzero);
			jsonObject.put("tempurl", tempurl);
			jsonObject.put("updatetime", updatetime);
			jsonObject.put("isallscreen", isallscreen);
			jsonObject.put("is_cloud_storage", is_cloud_storage);

			jsonObject.put("ap_version", ap_version);
			jsonObject.put("omcurl", omcurl);
			jsonObject.put("omc_update_topline", omc_update_topline);
			jsonObject.put("isuninstall", isuninstall);
			jsonObject.put("omc_update_vercode", omc_update_vercode);
			jsonObject.put("omc_heartbeat", omc_heartbeat);

			jsonObject.put("isallvideo", isallvideo);
			jsonObject.put("isclassvideo", isclassvideo);

			jsonObject.put("isbleban", isbleban);

			jsonObject.put("volume", volume);
			jsonObject.put("issportatt", issportatt);

			return writeFileSdcard(jsonObject.toString());
		} catch (Exception e) {
			Log.i("debug", "save_settingpara() err!");
			return false;
		}
	}

	public boolean writeFileSdcard(String message) {
		AppBaseFun appbasefun = new AppBaseFun();
		String filePathTemp = appbasefun.getPhoneCardPath() + PATH_SETTING_TXT_TEMP;
		File fileTemp = new File(filePathTemp);
		if (fileTemp.exists()) {
			fileTemp.delete();
		}
		String filePath = appbasefun.getPhoneCardPath() + PATH_SETTING_TXT;
		File file = new File(filePath);
		if (file.exists()) {
			file.renameTo(fileTemp);
		}
		appbasefun.makeFilePath(appbasefun.getPhoneCardPath() + PATH_SETTING_FILE, PATH_SETTING_NAME);
		return appbasefun.writeFileSdcard(filePath, message);
	}

	public String readFileSdcard() {
		AppBaseFun appbasefun = new AppBaseFun();
		String text = null;
		String filePath = appbasefun.getPhoneCardPath() + PATH_SETTING_TXT;
		File file = new File(filePath);
		if (file.exists()) {
			text = appbasefun.readFileSdcard(filePath);
			// Log.i("TPATT", "filetext:"+text);
		}
		if (text == null || text.length() == 0) {
			String filePathTemp = appbasefun.getPhoneCardPath() + PATH_SETTING_TXT_TEMP;
			File fileTemp = new File(filePathTemp);
			if (fileTemp.exists()) {
				text = appbasefun.readFileSdcard(filePathTemp);
				// Log.i("TPATT", "fileTemptext:"+text);
				if (file.exists()) {
					file.delete();
				}
				fileTemp.renameTo(file);
			}
		}
		return text;
	}

	public SettingPara(Context context) {
		sp = context.getSharedPreferences(SettingPara.OMC_SP_NAME, Context.MODE_PRIVATE);
		se = sp.edit();
		getSettingpara();
	}

	public SettingPara() {
		getSettingpara();
	}

	private void getSettingpara() {
		try {
			String text = readFileSdcard();
			if (text != null) {
				JSONObject jsonObject = new JSONObject(text);
				att_url = jsonObject.getString("att_url");
				att_pic_url = jsonObject.getString("att_pic_url");
				parents_pic_url = jsonObject.getString("parents_pic_url");
				card_info_url = jsonObject.getString("card_info_url");
				device_id = jsonObject.getString("device_id");
				school_id = jsonObject.getString("school_id");
				province_id = jsonObject.getString("province_id");
				att_platform = jsonObject.getInt("att_platform");
				att_pic_platform = jsonObject.getInt("att_pic_platform");
				card_disp = jsonObject.getInt("card_disp");
				card_upload = jsonObject.getInt("card_upload");
				att_upload_sum = jsonObject.getInt("att_upload_sum");
				try {
					att_upload_space = jsonObject.getInt("att_upload_space");
				} catch (Exception e) {
					att_upload_space = DEFAULT_ATT_UPLOAD_SPACE;
				}
				card_buma = jsonObject.getInt("card_buma");
				card_reversal = jsonObject.getBoolean("card_reversal");

				String values = new String();
				values = jsonObject.getString("go_school_start");
				go_school_start = values.split("#");
				values = jsonObject.getString("go_school_end");
				go_school_end = values.split("#");
				values = jsonObject.getString("out_school_start");
				out_school_start = values.split("#");
				values = jsonObject.getString("out_school_end");
				out_school_end = values.split("#");

				go_school_t1_voice = jsonObject.getInt("go_school_t1_voice");
				go_school_t2_voice = jsonObject.getInt("go_school_t2_voice");
				go_school_t3_voice = jsonObject.getInt("go_school_t3_voice");
				out_school_t1_voice = jsonObject.getInt("out_school_t1_voice");
				out_school_t2_voice = jsonObject.getInt("out_school_t2_voice");
				out_school_t3_voice = jsonObject.getInt("out_school_t3_voice");

				heartbeat = jsonObject.getInt("heartbeat");
				att_timeouts = jsonObject.getInt("att_timeouts");
				idle_pic_duration = jsonObject.getInt("idle_pic_duration");
				idle_vedio_switch = jsonObject.getBoolean("idle_vedio_switch");
				carderr_voice_tips = jsonObject.getBoolean("carderr_voice_tips");
				take_photo = jsonObject.getBoolean("take_photo");
				admin_passwd = jsonObject.getString("admin_passwd");
				take_internet = jsonObject.getBoolean("take_internet");
				take_voice = jsonObject.getBoolean("take_voice");

				try {
					account = jsonObject.getString("account");
				} catch (Exception e) {
					account = "";
				}
				try {
					password = jsonObject.getString("password");
				} catch (Exception e) {
					password = "";
				}
				try {
					schoolname = jsonObject.getString("schoolname");
				} catch (Exception e) {
					schoolname = "";
				}
				try {
					isap = jsonObject.getBoolean("isap");
				} catch (Exception e) {
					isap = false;
				}
				try {
					iscsv = jsonObject.getBoolean("iscsv");
				} catch (Exception e) {
					iscsv = false;
				}

				try {
					islg = jsonObject.getBoolean("islg");
				} catch (Exception e) {
					islg = true;
				}

				try {
					issgid = jsonObject.getBoolean("issgid");
				} catch (Exception e) {

					issgid = false;
				}
				try {
					istcap = jsonObject.getBoolean("istcap");
				} catch (Exception e) {
					istcap = false;
				}

				try {
					isenzero = jsonObject.getBoolean("isenzero");
				} catch (Exception e) {
					isenzero = false;
				}

				try {
					tempurl = jsonObject.getString("tempurl");
				} catch (Exception e) {
					tempurl = "";
				}

				try {
					updatetime = jsonObject.getInt("updatetime");
				} catch (Exception e) {
					updatetime = 1800;
				}

				try {
					isallscreen = jsonObject.getBoolean("isallscreen");
				} catch (Exception e) {
					isallscreen = false;
				}

				try {
					is_cloud_storage = jsonObject.getBoolean("is_cloud_storage");
				} catch (Exception e) {
					is_cloud_storage = true;
				}

				try {
					ap_version = jsonObject.getString("ap_version");
				} catch (Exception e) {
					ap_version = "";
				}

				try {
					omcurl = jsonObject.getString("omcurl");
				} catch (Exception e) {
					omcurl = DEFAULT_OMC_URL;
				}

				try {
					omc_update_topline = jsonObject.getString("omc_update_topline");
				} catch (Exception e) {
					omc_update_topline = "";
				}

				try {
					isuninstall = jsonObject.getBoolean("isuninstall");
				} catch (Exception e) {
					isuninstall = false;
				}

				try {
					omc_heartbeat = jsonObject.getInt("omc_heartbeat");
				} catch (Exception e) {
					omc_heartbeat = 1800;
				}

				try {
					omc_update_vercode = jsonObject.getInt("omc_update_vercode");
				} catch (Exception e) {
					omc_update_vercode = 0;
				}

				try {
					isallvideo=jsonObject.getBoolean("isallvideo");
				} catch (Exception e) {
					// TODO: handle exception
					isallvideo=false;
				}

				try {
					isclassvideo=jsonObject.getBoolean("isclassvideo");
				} catch (Exception e) {
					// TODO: handle exception
					isclassvideo=false;
				}


				try {
					isbleban=jsonObject.getBoolean("isbleban");
				} catch (Exception e) {
					// TODO: handle exception
					isbleban=false;
				}


				try {
					volume=jsonObject.getString("volume");
				} catch (Exception e) {
					// TODO: handle exception
					volume="1";
				}

				try {

					issportatt=jsonObject.getBoolean("issportatt");
				} catch (Exception e) {
					// TODO: handle exception
				}


			} else {
				// Log.i("TPATT", "reset_settingparam:"+text);
				reset_settingparam();
				save_settingpara();
			}
		} catch (Exception e) {
			Log.i("debug", "SettingPara() err!");
		}
	}

	public boolean isTake_internet() {
		return take_internet;
	}

	public void setTake_internet(boolean take_internet) {
		this.take_internet = take_internet;
	}

	public boolean isTake_voice() {
		return take_voice;
	}

	public void setTake_voice(boolean take_voice) {
		this.take_voice = take_voice;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSchoolname() {
		return schoolname;
	}

	public void setSchoolname(String schoolname) {
		this.schoolname = schoolname;
	}

	public boolean isIsap() {
		return isap;
	}

	public void setIsap(boolean isap) {
		this.isap = isap;
	}

	public boolean isIscsv() {
		return iscsv;
	}

	public void setIscsv(boolean iscsv) {
		this.iscsv = iscsv;
	}

	public String getOmc_update_topline() {
		return omc_update_topline;
	}

	public void setOmc_update_topline(String omc_update_topline) {
		this.omc_update_topline = omc_update_topline;
	}

	public String getAp_version() {
		return ap_version;
	}

	public void setAp_version(String ap_version) {
		this.ap_version = ap_version;
	}

	public String getOmcurl() {
		return omcurl;
	}

	public void setOmcurl(String omcurl) {
		String[] urls = omcurl.split(":");
		if (urls != null && urls.length == 2) {
			try {
				String ip = urls[0];
				int port = Integer.parseInt(urls[1]);
				setOmcurl(ip, port);
			} catch (Exception e) {
				this.omcurl = omcurl;
			}
		} else {
			this.omcurl = omcurl;
		}
	}

	public void setOmcurl(String ip, int port) {
		if (sp != null && se != null) {
			se.putString(OMC_SP_IP, ip);
			se.putInt(OMC_SP_PORT, port);
			se.commit();
		}
		this.omcurl = ip + ":" + port;
	}

	public String getOmcIP() {
		String ip = "";
		if (sp != null) {
			String sp_ip = sp.getString(OMC_SP_IP, null);
			if (sp_ip != null && sp_ip.length() > 0) {
				return sp_ip;
			}
		}
		String url = getOmcurl();
		if (url == null || url.length() == 0) {
			url = DEFAULT_OMC_URL;
		}
		String[] urls = url.split(":");
		ip = (urls != null && urls.length == 2) ? urls[0] : "";
		return ip;
	}

	public int getOmcPort() {
		if (sp != null) {
			int sp_port = sp.getInt(OMC_SP_PORT, 0);
			if (sp_port > 0) {
				return sp_port;
			}
		}
		String url = getOmcurl();
		if (url == null || url.length() == 0) {
			url = DEFAULT_OMC_URL;
		}
		String[] urls = url.split(":");
		if (urls != null && urls.length == 2) {
			try {
				return Integer.parseInt(urls[1]);
			} catch (Exception e) {
			}
		}
		return 0;
	}

	// 重启次数记录
	public void addReBootCount() {
		if (sp != null && se != null) {
			int count = sp.getInt(OMC_SP_RE_BOOT_COUNT, 0);
			count++;
			se.putInt(OMC_SP_RE_BOOT_COUNT, count);
			se.commit();
		}
	}

	public void clearReBootCount() {
		if (sp != null && se != null) {
			se.putInt(OMC_SP_RE_BOOT_COUNT, 0);
			se.commit();
		}
	}

	public int getReBootCount() {
		if (sp != null) {
			return sp.getInt(OMC_SP_RE_BOOT_COUNT, 0);
		}
		return 0;
	}

	public boolean statusAP() {
		Long lastTime = getApTime();
		Long nowTime = new Date().getTime();
		if(nowTime - lastTime < 1000 * 60 * 2){
			return true;
		}
		return false;
	}

	// AP最近一次返回数据时间
	public Long getApTime() {
		if (sp != null) {
			return sp.getLong(OMC_SP_AP_TIME, 0);
		}
		return null;
	}

	public void setApTime(Long time) {
		if (sp != null && se != null) {
			se.putLong(OMC_SP_AP_TIME, time);
			se.commit();
		}
	}

	public void setApTime() {
		Long time = new Date().getTime();
		setApTime(time);
	}

	// 云储存参数
	public String getCloudStorageToken() {
		if (sp != null) {
			return sp.getString(OMC_SP_CLOUD_STORAGE_TOKEN, null);
		}
		return null;
	}

	public void setCloudStorageToken(String token) {
		if (sp != null && se != null) {
			se.putString(OMC_SP_CLOUD_STORAGE_TOKEN, token);
			se.commit();
		}
	}

	public String getCloudStoragePrefix() {
		if (sp != null) {
			return sp.getString(OMC_SP_CLOUD_STORAGE_PREFIX, null);
		}
		return null;
	}

	public void setCloudStoragePrefix(String prefix) {
		if (sp != null && se != null) {
			se.putString(OMC_SP_CLOUD_STORAGE_PREFIX, prefix);
			se.commit();
		}
	}

	public String getCloudStorageHost() {
		if (sp != null) {
			return sp.getString(OMC_SP_CLOUD_STORAGE_HOST, null);
		}
		return null;
	}

	public void setCloudStorageHost(String host) {
		if (sp != null && se != null) {
			se.putString(OMC_SP_CLOUD_STORAGE_HOST, host);
			se.commit();
		}
	}

	public boolean isIslg() {
		return islg;
	}

	public void setIslg(boolean islg) {
		this.islg = islg;
	}

	public boolean isIssgid() {
		return issgid;
	}

	public void setIssgid(boolean issgid) {
		this.issgid = issgid;
	}

	public boolean isIstcap() {
		return istcap;
	}

	public void setIstcap(boolean istcap) {
		this.istcap = istcap;
	}

	public boolean isIsenzero() {
		return isenzero;
	}

	public void setIsenzero(boolean isenzero) {
		this.isenzero = isenzero;
	}

	public String getTempurl() {
		return tempurl;
	}

	public void setTempurl(String tempurl) {
		this.tempurl = tempurl;
	}

	public int getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(int updatetime) {
		this.updatetime = updatetime;
	}

	public boolean isIsallscreen() {
		return isallscreen;
	}

	public void setIsallscreen(boolean isallscreen) {
		this.isallscreen = isallscreen;
	}

	public boolean isIs_cloud_storage() {
		return is_cloud_storage;// TODO 云存储
	}

	public void setIs_cloud_storage(boolean is_cloud_storage) {
		this.is_cloud_storage = is_cloud_storage;
	}

	public boolean isIsuninstall() {
		return isuninstall;
	}

	public void setIsuninstall(boolean isuninstall) {
		this.isuninstall = isuninstall;
	}

	public int getAtt_upload_space() {
		if (att_upload_space >= 0) {
			return att_upload_space;
		} else {
			return DEFAULT_ATT_UPLOAD_SPACE;
		}
	}

	public void setAtt_upload_space(int att_upload_space) {
		this.att_upload_space = att_upload_space;
	}

	public int getOmc_heartbeat() {
		if (sp != null) {
			int heartbeat = sp.getInt(OMC_SP_HEART_BEAT, 0);
			if (heartbeat >= 30) {
				return heartbeat;
			}
		}
		if (omc_heartbeat >= 30) {
			return omc_heartbeat;
		} else {
			return 30;
		}
	}

	public void setOmc_heartbeat(int omc_heartbeat) {
		if (sp != null && se != null) {
			se.putInt(OMC_SP_HEART_BEAT, omc_heartbeat);
			se.commit();
		}
		this.omc_heartbeat = omc_heartbeat;
	}

	public int getOmc_update_vercode() {
		return omc_update_vercode;
	}

	public void setOmc_update_vercode(int omc_update_vercode) {
		this.omc_update_vercode = omc_update_vercode;
	}

	public boolean hasSetUp() {// 是否已进行必要设置
		if (getAtt_pic_platform() == 0) {
			if (getDevice_id() != null && getDevice_id().length() > 0 && getSchool_id() != null
					&& getSchool_id().length() > 0) {
				if (getAtt_url().equals(SettingPara.ATT_URL_CTXY_ALL)
						|| getAtt_url().equals(SettingPara.ATT_URL_CTXY_GANSU)) {
					if (getProvince_id() != null && getProvince_id().length() > 0) {
						return true;
					}
				} else if (getAtt_url().equals(SettingPara.ATT_URL_HEBB)
						|| getAtt_url().equals(SettingPara.ATT_URL_HEBB_TEST)
						|| getAtt_url().equals(SettingPara.ATT_URL_JKTX)
						|| getAtt_url().equals(SettingPara.ATT_URL_DDJY)
						|| getAtt_url().equals(SettingPara.ATT_URL_HEJY)) {
					return true;
				}
			}
		} else if (getAtt_pic_platform() == 1) {
			if (getDevice_id() != null && getDevice_id().length() > 0 && getSchool_id() != null
					&& getSchool_id().length() > 0) {
				return true;
			}
		} else if (getAtt_pic_platform() == 2) {
			return true;
		}
		return false;
	}

	public static boolean isIMEI(String imei) {
		try {
			Pattern p = Pattern.compile("^[0-9]{15}$|^[0-9]{17}$");
			Matcher m = p.matcher(imei);
			return m.matches();
		} catch (Exception e) {

		}
		return false;
	}

	public static int getWirelessModule(Context context, String imei) {// 0表示没有无线模块，1表示有
		if (imei != null && imei.length() > 0) {
			boolean isIMEI = isIMEI(imei);
			if (isIMEI && !imei.equals("000000000000000") && !imei.equals("012345678912345")) {
				return 1;
			}
		}
		return 0;
	}

	public int getWirelessModule(Context context) {
		String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		return getWirelessModule(context, imei);
	}

	public String getDeviceId(Context context) {
		if (sp == null) {
			sp = context.getSharedPreferences(SettingPara.OMC_SP_NAME, Context.MODE_PRIVATE);
		}
		if (se == null) {
			se = sp.edit();
		}
		String sp_device_id = sp.getString(OMC_SP_DEVICE_ID, "");
		// Log.i("OMC", "SharedPreferences中device_id："+ sp_device_id);
		if (sp_device_id != null && sp_device_id.length() > 0) {
			return sp_device_id;
		} else {
			String imei = getSystemIMEI(context);
			if (imei != null) {
				se.putString(OMC_SP_DEVICE_ID, imei);
				se.commit();
				return imei;
			} else {
				String androidID = getAndroidID(context);
				se.putString(OMC_SP_DEVICE_ID, androidID);
				se.commit();
				return androidID;
			}
		}
	}

	public boolean updateIMEI(Context context, String imei) {
		if (sp == null) {
			sp = context.getSharedPreferences(SettingPara.OMC_SP_NAME, Context.MODE_PRIVATE);
		}
		if (se == null) {
			se = sp.edit();
		}
		String sp_device_id = sp.getString(SettingPara.OMC_SP_DEVICE_ID, "");
		//String imei = null;
		// int try_count = 0;
		// while (try_count < 3 && (imei == null || imei.length() == 0)) {
		// try_count++;
		//imei = getSystemIMEI(context);
		// }
		if (imei != null && imei.length() > 0) {
			if (!imei.equals(sp_device_id)) {
				Log.e("OMC", "设备ID被更改为：" + imei + " 原来的值为：" + sp_device_id);
				se.putString(SettingPara.OMC_SP_DEVICE_ID, imei);
				se.commit();
			}
			return true;
		} else {
			String androidID = SettingPara.getAndroidID(context);
			if (!androidID.equals(sp_device_id)) {
				Log.e("OMC", "设备ID被更改为：" + androidID + " 原来的值为：" + sp_device_id);
				se.putString(SettingPara.OMC_SP_DEVICE_ID, androidID);
				se.commit();
			}
			return false;
		}
	}

	public String getIMEI(Context context) {
		if (sp == null) {
			sp = context.getSharedPreferences(SettingPara.OMC_SP_NAME, Context.MODE_PRIVATE);
		}
		if (se == null) {
			se = sp.edit();
		}
		String sp_imei = sp.getString(OMC_SP_IMEI, null);
		// Log.i("OMC", "SharedPreferences中imei："+ sp_imei);
		String imei = getSystemIMEI(context);
		if (imei != null) {
			if (!imei.equals(sp_imei)) {
				se.putString(OMC_SP_IMEI, imei);
				se.commit();
			}
			return imei;
		} else {
			if (sp_imei != null && sp_imei.length() > 0) {
				Log.e("OMC", "imei获取失败，从SharedPreferences取出imei");
				return sp_imei;
			}
		}
		return null;
	}

	public String getSystemIMEI(Context context) {
		String imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		if (imei != null && getWirelessModule(context, imei) == 1) {
			Log.i("OMC", "IMEI判断为合法：" + imei);
			return imei;
		} else {
			Log.i("OMC", "IMEI判断为非法：" + imei);
		}
		return null;
	}

	public static String getAndroidID(Context context) {
		return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
	}

	public static String getPlatformID() {
		SettingPara settingPara = new SettingPara();
		return settingPara.getPlatformID(settingPara);
	}

	public String getPlatformId() {
		return getPlatformID(SettingPara.this);
	}

	public String getPlatformID(SettingPara settingPara) {
		String platformID = "";
		if (settingPara.getAtt_pic_platform() == 0) {
			if (settingPara.getAtt_url() != null && settingPara.getAtt_url().length() > 0) {
				int index = -1;
				for (int i = 0; i < ATT_URLS.length; i++) {
					if (ATT_URLS[i].equals(settingPara.getAtt_url())) {
						index = i;
					}
				}
				if (index >= 0) {
					switch (index) {
						case 0:
							platformID = "6";// 幼学通
							break;
						case 1:
							platformID = "6";// 幼学通
							break;
						case 2:
							platformID = "1";// 和宝贝
							break;
						case 3:
							platformID = "5";// 健康童学
							break;
						case 4:
							platformID = "4";// 大地教育
							break;
						case 5:
							platformID = "3";// 和教育
							break;
						case 6:
							platformID="9";  //福建幼学通
							break;
						case 7:

							platformID="10";  //和而泰
							break;
						default:
							break;
					}
				} else if (ATT_URL_HDBB.equals(settingPara.getAtt_url())) {
					platformID = "2";// 互动宝宝
				} else if (ATT_URL_HEBB_TEST.equals(settingPara.getAtt_url())) {
					platformID = "1";// 和宝贝测试地址
				}
			}
		} else if (settingPara.getAtt_pic_platform() == 1) {
			platformID = "7";// 能龙平台
		} else if (settingPara.getAtt_pic_platform() == 2) {
			platformID = "8";// 中维平台
		}
		return platformID;
	}

	public boolean isIsallvideo() {
		return isallvideo;
	}

	public void setIsallvideo(boolean isallvideo) {
		this.isallvideo = isallvideo;
	}

	public boolean isIsclassvideo() {
		return isclassvideo;
	}

	public void setIsclassvideo(boolean isclassvideo) {
		this.isclassvideo = isclassvideo;
	}

	public boolean isIsbleban() {
		return isbleban;
	}

	public void setIsbleban(boolean isbleban) {
		this.isbleban = isbleban;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public boolean isIssportatt() {
		return issportatt;
	}

	public void setIssportatt(boolean issportatt) {
		this.issportatt = issportatt;
	}
}
