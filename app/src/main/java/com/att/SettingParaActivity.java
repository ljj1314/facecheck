package com.att;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.att.act.OkHttpPost;
import com.att.guide.MsgDialog;
import com.att.guide.StepFirstActivity;
import com.att.guide.StepFourthActivity;
import com.att.guide.UsbDiskCopyActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingParaActivity extends Activity {

	private ImageView ib_setting_btn_back;// 返回键

	private LinearLayout ll_urls;
	private EditText et_att_url; // 考勤上报URL
	private EditText et_att_pic_url; // 考勤图片上报URL
	private EditText et_parents_pic_url; // 家长图片下载URL
	private EditText et_card_info_url; // 卡信息下载URL
	private EditText et_temp_url = null; // 体温上报URL

	private LinearLayout ll_ids;
	private EditText et_device_id; // 终端ID
	private EditText et_school_id; // 学校ID

	private LinearLayout ll_province_id;
	private EditText et_province_id; // 省份编码

	private Spinner sp_att_platform; // 考勤平台协议
	private Spinner sp_att_pic_platform; // 考勤图片平台协议
	private Spinner sp_card_disp; // 卡号显示格式
	private Spinner sp_card_upload; // 卡号上报格式

	private EditText et_att_upload_sum; // 卡批量上报数量
	private EditText et_att_upload_space; // 重复刷卡限制时间
	private EditText et_card_buma; // 卡号补码
	private EditText updatetime = null; // 数据更新间隔
	private EditText schoolname = null; // 学校名称
	private EditText et_heartbeat; // 心跳时间
	private EditText et_att_timeout; // 考勤界面超时时间
	private EditText et_idle_pic_duration; // 待机轮播图片间隔
	private EditText account = null; // 账号
	private EditText password = null; // 密码

	public AudioManager audiomanage;
	public int maxVolume, currentVolume;
	private SeekBar sb_voice_volume; // 音量

	private SettingParaSwitch sw_card_reversal; // 卡号反转
	private SettingParaSwitch sw_enzero;// 卡号补0
	private SettingParaSwitch sw_isap; // 蓝牙ap开关
	private SettingParaSwitch sw_tcap; // 透传蓝牙ap
	private SettingParaSwitch sw_islg; // 厂家logo开关
	private SettingParaSwitch sw_issg; // 学校ID开关
	private SettingParaSwitch sw_iscsv; // 导入csv开关
	private SettingParaSwitch sw_idle_vedio_switch; // 待机轮播视频开关
	private SettingParaSwitch sw_idle_full_screen_switch; // 轮播图片全屏显示开关
	private SettingParaSwitch sw_carderr_voice_tips; // 无效卡语音提示
	private SettingParaSwitch sw_take_voice;// 刷卡声音开关
	private SettingParaSwitch sw_take_photo; // 抓拍开关

	private LinearLayout ll_cloud_storage;
	private SettingParaSwitch sw_cloud_storage; // 启用云存储开关
	private SettingParaSwitch sw_take_int; // 移动网络数据开关

	private Spinner sp_go_school_t1_voice; // 上学时间段1的语音提示
	private Spinner sp_go_school_t2_voice; // 上学时间段2的语音提示
	private Spinner sp_go_school_t3_voice; // 上学时间段3的语音提示
	private Spinner sp_out_school_t1_voice; // 放学时间段1的语音提示
	private Spinner sp_out_school_t2_voice; // 放学时间段2的语音提示
	private Spinner sp_out_school_t3_voice; // 放学时间段3的语音提示

	private TimePicker tp_go_school_t1; // 上学时间段1
	private TimePicker tp_go_school_t2; // 上学时间段1
	private TimePicker tp_go_school_t3; // 上学时间段2
	private TimePicker tp_go_school_t4; // 上学时间段2
	private TimePicker tp_go_school_t5; // 上学时间段3
	private TimePicker tp_go_school_t6; // 上学时间段3

	private TimePicker tp_out_school_t1; // 放学时间段1
	private TimePicker tp_out_school_t2; // 放学时间段1
	private TimePicker tp_out_school_t3; // 放学时间段2
	private TimePicker tp_out_school_t4; // 放学时间段2
	private TimePicker tp_out_school_t5; // 放学时间段3
	private TimePicker tp_out_school_t6; // 放学时间段3

	private EditText et_admin_passwd; // 管理员密码

	private Button bt_cardinfo_update; // 卡信息更新
	private Button bt_clear_att; // 清考勤记录
	private Button bt_check_version; // 检查新版本
	private Button bt_factory_reset; // 恢复出厂设置
	private Button bt_guide; // 进入安装向导
	private Button bt_u_copy; // U盘资料复制
	private Button bt_para_save; // 保存

	private TextView tv_app_version; // 软件版本号

	private LinearLayout ll_ap_version;
	private TextView tv_ap_version; // 蓝牙AP版本号

	private SettingPara settingPara = new SettingPara();
	private SharedPreferences sps = null;
	private SharedPreferences.Editor se = null;

	private TextView imei=null;

	private SettingParaSwitch sw_allvideo;   //完整播报
	private SettingParaSwitch sw_classvideo; //班级播报
	private SettingParaSwitch sw_bleban;   //板载蓝牙
	private SettingParaSwitch sw_sportatt;//运动考勤


	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题

		// 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
		// 设置你申请的应用appid
		// StringBuffer param = new StringBuffer();
		// param.append("appid="+getString(R.string.app_id));
		// param.append(",");
		// 设置使用v5+
		// param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
		// SpeechUtility.createUtility(SettingParaActivity.this,
		// param.toString());

		super.onCreate(savedInstanceState);

		// 加入bugly
		// CrashReport.initCrashReport(SettingParaActivity.this,
		// getString(R.string.app_id), false);

		setContentView(R.layout.settingpara);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				init();
			}
		}, 500);
	}

	private void init() {

		ll_urls = (LinearLayout) findViewById(R.id.ll_urls);
		et_att_url = (EditText) findViewById(R.id.et_att_url);
		et_att_pic_url = (EditText) findViewById(R.id.et_att_pic_url);
		et_parents_pic_url = (EditText) findViewById(R.id.et_parents_pic_url);
		et_card_info_url = (EditText) findViewById(R.id.et_card_info_url);
		et_temp_url = (EditText) findViewById(R.id.et_temp_url);

		sps = getSharedPreferences("json", Activity.MODE_PRIVATE);
		se = sps.edit();

		ll_ids = (LinearLayout) findViewById(R.id.ll_ids);
		et_device_id = (EditText) findViewById(R.id.et_device_id);
		et_school_id = (EditText) findViewById(R.id.et_school_id);
		ll_province_id = (LinearLayout) findViewById(R.id.ll_province_id);
		et_province_id = (EditText) findViewById(R.id.et_province_id);
		et_att_upload_sum = (EditText) findViewById(R.id.et_att_upload_sum);
		et_att_upload_space = (EditText) findViewById(R.id.et_att_upload_space);
		et_card_buma = (EditText) findViewById(R.id.ed_card_buma);

		sw_card_reversal = (SettingParaSwitch) findViewById(R.id.sw_card_reversal);

		sp_att_platform = (Spinner) findViewById(R.id.sp_att_platform);
		sp_att_pic_platform = (Spinner) findViewById(R.id.sp_att_pic_platform);
		sp_card_disp = (Spinner) findViewById(R.id.sp_card_disp);
		sp_card_upload = (Spinner) findViewById(R.id.sp_card_upload);
		sp_go_school_t1_voice = (Spinner) findViewById(R.id.sp_go_school_t1_voice);
		sp_go_school_t2_voice = (Spinner) findViewById(R.id.sp_go_school_t2_voice);
		sp_go_school_t3_voice = (Spinner) findViewById(R.id.sp_go_school_t3_voice);
		sp_out_school_t1_voice = (Spinner) findViewById(R.id.sp_out_school_t1_voice);
		sp_out_school_t2_voice = (Spinner) findViewById(R.id.sp_out_school_t2_voice);
		sp_out_school_t3_voice = (Spinner) findViewById(R.id.sp_out_school_t3_voice);
		// 将可选内容与ArrayAdapter连接起来
		String[] jinzhi = getResources().getStringArray(R.array.jinzhi);
		SpinnerAdapter adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, jinzhi);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// 将adapter 添加到spinner中
		sp_card_disp.setAdapter(adapter);
		sp_card_upload.setAdapter(adapter);

		// 将可选内容与ArrayAdapter连接起来
		String[] platform = getResources().getStringArray(R.array.platform);
		adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, platform);
		sp_att_platform.setAdapter(adapter);
		sp_att_pic_platform.setAdapter(adapter);
		sp_att_pic_platform.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int platform, long arg3) {
				if (platform == 0) {
					ll_urls.setVisibility(View.VISIBLE);
					ll_cloud_storage.setVisibility(View.VISIBLE);// TODO
				} else {
					ll_urls.setVisibility(View.GONE);
					ll_cloud_storage.setVisibility(View.GONE);
				}
				if (platform == 2) {
					ll_ids.setVisibility(View.GONE);
				} else {
					ll_ids.setVisibility(View.VISIBLE);
					if (platform == 0 && (settingPara.getAtt_url().equals(SettingPara.ATT_URL_CTXY_ALL)
							|| settingPara.getAtt_url().equals(SettingPara.ATT_URL_CTXY_GANSU))) {
						//	ll_province_id.setVisibility(View.VISIBLE);
					} else {
						//ll_province_id.setVisibility(View.GONE);
						//	et_province_id.setText("62000000");
					}
				}
				setbtUcopy(platform);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		String[] go_school = getResources().getStringArray(R.array.go_school);
		adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, go_school);
		sp_go_school_t1_voice.setAdapter(adapter);
		sp_go_school_t2_voice.setAdapter(adapter);
		sp_go_school_t3_voice.setAdapter(adapter);

		String[] out_school = getResources().getStringArray(R.array.out_school);
		adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, out_school);
		sp_out_school_t1_voice.setAdapter(adapter);
		sp_out_school_t2_voice.setAdapter(adapter);
		sp_out_school_t3_voice.setAdapter(adapter);

		tp_go_school_t1 = (TimePicker) findViewById(R.id.tp_go_school_t1);
		tp_go_school_t2 = (TimePicker) findViewById(R.id.tp_go_school_t2);
		tp_go_school_t3 = (TimePicker) findViewById(R.id.tp_go_school_t3);
		tp_go_school_t4 = (TimePicker) findViewById(R.id.tp_go_school_t4);
		tp_go_school_t5 = (TimePicker) findViewById(R.id.tp_go_school_t5);
		tp_go_school_t6 = (TimePicker) findViewById(R.id.tp_go_school_t6);

		setTimePicker(tp_go_school_t1);
		setTimePicker(tp_go_school_t2);
		setTimePicker(tp_go_school_t3);
		setTimePicker(tp_go_school_t4);
		setTimePicker(tp_go_school_t5);
		setTimePicker(tp_go_school_t6);

		tp_out_school_t1 = (TimePicker) findViewById(R.id.tp_out_school_t1);
		tp_out_school_t2 = (TimePicker) findViewById(R.id.tp_out_school_t2);
		tp_out_school_t3 = (TimePicker) findViewById(R.id.tp_out_school_t3);
		tp_out_school_t4 = (TimePicker) findViewById(R.id.tp_out_school_t4);
		tp_out_school_t5 = (TimePicker) findViewById(R.id.tp_out_school_t5);
		tp_out_school_t6 = (TimePicker) findViewById(R.id.tp_out_school_t6);

		setTimePicker(tp_out_school_t1);
		setTimePicker(tp_out_school_t2);
		setTimePicker(tp_out_school_t3);
		setTimePicker(tp_out_school_t4);
		setTimePicker(tp_out_school_t5);
		setTimePicker(tp_out_school_t6);

		account = (EditText) findViewById(R.id.et_idle_account);
		password = (EditText) findViewById(R.id.et_idle_password);
		schoolname = (EditText) findViewById(R.id.et_schoolname);

		imei=(TextView) findViewById(R.id.tv_imei);
		if (settingPara.getIMEI(getApplicationContext())==null) {
			imei.setText("终端ID是:"+settingPara.getAndroidID(getApplicationContext()));
		}else {
			imei.setText("终端ID是:"+settingPara.getIMEI(getApplicationContext()));
		}


		if (settingPara.getAtt_pic_platform() == 2) {
			schoolname.setFocusable(true);
			schoolname.requestFocus();
		} else {
			et_device_id.setFocusable(true);
			et_device_id.requestFocus();
			// et_device_id.setFocusableInTouchMode(true);
			// et_device_id.requestFocusFromTouch();
		}

		sw_isap = (SettingParaSwitch) findViewById(R.id.sw_blueap);
		sw_iscsv = (SettingParaSwitch) findViewById(R.id.sw_csv);
		sw_tcap = (SettingParaSwitch) findViewById(R.id.sw_tcap);
		sw_enzero = (SettingParaSwitch) findViewById(R.id.sw_enzero);

		sw_allvideo=(SettingParaSwitch) findViewById(R.id.sw_idle_allvideo_switch);
		sw_classvideo=(SettingParaSwitch) findViewById(R.id.sw_idle_classvideo_switch);
		sw_bleban=(SettingParaSwitch) findViewById(R.id.sw_idle_bleban_switch);

		// 返回
		ib_setting_btn_back = (ImageView) findViewById(R.id.ib_setting_btn_back);
		ib_setting_btn_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		bt_guide = (Button) findViewById(R.id.bt_guide);
		bt_guide.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(SettingParaActivity.this, StepFirstActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("isSet", true);
				getApplicationContext().startActivity(intent);
				overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
				SettingParaActivity.this.finish();
			}
		});

		//// 卡信息更新
		bt_cardinfo_update = (Button) findViewById(R.id.bt_cardinfo_update);
		bt_cardinfo_update.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {

				// 从MyActivity跳转到MainActivity

				if (settingPara.getAtt_pic_platform() == 0) {

					@SuppressWarnings("unused")
					String isf = sps.getString("studentin", null);

					boolean hasProvinceId = sp_att_pic_platform.getSelectedItemPosition() == 0 && (settingPara.getAtt_url().equals(SettingPara.ATT_URL_CTXY_ALL)
							|| settingPara.getAtt_url().equals(SettingPara.ATT_URL_CTXY_GANSU));
					if (et_device_id.getText().toString().trim() != null
							&& !"".equals(et_device_id.getText().toString().trim())
							&& et_school_id.getText().toString().trim() != null
							&& !"".equals(et_school_id.getText().toString().trim())
							&& (!hasProvinceId||(et_province_id.getText().toString().trim() != null
							&& !"".equals(et_province_id.getText().toString().trim())))){
						Intent intent = new Intent(SettingParaActivity.this, StepFourthActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("isSet", true);
						getApplicationContext().startActivity(intent);// 跳转
						finish();// 结束本欢迎画面Activity
					} else {
						if(hasProvinceId){
							Toast.makeText(SettingParaActivity.this, "学校ID，设备ID，省份编码不能为空,请填写完整", Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(SettingParaActivity.this, "学校ID，设备ID不能为空,请填写完整", Toast.LENGTH_SHORT).show();
						}
					}
				} else if (settingPara.getAtt_pic_platform() == 1) {
					// 能龙无须下载
					Intent intent = new Intent(SettingParaActivity.this, StepFourthActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("isSet", true);
					getApplicationContext().startActivity(intent);
				} else if (settingPara.getAtt_pic_platform() == 2) {
					Intent intent = new Intent(SettingParaActivity.this, StepFourthActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("isSet", true);
					getApplicationContext().startActivity(intent);
				}

				// }else {
				// Toast.makeText(SettingParaActivity.this, "没有数据需要更新",
				// Toast.LENGTH_SHORT).show();
				// }

			}
		});

		// 清考勤
		bt_clear_att = (Button) findViewById(R.id.bt_clear_att);
		bt_clear_att.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {//TODO ANR in com.att (com.att/.SettingParaActivity) Reason: keyDispatchingTimedOut
				AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
					@Override
					protected String doInBackground(String... arg0) {
						File file;
						AppBaseFun appBaseFun = new AppBaseFun();

						DBOpenHelper sqlTpatt = new DBOpenHelper(SettingParaActivity.this);
						try {
							sqlTpatt.delDataBase();
							sqlTpatt.createDataBase();

							sqlTpatt.close();
						} catch (Exception e) {
							Log.i("TPATT", "createDataBase 失败:"+e.toString());
						}

						file = new File(appBaseFun.getPhoneCardPath() + "/tpatt/AttPhoto");
						appBaseFun.delete(file);

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						appBaseFun.makeRootDirectory(appBaseFun.getPhoneCardPath() + "/tpatt/AttPhoto");
						return null;
					}

					protected void onPostExecute(String result) {
						Toast.makeText(SettingParaActivity.this, "清考勤成功！", 0).show();
					};
				};
				asyncTask.execute("");
			}
		});

		// 检查更新版本
		bt_check_version = (Button) findViewById(R.id.bt_check_version);
		bt_check_version.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				int newVerCode;
				try {
					String downPath = SettingPara.getDownPath();
					String newVerJSON = CurrentVersion.getUpdataVerJSON(downPath + "幼儿宝ver.json");
					if (newVerJSON != null) {
						JSONArray jsonArray = new JSONArray(newVerJSON);
						if (jsonArray.length() > 0) {
							Log.i("TPATT", "升级获取版本: " + jsonArray.toString());
							JSONObject obj = jsonArray.getJSONObject(0);
							try {
								newVerCode = Integer.parseInt(obj.getString("verCode"));
								obj.getString("verName");
							} catch (Exception e) {
								newVerCode = 0;
							}
						} else {
							newVerCode = 0;
						}
					} else {
						newVerCode = 0;
					}
				} catch (Exception e) {
					newVerCode = 0;
					Log.i("TPATT", "升级:获取版本异常 " + e.toString());
				}
				if (newVerCode > 0) {
					int currentCode;
					currentCode = CurrentVersion.getVerCode(SettingParaActivity.this);
					if (newVerCode > currentCode) {
						final Intent intent = new Intent(SettingParaActivity.this, UpdateAppActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getApplicationContext().startActivity(intent);
						finish();
					} else {
						StepFirstActivity.showMsgDialog(SettingParaActivity.this, "已是最新版本!", false);
					}
				} else {
					StepFirstActivity.showMsgDialog(SettingParaActivity.this, "未检测到新版本!", false);
				}
			}

		});

		// 恢复出厂设置
		bt_factory_reset = (Button) findViewById(R.id.bt_factory_reset);
		bt_factory_reset.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {

				factory_reset();
				save_param();
			}

		});

		// 保存
		bt_para_save = (Button) findViewById(R.id.bt_para_save);
		bt_para_save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				save_param();
			}

		});

		updatetime = (EditText) findViewById(R.id.et_updatetime);
		et_heartbeat = (EditText) findViewById(R.id.et_heartbeat);
		et_att_timeout = (EditText) findViewById(R.id.et_att_timeout);
		et_idle_pic_duration = (EditText) findViewById(R.id.et_idle_pic_duration);
		et_admin_passwd = (EditText) findViewById(R.id.et_admin_passwd);

		sw_idle_vedio_switch = (SettingParaSwitch) findViewById(R.id.sw_idle_vedio_switch);
		sw_idle_full_screen_switch = (SettingParaSwitch) findViewById(R.id.sw_idle_full_screen_switch);
		sw_carderr_voice_tips = (SettingParaSwitch) findViewById(R.id.sw_carderr_voice_tips);
		sw_take_photo = (SettingParaSwitch) findViewById(R.id.sw_take_photo);

		ll_cloud_storage = (LinearLayout) findViewById(R.id.ll_cloud_storage);
		sw_cloud_storage = (SettingParaSwitch) findViewById(R.id.sw_cloud_storage);
		String platformID = settingPara.getPlatformId();
		if(platformID.equals("1")||platformID.equals("4")||platformID.equals("5")){//和宝贝,大地教育,健康童学
			ll_cloud_storage.setVisibility(View.VISIBLE);//TODO
		}else{
			ll_cloud_storage.setVisibility(View.GONE);
		}
		sw_take_int = (SettingParaSwitch) findViewById(R.id.sw_take_3G);
		sw_take_voice = (SettingParaSwitch) findViewById(R.id.sw_carderr_voice_take);

		sw_islg = (SettingParaSwitch) findViewById(R.id.sw_logo);
		sw_issg = (SettingParaSwitch) findViewById(R.id.sw_schoolid);
		sw_sportatt=(SettingParaSwitch) findViewById(R.id.sw_sportatt);

		sb_voice_volume = (SeekBar) findViewById(R.id.sb_voice_volume);
		audiomanage = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audiomanage.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获取系统最大音量
		sb_voice_volume.setMax(maxVolume); // 拖动条最高值与系统最大声匹配
		currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
		sb_voice_volume.setProgress(currentVolume);
		sb_voice_volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {

				// if (progress == (maxVolume - 1)) {
				// audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC,
				// maxVolume, 0);
				// } else {
				audiomanage.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
				// }

				currentVolume = audiomanage.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
				sb_voice_volume.setProgress(currentVolume);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		// setbtUcopy(sp.getAtt_pic_platform());

		tv_app_version = (TextView) findViewById(R.id.tv_app_version);
		tv_app_version.setText("软件版本号： V" + CurrentVersion.getVerName(this) + "." + CurrentVersion.getVerCode(this));
		ll_ap_version = (LinearLayout) findViewById(R.id.ll_ap_version);
		if((settingPara.isIsap()||settingPara.isIstcap())&&settingPara.getAp_version()!=null&&settingPara.getAp_version().length()>0){
			ll_ap_version.setVisibility(View.VISIBLE);
			tv_ap_version = (TextView) findViewById(R.id.tv_ap_version);
			tv_ap_version.setText("蓝牙AP版本号： " + settingPara.getAp_version());
		}else{
			ll_ap_version.setVisibility(View.GONE);
		}
		restore_param();

	}

	private void setbtUcopy(int platform) {
		bt_u_copy = (Button) findViewById(R.id.bt_u_copy);
		if (platform == 2) {
			bt_u_copy.setVisibility(View.GONE);
		} else {
			bt_u_copy.setVisibility(View.VISIBLE);
			bt_u_copy.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setClass(SettingParaActivity.this, UsbDiskCopyActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplicationContext().startActivity(intent);
					overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
					SettingParaActivity.this.finish();
				}
			});
		}

	}



	public boolean save_param() {
		try {
			settingPara.setAtt_url(et_att_url.getText().toString().trim());
			settingPara.setAtt_pic_url(et_att_pic_url.getText().toString().trim());
			settingPara.setParents_pic_url(et_parents_pic_url.getText().toString().trim());
			settingPara.setCard_info_url(et_card_info_url.getText().toString().trim());
			settingPara.setDevice_id(et_device_id.getText().toString().trim());
			settingPara.setSchool_id(et_school_id.getText().toString().trim());
			settingPara.setProvince_id(et_province_id.getText().toString().trim());
			settingPara.setAtt_platform(sp_att_platform.getSelectedItemPosition());
			settingPara.setAtt_pic_platform(sp_att_pic_platform.getSelectedItemPosition());
			settingPara.setCard_disp(sp_card_disp.getSelectedItemPosition());
			settingPara.setCard_upload(sp_card_upload.getSelectedItemPosition());
			settingPara.setAtt_upload_sum(Integer.parseInt(et_att_upload_sum.getText().toString().trim()));
			settingPara.setAtt_upload_space(Integer.parseInt(et_att_upload_space.getText().toString().trim()));
			settingPara.setCard_buma(Integer.parseInt(et_card_buma.getText().toString().trim()));
			settingPara.setCard_reversal(sw_card_reversal.isChecked());

			tp_go_school_t1.clearFocus();
			tp_go_school_t2.clearFocus();
			tp_go_school_t3.clearFocus();
			tp_go_school_t4.clearFocus();
			tp_go_school_t5.clearFocus();
			tp_go_school_t6.clearFocus();

			String[] start = new String[3];
			String[] end = new String[3];
			start[0] = tp_go_school_t1.getCurrentHour().toString() + ":"
					+ tp_go_school_t1.getCurrentMinute().toString();
			start[1] = tp_go_school_t3.getCurrentHour().toString() + ":"
					+ tp_go_school_t3.getCurrentMinute().toString();
			start[2] = tp_go_school_t5.getCurrentHour().toString() + ":"
					+ tp_go_school_t5.getCurrentMinute().toString();

			end[0] = tp_go_school_t2.getCurrentHour().toString() + ":" + tp_go_school_t2.getCurrentMinute().toString();
			end[1] = tp_go_school_t4.getCurrentHour().toString() + ":" + tp_go_school_t4.getCurrentMinute().toString();
			end[2] = tp_go_school_t6.getCurrentHour().toString() + ":" + tp_go_school_t6.getCurrentMinute().toString();

			settingPara.setGo_school_start(start);
			settingPara.setGo_school_end(end);

			tp_out_school_t1.clearFocus();
			tp_out_school_t2.clearFocus();
			tp_out_school_t3.clearFocus();
			tp_out_school_t4.clearFocus();
			tp_out_school_t5.clearFocus();
			tp_out_school_t6.clearFocus();

			String[] start2 = new String[3];
			String[] end2 = new String[3];
			start2[0] = tp_out_school_t1.getCurrentHour().toString() + ":"
					+ tp_out_school_t1.getCurrentMinute().toString();
			start2[1] = tp_out_school_t3.getCurrentHour().toString() + ":"
					+ tp_out_school_t3.getCurrentMinute().toString();
			start2[2] = tp_out_school_t5.getCurrentHour().toString() + ":"
					+ tp_out_school_t5.getCurrentMinute().toString();

			end2[0] = tp_out_school_t2.getCurrentHour().toString() + ":"
					+ tp_out_school_t2.getCurrentMinute().toString();
			end2[1] = tp_out_school_t4.getCurrentHour().toString() + ":"
					+ tp_out_school_t4.getCurrentMinute().toString();
			end2[2] = tp_out_school_t6.getCurrentHour().toString() + ":"
					+ tp_out_school_t6.getCurrentMinute().toString();

			settingPara.setOut_school_start(start2);
			settingPara.setOut_school_end(end2);

			settingPara.setGo_school_t1_voice(sp_go_school_t1_voice.getSelectedItemPosition());
			settingPara.setGo_school_t2_voice(sp_go_school_t2_voice.getSelectedItemPosition());
			settingPara.setGo_school_t3_voice(sp_go_school_t3_voice.getSelectedItemPosition());
			settingPara.setOut_school_t1_voice(sp_out_school_t1_voice.getSelectedItemPosition());
			settingPara.setOut_school_t2_voice(sp_out_school_t2_voice.getSelectedItemPosition());
			settingPara.setOut_school_t3_voice(sp_out_school_t3_voice.getSelectedItemPosition());

			settingPara.setHeartbeat(Integer.parseInt(et_heartbeat.getText().toString().trim()));
			settingPara.setAtt_timeouts(Integer.parseInt(et_att_timeout.getText().toString().trim()));
			settingPara.setIdle_pic_duration(Integer.parseInt(et_idle_pic_duration.getText().toString().trim()));
			settingPara.setIdle_vedio_switch(sw_idle_vedio_switch.isChecked());
			settingPara.setCarderr_voice_tips(sw_carderr_voice_tips.isChecked());
			settingPara.setTake_photo(sw_take_photo.isChecked());
			settingPara.setIs_cloud_storage(sw_cloud_storage.isChecked());
			settingPara.setTake_internet(sw_take_int.isChecked());
			settingPara.setTake_voice(sw_take_voice.isChecked());
			if (schoolname.getText() != null) {
				settingPara.setSchoolname(schoolname.getText().toString());
			}
			settingPara.setIsap(sw_isap.isChecked());
			settingPara.setIscsv(sw_iscsv.isChecked());
			settingPara.setIslg(sw_islg.isChecked());
			settingPara.setIssgid(sw_issg.isChecked());
			if (et_admin_passwd.getText().toString().equals("")) {
				StepFirstActivity.showMsgDialog(SettingParaActivity.this, "密码不能为空！", false);
				return false;
			}
			settingPara.setAdmin_passwd(et_admin_passwd.getText().toString().trim());

			if (account.getText() != null && !"".equals(account.getText())) {
				settingPara.setAccount(account.getText().toString());
			}
			if (password.getText() != null && !"".equals(password.getText())) {
				settingPara.setPassword(password.getText().toString());
			}

			settingPara.setIstcap(sw_tcap.isChecked());

			settingPara.setIsenzero(sw_enzero.isChecked());

			if (et_temp_url.getText() != null && !"".equals(et_temp_url.getText())) {
				settingPara.setTempurl(et_temp_url.getText().toString());
			}

			if (updatetime.getText() != null && !updatetime.getText().toString().equals("")) {

				int times = Integer.valueOf(updatetime.getText().toString()).intValue();

				if (times < 1800) {
					StepFirstActivity.showMsgDialog(SettingParaActivity.this, "保存失败!\n数据更新间隔不能少于1800秒(30分钟)", false);
					return false;
				}

				settingPara.setUpdatetime(times);
			}

			settingPara.setIsallscreen(sw_idle_full_screen_switch.isChecked());

			settingPara.setIsallvideo(sw_allvideo.isChecked());
			settingPara.setIsclassvideo(sw_classvideo.isChecked());

			settingPara.setIsbleban(sw_bleban.isChecked());

			settingPara.setIssportatt(sw_sportatt.isChecked());

			if (!settingPara.save_settingpara()) {
				StepFirstActivity.showMsgDialog(SettingParaActivity.this, "保存失败！", false);
				return false;
			}

			Toast.makeText(this, "保存成功!", 0).show();
			return true;
		} catch (Exception e) {
			StepFirstActivity.showMsgDialog(SettingParaActivity.this, "保存失败，不能为空!", false);
			return false;
		}
	}

	@SuppressLint("UseValueOf")
	public void restore_param() {
		try {
			et_att_url.setText(settingPara.getAtt_url());
			et_att_pic_url.setText(settingPara.getAtt_pic_url());
			et_parents_pic_url.setText(settingPara.getParents_pic_url());
			et_card_info_url.setText(settingPara.getCard_info_url());
			et_device_id.setText(settingPara.getDevice_id());
			et_school_id.setText(settingPara.getSchool_id());
			et_province_id.setText(settingPara.getProvince_id());
			sp_att_platform.setSelection(settingPara.getAtt_platform());
			sp_att_pic_platform.setSelection(settingPara.getAtt_pic_platform());
			sp_card_disp.setSelection(settingPara.getCard_disp());
			sp_card_upload.setSelection(settingPara.getCard_upload());
			et_att_upload_sum.setText(Integer.toString(settingPara.getAtt_upload_sum()));
			et_att_upload_space.setText(Integer.toString(settingPara.getAtt_upload_space()));
			et_card_buma.setText(Integer.toString(settingPara.getCard_buma()));
			sw_card_reversal.setChecked(settingPara.isCard_reversal());

			String[] start = new String[3];
			String[] end = new String[3];
			String[] time = new String[2];

			start = settingPara.getGo_school_start();
			time = start[0].split(":");
			tp_go_school_t1.setCurrentHour(Integer.valueOf(time[0]));
			tp_go_school_t1.setCurrentMinute(Integer.valueOf(time[1]));
			time = start[1].split(":");
			tp_go_school_t3.setCurrentHour(Integer.valueOf(time[0]));
			tp_go_school_t3.setCurrentMinute(Integer.valueOf(time[1]));
			time = start[2].split(":");
			tp_go_school_t5.setCurrentHour(Integer.valueOf(time[0]));
			tp_go_school_t5.setCurrentMinute(Integer.valueOf(time[1]));

			end = settingPara.getGo_school_end();
			time = end[0].split(":");
			tp_go_school_t2.setCurrentHour(Integer.valueOf(time[0]));
			tp_go_school_t2.setCurrentMinute(Integer.valueOf(time[1]));
			time = end[1].split(":");
			tp_go_school_t4.setCurrentHour(Integer.valueOf(time[0]));
			tp_go_school_t4.setCurrentMinute(Integer.valueOf(time[1]));
			time = end[2].split(":");
			tp_go_school_t6.setCurrentHour(Integer.valueOf(time[0]));
			tp_go_school_t6.setCurrentMinute(Integer.valueOf(time[1]));

			start = settingPara.getOut_school_start();
			time = start[0].split(":");
			tp_out_school_t1.setCurrentHour(Integer.valueOf(time[0]));
			tp_out_school_t1.setCurrentMinute(Integer.valueOf(time[1]));
			time = start[1].split(":");
			tp_out_school_t3.setCurrentHour(Integer.valueOf(time[0]));
			tp_out_school_t3.setCurrentMinute(Integer.valueOf(time[1]));
			time = start[2].split(":");
			tp_out_school_t5.setCurrentHour(Integer.valueOf(time[0]));
			tp_out_school_t5.setCurrentMinute(Integer.valueOf(time[1]));

			end = settingPara.getOut_school_end();
			time = end[0].split(":");
			tp_out_school_t2.setCurrentHour(Integer.valueOf(time[0]));
			tp_out_school_t2.setCurrentMinute(Integer.valueOf(time[1]));
			time = end[1].split(":");
			tp_out_school_t4.setCurrentHour(Integer.valueOf(time[0]));
			tp_out_school_t4.setCurrentMinute(Integer.valueOf(time[1]));
			time = end[2].split(":");
			tp_out_school_t6.setCurrentHour(Integer.valueOf(time[0]));
			tp_out_school_t6.setCurrentMinute(Integer.valueOf(time[1]));

			sp_go_school_t1_voice.setSelection(settingPara.getGo_school_t1_voice());
			sp_go_school_t2_voice.setSelection(settingPara.getGo_school_t2_voice());
			sp_go_school_t3_voice.setSelection(settingPara.getGo_school_t3_voice());
			sp_out_school_t1_voice.setSelection(settingPara.getOut_school_t1_voice());
			sp_out_school_t2_voice.setSelection(settingPara.getOut_school_t2_voice());
			sp_out_school_t3_voice.setSelection(settingPara.getOut_school_t3_voice());

			et_heartbeat.setText(Integer.toString(settingPara.getHeartbeat()));
			et_att_timeout.setText(Integer.toString(settingPara.getAtt_timeouts()));
			et_idle_pic_duration.setText(Integer.toString(settingPara.getIdle_pic_duration()));

			sw_idle_vedio_switch.setChecked(settingPara.isIdle_vedio_switch());
			sw_carderr_voice_tips.setChecked(settingPara.isCarderr_voice_tips());
			sw_take_photo.setChecked(settingPara.isTake_photo());
			sw_cloud_storage.setChecked(settingPara.isIs_cloud_storage());
			sw_take_int.setChecked(settingPara.isTake_internet());
			et_admin_passwd.setText(settingPara.getAdmin_passwd());
			sw_take_voice.setChecked(settingPara.isTake_voice());
			try {
				account.setText(settingPara.getAccount());
				password.setText(settingPara.getPassword());
			} catch (Exception e) {

			}
			try {
				schoolname.setText(settingPara.getSchoolname());
			} catch (Exception e) {

			}
			try {
				sw_isap.setChecked(settingPara.isIsap());
				sw_iscsv.setChecked(settingPara.isIscsv());
			} catch (Exception e) {

			}
			try {
				sw_islg.setChecked(settingPara.isIslg());
			} catch (Exception e) {

				sw_islg.setChecked(true);
			}

			try {
				sw_issg.setChecked(settingPara.isIssgid());
			} catch (Exception e) {

				sw_issg.setChecked(false);
			}

			try {
				sw_tcap.setChecked(settingPara.isIstcap());
			} catch (Exception e) {

				sw_tcap.setChecked(false);
			}

			try {
				sw_enzero.setChecked(settingPara.isIsenzero());
			} catch (Exception e) {

				sw_enzero.setChecked(false);
			}


			try {
				sw_allvideo.setChecked(settingPara.isIsallvideo());
			} catch (Exception e) {
				// TODO: handle exception
				sw_allvideo.setChecked(false);
			}


			try {
				sw_classvideo.setChecked(settingPara.isIsclassvideo());
			} catch (Exception e) {
				// TODO: handle exception
				sw_classvideo.setChecked(false);
			}


			try {
				sw_bleban.setChecked(settingPara.isIsbleban());
			} catch (Exception e) {
				// TODO: handle exception
				sw_bleban.setChecked(false);
			}

			try {
				sw_sportatt.setChecked(settingPara.isIssportatt());
			} catch (Exception e) {
				// TODO: handle exception
				sw_sportatt.setChecked(false);
			}

			updatetime.setText("" + settingPara.getUpdatetime());

			et_temp_url.setText(settingPara.getTempurl());

			sw_idle_full_screen_switch.setChecked(settingPara.isIsallscreen());

		} catch (Exception e) {
		}
	}

	@SuppressLint("RtlHardcoded")
	private class SpinnerAdapter extends ArrayAdapter<String> {
		Context context;
		String[] items = new String[] {};
		int size = 28;

		public SpinnerAdapter(final Context context, final int textViewResourceId, final String[] objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
			this.context = context;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
			}

			TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
			tv.setText(items[position]);
			tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
			tv.setTextColor(getResources().getColor(R.color.blue_4483ff));
			tv.setPadding(20, 20, 20, 20);
			tv.setTextSize(size);
			return convertView;
		}

		@SuppressLint("RtlHardcoded")
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
			}

			TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
			tv.setText(items[position]);
			tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
			tv.setTextColor(getResources().getColor(R.color.blue_4483ff));
			tv.setPadding(0, 0, 45, 0);
			tv.setTextSize(size);
			return convertView;
		}
	}

	public void factory_reset() {
		final MsgDialog ad = new MsgDialog(SettingParaActivity.this);
		ad.setTitle("警告");
		ad.setMessage("是否恢复出厂设置？");
		ad.setPositiveButton("确认", new OnClickListener() {
			public void onClick(View v) {
				ad.dismiss();
//				ProgressDialog pd = new ProgressDialog(SettingParaActivity.this);
//				pd.setMessage("加载中,请稍等");
//				pd.setCancelable(false);
//				pd.show();
				File file;
				DBOpenHelper sqlTpatt = new DBOpenHelper(SettingParaActivity.this);
				try {
					sqlTpatt.delDataBase();
					sqlTpatt.createDataBase();

					sqlTpatt.close();
				} catch (Exception e) {
					Log.i("TPATT", "createDataBase 失败"+e.toString());
				}

				try {

					OkHttpPost.token=null;

					SharedPreferences sPreferences=getSharedPreferences("omc", Activity.MODE_PRIVATE);
					SharedPreferences.Editor ee=sPreferences.edit();
					ee.clear();
					ee.commit();

					se.clear();
					se.commit();
					// String kk=sps.getString("studentin", null);
					// String ll=sps.getString("picimfo", null);
					// if (kk!=null) {
					// Gson gson=new Gson();
					// List<ChildCard> listca=gson.fromJson(kk,
					// new TypeToken<ArrayList<ChildCard>>() {
					// }.getType());
					//
					// for (int i = 0; i < listca.size(); i++) {
					//
					// String idm=listca.get(i).getCard_id();
					// String idm1=listca.get(i).getCard_id();
					// String idm2=listca.get(i).getCard_id();
					// String idm3=listca.get(i).getCard_id();
					// String idname=listca.get(i).getChild_name();
					// savesp(idm+"name", "");
					// savesp(idm1+"name", "");
					// savesp(idm2+"name", "");
					// savesp(idm3+"name", "");
					//
					// savesp(idm+"idimfo", "");
					// savesp(idm1+"idimfo", "");
					// savesp(idm2+"idimfo", "");
					// savesp(idm3+"idimfo", "");
					// }
					// }
					// savesp("port", "");

					AppBaseFun appBaseFun = new AppBaseFun();
					file = new File(appBaseFun.getPhoneCardPath() + "/tpatt/AttPhoto");
					appBaseFun.delete(file);
					File file1 = new File(appBaseFun.getPhoneCardPath() + "/tpatt/CardInfo");
					appBaseFun.delete(file1);
//					File file2 = new File(appBaseFun.getPhoneCardPath() + "/tpatt/PlayPhoto");
//					appBaseFun.delete(file2);
					File file3 = new File(appBaseFun.getPhoneCardPath() + "/tpatt/Setting.txt");
					appBaseFun.delete(file3);
					settingPara.reset_settingparam();
					settingPara.save_settingpara();
					restore_param();

					appBaseFun.makeRootDirectory(appBaseFun.getPhoneCardPath() + "/tpatt/AttPhoto");
				} catch (Exception e) {

				} finally {
					//	pd.dismiss();

				}
			}
		});
		ad.setNegativeButton("取消", new OnClickListener() {
			public void onClick(View v) {
				ad.dismiss();
			}
		});
	}

	@Override
	public void onBackPressed() {
		final Intent intent = new Intent(SettingParaActivity.this, MainIdleActivity.class);
		// 如果之前启动过这个Activity，并还没有被destroy的话，而是无论是否存在，都重新启动新的activity
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 获取应用的上下文，生命周期是整个应用，应用结束才会结束
		getApplicationContext().startActivity(intent);// 跳转
		finish();// 结束本Activity
		super.onBackPressed();
	}

	@SuppressWarnings("unused")
	private void savesp(String key, String value) {
		se.putString(key, value);
		se.commit();
	}

	private void setTimePicker(TimePicker tp) {
		tp.setIs24HourView(true);
		tp.setFocusable(true);
		tp.setClickable(true);
		tp.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// EditText et = findEditText(tp_go_school_t1);
				// if (et != null) {
				// InputMethodManager inputManager = (InputMethodManager)
				// et.getContext()
				// .getSystemService(Context.INPUT_METHOD_SERVICE);
				// inputManager.showSoftInput(et, 0);
				// }
				Toast.makeText(SettingParaActivity.this, "请进入鼠标模式选择时分进行修改", Toast.LENGTH_LONG).show();
			}
		});
	}

	public EditText findEditText(TimePicker timePicker) {
		List<NumberPicker> npList = findNumberPicker(timePicker);
		if (null != npList) {
			for (NumberPicker np : npList) {
				if (null != np) {
					for (int i = 0; i < np.getChildCount(); i++) {
						View childI = np.getChildAt(i);
						if (childI instanceof EditText) {
							return (EditText) childI;
						}
					}
				}

			}
		}

		return null;
	}

	private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
		List<NumberPicker> npList = new ArrayList<NumberPicker>();
		View child = null;

		if (null != viewGroup) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				child = viewGroup.getChildAt(i);
				if (child instanceof NumberPicker) {
					npList.add((NumberPicker) child);
				} else if (child instanceof LinearLayout) {
					List<NumberPicker> result = findNumberPicker((ViewGroup) child);
					if (result.size() > 0) {
						return result;
					}
				}
			}
		}

		return npList;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_TAB) {
			bt_cardinfo_update.setFocusable(true);
			bt_cardinfo_update.requestFocus();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
			bt_para_save.setFocusable(true);
			bt_para_save.requestFocus();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
			if (settingPara.getAtt_pic_platform() == 0) {
				et_att_url.setFocusable(true);
				et_att_url.requestFocus();
			} else if (settingPara.getAtt_pic_platform() == 1) {
				et_device_id.setFocusable(true);
				et_device_id.requestFocus();
			} else if (settingPara.getAtt_pic_platform() == 2) {
				et_att_upload_space.setFocusable(true);
				et_att_upload_space.requestFocus();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		et_att_upload_space.setFocusable(true);
		et_att_upload_space.requestFocus();
		return false;// super.onCreateOptionsMenu(menu);
	}
}
