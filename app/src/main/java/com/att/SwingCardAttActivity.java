/**************************************************************************
 Copyright (C) 广东天波教育科技有限公司　版权所有
 文 件 名：
 创 建 人：
 创建时间：2015.10.30
 功能描述：刷卡界面
 **************************************************************************/
package com.att;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.att.act.ChildCard;
import com.att.act.OkHttpPost;
import com.att.act.PicDialog;
import com.att.act.TtsPlay;
import com.att.act.WriteUnit;
import com.att.usecase.Nlproson;
import com.att.usecase.Nlproson.Relations;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.wch.ch9326driver.CH9326UARTDriver;

public class SwingCardAttActivity extends Activity implements
		SpeechSynthesizerListener {

	private static final String ACTION_USB_PERMISSION = "cn.wch.CH9326GPIODemoDriver.USB_PERMISSION";
	private Handler saveHandler;
	private Handler playHandler = null;
	private Handler showHandler = null;
	private Handler dataHandler = null;
	private Handler cameraHandler = null;

	private Handler timeHandler = new Handler();
	private Handler postPicHandler = new Handler();
	private Handler playSongHandler = new Handler();
	// private Handler cameraEndHandler = new Handler();

	private Handler exitTimer = new Handler();
	private Handler displayTimer = new Handler();
	private Handler serialPortHandler = new Handler();// 刷卡检测

	private ExecutorService serialPortExecutorService;
	private ExecutorService uploadExecutorService = Executors
			.newFixedThreadPool(1);

	private TextView textV_curDtime;
	private TextView textV_schoolname;
	private TextView textV_studentname;
	private TextView textV_studentid;
	// private TextView textV_studenttime;
	// private ImageView imageV_student;
	// private ImageView imageV_parent1, imageV_parent2, imageV_parent3;
	// private ImageView imageV_parent4, imageV_parent5, imageV_parent6;

	private LinearLayout swingLayout;
	private LinearLayout cameraDisplay;
	private ImageView[] parentImageView = new ImageView[7];

	private DBOpenHelper sqlTpatt;
	private SettingPara settingPara;
	private SharedPreferences shp = null;
	private CameraPriviewCallBack cameraCallBack;
	private AppBaseFun appBaseFun = new AppBaseFun();
	private AttPlatformProto attPlatformProto = null;

	private byte[] cardIdBytes;
	private int takeCardNum = 0;
	private int takecamera = 0, savecamera = 0, gocamera = 0;
	private int cameraViewTry = 0;
	private int cameraErrorTime = 0;
	private int goShoolStatus = 0; // 进出校 0未知，1进校，2出校
	@SuppressWarnings("unused")
	private int cameraStatus = 0; //
	// private int tpattCameraReady = 0; // 启动拍照

	private static String IS_REPEATED = "is_repeated";
	private String nowCard = null;
	// private String oldcard = null;
	private String sdcardPath = null; // SD卡路径
	private String strCardId = null; // 卡号
	private String strswingCardDtime = null; // 刷卡时间
	// private String strswingCardDtimeDisplay = null; // 刷卡时间
	// private String nowtime = null;
	private String gotime1 = "", gotime2 = "", gotime3 = "", gotimeend1 = "",
			gotimeend2 = "", gotimeend3 = "", outtime1 = "", outtime2 = "",
			outtime3 = "", outtimeend1 = "", outtimeend2 = "",
			outtimeend3 = "";
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");

	// private long oldtime = 0;
	private long lgotime1 = 0, lgotime2 = 0, lgotime3 = 0, lgotimeend1 = 0,
			lgotimeend2 = 0, lgotimeend3 = 0, louttime1 = 0, louttime2 = 0,
			louttime3 = 0, louttimeend1 = 0, louttimeend2 = 0,
			louttimeend3 = 0;

	private Bitmap saveBitmap = null;
	private Bitmap imageBitmap = null;
	private HandlerThread handlerThread = null;

	private SurfaceHolder surfaceHolder = null;
	private Camera tpattCamera = null;// camera 类
	private Camera.Parameters cameraParameters = null;
	private CameraView cameraView = null;// 继承surfaceView的自定义view 用于存放照相的图片
	private boolean isCameraOver = true;// 表示拍照是否完成
	private Attplatm ap = new Attplatm();
	private Nlproson nlProson;
	private List<Relations> lnRelations;
	private MediaPlayer beepPlayer = null;
	// private MediaPlayer md1 = null;
	// private MediaPlayer md2 = null;
	// private AssetManager am = null;
	// private String[] attinfoText = new String[3];
	// private SpeechSynthesizer speechSynthesizer=null;
	private Timer cameptm = null;

	private boolean isStop = false;

	public static boolean isnet = true;

	private Handler tbhandle = null;
	private String photopath = null;
	private String timepath = null;
	private String truecard = null;
	private Calendar cad;
	private ImageView iv = null;
	private ImageView sporton, endsport, startsport;
	private Timer scanTimer = null;
	private Handler gp = new Handler();
	private boolean isOpen;
	private  PicDialog pd;
	private Handler pich=new Handler();
	private ImageView insimp;
	public static Handler sh;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		isStop = false;

		settingPara = new SettingPara(getApplicationContext());

		// 加入bugly
		// CrashReport.initCrashReport(SwingCardAttActivity.this,
		// getString(R.string.app_id), false);

		setContentView(R.layout.swimseven);

		// swingLayout = (LinearLayout) findViewById(R.id.swingcardlayout);
		// swinglayout.setBackground(getResources().getDrawable(R.drawable.swingcard));//
		// 电信和健康同学
		// swinglayout.setBackground(getResources().getDrawable(R.drawable.swingcardyd));//和宝贝

		cad = Calendar.getInstance();
		cameraDisplay = ((LinearLayout) findViewById(R.id.cameraView));

		//add by ljj
		settingPara.setTake_photo(false);

		if (settingPara.isTake_photo() == true) {

			// Message msg=new Message();
			// msg.what=0;
			// khandle.sendMessage(msg);

			try {

				cameraStatus = 1;
				openCamera();

			} catch (Exception e) {
				Log.i("TPATT", "打开照机异常" + e.toString());
				WriteUnit.loadlist("打开照机异常" + e.toString());
				cameraStatus = 4;
				tpattCamera = null;
			}

		}

		new Handler().postDelayed(new Runnable() {
			public void run() {
				init();
			}
		}, 500);

	}

	private void init() {
		shp = getSharedPreferences("json", Activity.MODE_PRIVATE);
		serialPortExecutorService = Executors.newSingleThreadExecutor();

		// 接收刷卡卡号
		Intent intent = this.getIntent(); // 获取已有的intent对象
		Bundle bundle = intent.getExtras(); // 获取intent里面的bundle对象
		if (bundle != null) {
			try {
				strCardId = bundle.getString("swingcardid").toUpperCase();// 获取Bundle里面的字符串
			} catch (Exception e) {
				// TODO: handle exception
				strCardId=null;
			}

		}

		// oldcard=strcardid;
		nowCard = strCardId;
		// nowtime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new
		// java.util.Date());
		// oldtime=System.currentTimeMillis();

		// md.create(SwingCardAttActivity.this, R.drawable.beep);
		// songplay();

		// Playbeep();
		sdcardPath = appBaseFun.getPhoneCardPath(); // 获取外置SD路径

		iv = (ImageView) findViewById(R.id.sfviewim);




		// 初始显示界面
		textV_curDtime = (TextView) findViewById(R.id.home_text_time1);
		textV_schoolname = (TextView) findViewById(R.id.home_text_schoolname1);
		textV_schoolname.setText(" ");

		textV_studentname = (TextView) findViewById(R.id.home_text_student_name);
		textV_studentid = (TextView) findViewById(R.id.home_text_student_id);
		// textV_studenttime = (TextView)
		// findViewById(R.id.home_text_student_time);
		// textV_studenttime.setText(" ");

		// imageV_student = (ImageView) findViewById(R.id.home_img_student);
		// \

		startsport = (ImageView) findViewById(R.id.startsports);
		endsport = (ImageView) findViewById(R.id.endsoprts);
		sporton = (ImageView) findViewById(R.id.sporton);

		if (!settingPara.isIssportatt()) {
			startsport.setVisibility(View.GONE);
			endsport.setVisibility(View.GONE);
		}
		MyApp.driver = new CH9326UARTDriver(
				(UsbManager) getSystemService(Context.USB_SERVICE), this,
				ACTION_USB_PERMISSION);
		if (!MyApp.driver.UsbFeatureSupported())// 判断系统是否支持USB HOST
		{
			Toast.makeText(SwingCardAttActivity.this, "您的手机不支持USB HOST", 0)
					.show();
		}
		isOpen = false;
		if (!isOpen) {

			if (MyApp.driver.ResumeUsbList())// 打开CH9326设备，并初始化相关操作；如果拆分下来可使用EnumerateDevice，OpenDevice
			{
				Log.i("tpatt", "设备打开成功!");

				isOpen = true;
				int baud = 13;
				int stop_bit = 1;
				int data_bit = 4;
				int parity = 4;

				if (MyApp.driver.SetConfig(baud, data_bit, stop_bit, parity)) {// 函数说明请参照编程手册
					Log.i("tpatt", "串口设置成功!");
				} else {
					Log.i("tpatt", "串口设置失败!");

				}
			} else {
				Log.i("tpatt", "设备打开失败!");

			}
		}
		startsport.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (MyApp.driver.isConnected()) {
					System.out.println("发送AAA");
					try {
						byte[] bs = checkToML((byte) 0x03);
						int flag = MyApp.driver.WriteData(bs, bs.length);
						Log.e("flag", "" + flag);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				onWWpostpic("5");
				showDlg(0);
			}

		});

		endsport.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (MyApp.driver.isConnected()) {
					System.out.println("发送BBB");
					try {
						byte[] bs = checkToML((byte) 0x00);
						int flag = MyApp.driver.WriteData(bs, bs.length);
						Log.e("flag", "" + flag);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				onWWpostpic("6");
				showDlg(1);

			}
		});

		sporton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SwingCardAttActivity.this, WebAcitivity.class);
				intent.putExtra("card", nowCard);
				startActivity(intent);
				ExitSwingCardAttActivity(1);
			}
		});


		sh=new Handler(){

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:
						if (pd.isshowing()) {
							pd.dismiss();
						}
						showDlg(3);
						break;

					default:
						break;
				}

			}
		};

		// imageV_parent1 = (ImageView) findViewById(R.id.home_img_parent1);
		// imageV_parent2 = (ImageView) findViewById(R.id.home_img_parent2);
		// imageV_parent3 = (ImageView) findViewById(R.id.home_img_parent3);
		// imageV_parent4 = (ImageView) findViewById(R.id.home_img_parent4);
		// imageV_parent5 = (ImageView) findViewById(R.id.home_img_parent5);
		// imageV_parent6 = (ImageView) findViewById(R.id.home_img_parent6);
		// parentImageView[0] = imageV_parent1;
		// parentImageView[1] = imageV_parent2;
		// parentImageView[2] = imageV_parent3;
		// parentImageView[3] = imageV_parent4;
		// parentImageView[4] = imageV_parent5;
		// parentImageView[5] = imageV_parent6;

		if (shp.getString("school", " ") != null
				&& shp.getString("school", " ").length() > 0) {
			textV_schoolname.setText(shp.getString("school", " "));
		}

		// 拍照定时器

		handlerThread = new HandlerThread("telbo");
		handlerThread.start();

		HandlerThread handlerThreads = new HandlerThread("telbos");
		handlerThreads.start();

		cameraHandler = new Handler(handlerThreads.getLooper()) {

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:// 0表示未初始化 打开照机
						// cameratimer.postDelayed(cameraTimer, 50);

						try {

							// cameraStatus = 1;
							// openCamera();

						} catch (Exception localException2) {
							Log.i("TPATT", "打开照机异常");
							// writeinfile("打开照机异常" + localException2.toString() );
							WriteUnit.loadlist("打开照机异常"
									+ localException2.toString());
							cameraStatus = 4;
							tpattCamera = null;
							localException2.printStackTrace();
						}
						break;

					case 3: // 等待初始化完成
						// cameratimer.postDelayed(cameraTimer, 300);
						break;

					case 4:// 启动拍照

						// while (true) {
						// if (isover) {
						// break;
						// }
						// continue;
						//
						// }
						// cameratimer.postDelayed(cameraTimer, 20);
						// while (true) {
						// if (isover) {
						// break;
						// }
						// }
						Bundle j = msg.getData();
						nowCard = j.getString("card");
						// nowtime = j.getString("time");
						boolean isRepeated = j.getBoolean(IS_REPEATED, false);

						if (isCameraOver && tpattCamera != null) {
							// if (/* hasACamera()&& */tpattCamera != null /*&&
							// getCameraParameters()*/ && cameraStatus != 4) {
							// if (!isRepeated) {
							// try {
							// }
							// if (isCameraOver || tpattCamera == null) {
							// if (tpattCamera != null /* && getCameraParameters()
							// */ && cameraStatus != 4) {
							try {
								// getCameraParameters();
								// if (!isRepeated) {
								timepath = new SimpleDateFormat("yyyyMMddHHmmss")
										.format(System.currentTimeMillis());
								truecard = nowCard;
								photopath = timepath + truecard;
								gocamera++;
								Log.i("camerapic", "启动拍照 已刷卡数量:" + takeCardNum
										+ ",已拍照数量:" + gocamera + ",异常重启次数:"
										+ settingPara.getReBootCount());
								// settingPara.clearReBootCount();
								// Log.i("TPATT", "启动拍照 cameraStatus:" +
								// cameraStatus);
								isCameraOver = false;
								// tpattCameraReady = 0;
								cameraStatus = 3;
								takecamera++;
								Message mg = new Message();
								mg.what = 0;
								Bundle b = new Bundle();
								b.putString(
										"data",
										"下发拍照次数:"
												+ takecamera
												+ "||时间:"
												+ new SimpleDateFormat(
												"yyyy-MM-dd HH:mm:ss|SSS")
												.format(new java.util.Date())
												+ "卡号：" + nowCard + ".jpg");
								mg.setData(b);
								if (playHandler != null) {
									playHandler.sendMessage(mg);
								}
								tpattCamera.stopPreview();
								initCameraParameters();
								if (!isRepeated) {
									tpattCamera
											.setOneShotPreviewCallback(cameraCallBack);
								} else {
									isCameraOver = true;
								}

								// tpattCamera.takePicture(null,null,null,cameraPicture);
								// }
								// } else {// 重复刷卡
								// isCameraOver = false;
								// tpattCamera.stopPreview();
								// if (initCameraParameters()) {
								// cameraCallBack.setIsRepeated(true);
								// tpattCamera.setOneShotPreviewCallback(cameraCallBack);
								// }
								// }
								// Log.i("TPATT", "等待20秒后执行cameraEnd");
								// cameraEndHandler.postDelayed(cameraEnd, 1000 *
								// 20);
							} catch (Exception e) {
								// } else {// 重复刷卡
								// isCameraOver = false;
								// tpattCamera.stopPreview();
								// if (initCameraParameters()) {
								// cameraCallBack.setIsRepeated(true);
								// tpattCamera.setOneShotPreviewCallback(cameraCallBack);
								// }
								// }
								// Log.i("TPATT", "等待20秒后执行cameraEnd");
								// cameraEndHandler.postDelayed(cameraEnd, 1000 *
								// 20);
								// } else {
								// } catch (Exception e) {
								Log.i("TPATT", "相机启动出错：" + e.toString());

								cameraStatus = 4;
								if (tpattCamera != null) {
									try {
										tpattCamera.setPreviewCallback(null);
										tpattCamera.release();
										tpattCamera = null;
									} catch (RuntimeException ex) {
										Log.i("TPATT", "相机关闭出错：" + ex.toString());
									}
								}
								isCameraOver = true;

								// cameraEndHandler.removeCallbacks(cameraEnd);

								cameraErrorTime++;
								Log.i("TPATT", "启动拍照异常次数：" + cameraErrorTime);
								WriteUnit.loadlist("启动拍照异常次数：" + cameraErrorTime);

								Log.i("TPATT", "发送拍照异常");
								Intent intent = new Intent();
								intent.setAction("com.telpoedu.omc.FROM_ATT_ACTION");
								intent.putExtra("type", "camera_error");
								sendBroadcast(intent);

								if (cameraErrorTime < 5) {
									Message mg = new Message();
									mg.what = 1;
									if (playHandler != null) {
										playHandler.sendMessage(mg);
									}
								} else {

									settingPara.addReBootCount();
									Log.i("TPATT", "摄像头异常，设备进行重启");
									WriteUnit.writeInFile("摄像头异常，设备进行重启");
									// WriteUnit.loadlist("摄像头异常，设备进行重启");

									try {
										Thread.sleep(3000);
									} catch (InterruptedException e1) {
										Log.i("TPATT",
												"摄像头异常，设备进行重启 sleep出错"
														+ e1.toString());
									}

									if (android.os.Build.MODEL.startsWith("rk3066")) {
										HelperAV.exec("reboot");

									} else {
										AppBaseFun.execSuCmd("reboot");
									}
								}

								// Message mg=new Message();
								// mg.what=1;
								// Xhandle.sendMessage(mg);
								Toast.makeText(SwingCardAttActivity.this,
										"请检测摄像头是否正确运行", Toast.LENGTH_SHORT).show();
							}
						} else {
							Log.i("TPATT", "图片保存未完成，放弃当前保存");
							WriteUnit.loadlist("图片保存未完成，放弃当前保存");
							String days = new SimpleDateFormat("yyyyMMddHHmmss")
									.format(new java.util.Date());

							sqlTpatt.saveAttInfo(nowCard, days,
									String.valueOf(goShoolStatus),
									java.util.UUID.randomUUID().toString()
											.replaceAll("-", ""), photopath);
							sqlTpatt.saveAttPhotoInfo(true);
						}

						break;

					case 5:// 拍照完成
						cameraStatus = 2;
						// cameratimer.postDelayed(cameraTimer, 50);
						break;
					case 6:

						// openNewCamera();
						break;
					case 7:

						// initVoice();
						break;

					default:// 其它
						cameraStatus = 0;
						// cameratimer.postDelayed(cameraTimer, 50);
						if (tpattCamera != null) {
							surfaceHolder = null;
							tpattCamera.setPreviewCallback(null);
							tpattCamera.stopPreview();
							tpattCamera.release();
							tpattCamera = null;
						}
						break;

				}

			}
		};

		saveHandler = new Handler(handlerThread.getLooper()) {

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {

					case 1:

						Bundle bundle = msg.getData();
						byte[] _data = bundle.getByteArray("pic");
						String path = bundle.getString("path");
						File f = new File(path);
						FileOutputStream fOut = null;

						try {
							fOut = new FileOutputStream(f);
							try {
								fOut.write(_data);
								// mBitmap.compress(Bitmap.CompressFormat.PNG, 100,
								// fOut);
								// fOut.flush();
								fOut.close();
							} catch (IOException e) {
								Log.i("TPATT", "保存异常: flush IOException ");
								e.printStackTrace();
							}

						} catch (FileNotFoundException e) {
							Log.i("TPATT", "保存异常:FileNotFoundException ");
							e.printStackTrace();
						}
						// appBaseFun.saveBitmapdata(data,appBaseFun.getSDPath() +
						// "/tpatt/AttPhoto/" + strswingCardDtime +
						// String.valueOf(goShoolStatus) + strcardid + ".jpg");
						// bm.recycle();//回收bitmap空间

						sqlTpatt.saveAttPhotoInfo(true);

						break;

					case 2:// 照片保存

						Bundle bundles = msg.getData();
						byte[] _datas = bundles.getByteArray("pic");
						String paths = bundles.getString("path");
						int width = bundles.getInt("width");
						int height = bundles.getInt("height");

						if (tpattCamera == null) {
							return;
						}

						try {
							// Size size =
							// tpattCamera.getParameters().getPreviewSize();
							YuvImage image = new YuvImage(_datas, ImageFormat.NV21,
									width, height, null);
							_datas = null;
							if (image != null) {
								ByteArrayOutputStream stream = new ByteArrayOutputStream();
								image.compressToJpeg(new Rect(0, 0, width, height),
										80, stream);

								BitmapFactory.Options opt = new BitmapFactory.Options();

								opt.inPreferredConfig = Bitmap.Config.RGB_565;

								opt.inPurgeable = true;

								opt.inInputShareable = true;
								opt.inSampleSize = 1;
								saveBitmap = BitmapFactory
										.decodeByteArray(stream.toByteArray(), 0,
												stream.size(), opt);
								stream.close();

								try {
									saveBitmap=appBaseFun.rotateBitmap(saveBitmap, 180);
								} catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();
									Log.i("tp", "转角度失败"+e.getMessage());
								}
								appBaseFun.saveZZBitmap(saveBitmap, paths);
								if (!saveBitmap.isRecycled()) {
									saveBitmap.recycle();

								}
								saveBitmap = null;
								System.gc();
								// writeinfile(" 图片保存成功 " );
								WriteUnit.loadlist(" 图片保存成功 ");
							}
						} catch (Exception ex) {
							Log.e("TPATT", "图片保存出错:" + ex.toString());
							WriteUnit.loadlist(" 图片保存异常: " + ex.toString());
							Message mg = new Message();
							mg.what = 0;
							Bundle b = new Bundle();
							b.putString("data", "图片保存异常");
							mg.setData(b);
							if (playHandler != null) {
								playHandler.sendMessage(mg);
							}
							if (saveBitmap != null && !saveBitmap.isRecycled()) {
								saveBitmap.recycle();
							}
							saveBitmap = null;
							System.gc();
						} catch (OutOfMemoryError er) {

							if (saveBitmap != null && !saveBitmap.isRecycled()) {
								saveBitmap.recycle();
							}

							saveBitmap = null;
							System.gc();
						}

						sqlTpatt.saveAttPhotoInfo(true);

						break;

				}

			}
		};

		HandlerThread hs = new HandlerThread("telb");
		hs.start();

		showHandler = new Handler(hs.getLooper()) {

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:

						// if (settingPara.getAtt_pic_platform() ==
						// 0||settingPara.getAtt_pic_platform() == 1) {
						redrawWW();
						// } else if (settingPara.getAtt_pic_platform() == 2) {
						// redrawZW();
						// }
						// else if (settingPara.getAtt_pic_platform() == 1) {
						// redrawNL();
						// }

						break;

					default:
						break;
				}

			}
		};

		dataHandler = new Handler() {

			@SuppressWarnings("static-access")
			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:

						String caidstr = msg.getData().getString("card");
						if (settingPara.getCard_upload() == 1) {
							if (settingPara.getCard_disp() == 0) {
								// strcardid=hex2dec(strcardid);
								textV_studentid.setText(hex2dec(caidstr));
							} else {
								textV_studentid.setText(caidstr);
							}
						} else {
							if (settingPara.getCard_disp() == 1) {
								// strcardid=dec2hex(strcardid);
								textV_studentid.setText(dec2hex(caidstr));
							} else {
								textV_studentid.setText(caidstr);
							}
						}

						// textV_studenttime.setText(
						// new SimpleDateFormat("yyyy年MM月dd日    HH:mm").format(new
						// Date(System.currentTimeMillis())));
						break;

					case 1:

						String studentname = msg.getData().getString("name");
						textV_studentid.setText(studentname);
						textV_studentname.setText(new SimpleDateFormat("HH:mm:ss")
								.format(new Date(System.currentTimeMillis())));
						if (settingPara.isIssportatt()) {
							startsport.setVisibility(View.VISIBLE);
							endsport.setVisibility(View.VISIBLE);
							sporton.setVisibility(View.VISIBLE);
						} else {
							sporton.setVisibility(View.VISIBLE);
						}

						appBaseFun.gpiocon("b");
						gp.removeCallbacks(runnable);
						gp.postDelayed(runnable, 1000 * 3);

						break;

					case 2:
						textV_studentid.setText("未注册");
						textV_studentname.setText(new SimpleDateFormat("HH:mm:ss")
								.format(new Date(System.currentTimeMillis())));
						startsport.setVisibility(View.INVISIBLE);
						endsport.setVisibility(View.INVISIBLE);
						sporton.setVisibility(View.INVISIBLE);
						// textV_studenttime.setText("");
						break;

					case 3:

						if (settingPara.getSchoolname() != null
								&& !"".equals(settingPara.getSchoolname())) {
							textV_schoolname.setText(settingPara.getSchoolname());
						}

						break;

					default:
						break;
				}

			}
		};

		playHandler = new Handler() {

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:// 记录日志

						Bundle b = msg.getData();
						String data = b.getString("data");
						// writeinfile(data);
						WriteUnit.loadlist(data);

						break;

					case 1:// 重新打开相机
						try {
							if (tpattCamera == null) {
								openNewCamera();
							}

						} catch (Exception e) {

							Log.i("tappo", "重新打开相机出错：" + e.toString());
						}

						break;

					case 2:

						// Playbeep();
						break;

					case 3:

						// Playbeeps();
						break;
					case 4:// 播放音乐

						songBeepPlay();
						break;

					case 8:// 播放语音
						String dd = msg.getData().getString("data");
						// mSpeechSynthesizer.speak(dd);
						// String classname=null;
						// try {
						// classname=msg.getData().getString("classname", null);
						// // if (classname!=null&&!"".equals(classname.trim())) {
						// // textV_studenttime.setText(classname);
						// // }
						// } catch (Exception e) {
						// // TODO: handle exception
						// }

						if (TtsPlay.istts()) {
							if (!settingPara.isIsallvideo()) {

								TtsPlay.play();
							}

							// if
							// (settingPara.isIsclassvideo()&&classname!=null&&!"".equals(classname.trim()))
							// {
							// dd=classname+dd;
							// }

							TtsPlay.speaktts(dd);
						}

						break;

					case 9:// 能龙

						// redrawNL();

						break;

					default:
						break;
				}

			}

		};

		thread.start();

		tbhandle = new Handler() {

			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:

						try {

							String ll = msg.getData().getString("ll");
							String voice = msg.getData().getString("voice");

							TtsPlay.speaktts("未注册");
							textV_studentname.setText("未注册");

						} catch (Exception e) {
							// TODO: handle exception
						}

						break;

					case 1:

						String caidstr = msg.getData().getString("card");
						if (settingPara.getCard_upload() == 1) {
							if (settingPara.getCard_disp() == 0) {
								// strcardid=hex2dec(strcardid);
								textV_studentid.setText(hex2dec(caidstr));
							} else {
								textV_studentid.setText(caidstr);
							}
						} else {
							if (settingPara.getCard_disp() == 1) {
								// strcardid=dec2hex(strcardid);
								textV_studentid.setText(dec2hex(caidstr));
							} else {
								textV_studentid.setText(caidstr);
							}
						}

						// textV_studenttime.setText(
						// new SimpleDateFormat("yyyy年MM月dd日    HH:mm").format(new
						// Date(System.currentTimeMillis())));
						break;

					case 2:
						// try {
						// String classname=msg.getData().getString("classname",
						// null);
						// if (classname!=null&&!"".equals(classname.trim())) {
						// textV_studenttime.setText(classname);
						// }else {
						// textV_studenttime.setText("");
						// }
						// } catch (Exception e) {
						// // TODO: handle exception
						// }

						break;
					case 3:
						// textV_studenttime.setText("");

						break;
					default:
						break;
				}

			}

		};

		serialPortExecutorService.submit(serialPortThread);

		cameptm = new Timer();// TODO 检查video0定时器
		cameptm.schedule(new TimerTask() {

			@Override
			public void run() {
				if (!isStop) {
					try {
						if (!getCameraParameters()) {

							WriteUnit.writeInFile("摄像头异常，设备进行重启");

							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								Log.i("TPATT",
										"照机video0的sleep出错：" + e.toString());
							}

							AppBaseFun.execSuCmd("reboot");
						} else {
							Log.i("TPATT", "照机video0正常");
						}
					} catch (Exception e) {
						Log.i("TPATT", "照机video0出错：" + e.toString());

						// try {
						// Thread.sleep(3000);
						// } catch (InterruptedException e1) {
						// Log.v("TPATT", "照机video0的sleep出错：" + e1.toString());
						// }
						//
						// AppBaseFun.execSuCmd("reboot");
					}
				} else {
					if (cameptm != null) {
						cameptm.cancel();
					}
				}
			}
		}, 1000 * 60 * 3, 1000 * 30);

	}

	Runnable runnable = new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			appBaseFun.gpiocon("a");
		}
	};

	Thread serialPortThread = new Thread(new Runnable() {

		public void run() {

			while (true) {

				if (serialPortExecutorService.isShutdown()) {
					return;
				}

				try {
					serialPortHandler.postDelayed(swingReadSerialPortRunnable,
							0); // 1.2s
					Thread.sleep(400);
				} catch (InterruptedException e) {

					e.printStackTrace();
					serialPortHandler.postDelayed(swingReadSerialPortRunnable,
							0); // 1.2s
				}

				continue;

			}

		}
	});

	Thread thread = new Thread(new Runnable() {

		public void run() {

			sqlTpatt = new DBOpenHelper(SwingCardAttActivity.this);
			attPlatformProto = new AttPlatformProto();
			try {
				Log.i("TPATT", "SwingCardAttActivity打开数据库");
				sqlTpatt.createDataBase();
			} catch (Exception e) {// TODO
				Log.e("TPATT", "数据库打开失败失败" + e.toString());
			}

			inittime();
			// 刷卡定时器
			// serialPortTimer.postDelayed(mswingReadSerialPortTimer, 800);
			// //1.2s

			timeHandler.postDelayed(timerForNow, 1000);

			postPicHandler.postDelayed(threadRiverPostPic, 10000);

			// new showact().execute();
			if (strCardId != null) {
				playSongHandler.postDelayed(playSongs, 0);
				displayTimer.postDelayed(redrawDisplayTimer, 100);
			}

			exitTimer.postDelayed(DisplayTimeoutExitTimer,
					settingPara.getSwingCardDisplayTimeout()); // Ns后执行
			// initVoice();
			// writeinfile(shp.getString("studentin", null));
		}
	});



	Runnable playSongs = new Runnable() {

		public void run() {

			songBeepPlay();

		}
	};

	private void songBeepPlay() {

		if (beepPlayer != null && beepPlayer.isPlaying()) {
			beepPlayer.stop();
		}
		try {
			if (beepPlayer != null) {
				// md.reset();
				beepPlayer.start();
			} else {
				beepPlayer = MediaPlayer.create(SwingCardAttActivity.this,
						R.raw.beep);
				// md=new MediaPlayer();
				// // md.reset();
				// md.setDataSource(afd.getFileDescriptor());
				// md.prepare();
				beepPlayer.start();
			}
			// AssetManager am = getAssets();//获得该应用的AssetManager

			// Thread.sleep(200);
			// md.stop();
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
		} catch (SecurityException e) {
			// e.printStackTrace();
		} catch (IllegalStateException e) {
			// e.printStackTrace();
		}

	}

	// 对准时间
	private void inittime() {

		String nowday = simpleDateFormat.format(new java.util.Date()) + " ";

		String[] start = new String[3];
		String[] end = new String[3];
		// String[] time = new String[2];

		start = settingPara.getGo_school_start();
		Log.i("...", "" + start);

		gotime1 = nowday + start[0];
		gotime2 = nowday + start[1];
		gotime3 = nowday + start[2];

		end = settingPara.getGo_school_end();

		gotimeend1 = nowday + end[0];
		gotimeend2 = nowday + end[1];
		gotimeend3 = nowday + end[2];

		start = settingPara.getOut_school_start();

		outtime1 = nowday + start[0];
		outtime2 = nowday + start[1];
		outtime3 = nowday + start[2];

		end = settingPara.getOut_school_end();

		outtimeend1 = nowday + end[0];
		outtimeend2 = nowday + end[1];
		outtimeend3 = nowday + end[2];

		lgotime1 = fortime(gotime1);
		lgotime2 = fortime(gotime2);
		lgotime3 = fortime(gotime3);

		lgotimeend1 = fortime(gotimeend1);
		lgotimeend2 = fortime(gotimeend2);
		lgotimeend3 = fortime(gotimeend3);

		louttime1 = fortime(outtime1);
		louttime2 = fortime(outtime2);
		louttime3 = fortime(outtime3);

		louttimeend1 = fortime(outtimeend1);
		louttimeend2 = fortime(outtimeend2);
		louttimeend3 = fortime(outtimeend3);

	}

	// 转换为long格式，
	private long fortime(String sday) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		java.util.Date dt2 = null;
		try {
			dt2 = sdf.parse(sday);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		// 继续转换得到秒数的long型

		return dt2.getTime();
	}

	Runnable timerForNow = new Runnable() {

		public void run() {

			textV_schoolname
					.setText(new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss")
							.format(new Date(System.currentTimeMillis()))
							+ "        "
							+ MainIdleActivity.weeknum((cad
							.get(Calendar.DAY_OF_WEEK) - 1)));
			// attinfoText=sqlTpatt.readAttInfo();
			timeHandler.postDelayed(timerForNow, 1000);

		}
	};

	Runnable threadRiverPostPic = new Runnable() {
		public void run() {

			if (!isnet && settingPara.getAtt_pic_platform() == 1) {
				postPicHandler.postDelayed(threadRiverPostPic, 1000 * 60 * 10);// TODO
			} else {
				postPicHandler.postDelayed(threadRiverPostPic, 1000 * 2);// TODO
			}
			if (!settingPara.isIssportatt()) {
				onWWpostpic(null);
			}

		}
	};

	/*
	 * 刷卡检测线程
	 *
	 * @param
	 *
	 * @return
	 */
	// int testcout = 0;
	// int testcout1 = 0;
	Runnable swingReadSerialPortRunnable = new Runnable() {
		public void run() {

			try {
				int i;
				String cardid = null;
				String strTemp;
				// long nowtime = System.currentTimeMillis();
				// textV_curDtime.setText( new SimpleDateFormat("yyyy-MM-dd
				// HH:mm:ss").format(new java.util.Date()));

				cardid = SwingCard.AttChkIdCardNormal(
						settingPara.getCard_upload(),
						settingPara.isCard_reversal());

				// strcardid = cardid;
				if (cardid != null) {

					// Log.i("TPATT","刷卡页面 刷卡卡号："+cardid);

					if (settingPara.isIsenzero()) {

						if (settingPara.getCard_upload() == 0) {

							if (cardid.length() < 10) {
								cardid = "0000000000".substring(0,
										10 - cardid.length())
										+ cardid;
							}

						} else {

							if (cardid.length() < 8) {
								cardid = "00000000".substring(0,
										8 - cardid.length())
										+ cardid;
							}

						}

					} else {
						cardIdBytes = cardid.getBytes();
						for (i = 0; i < cardid.length(); i++) {
							strTemp = new String(cardIdBytes, i, 1);
							if (strTemp.equals("0")) {
							} else {
								cardid = new String(cardIdBytes, i,
										cardid.length() - i);
								break;
							}
						}
					}

					strCardId = cardid.toUpperCase();
					// 考勤卡号与考勤时间
					strswingCardDtime = new SimpleDateFormat("yyyyMMddHHmmss")
							.format(new Date(System.currentTimeMillis()));

					Log.i("TPATT", "----------刷卡的卡号:" + strCardId + "(时间:"
							+ strswingCardDtime + ")----------");
					takeCardNum++;

					playSongHandler.removeCallbacks(playSongs);
					playSongHandler.postDelayed(playSongs, 0);

					// Create a mPlayPhotoAndTVThread thread
					exitTimer.removeCallbacks(DisplayTimeoutExitTimer);
					exitTimer.postDelayed(DisplayTimeoutExitTimer,
							settingPara.getSwingCardDisplayTimeout()); // Ns后执行
					// didplayTimer.removeCallbacks(redrawDidplayTimer);
					// didplayTimer.postDelayed(redrawDidplayTimer, 0);

					// if (settingPara.getAtt_pic_platform() == 0 ||
					// settingPara.getAtt_pic_platform() == 2) {
					Message mg = new Message();
					mg.what = 0;
					showHandler.sendMessage(mg);
					// } else if (settingPara.getAtt_pic_platform() == 1) {
					// Message mg = new Message();
					// mg.what = 9;
					// if (playHandler != null) {
					// playHandler.sendMessage(mg);
					// }
					// }
				}
				// serialPortTimer.postDelayed(mswingReadSerialPortTimer, 200);
				// //50ms
			} catch (Exception e) {

				Log.i("TPATT", "定时更新时间线程: Exception");
				// serialPortTimer.postDelayed(mswingReadSerialPortTimer, 200);
				// //50ms
			}

		}
	};

	/*
	 *
	 * 退出刷卡显示窗口
	 *
	 * @param
	 *
	 * @return
	 */

	private void ExitSwingCardAttActivity(int flag) {
		Log.i("TPATT", "刷卡页面退出");
		isStop = true;

		try {
			int attTime = 0;

			sqlTpatt.close();

			if (tpattCamera != null) {
				// 顾名思义可以看懂
				tpattCamera.setPreviewCallback(null);
				tpattCamera.stopPreview();
				tpattCamera.release();
				tpattCamera = null;
			}

			saveHandler = null;
			cameraHandler = null;
			playHandler = null;

			if (beepPlayer != null) {
				beepPlayer.release();
				beepPlayer = null;
			}
			// if (md1 != null) {
			// md1.release();
			// md1 = null;
			// }
			// if (md2 != null) {
			// md2.release();
			// md2 = null;
			// }
			// mSpeechSynthesizer.release();

			if (saveBitmap != null && !saveBitmap.isRecycled()) {
				saveBitmap.recycle();
			}
			saveBitmap = null;
			System.gc();
			postPicHandler.removeCallbacks(threadRiverPostPic);
			timeHandler.removeCallbacks(timerForNow);
			displayTimer.removeCallbacks(redrawDisplayTimer);
			serialPortHandler.removeCallbacks(swingReadSerialPortRunnable);
			// if (flag == 1) {
			exitTimer.removeCallbacks(DisplayTimeoutExitTimer);
			// }
			serialPortExecutorService.shutdown();
			AttPlatformProto.setPlatformProtoStatus(3);
		} catch (Exception e) {
			Log.i("TPATT", "刷卡页面退出出错：" + e.toString());
		}

		if (flag == 0) {
			// 从MainIdleActivity跳转到SwingCardAttActivity
			final Intent intent = new Intent(SwingCardAttActivity.this,
					MainIdleActivity.class);

			// 如果之前启动过这个SwingCardAttActivity，并还没有被destroy的话，而是无论是否存在，都重新启动新的MainIdleActivity
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// 获取应用的上下文，生命周期是整个应用，应用结束才会结束
			getApplicationContext().startActivity(intent);// 跳转
		}

		finish();// 结束本欢迎画面MainIdleActivity
	}

	/*
	 * 刷卡显示窗口超时退出
	 *
	 * @param
	 *
	 * @return
	 */
	Runnable DisplayTimeoutExitTimer = new Runnable() {
		public void run() {
			// handler自带方法实现定时器
			try {
				ExitSwingCardAttActivity(0);
			} catch (Exception e) {

				Log.i("TPATT", "刷卡显示窗口:异常");
			}
		}
	};

	class CameraView extends SurfaceView {
		public CameraView(Context context) {
			super(context);
			surfaceHolder = this.getHolder();
			// Log.i("TPATT", "打开照机1");
			SurfaceHolder.Callback cameraCallback = new SurfaceHolder.Callback() {
				public void surfaceCreated(SurfaceHolder holder) {
					try {
						if (tpattCamera == null) {
							// Log.i("TPATT", "start打开照机");
							try {
								//
								// try {
								// tpattCamera = Camera.open(1);
								// } catch (Exception e) {
								// tpattCamera=null;
								// Log.i("TPATT", "start打开照机1出错：" +
								// e.toString());
								// }
								//
								// if (tpattCamera == null) {
								// try {
								// tpattCamera = Camera.open(0);
								// } catch (Exception e) {
								// tpattCamera=null;
								// Log.i("TPATT", "start打开照机0出错：" +
								// e.toString());
								// }
								// }

								int CammeraIndex = FindBackCamera();

								if (CammeraIndex == -1) {
									CammeraIndex = FindFrontCamera();
								}

								try {
									tpattCamera = Camera.open(CammeraIndex);
								} catch (Exception e) {
									// TODO: handle exception
									Log.i("TPATT",
											"start打开照机0出错：" + e.toString());
								}

								if (tpattCamera != null) {

									// 设置camera预览的角度，因为默认图片是倾斜90度的
									tpattCamera.setDisplayOrientation(180);
									// 设置holder主要是用于surfaceView的图片的实时预览，以及获取图片等功能，可以理解为控制camera的操作..
									tpattCamera.setPreviewDisplay(holder);

									/* 先判断是否支持，否则可能报错 */
									cameraCallBack = new CameraPriviewCallBack();// 建立预览回调对象
									tpattCamera
											.setPreviewCallback(cameraCallBack); // 设置预览回调对象
									// mCamera.getParameters().setPreviewFormat(ImageFormat.JPEG);

									tpattCamera.startPreview();// TODO 报错行
									Log.i("tappo", "摄像头初始化成功");
									// appBaseFun.writeinfile("摄像头初始化成功");
									WriteUnit.loadlist("摄像头初始化成功");
								} else {
									Log.i("TPATT", "打开照机:异常 tpattCamera为null");
									// writeinfile("打开照机:异常 ");
									WriteUnit
											.loadlist("打开照机:异常 tpattCamera为null");
								}

							} catch (Exception e) {
								Log.i("TPATT", "打开照机3:异常 " + e.toString());
								WriteUnit.loadlist("打开照机3:异常 " + e.toString());
								if (tpattCamera != null) {
									cameraStatus = 4;
									tpattCamera.setPreviewCallback(null);
									tpattCamera.release();
									tpattCamera = null;
									Log.i("TPATT", "关闭相机，进行重试 " + e.toString());
								}

								Message mg = new Message();
								mg.what = 1;
								if (playHandler != null) {
									playHandler.sendMessage(mg);
								}

							}
						}
					} catch (Exception e) {
						Log.i("TPATT", "打开照机2:异常" + e.toString());
						// appBaseFun.writeinfile("打开照机2:异常" + e.toString());
						WriteUnit.loadlist("打开照机2:异常" + e.toString());
						cameraStatus = 4;
					}
				}

				public void surfaceChanged(SurfaceHolder holders, int format,
										   int width, int height) {
					// Log.i("TPATT", "打开照机4");
					// if (holder.getSurface() == null){
					// // preview surface does not exist
					// return;
					// }

					if (tpattCamera != null) {
						try {
							tpattCamera.stopPreview();
							initCameraParameters();
							cameraStatus = 2;
						} catch (Exception e) {

						}

					} else {
						try {
							if (cameraViewTry < 5) {
								Message mg = new Message();
								mg.what = 1;
								if (playHandler != null) {
									playHandler.sendMessage(mg);
								}
								cameraViewTry++;
							}
						} catch (Exception e) {

						}

					}
					Log.i("TPATT", "打开照机执行 ");
				}

				public void surfaceDestroyed(SurfaceHolder holder) {
					Log.i("TPATT", "关闭照机");

					cameraStatus = 5;
					if (tpattCamera != null) {
						try {
							// 顾名思义可以看懂
							tpattCamera.setPreviewCallback(null); // ！！这个必须在前，不然退出出错
							// holder=null;
							tpattCamera.stopPreview();
							tpattCamera.release();
							tpattCamera = null;
						} catch (Exception e) {
							Log.i("TPATT", "关闭照机出错：" + e.toString());
						}

					}
				}
			};
			surfaceHolder.addCallback(cameraCallback);
		}
	}

	private int FindFrontCamera() {
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
				Log.i("tapp", "前置");
				return camIdx;
			}
		}
		return -1;
	}

	private int FindBackCamera() {
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
				Log.i("tapp", "后置");
				return camIdx;
			}
		}
		return -1;
	}

	public Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	public Bitmap onPreview(byte[] arg0, Camera arg1) {
		Size size = arg1.getParameters().getPreviewSize();
		int width = size.width;
		int height = size.height;

		final YuvImage image = new YuvImage(arg0, ImageFormat.NV21, width,
				height, null);
		ByteArrayOutputStream os = new ByteArrayOutputStream(arg0.length);
		if (!image.compressToJpeg(new Rect(0, 0, width, height), 50, os)) {
			return null;
		}
		byte[] tmp = os.toByteArray();
		Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);

		return bmp;
	}

	private final class CameraPriviewCallBack implements
			android.hardware.Camera.PreviewCallback {

		public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {
			// TODO 拍照回调
			savecamera++;

			//
			// try {
			// Bitmap bitmap = onPreview(data, camera);
			// iv.setVisibility(View.VISIBLE);
			//
			// iv.setImageBitmap(bitmap);
			// new Handler().postDelayed(new Runnable() {
			//
			// public void run() {
			// // TODO Auto-generated method stub
			// iv.setVisibility(View.GONE);
			// }
			// }, 300);
			// } catch (Exception e) {
			// // TODO: handle exception
			// }

			String days = null;
			try {
				Calendar clCalendar = Calendar.getInstance();

				days = new SimpleDateFormat("yyyyMMddHHmmss").format(clCalendar
						.getTime());
				Log.i("TPATT", "拍照回调 时间:" + days + ",卡号:" + nowCard);
				byte[] buffers = data.clone();
				Size size = tpattCamera.getParameters().getPreviewSize();
				Message msg = new Message();
				msg.what = 2;
				Bundle bundle = new Bundle();
				bundle.putByteArray("pic", buffers);
				bundle.putInt("width", size.width);
				bundle.putInt("height", size.height);

				bundle.putString("path", sdcardPath + "/tpatttp/AttPhoto/"
						+ photopath + ".jpg");
				msg.setData(bundle);
				if (saveHandler != null) {
					saveHandler.sendMessage(msg);
				}
			} catch (Exception e) {
				Log.i("TPATT", "保存照片出错：" + e.toString());
			} finally {
				sqlTpatt.saveAttInfo(truecard, days,
						String.valueOf(goShoolStatus), java.util.UUID
								.randomUUID().toString().replaceAll("-", ""),
						photopath);
				Log.i("camerapic", "已保存到数据库照片记录数量：" + savecamera);
				isCameraOver = true;
			}

			// downloadPic(data, size.width, size.height, sdcardpath +
			// "/tpatt/AttPhoto/" + strswingCardDtime +
			// String.valueOf(goShoolStatus) + strcardid + ".jpg");
			// _data=null;
		}
	}

	private boolean getCameraParameters() throws Exception {
		// try {
		if (tpattCamera == null) {
			return false;
		}
		Parameters parameters = tpattCamera.getParameters();
		File file = new File("/dev/video0");
		boolean exists = file != null && file.exists();
		if (parameters != null && exists) {
			return true;
		}
		// else {
		// return false;
		// throw new Exception();
		// }
		// } catch (Exception e) {
		// Log.e("TPATT", "获取video0出错:" + e.toString());
		// }
		return false;
	}

	private void initCameraParameters() {
		// try {
		cameraParameters = tpattCamera.getParameters(); // 获取各项参数
		// parameters.setPictureFormat(256); // 设置图片格式
		// parameters.setPreviewSize(width, height); // 设置预览大小
		// parameters.setFocusMode("auto");
		// parameters.setPictureSize(width, height); // 设置保存的图片尺寸
		// parameters.setJpegQuality(80); // 设置照片质量
		// parameters.setFocusMode(Camera.Parameters);
		if (cameraParameters.getSupportedFocusModes().contains(
				Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
		}
		// tpattCamera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
		tpattCamera.setParameters(cameraParameters);
		tpattCamera.startPreview();
		// return true;
		// } catch (Exception e) {
		// cameraStatus = 4;
		// Log.i("TPATT", "初始化相机出错：" + e.toString());
		// }
		// return false;
	}

	private void openCamera() {
		Log.i("TPATT", "打开相机");
		cameraDisplay.removeAllViews();
		cameraView = new CameraView(SwingCardAttActivity.this);
		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
				-1, -1);
		cameraDisplay.addView(cameraView, localLayoutParams);
	}

	private void openNewCamera() {
		Log.i("TPATT", "重新打开相机");
		cameraDisplay.removeView(cameraView);
		cameraDisplay.removeAllViews();

		cameraView = new CameraView(SwingCardAttActivity.this);
		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
				-1, -1);
		cameraDisplay.addView(cameraView, localLayoutParams);

	}

	// 0表示不在时间段内,1表示在时间段内
	public Boolean GetAttTimesStatus(String cTime, String sTime, String eTime) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		try {
			java.util.Date cDate = df.parse(cTime);
			java.util.Date sDate = df.parse(sTime);
			java.util.Date eDate = df.parse(eTime);
			if ((sDate.getTime() < cDate.getTime())
					&& (cDate.getTime() < eDate.getTime())) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e("TPATT", "获取考勤时间出错:" + e.toString());
		}

		return false;
	}

	private boolean isRepeatedSwingCard(Date curDate) {
		if (settingPara.getAtt_upload_space() == 0) {
			return false;
		}
		// String dtime = "1";
		String dtime = sqlTpatt.findByCardIdAttInfoTime(strCardId);
		if (dtime != null && dtime.length() > 0) {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			try {
				java.util.Date date = df.parse(dtime);
				long diff = curDate.getTime() - date.getTime();
				// if (dtime!=null) {
				if (diff < settingPara.getAtt_upload_space() * 1000) {

					Message msg = new Message();
					msg.what = 4;
					Bundle bs = new Bundle();
					bs.putString("time", strswingCardDtime);
					bs.putString("card", strCardId);
					bs.putBoolean(IS_REPEATED, true);// 启用拍照但不保存
					msg.setData(bs);
					if (cameraHandler != null) {
						cameraHandler.sendMessage(msg);
					}

					Message namemg = new Message();
					namemg.what = 1;
					Bundle bundles = new Bundle();
					bundles.putString("name", "重复刷卡");
					namemg.setData(bundles);
					dataHandler.sendMessage(namemg);

					if (settingPara.isCarderr_voice_tips()) {
						Message mg = new Message();
						mg.what = 8;
						Bundle b = new Bundle();
						b.putString("data", "重复刷卡");
						mg.setData(b);
						if (playHandler != null) {
							playHandler.sendMessage(mg);
						}
					}

					Message mg = new Message();
					mg.what = 3;
					Bundle b = new Bundle();
					b.putString("strPhotoPath", "");
					mg.setData(b);
					dataHandler.sendMessage(mg);

					return true;
				}
			} catch (Exception e) {
				Log.e("TPATT", "判断重复刷卡出错:" + e.toString());
			}
		}
		return false;
	}

	// 万维协议
	private void redrawWW() {
		int i = 0;
		String strPhotoPath = null;
		String strPlayName;
		// String strTransactionid;
		String voice = null;
		// boolean islocal = false;

		Date date = new Date(System.currentTimeMillis());
		// strswingCardDtimeDisplay = new SimpleDateFormat("yyyy年MM月dd日
		// HH:mm:ss").format(date);
		strswingCardDtime = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
		// strTransactionid =
		// java.util.UUID.randomUUID().toString().replaceAll("-", "");

		// Log.i("TPATT", "刷卡显示 strswingCardDtime:" + strswingCardDtime);

		// Message textin = new Message();
		// textin.what = 0;
		// Bundle bundle = new Bundle();
		// bundle.putString("card", strCardId);
		// textin.setData(bundle);
		// dataHandler.sendMessage(textin);

		// Log.i("TPATT", "万维刷卡cardid:" + strCardId);

		if (isRepeatedSwingCard(date)) {
			return;
		}

		String combo = shp.getString(strCardId + "local", "no");

		// 启动拍照

		goShoolStatus = 0;

		long nowt = System.currentTimeMillis();
		if ((nowt - lgotime1) > 0 && (lgotimeend1 - nowt) > 0) {
			i = 0;
			goShoolStatus = 1;// 进校
		} else if ((nowt - lgotime2) > 0 && (lgotimeend2 - nowt) > 0) {
			i = 1;
			goShoolStatus = 1;// 进校
		} else if ((nowt - lgotime3) > 0 && (lgotimeend3 - nowt) > 0) {
			i = 2;
			goShoolStatus = 1;// 进校
		} else if ((nowt - louttime1) > 0 && (louttimeend1 - nowt) > 0) {
			i = 0;
			goShoolStatus = 2;// 出校
		} else if ((nowt - louttime2) > 0 && (louttimeend2 - nowt) > 0) {
			i = 1;
			goShoolStatus = 2;// 出校
		} else if ((nowt - louttime3) > 0 && (louttimeend3 - nowt) > 0) {
			i = 2;
			goShoolStatus = 2;// 出校
		}

		if (goShoolStatus == 1) {
			String[] go_school = getResources().getStringArray(
					R.array.go_school);
			switch (i) {
				case 0:
					voice = go_school[settingPara.getGo_school_t1_voice()];
					break;

				case 1:
					voice = go_school[settingPara.getGo_school_t2_voice()];
					break;

				case 2:
					voice = go_school[settingPara.getGo_school_t3_voice()];
					break;
			}
		}

		if (goShoolStatus == 2) {
			String[] out_school = getResources().getStringArray(
					R.array.out_school);
			switch (i) {
				case 0:
					voice = out_school[settingPara.getOut_school_t1_voice()];
					break;

				case 1:
					voice = out_school[settingPara.getOut_school_t2_voice()];
					break;

				case 2:
					voice = out_school[settingPara.getOut_school_t3_voice()];
					break;
			}
		}

		// 保存未上报考勤记录和示上报考勤图片
		// appBaseFun.saveNotUploadAttFile(strattRecord);
		// appBaseFun.saveAttInfoFile(0,0,1,0);

		if (tpattCamera == null || settingPara.isTake_photo() == false) {
			sqlTpatt.saveAttInfo(strCardId, new SimpleDateFormat(
					"yyyyMMddHHmmss").format(new java.util.Date()), String
					.valueOf(goShoolStatus), java.util.UUID.randomUUID()
					.toString().replaceAll("-", ""), null);
		}

		// 0表示语音，1表示图片
		// 姓名
		// Log.i("TPATT", "打开文件名:" + strcardid);
		// strPlayName = appBaseFun.readCardInfoFile(strcardid,0);
		strPlayName = shp.getString(strCardId + "name", null);
		// Log.i("TPATT", "用户名:" + strPlayName+shp.getAll());
		// writeinfile(strcardid+"用户名:" + strPlayName+"储存空间为:"+shp.getAll());

		// 学生/家长图片路径
		try {
			strPhotoPath = shp.getString(strCardId + "idimfo", null);

		} catch (Exception e) {
			Log.e("TPATT", "获取学生照片出错:" + e.toString());
		}

		Message namemg = new Message();
		if (strPlayName != null && !"".equals(strPlayName)) {

			namemg.what = 1;
			Bundle bundles = new Bundle();
			bundles.putString("name", strPlayName);
			namemg.setData(bundles);
			dataHandler.sendMessage(namemg);

			String duoyin = shp.getString(strPlayName + "dyname", null);

			if (settingPara.isTake_photo() == true) {
				if ("no".equals(combo)) {
					Message msg = new Message();
					msg.what = 4;
					Bundle bs = new Bundle();
					bs.putString("time", strswingCardDtime);
					bs.putString("card", strCardId);
					bs.putBoolean(IS_REPEATED, !settingPara.isTake_photo());// 拍照但不保存
					msg.setData(bs);
					if (cameraHandler != null) {
						cameraHandler.sendMessage(msg);
					}
					// tpattCameraReady = 1;
					// }else {
				}
			}

			// textV_studentname.setText(strPlayName);
			if (settingPara.isTake_voice() == true) {
				Message mg = new Message();
				mg.what = 8;
				Bundle b = new Bundle();

				if (voice != null) {
					if (duoyin != null) {
						b.putString("data", duoyin + voice);
					} else {
						b.putString("data", strPlayName + voice);
					}

				} else {
					if (duoyin != null) {
						b.putString("data", duoyin);
					} else {
						b.putString("data", strPlayName);
					}
					// b.putString("data", strPlayName);
				}

				mg.setData(b);

				if (playHandler != null) {
					playHandler.sendMessage(mg);
				}
			}
			Log.i("TPATT", "信息推送完成");
		} else {

			List<ChildCard> lc = null;
			// if (settingPara.getAtt_pic_platform() == 0) {
			// lc=loadunknowcard(strCardId);
			// }
			// List<ChildCard>

			if (lc == null) {

				if (settingPara.isCarderr_voice_tips()) {
					// playbs.removeCallbacks(pbs);
					//
					// playbs.postDelayed(pbs, 0);

					Message mg = new Message();
					mg.what = 8;
					Bundle b = new Bundle();
					b.putString("data", "未注册");
					mg.setData(b);
					if (playHandler != null) {
						playHandler.sendMessage(mg);
					}

				}

				// textV_studentname.setText("未注册");
				namemg.what = 2;
				dataHandler.sendMessage(namemg);

			} else {

				namemg.what = 1;
				Bundle bundles = new Bundle();
				strPlayName = lc.get(0).getChild_name();
				bundles.putString("name", strPlayName);
				namemg.setData(bundles);
				dataHandler.sendMessage(namemg);

				String duoyin = shp.getString(strPlayName + "dyname", null);

				String classname = lc.get(0).getClass_name();

				if (classname != null && !classname.trim().equals("")) {

					Message mbe = tbhandle.obtainMessage();
					mbe.what = 2;
					Bundle bs = new Bundle();
					bs.putString("classname", classname);
					mbe.setData(bs);
					tbhandle.sendMessage(mbe);

				} else {
					Message mbe = tbhandle.obtainMessage();
					mbe.what = 3;

					tbhandle.sendMessage(mbe);
				}

				// textV_studentname.setText(strPlayName);
				if (settingPara.isTake_voice() == true) {
					Message mg = new Message();
					mg.what = 8;
					Bundle b = new Bundle();

					if (voice != null) {
						if (duoyin != null) {
							b.putString("data", duoyin + voice);
						} else {
							b.putString("data", strPlayName + voice);
						}

					} else {
						if (duoyin != null) {
							b.putString("data", duoyin);
						} else {
							b.putString("data", strPlayName);
						}
						// b.putString("data", strPlayName);
					}

					if (classname != null && !"".equals(classname.trim())) {
						b.putString("classname", classname);
					}

					mg.setData(b);

					if (playHandler != null) {
						playHandler.sendMessage(mg);
					}
				}
				Log.i("TPATT", "信息推送完成");

			}

			// writeinfile(strcardid+"无效卡"+strswingCardDtime);
		}

		Log.i("TPATT", "学生/家长图片路径：" + strPhotoPath + ",卡号：" + strCardId);

		// Message mg = new Message();
		// mg.what = 3;
		// Bundle b = new Bundle();
		// b.putString("strPhotoPath", strPhotoPath);
		// mg.setData(b);
		// dataHandler.sendMessage(mg);

	}

	private String filename(String url) {

		if (url == null) {
			return null;
		}

		if (settingPara.getAtt_pic_platform() == 1) {

			return url.substring(url.lastIndexOf("=") + 1) + ".jpg";

		} else {
			if (url.startsWith("http://wx.qlogo.cn")) {
				String sub = url.substring(25, url.length());
				String mm = sub.replaceAll("/", "_");
				return mm;
			} else {
				return url.substring(url.lastIndexOf("/") + 1);
			}
		}

	}

	// private void onRestore() {
	//
	// imageV_parent6.setImageResource(R.drawable.head_popo);
	// imageV_parent1.setImageResource(R.drawable.head_father);
	// imageV_parent2.setImageResource(R.drawable.head_grandfather);
	// imageV_parent3.setImageResource(R.drawable.head_gonggong);
	// imageV_parent4.setImageResource(R.drawable.head_mother);
	// imageV_parent5.setImageResource(R.drawable.head_grandmother);
	// imageV_student.setImageResource(R.drawable.head_student);
	//
	// }

	private List<ChildCard> loadunknowcard(String card) {

		String some = null;
		String str = settingPara.getCardInfoUrl() + "?CardID=" + card;

		try {
			HttpApp httpApp = new HttpApp();
			String res = httpApp.getSendAndReceive(str);
			JsonValidator jsonValidator = new JsonValidator();
			try {
				boolean isjson = jsonValidator.validate(res);
				if (!isjson) {

					Log.i("tapp", "不是正确josn格式");
					// appBaseFun.writeinfile("不是正确josn格式" );
					// appBaseFun.writeinfile("不是正确josn格式为"+strRes);
					WriteUnit.loadlist("不是正确josn格式为" + res);
					return null;
				}
			} catch (Exception e) {

			}

			JSONObject jsonObject;
			Gson gson = new Gson();

			jsonObject = new JSONObject(res).getJSONObject("_metadata");
			if (jsonObject.getString("code").equals("200")) {

				String strdata = new JSONObject(res).getString("data");

				List<ChildCard> listca = gson.fromJson(strdata,
						new TypeToken<ArrayList<ChildCard>>() {
						}.getType());

				if (card.equals(listca.get(0).getCard_id())
						|| card.equals(listca.get(0).getCard_id1())
						|| card.equals(listca.get(0).getCard_id2())
						|| card.equals(listca.get(0).getCard_id3())
						|| card.equals(listca.get(0).getCard_id4())
						|| card.equals(listca.get(0).getCard_id5())) {

					return listca;

				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}

	Runnable redrawDisplayTimer = new Runnable() {
		public void run() {
			// handler自带方法实现定时器

			redrawWW();

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_HOME))
				&& event.getRepeatCount() == 0) {
			MainIdleActivity.isback = false;
			ExitSwingCardAttActivity(0);
		}
		return false;
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

	@Override
	protected void onPause() {

		super.onPause();

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		isStop = true;
		// md.release();
		serialPortExecutorService.shutdown();
		uploadExecutorService.shutdown();
		// mSpeechSynthesizer.release();
		// mSpeechSynthesizer=null;
		if (tpattCamera != null) {
			tpattCamera.setPreviewCallback(null);
			tpattCamera.release();
			tpattCamera = null;
		}
		if (cameptm != null) {
			cameptm.cancel();
			cameptm = null;
		}
		if (isOpen) {
			MyApp.driver.CloseDevice();// 关闭设备的函数
		}

		if (pd!=null&&pd.isshowing()) {
			pd.dismiss();
			pich.removeCallbacks(rn);
		}
	}

	boolean isPostOk = true;// 上传是否完成

	private void onWWpostpic(final String status) {
		Log.i("Tapp", "准备上报");

		if (!isPostOk) {
			Log.i("Tapp", "未上报完，这次放弃");
			return;
		}
		if (uploadExecutorService.isShutdown()) {
			return;
		}

		uploadExecutorService.submit(new Runnable() {

			public synchronized void run() {

				try {

					int uploadAttCount;
					int uploadAttPhotoCount;

					uploadAttCount = 0;
					uploadAttPhotoCount = 0;

					Log.i("Tapp", "开始上报");

					String platformID = settingPara.getPlatformId();

					OkHttpPost request = new OkHttpPost(getApplicationContext()) {
						@Override
						public void onSuccess(String resposeBody) {

							Log.i(OkHttpPost.TAG, "云存储上报完毕");
							sqlTpatt.saveUploadAttInfo(0, 1);
							isPostOk = true;
						}

						@Override
						public void onFailure(String exceptionMsg) {

							Log.i("TPATT", exceptionMsg);
							// sqlTpatt.saveUploadAttInfo(0, 1);
							isPostOk = true;
						}
					};

					String[] attinfoText = sqlTpatt.readAttInfo(settingPara);

					if (attinfoText[0] != null) {
						isPostOk = false;
						uploadAttCount = request.uploadAttend(settingPara,
								sqlTpatt, platformID, null, status);

						if (uploadAttCount > 0) {
							sqlTpatt.saveUploadAttInfo(uploadAttCount, 0);

						}
						isPostOk = true;

					}
					// uploadWWAttend(platformID, platform, fileName, photoPath,
					// attInfo);

					if (uploadAttCount == 0) {
						if (attinfoText[1] != null && isPostOk) {
							isPostOk = false;
							request.uploadAttendpic(settingPara, sqlTpatt,
									platformID);

						}
					}

					Thread.sleep(500);
					// isPostOk = true;
					// isok=true;
				} catch (Exception e) {
					Log.e("TPATT", "上报图片出错2:" + e.toString());
				} finally {
					// isPostOk = true;
					Log.i("Tapp", "上报完毕");
				}

			}
		});

	}

	public void onError(String arg0, SpeechError arg1) {

		Log.i("TAPP", "error:" + arg0 + ".." + arg1);
	}

	public void onSpeechFinish(String arg0) {

		Log.i("TAPP", "onSpeechFinish:" + arg0);
	}

	public void onSpeechProgressChanged(String arg0, int arg1) {

		Log.i("TAPP", "onSpeechProgressChanged:" + arg0 + ".." + arg1);
	}

	public void onSpeechStart(String arg0) {

		Log.i("TAPP", "onSpeechStart:" + arg0);
	}

	public void onSynthesizeDataArrived(String arg0, byte[] arg1, int arg2) {

		Log.i("TAPP", "onSynthesizeDataArrived:" + arg0 + ".." + arg1);
	}

	public void onSynthesizeFinish(String arg0) {

		Log.i("TAPP", "onSynthesizeFinish:" + arg0);
	}

	public void onSynthesizeStart(String arg0) {

		Log.i("TAPP", "onSynthesizeStart:" + arg0);
	}

	private byte[] checkToML(byte ml) {
		byte[] bbs = tobyte(nowCard);
		byte[] bs = new byte[12];
		bs[0] = 0x0a;// 固定头
		bs[1] = (byte) 0xff;// 固定占位
		bs[2] = 0x09;// 长度 长度的计算方法是从0xDA-0xcrc
		bs[3] = (byte) 0xDA;// 表示命令
		bs[4] = 0x00;// 设备id，共5个字节，第一字节为0x00 高位在前 低位在后
		bs[5] = bbs[0];// 设备id，共5个字节，第一字节为0x00 高位在前 低位在后
		bs[6] = bbs[1];// 设备id，共5个字节，第一字节为0x00 高位在前 低位在后
		bs[7] = bbs[2];// 设备id，共5个字节，第一字节为0x00 高位在前 低位在后
		bs[8] = bbs[3];// 设备id，共5个字节，第一字节为0x00 高位在前 低位在后
		bs[9] = 0x00;// 手环运动开关值 共两个字节 按位表示运动类型 bit0表示计步，bit1表示心率
		// 高位在前低位在后
		bs[10] = ml;// 手环运动开关值 共两个字节 按位表示运动类型
		// bit0表示计步，bit1表示心率
		// 高位在前低位在后
		byte b = 0;
		for (int i = 0; i < bs.length; i++) {
			b += bs[i];
		}
		byte low4 = (byte) (b & 0xff);
		low4 = (byte) (~low4 + 1);
		bs[11] = low4;// 校验码
		return bs;
	}

	private static byte[] tobyte(String s) {
		long l = Long.parseLong(s);
		byte[] bs = long2Bytes(l);
		byte[] score = new byte[4];
		for (int i = 0; i < score.length; i++) {
			score[i] = bs[i + 4];
		}

		return score;
	}

	public static byte[] long2Bytes(long num) {
		byte[] byteNum = new byte[8];
		for (int ix = 0; ix < 8; ++ix) {
			int offset = 64 - (ix + 1) * 8;
			byteNum[ix] = (byte) ((num >> offset) & 0xff);
		}
		return byteNum;
	}


	private void showDlg(int flag){

		pd =new PicDialog(SwingCardAttActivity.this);
		if (flag==0) {
//			  pd.setMessage(R.drawable.sportopen);
//			  pd.setimpvisible();
			pd.setUrl("http://school.lovicoco.com/Monitor/MyRunRanking.aspx?deviceid="+nowCard);
		}else if (flag==1){
//			pd.setMessage(R.drawable.sportclose);
//			 pd.setimpvisible();
			pd.setUrl("http://open.lovicoco.com/v50/realtimeheart_demo.html?mpid=2&accesstoken=toueqziI/blnvYHQEJkj3FhbuOZXK7RksnbOTHoyEQ8=&deviceid="+nowCard);
		}else if (flag==3) {
			pd.setMessage(R.drawable.tishi04);
			pd.setimpinvisible();

		}

//		pd.setPositiveButton("关闭", new View.OnClickListener() {
//			
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				pd.dismiss();
//			}
//		});

		pich.removeCallbacks(rn);
		pich.postDelayed(rn, 1000*30);

	}

	Runnable rn=new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			if (pd.isshowing()) {
				pd.dismiss();
			}
		}
	};


}
