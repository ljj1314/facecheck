/*************************************************************************
 Copyright (C) 广东天波教育科技有限公司　版权所有
 文 件 名：
 创 建 人：
 创建时间：2015.10.30
 功能描述：开机界面
 **************************************************************************/
package com.att;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.arcsoft.sdk.PermissionAcitivity;
import com.att.act.TtsPlay;
import com.att.act.UtilImfo;
import com.att.act.WriteUnit;
import com.att.server.HttppostAst;
import com.att.server.NlServer;
import com.att.server.Nlpostast;
import com.att.server.OmcLoadService;
import com.att.server.TelpoService;
import com.att.server.TpBleService;
import com.att.server.WwpostServer;
import com.att.server.ZwService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;

import android_serialport_api.SerialPort;

/**
 * Created by telpo on 14-7-18.
 */
public class VersionActivity extends PermissionAcitivity {
    private Handler handler = new Handler();

    private TextView tv_version;
    private SerialPort mSerialPort = null;
    private AppBaseFun appBaseFun = new AppBaseFun();
    //private LinearLayout verlayout;
    private String mSampleDirPath;
    @SuppressWarnings("unused")
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    // private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
    // private SettingPara settingpara=new SettingPara();
    private SettingPara settingpara = null;
    private String isfirst = null;
    private SharedPreferences sp;
    private String sdpath=null;
    private String locatpath=null;
    boolean canBack = false;

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {

        if (mSerialPort == null) {
            mSerialPort = new SerialPort(new File("/dev/ttyS1"), 4800, 0);
        }

        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
        // 设置你申请的应用appid
        // StringBuffer param = new StringBuffer();
        // param.append("appid="+getString(R.string.app_id));
        // param.append(",");
        // 设置使用v5+
        // param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
        // SpeechUtility.createUtility(VersionActivity.this, param.toString());

        super.onCreate(savedInstanceState);

        setContentView(R.layout.version);

        tv_version = (TextView) findViewById(R.id.TVversion);
        tv_version.setText("V" + CurrentVersion.getVerName(VersionActivity.this)
                + "." + CurrentVersion.getVerCode(this));

        new Handler().postDelayed(new Runnable() {
            public void run() {
                init();
            }
        }, 500);
    }

    private void init() {

        settingpara = new SettingPara(getApplicationContext());

        //TODO 加入bugly
//		UserStrategy strategy = new UserStrategy(getApplicationContext());
//		strategy.setDeviceID(settingpara.getDeviceId(getApplicationContext()));//设置ID
//		strategy.setAppChannel("PlatformID:"+SettingPara.getPlatformID());//设置渠道
//		strategy.setAppVersion( CurrentVersion.getVerName(VersionActivity.this) + "." + CurrentVersion.getVerCode(this));      //App的版本
//		//strategy.setAppPackageName("com.tencent.xx");  //App的包名
//		CrashReport.initCrashReport(getApplicationContext(), "900030064", true, strategy);
//		CrashReport.setUserId(settingpara.getDeviceId(getApplicationContext()));//设置用户ID
        //CrashReport.setUserSceneTag(getApplicationContext(), 9527);

        Log.i("TAPPPP", "型号" + android.os.Build.MODEL);

        //sp = getSharedPreferences("json", Activity.MODE_PRIVATE);

        //verlayout = (LinearLayout) findViewById(R.id.verlayout);
        //verlayout.setBackgroundResource(R.drawable.welcome);

        //ImageView imageView = (ImageView) findViewById(R.id.imtel);
        //imageView.setVisibility(View.INVISIBLE);// 移动专用

        sdpath=appBaseFun.getSDPath();
        locatpath=appBaseFun.getPhoneCardPath();


        sp=getSharedPreferences("json", Activity.MODE_PRIVATE);
        isfirst=sp.getString("sdfirst", "no");



        //appBaseFun.makeRootDirectory(appBaseFun.getSDPath() + "/tpatt");





        try {
            File myfileFile = new File(locatpath + SettingPara.PATH_SETTING_TXT);
            if (!myfileFile.exists()) {
                File thisfile = new File(sdpath + SettingPara.PATH_SETTING_TXT);
                if (thisfile.exists()) {
                    appBaseFun.copyFile(sdpath + SettingPara.PATH_SETTING_TXT,
                            locatpath + SettingPara.PATH_SETTING_TXT);
                }
            }
        } catch (Exception e) {

        }




        // 检测是否有SD卡，如果有检测对应的文件是否存在，如不存在创建
        if (appBaseFun.isHaveSDCard() == true) {

//			if (isfirst.equals("no")) {
//				appBaseFun.copyFolder(sdpath + "/tpatt",locatpath+ "/tpatt");
//				SharedPreferences.Editor se=sp.edit();
//				se.putString("sdfirst", "yes");
//				se.commit();
//			}

            appBaseFun.makeRootDirectory(sdpath + "/tpatttp");

            appBaseFun.makeRootDirectory(sdpath + "/tpatttp/PlayPhoto");

            appBaseFun.makeRootDirectory(sdpath + "/tp");
        }

        loaddata();


    }

	/*
	 * 获取拍照文件夹大小
	 *
	 * @param
	 *
	 * @return
	 */
//	Runnable getFileSizeThread = new Runnable() {
//		public void run() {
//			// handler自带方法实现定时器
//			try {
//				// 考勤图片大于100M时,清1小时前的数据
//				long size = appBaseFun.getAutoFileOrFilesSize(appBaseFun.getSDPath() + "/tpatt/AttPhoto");
//				Log.i("AttPhoto文件夹大小:", String.valueOf(size));
//				if (size > (1000 * 100 * 1024)) {
//					appBaseFun.delAllFile(appBaseFun.getSDPath() + "/tpatt/AttPhoto", 1);
//					handler.postDelayed(getFileSizeThread, 24 * 60 * 60 * 1000); // 1天
//				}
//			} catch (Exception e) {
//				Log.i("TPATT", "定时更新时间线程: Exception");
//				handler.postDelayed(getFileSizeThread, 24 * 60 * 60 * 1000); // 1天
//			}
//		}
//	};

    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/data";
        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        // copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/"
        // + LICENSE_FILE_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME,
                mSampleDirPath + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME,
                mSampleDirPath + "/" + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME,
                mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME);


        copyFromAssetsToSdcard(false, "duoyin.csv", locatpath + "/duoyin.csv");

    }

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover
     *            是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(canBack){
            super.onBackPressed();
        }
    }


    private void loaddata(){

        canBack = false;
        DBOpenHelper sqlTpatt = new DBOpenHelper(VersionActivity.this);
        try {
            Log.i("TPATT", "createDataBase VersionActivity");

            sqlTpatt.createDataBase();

            Log.i("TPATT", "createDataBase VersionActivity0");
            sqlTpatt.close();
            Log.i("TPATT", "createDataBase VersionActivity1");
        } catch (Exception e) {
            Log.i("TPATT", "createDataBase 失败"+e.toString());
        }
        appBaseFun.makeRootDirectory(appBaseFun.getPhoneCardPath() + "/tpatttp");
        appBaseFun.makeFilePath(locatpath + SettingPara.PATH_SETTING_FILE, SettingPara.PATH_SETTING_NAME);

        appBaseFun.makeRootDirectory(locatpath + "/tpatttp/AttPhoto");
        appBaseFun.makeRootDirectory(locatpath + "/tpatttp/PlayPhoto");
        appBaseFun.makeRootDirectory(locatpath + "/tpatttp/UploadAttBak");
        appBaseFun.makeRootDirectory(locatpath + "/tpatttp/CardInfo");
        appBaseFun.makeRootDirectory(locatpath + "/tpatttp/CardInfo/Photo");
        appBaseFun.makeRootDirectory(locatpath + "/tpatttp/SQLDB");
        appBaseFun.makeRootDirectory(locatpath + "/logtext");
        //appBaseFun.makeRootDirectory(locatpath + "/tpatt/multimedia");
        appBaseFun.makeRootDirectory(locatpath + "/tpatttp/tp");
        // appBaseFun.makeRootDirectory(appBaseFun.getPhoneCardPath()+"/tpatt");
        // appBaseFun.makeRootDirectory(appBaseFun.getPhoneCardPath()+"/tpatt/AttPhoto");
        // SettingPara settingpara = new SettingPara();

        initialEnv();
        TtsPlay.init(getApplicationContext(), Environment.getExternalStorageDirectory().toString());


        UtilImfo.start(getApplicationContext());


        WriteUnit.loadlist(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "启动程序，版本号为:"
                + CurrentVersion.getVerCode(this));


        Intent intent = new Intent();
        intent.setAction("com.telpoedu.omc.FROM_ATT_ACTION");
        intent.putExtra("type", "broadcast_support");
        sendBroadcast(intent);


        Intent intentst1 = new Intent(VersionActivity.this, OmcLoadService.class);
        getApplication().startService(intentst1);
        try {

            if (settingpara.isIsap()) {
                if (settingpara.getAtt_pic_platform() == 0||settingpara.getAtt_pic_platform() == 2) {
                    Intent intentst = new Intent(VersionActivity.this, WwpostServer.class);
                    getApplication().startService(intentst);
                } else if(settingpara.getAtt_pic_platform() == 1){
                    Intent intentst = new Intent(VersionActivity.this, Nlpostast.class);
                    getApplication().startService(intentst);
                }

            }
        } catch (Exception e) {

        }

        try {
            if (settingpara.isIstcap() && settingpara.getAtt_pic_platform() == 0) {
                Intent intentst = new Intent(VersionActivity.this, HttppostAst.class);
                getApplication().startService(intentst);
            }
        } catch (Exception e) {

        }


        try {

            if (settingpara.isIsbleban()) {
                Intent intentst = new Intent(VersionActivity.this, TpBleService.class);
                getApplication().startService(intentst);
            }


        } catch (Exception e) {
            // TODO: handle exception
        }

        // Intent intentst1=new
        // Intent(getApplicationContext(),OmcServer.class);
        // getApplication().startService(intentst1);
        // Log.i("TPATT","密码:"+settingpara.getAdmin_passwd());

        // SharedPreferences spPreferences=getSharedPreferences("json",
        // Activity.MODE_PRIVATE);
        // String mc=spPreferences.getString("1329866452"+"idimfo",
        // "nothing");
        // String kk=spPreferences.getString("4F4426D4"+"idimfo",
        // "nothing");
        // Log.i("huanghuang", "mc="+mc+"||KK="+kk);

        // 从MyActivity跳转到MainActivity

        // 创建一个新的线程来显示欢迎动画，指定时间后结束，跳转至指定界面
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 测试bugly
                    // CrashReport.testJavaCrash();
                    //isfirst = sp.getString("studentin", null);
                    SwingCard.start();
                    WriteUnit.start();
                    // settingpara.setAtt_platform(2);
                    AttPlatformProto.setPlatformProtoStatus(0);

//					if (settingpara.hasSetUp()||settingpara.isIsuninstall()) {
//						if(settingpara.isIsuninstall()){
//							settingpara.setIsuninstall(false);
//							settingpara.save_settingpara();
//						}
                    if (settingpara.getAtt_pic_platform() == 0) {

                        Intent intents = new Intent(VersionActivity.this, TelpoService.class);
                        startService(intents);

                        Thread.sleep(2500);// 用线程暂停3秒来模拟做了一个耗时3秒的检测操作,为了省时间，改为1秒
                        final Intent intent = new Intent(VersionActivity.this, MainIdleActivity.class);

                        // 如果之前启动过这个Activity，并还没有被destroy的话，而是无论是否存在，都重新启动新的activity
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // 获取应用的上下文，生命周期是整个应用，应用结束才会结束
                        startActivity(intent);// 跳转

                        VersionActivity.this.finish();// 结束本欢迎画面Activity

                    } else if (settingpara.getAtt_pic_platform() == 1) {

                        Intent intents = new Intent(VersionActivity.this, NlServer.class);
                        startService(intents);

                        Thread.sleep(2500);// 用线程暂停3秒来模拟做了一个耗时3秒的检测操作,为了省时间，改为1秒
                        final Intent intent = new Intent(VersionActivity.this, MainIdleActivity.class);

                        // 如果之前启动过这个Activity，并还没有被destroy的话，而是无论是否存在，都重新启动新的activity
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // 获取应用的上下文，生命周期是整个应用，应用结束才会结束
                        startActivity(intent);// 跳转

                        VersionActivity.this.finish();// 结束本欢迎画面Activity

                    } else if (settingpara.getAtt_pic_platform() == 2) {
                        Intent intents = new Intent(VersionActivity.this, ZwService.class);
                        startService(intents);
                        Thread.sleep(1000);// 用线程暂停3秒来模拟做了一个耗时3秒的检测操作,为了省时间，改为1秒
                        final Intent intent = new Intent(VersionActivity.this, MainIdleActivity.class);
                        // 如果之前启动过这个Activity，并还没有被destroy的话，而是无论是否存在，都重新启动新的activity
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // 获取应用的上下文，生命周期是整个应用，应用结束才会结束
                        startActivity(intent);// 跳转
                        VersionActivity.this.finish();// 结束本欢迎画面Activity
                    }

//					} else {//没有必要的设置，进入安装向导
//						final Intent intent = new Intent(VersionActivity.this, StepFirstActivity.class);
//						// 如果之前启动过这个Activity，并还没有被destroy的话，而是无论是否存在，都重新启动新的activity
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						// 获取应用的上下文，生命周期是整个应用，应用结束才会结束
//						startActivity(intent);// 跳转
//						VersionActivity.this.finish();// 结束本欢迎画面Activity
//					}
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }

}
