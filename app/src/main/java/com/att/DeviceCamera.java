///**************************************************************************
//Copyright (C) 广东天波教育科技有限公司　版权所有
//文 件 名：
//创 建 人：
//创建时间：2015.10.30
//功能描述：刷卡界面
//**************************************************************************/
//package com.att;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.sql.Date;
//import java.text.SimpleDateFormat;
//import java.util.Timer;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import com.baidu.tts.client.SpeechError;
//import com.baidu.tts.client.SpeechSynthesizer;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.res.AssetFileDescriptor;
//import android.content.res.AssetManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.ImageFormat;
//import android.graphics.Rect;
//import android.graphics.YuvImage;
//import android.graphics.drawable.Drawable;
//import android.hardware.Camera;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//public class DeviceCamera extends Activity
//{
//	private Handler didplayTimer = new Handler();
//	private Handler cameratimer = new Handler();
//	private Handler exitTimer = new Handler();
//	private Handler serialPortTimer = new Handler();
//
//	private DBOpenHelper sqlTpatt;
//	private String    sdcardpath=null;                              //SD卡路径
//	private String                strcardid = null;                  //卡号
//	private String                strswingCardDtime = null;          //刷卡时间
//	private String                strswingCardDtimeDisplay = null;   //刷卡时间
//	private int                   goShoolStatus = 0;                 //进出校 0未知，1进校，2出校
//	private int                   cameraStatus = 0;                  //
//	private int                   tpattCameraReady = 0;              //启动拍照
//
//	private byte[]                midbytes;
//
//	private TextView              textV_curDtime;
//	private TextView              textV_schoolname;
//	private TextView              textV_studentname;
//	private TextView              textV_studentid;
//	private TextView              textV_studenttime;
//	private ImageView             imageV_student;
//	private ImageView             imageV_parent1,imageV_parent2,imageV_parent3;
//	private ImageView             imageV_parent4,imageV_parent5,imageV_parent6;
//
//	private LinearLayout          cameraDisplay = null;
//	private LinearLayout          swinglayout;
//
//	private SettingPara settingPara = new SettingPara();
//	private AppBaseFun  appBaseFun = new AppBaseFun();
//	private Handler mhandle;
//	private priviewCallBack pre;
//	private SettingPara sp=new SettingPara();
//	private String gotime1="",gotime2="",gotime3="",gotimeend1="",gotimeend2="",
//			gotimeend3="",outtime1="",outtime2="",outtime3="",outtimeend1="",outtimeend2="",outtimeend3="";
//	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
//			"yyyy-MM-dd");
//	private long lgotime1=0,lgotime2=0,lgotime3=0,lgotimeend1=0,lgotimeend2=0,
//			lgotimeend3=0,louttime1=0,louttime2=0,louttime3=0,louttimeend1=0,louttimeend2=0,louttimeend3=0;
//	private  Bitmap bmp=null;
//	private HandlerThread handlerThread=null;
//	private String oldcard=null;
//	private long oldtime=0;
//	private boolean isover=true;
//	private ExecutorService service = Executors.newFixedThreadPool(5);
//	private int takecame=0,savecame=0,gocame=0;
//	//private  SpeechSynthesizer speechSynthesizer=null;
//	 private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
//	    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
//	    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
//	    private SpeechSynthesizer mSpeechSynthesizer;
//	    private int takecard=0;
//	  private   AssetManager am = null;
//	  private MediaPlayer md=null;
//	  private Handler khandle=null;
//	  private Handler Xhandle=null;
//	  private String nowcard=null;
//	  private String nowtime=null;
//	  private AssetFileDescriptor afd=null;
//	  private Timer timer=null;
//
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState)
//    {
//    	// 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
//		// 设置你申请的应用appid
//		StringBuffer param = new StringBuffer();
//		param.append("appid="+getString(R.string.app_id));
//		param.append(",");
//		// 设置使用v5+
////		param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
////	    SpeechUtility.createUtility(DeviceCamera.this, param.toString());
//        super.onCreate(savedInstanceState);
//
//
//
//        //加入bugly
//        //CrashReport.initCrashReport(DeviceCamera.this, getString(R.string.app_id), false);
//
//        setContentView(R.layout.mobileswingcard);
//        swinglayout = (LinearLayout) findViewById(R.id.swingcardlayout);
//        swinglayout.setBackground(getResources().getDrawable(R.drawable.swingcard));
//
//
//        //接收刷卡卡号
//        Intent intent = this.getIntent();           //获取已有的intent对象
//        Bundle bundle = intent.getExtras();         //获取intent里面的bundle对象
//        strcardid = bundle.getString("swingcardid");//获取Bundle里面的字符串
//		oldcard=strcardid;
//		nowcard=strcardid;
//		nowtime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
//		oldtime=System.currentTimeMillis();
//
//	    	// md.create(DeviceCamera.this, R.drawable.beep);
//        //	songplay();
//	    //	Playbeep();
//     //   sdcardpath=appBaseFun.getSDPath();  //获取外置路径
//        sdcardpath=appBaseFun.getSDPath();    //获取内置路径
//		//初始显示界面
//		textV_curDtime = (TextView) findViewById(R.id.home_text_time1);
//		textV_schoolname = (TextView) findViewById(R.id.home_text_schoolname1);
//		textV_schoolname.setText(" ");
//		textV_studentname = (TextView) findViewById(R.id.home_text_student_name);
//		textV_studentid = (TextView) findViewById(R.id.home_text_student_id);
//		textV_studenttime = (TextView) findViewById(R.id.home_text_student_time);
//		textV_studenttime.setText(" ");
//		imageV_student = (ImageView) findViewById(R.id.home_img_student);
//		imageV_parent1 = (ImageView) findViewById(R.id.home_img_parent1);
//		imageV_parent2 = (ImageView) findViewById(R.id.home_img_parent2);
//		imageV_parent3 = (ImageView) findViewById(R.id.home_img_parent3);
//		imageV_parent4 = (ImageView) findViewById(R.id.home_img_parent4);
//		imageV_parent5 = (ImageView) findViewById(R.id.home_img_parent5);
//		imageV_parent6 = (ImageView) findViewById(R.id.home_img_parent6);
//		cameraDisplay = ((LinearLayout)findViewById(R.id.cameraView));
//
//
//		try {
//
//			 openCamera();
//		//	 VoiceBroadcast.ttsInit(DeviceCamera.this);
//		} catch (Exception e) {
//
//		}
//
//
//
//
////		timer = new Timer();
////		timer.schedule(new TimerTask() {
//
////			@Override
////			public void run() {
////
////
////				Message message=new Message();
////				message.what=4;
////				khandle.sendMessage(message);
////				 VoiceBroadcast.ttsPlay(DeviceCamera.this,"播放完成");
////
////
////			}
////		}, 2000, 1000);
//
//		HandlerThread handlerThreads=new HandlerThread("lie");
//		handlerThreads.start();
//
//		khandle= new Handler(handlerThreads.getLooper()) {
//
//			@Override
//			public void handleMessage(final Message msg) {
//
//
//				switch (msg.what) {
//				case  0 ://0表示未初始化 打开照机
//	        	    // cameratimer.postDelayed(cameraTimer, 50);
//
//	    			 try
//	 				 {
//	 					Log.v("TPATT", "打开照机");
//
//	 					cameraStatus = 1;
//	 			        openCamera();
//	 			        Thread.sleep(300);
//	 			     }
//	 			     catch (Exception localException2)
//	 			     {
//	 			    	Log.v("TPATT", "打开照机异常");
//
//	 			    	cameraStatus = 4;
//	 			    	tpattCamera=null;
//	 			        localException2.printStackTrace();
//	 			     }
//	        	     break;
//
//	        	    case  3 : //等待初始化完成
//	        	    // cameratimer.postDelayed(cameraTimer, 300);
//	        	     break;
//
//	        	    case  4 ://2表示可以启动拍照
//
////	        	    	while (true) {
////							if (isover) {
////								break;
////							}
////								continue;
////
////						}
//	        	    	while (true) {
//							if (isover) {
//								break;
//							}
//						}
////	        	    	Bundle j=msg.getData();
////	        	    	nowcard=j.getString("card");
////	        	    	nowtime=j.getString("time");
//
//	        	     if ( isover )
//	        	     {
//	        	    	try
//	         			{
//	         				Log.v("TPATT", "启动拍照");
//	         				 isover=false;
//	         				tpattCameraReady = 0;
//	         				cameraStatus = 3;
//	         				takecame++;
//
//	            	    	Log.i("camerapic", "takecame....."+takecame);
//	         				tpattCamera.stopPreview();
//	         				init();
//	         				tpattCamera.setOneShotPreviewCallback(pre);
//	         		//		tpattCamera.takePicture(null,null,null,cameraPicture);
//	                    }
//	         			catch(Exception e)
//	         			{
//	         				cameraStatus = 4;
//	         				Log.v("TPATT", "启动拍照异常"  + e.toString());
//
//	         			}
//	        	     }
//
//
//	        	     break;
//
//	        	    case  5 ://拍照完成,重新startPreview
//	        	     cameraStatus = 2;
//	        	    // cameratimer.postDelayed(cameraTimer, 50);
//	        	     break;
//
//	        	    default://其它
//	        	     cameraStatus = 0;
//	        	   //  cameratimer.postDelayed(cameraTimer, 50);
//	        	     if ( tpattCamera != null )
//	        	     {
//	        	    	 holder=null;
//	        	    	 tpattCamera.setPreviewCallback(null);
//		    	    	 tpattCamera.stopPreview();
//		            	 tpattCamera.release();
//		            	 tpattCamera = null;
//	        	     }
//	            	 break;
//	            	 case 6:
//
//	            		 opennewcamera();
//	            		 break;
//
//
//
//				}
//
//
//
//			}};
//
//			handlerThread=new HandlerThread("tel");
//			handlerThread.start();
//
//			mhandle = new Handler(handlerThread.getLooper()) {
//
//				@Override
//				public void handleMessage(final Message msg) {
//
//
//
//					switch (msg.what) {
//
//
//
//					case 1:
//
//						Bundle bundle=msg.getData();
//						byte[] _data=bundle.getByteArray("pic");
//						String path=bundle.getString("path");
//						 File f = new File(path);
//			     	        FileOutputStream fOut = null;
//
//			     	        try
//			     	        {
//			     	            fOut = new FileOutputStream(f);
//			     	            try
//			     	            {
//			     	            	fOut.write(_data);
//			     	            	//mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//			     	            //    fOut.flush();
//			     	                fOut.close();
//			     	            }
//			     	            catch (IOException e)
//			     	            {
//			     	            	Log.v("TPATT", "保存异常: flush IOException ");
//			     	                e.printStackTrace();
//			     	            }
//
//			     	        }
//			     	        catch (FileNotFoundException e)
//			     	        {
//			     	        	Log.v("TPATT", "保存异常:FileNotFoundException ");
//			     	        	e.printStackTrace();
//			     	        }
//			            //	appBaseFun.saveBitmapdata(data,appBaseFun.getSDPath() + "/tpatt/AttPhoto/" + strswingCardDtime + String.valueOf(goShoolStatus) + strcardid + ".jpg");
//			            //	bm.recycle();//回收bitmap空间
//
//
//
//						break;
//
//					case 2:
//
//						Bundle bundles=msg.getData();
//						byte[] _datas=bundles.getByteArray("pic");
//						String paths=bundles.getString("path");
//						int width=bundles.getInt("width");
//						int height=bundles.getInt("height");
//
//						if (tpattCamera==null) {
//							return;
//						}
//
//					        try {
//					        	// Size size = tpattCamera.getParameters().getPreviewSize();
//					            YuvImage image = new YuvImage(_datas, ImageFormat.NV21, width,
//					                    height, null);
//					           _datas=null;
//					            if (image != null) {
//					                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//					                image.compressToJpeg(new Rect(0, 0, width, height),
//					                        80, stream);
//
//					                BitmapFactory.Options opt = new BitmapFactory.Options();
//
//					                opt.inPreferredConfig = Bitmap.Config.RGB_565;
//
//					                opt.inPurgeable = true;
//
//					                opt.inInputShareable = true;
//					                opt.inSampleSize=4;
//					                 bmp = BitmapFactory.decodeByteArray(
//					                        stream.toByteArray(), 0, stream.size(),opt);
//					                stream.close();
//
//					                appBaseFun.saveBitmap(bmp, paths);
//					                if (!bmp.isRecycled()) {
//					                	 bmp.recycle();
//
//									}
//					                bmp=null;
//				                	 System.gc();
//
//					            }
//					        } catch (Exception ex) {
//					            Log.e("Sys", "Error:" + ex.toString());
//
//					            if (bmp!=null&&!bmp.isRecycled()) {
//					        		bmp.recycle();
//								}
//					            bmp=null;
//					            System.gc();
//					        }catch (OutOfMemoryError er) {
//
//					        	if (bmp!=null&&!bmp.isRecycled()) {
//					        		bmp.recycle();
//								}
//
//						            bmp=null;
//					        	System.gc();
//							}
//
//					   //     sqlTpatt.saveAttPhotoInfo(true);
//
//						break;
//
//
//
//					}
//
//
//				}
//				};
//
//
//				//刷卡定时器
//				serialPortTimer.postDelayed(mswingReadSerialPortTimer, 10); //1.2s
//
//		        //刷屏定时器
//				didplayTimer.postDelayed(redrawDidplayTimer, 100);
//
//
//
//    }
//
//
//    /*
//     * 刷卡检测线程
//     *
//     * @param
//     * @return
//     */
//    int testcout = 0;
//    int testcout1 = 0;
//    Runnable mswingReadSerialPortTimer = new Runnable()
//	{
//        public void run()
//        {
//
//            try
//            {
//            	int     i;
//  				String  cardid=null;
//  				String  strTemp;
//  				long nowtime=System.currentTimeMillis();
//  				textV_curDtime.setText( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
//  				serialPortTimer.postDelayed(mswingReadSerialPortTimer, 10); //50ms
//  				cardid = SwingCard.AttChkIdCardNormal(settingPara.getCard_upload(),settingPara.isCard_reversal());
//
//
//					if (cardid!=null&&cardid.length()>0) {
//						if (settingPara.isTake_voice()) {
//					//		songplay();
//						}
//						if ((nowtime-oldtime)<5000) {
//							if (oldcard.equals(cardid)) {//Log.v("TPATT", "刷卡显示窗口:刷卡检测:有效卡m" + cardid);
//								return;
//							}else {
//								oldcard=cardid;
//								oldtime=nowtime;
//							}
//
//
//					}else {
//						oldtime=nowtime;
//						oldcard=cardid;
//					}
//  					//
//					}else {
//						//Log.v("TPATT", "刷卡显示窗口:刷卡检测:有效d卡" + cardid);
//						return;
//					}
////
//
//
//
////
////  				testcout++;
////  				if (testcout>10) {
////  					if (testcout1==0) {
////  						cardid="1111111111";
////  						testcout1=1;
////					}else {
////						cardid="2111111112";
////						testcout1=0;
////					}
////
////
////  					testcout=0;
////				}else {
////					return;
////				}
//
//
//
//  				if ( cardid != null )
//  				{
//  					 Log.v("TPATT", "刷卡显示窗口:刷卡检测:有效卡" + cardid);
//
//  					 midbytes = cardid.getBytes();
//					 for ( i = 0; i < cardid.length(); i++ )
//					 {
//					     strTemp = new String(midbytes, i, 1);
//					     if ( strTemp.equals("0") )
//					     {
//					     }
//					     else
//					     {
//					    	 cardid = new String(midbytes, i, cardid.length()-i);
//					    	 break;
//					     }
//					 }
//
//  					 //考勤卡号与考勤时间
//  					 strcardid = cardid;
//  					takecard++;
//  					Message mg=new Message();
//  					mg.what=0;
//  					Bundle b=new Bundle();
//  					b.putString("data", "刷卡次数:"+takecard+"||时间:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss|SSS").format(new java.util.Date())+"卡号："+cardid);
//  					mg.setData(b);
//  					Xhandle.sendMessage(mg);
//
//
//
//   					 //Create a mPlayPhotoAndTVThread thread
//  				//	 exitTimer.removeCallbacks(DisplayimeoutExitTimer);
//  				//	 exitTimer.postDelayed(DisplayimeoutExitTimer, settingPara.getSwingCardDisplayTimeout()); //Ns后执行
//  					 didplayTimer.postDelayed(redrawDidplayTimer, 2);
//				}
//            }
//            catch (Exception e)
//            {
//
//            	Log.v("TPATT", "定时更新时间线程: Exception" );
//            }
//        }
//	};
//
//
//
//
//    //-------------------------------------------------camera--------------------------------------------//
//
//
//    private SurfaceHolder holder = null;
//    private Camera.Parameters parameters = null;
//    // camera 类
//    private Camera tpattCamera = null;
//    // 继承surfaceView的自定义view 用于存放照相的图片
//    private CameraView cameraV = null;
//
//    class CameraView extends SurfaceView
//    {
//		@SuppressWarnings("deprecation")
//		public CameraView(Context context)
//        {
//            super(context);
//            holder = this.getHolder();
//            //Log.v("TPATT", "打开照机1");
//            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//            holder.addCallback(new SurfaceHolder.Callback()
//            {
//				public void surfaceCreated(SurfaceHolder holder)
//                {
//                	try
//                	{
//                		if ( tpattCamera == null )
//                		{
//                  		    try
//                            {
//                  		    //	Camera.CameraInfo info = new Camera.CameraInfo();
//                  		    //	int cameraCount = Camera.getNumberOfCameras();
//                  		   // 	for (int cameraId = 0; cameraId < cameraCount; cameraId++ )
//                  		   // 	{
//                  		   // 		Camera.getCameraInfo(cameraId, info);
//	                           // 	if( info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT )
//	                          //  	{
//	                           		 try
//	                                     {
//	                            		    tpattCamera = Camera.open();
//	                            	//	    Log.v("TPATT", " CameraId = " + String.valueOf(cameraId));
//	                            		//    break;
//	                                     }
//	                            		 catch (RuntimeException e)
//	                            		 {
//	                            			 Log.v("TPATT", " Camera open erro " + e.toString() );
//	                            		 }
//	                          //  	}
//                  		   // 	}
//
//
//                  		    	if ( tpattCamera != null )
//                  		    	{
//
//                  		    		 //设置camera预览的角度，因为默认图片是倾斜90度的
//	                            	tpattCamera.setDisplayOrientation(0);
//	                            	 //设置holder主要是用于surfaceView的图片的实时预览，以及获取图片等功能，可以理解为控制camera的操作..
//	                            	tpattCamera.setPreviewDisplay(holder);
//
//
//	                            	/*先判断是否支持，否则可能报错*/
//	                            	 pre = new priviewCallBack();//建立预览回调对象
//	                            	tpattCamera.setPreviewCallback(pre); //设置预览回调对象
//	                                //mCamera.getParameters().setPreviewFormat(ImageFormat.JPEG);
//
//	                            	tpattCamera.startPreview();
//
//                  		    	}
//                  		    	else
//                  		    	{
//                  		    		Log.v("TPATT", "打开照机:异常 null " );
//                  		    	}
//
//                            }
//                            catch (IOException e)
//                            {
//                            	Log.v("TPATT", "打开照机3:异常 " + e.toString() );
//
//                            	if ( tpattCamera != null )
//                            	{
//	                            	cameraStatus = 4;
//	                            	tpattCamera.release();
//	                            	tpattCamera = null;
//                            	}
//                            }
//                		}
//                    }
//                	catch (Exception e)
//                    {
//                		Log.v("TPATT", "打开照机2:异常" + e.toString() );
//
//                		cameraStatus = 4;
//                    }
//                }
//
//				public void surfaceChanged(SurfaceHolder holders, int format,int width, int height)
//                {
//                	//Log.v("TPATT", "打开照机4"); s
//					if (holder.getSurface() == null){
//				          // preview surface does not exist
//				          return;
//				        }
//
//                	if ( tpattCamera != null )
//                	{
//
//                       tpattCamera.stopPreview();
//                		init();
//	                    cameraStatus = 2;
//                	}
//                }
//
//                public void surfaceDestroyed(SurfaceHolder holder)
//                {
//                	Log.v("TPATT", "关闭照机");
//
//                	cameraStatus = 5;
//                	if ( tpattCamera != null )
//                	{
//	                    //顾名思义可以看懂
//                		tpattCamera.setPreviewCallback(null); //！！这个必须在前，不然退出出错
//                		holder=null;
//	                	tpattCamera.stopPreview();
//	                	tpattCamera.release();
//	                	tpattCamera = null;
//                	}
//                }
//            });
//        }
//    }
//
//         public Bitmap Bytes2Bimap(byte[] b) {
//    	        if (b.length != 0) {
//    	            return BitmapFactory.decodeByteArray(b, 0, b.length);
//    	         } else {
//    	             return null;
//    	         }
//    	     }
//    // 检测摄像头是否存在的私有方法
//    @SuppressWarnings("unused")
//	private boolean checkCameraHardware(Context context)
//    {
//        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
//        {
//            // 摄像头存在
//            return true;
//        }
//        else
//        {
//            // 摄像头不存在
//            return false;
//        }
//    }
//
//
//
//    private final class priviewCallBack implements
//    android.hardware.Camera.PreviewCallback {
//        public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {
//
//
////        	byte[]  bufferbyte=data.clone();
////
////       	 Size size = tpattCamera.getParameters().getPreviewSize();
////      	Message msg=new Message();
////      	msg.what=2;
////      	Bundle bundle=new Bundle();
////      	bundle.putByteArray("pic", bufferbyte);
////      	bundle.putInt("width", size.width);
////      	bundle.putInt("height", size.height);
////      	bundle.putString("path", sdcardpath + "/tpatt/AttPhoto/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()) + String.valueOf(goShoolStatus) + nowcard + ".jpg");
////      	msg.setData(bundle);
////      	mhandle.sendMessage(msg);
//      	isover=true;
//
//
//     // 	sqlTpatt.saveAttInfo(nowcard, new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()), String.valueOf(goShoolStatus), java.util.UUID.randomUUID().toString().replaceAll("-",""), 0);
//
//
//
//        }
//   }
//
//
//
//    private void init(){
//    	parameters = tpattCamera.getParameters(); // 获取各项参数
////      parameters.setPictureFormat(256); // 设置图片格式
////      parameters.setPreviewSize(width, height); // 设置预览大小
////      parameters.setFocusMode("auto");
////      parameters.setPictureSize(width, height); // 设置保存的图片尺寸
////      parameters.setJpegQuality(80); // 设置照片质量
//      //parameters.setFocusMode(Camera.Parameters);
//      if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
//      {
//          parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
//      }
//    //  tpattCamera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
//      tpattCamera.setParameters(parameters);
//      tpattCamera.startPreview();
//
//    }
//
//
//
//
//
//
//
//
//
//
//    private void openCamera()
//    {
//    	cameraDisplay.removeAllViews();
//    	cameraV = new CameraView(DeviceCamera.this);
//		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, -1);
//		cameraDisplay.addView(cameraV, localLayoutParams);
//    }
//
//    private void opennewcamera(){
//    	cameraDisplay.removeView(cameraV);
//    	cameraDisplay.removeAllViews();
//
//    	cameraV = new CameraView(DeviceCamera.this);
//		LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, -1);
//		cameraDisplay.addView(cameraV, localLayoutParams);
//
//
//    }
//
////-------------------------------------------------camera--------------------------------------------//
//
//
//	public boolean onKeyDown(int keyCode, KeyEvent event)
//	{
//
//		if (((keyCode == KeyEvent.KEYCODE_BACK) || (keyCode == KeyEvent.KEYCODE_HOME)) && event.getRepeatCount() == 0)
//		{
//			//ExitDeviceCamera(1);
//		}
//		return false;
//	}
//
//	public static String hex2dec(String hex)
//	{
//		String dec = new String();
//		long num = Long.parseLong(hex,16);
//		dec = Long.toString(num, 10);
//		return dec;
//	}
//
//	public static String dec2hex(String dec)
//	{
//		String hex = new String();
//		long num = Long.parseLong(dec,10);
//		hex = Long.toString(num, 16);
//		return hex;
//	}
//
//    @Override
//    protected void onDestroy() {
//
//    	super.onDestroy();
//
//    	mhandle=null;
//    	khandle=null;
//    //	md.release();
//    //	mSpeechSynthesizer.release();
//
//    	if (tpattCamera!=null) {
//        	tpattCamera.setPreviewCallback(null);
//        	tpattCamera.stopPreview();
//			tpattCamera.release();
//			tpattCamera=null;
//		}
//
//    	if (bmp!=null&&!bmp.isRecycled()) {
//			bmp.recycle();
//		}
//    	bmp=null;
//    	System.gc();
//
//    }
//
//
//    @SuppressWarnings("unused")
//	private void redrawDidplay()
//    {
//    	int       i;
//    	String    strPhotoPath;
//    	String    strPlayName;
//    	String    strTransactionid;
//    	String    voice=null;
//
//		Log.v("TPATT", "刷卡显示");
//
//		strswingCardDtimeDisplay = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss").format(new Date(System.currentTimeMillis()));
//		strswingCardDtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
//		strTransactionid = java.util.UUID.randomUUID().toString().replaceAll("-","");
//		//textV_curDtime.setText(strswingCardDtimeDisplay);
//		//启动拍照
//	        if ( settingPara.isTake_photo() == true )
//	        {
//	        	 Message msg=new Message();
//	         	msg.what=4;
//	         	Bundle bs=new Bundle();
//	         	bs.putString("time", strswingCardDtime);
//	         	bs.putString("card",strcardid );
//	         	msg.setData(bs);
//	         	khandle.sendMessage(msg);
//	        	tpattCameraReady = 1;
//
//	        }
//		//显示卡号
//		if ( settingPara.getCard_upload() == 1 )
//		{
//			if (settingPara.getCard_disp() == 0 )
//			{
//				textV_studentid.setText(hex2dec(strcardid));
//			}
//			else
//			{
//				textV_studentid.setText(strcardid);
//			}
//		}
//		else
//		{
//			if ( settingPara.getCard_disp() == 1 )
//			{
//				textV_studentid.setText(dec2hex(strcardid));
//			}
//			else
//			{
//				textV_studentid.setText(strcardid);
//			}
//		}
//
//		goShoolStatus = 0;
//		SettingPara settingpara = new SettingPara();
//		SimpleDateFormat formatters = new SimpleDateFormat("HH:mm");
//        Date curDates = new Date(System.currentTimeMillis());// 获取当前时间
//        String   ctime = formatters.format(curDates);
//		String[] sTime = new String[3];
//		String[] eTime = new String[3];
//
//		sTime = settingpara.getGo_school_start();
//		eTime = settingpara.getGo_school_end();
//
//
//					goShoolStatus = 2;//出校
//
//
//		//保存未上报考勤记录和示上报考勤图片
//		//appBaseFun.saveNotUploadAttFile(strattRecord);
//		//appBaseFun.saveAttInfoFile(0,0,1,0);
//
//
//
//		//0表示语音，1表示图片
//		//姓名
//		strPlayName = appBaseFun.readCardInfoFile(strcardid,0);
//		if ( strPlayName != null )
//		{
//			textV_studentname.setText(strPlayName);
//			if ( voice != null )
//			{
//			    strPlayName += voice;
//			}
//			//TtsApp.playTts(strPlayName);
//		 //  VoiceBroadcast.ttsPlay(DeviceCamera.this,strPlayName);
//		//	mSpeechSynthesizer.speak(strPlayName);
//		}
//		else
//		{
//			textV_studentname.setText("未知");
//			if ( settingPara.isCarderr_voice_tips() == true )
//			{
//				//TtsApp.playTts(strPlayName);
//			//	VoiceBroadcast.ttsPlay(DeviceCamera.this,"无效卡");
//			//	mSpeechSynthesizer.speak(""+"无效卡");
//			}
//			else
//			{
//				if ( voice != null )
//				{
//					//TtsApp.playTts(strPlayName);
//				//	VoiceBroadcast.ttsPlay(DeviceCamera.this,voice);
//				//	mSpeechSynthesizer.speak(voicede;
//				}
//			}
//		}
//
//		//学生/家长图片路径
//		strPhotoPath = appBaseFun.readCardInfoFile(strcardid,1);
//        if ( strPhotoPath != null )
//	    {
//        	//0表示学生姓名,1表示入园时间,2表示学校,3~7表示家长称呼
//        	String strName = appBaseFun.readPhotoInfoFile(strPhotoPath,0);
//        	if ( strName != null )
//    		{
//    			textV_studentname.setText(strName);
//    		}
//
//        	//入园时间
//        	strName = appBaseFun.readPhotoInfoFile(strPhotoPath,1);
//        	if ( strName != null )
//        	{
//	        	if ( strName.equals("null") )
//	    		{
//	        		textV_studenttime.setText("");
//	    		}
//	        	else
//	        	{
//	        		textV_studenttime.setText(strName);
//	        	}
//        	}
//        	else
//        	{
//        		textV_studenttime.setText("");
//        	}
//
//        	//学校
//        	strName = appBaseFun.readPhotoInfoFile(strPhotoPath,2);
//        	if ( strName != null )
//    		{
//        		textV_schoolname.setText(strName);
//    		}
//        	else
//        	{
//        		textV_schoolname.setText("");
//        	}
//
//			//学生图片
//			try
//	        {
//				//Log.v("TPATT", "学生图片");
//				if ( imageV_student != null )
//				{
//					imageV_student.setImageDrawable(null);//imageV_student.setImageResource(0);
//				}
//				imageV_student.setImageDrawable(Drawable.createFromPath(appBaseFun.getSDPath() + "/tpatt/CardInfo/Photo/" + strPhotoPath + "/userName.jpg"));
//	        }
//	        catch (Exception e)
//		    {
//	        	Log.v("TPATT", "学生图片:无");
//		    	e.printStackTrace();
//		    }
//
//			//家长图片1
//			try
//	        {
//				//Log.v("TPATT", "家长图片1");
//				if ( imageV_parent1 != null )
//				{
//					imageV_parent1.setImageDrawable(null);//imageV_parent1.setImageResource(0);
//				}
//				imageV_parent1.setImageDrawable(Drawable.createFromPath(appBaseFun.getSDPath() + "/tpatt/CardInfo/Photo/" + strPhotoPath + "/parent1.jpg"));
//	        }
//	        catch (Exception e)
//		    {
//	        	Log.v("TPATT", "家长图片1" + ":无");
//		    	e.printStackTrace();
//		    }
//
//			//家长图片2
//			try
//	        {
//				//Log.v("TPATT", "家长图片2");
//				if ( imageV_parent2 != null )
//				{
//					imageV_parent2.setImageDrawable(null);//imageV_parent2.setImageResource(0);
//				}
//				imageV_parent2.setImageDrawable(Drawable.createFromPath(appBaseFun.getSDPath() + "/tpatt/CardInfo/Photo/" + strPhotoPath + "/parent2.jpg"));
//	        }
//	        catch (Exception e)
//		    {
//	        	Log.v("TPATT", "家长图片2" + ":无");
//		    	e.printStackTrace();
//		    }
//
//			//家长图片3
//			try
//	        {
//				//Log.v("TPATT", "家长图片3");
//				if ( imageV_parent3 != null )
//				{
//					imageV_parent3.setImageDrawable(null);//imageV_parent3.setImageResource(0);
//				}
//				imageV_parent3.setImageDrawable(Drawable.createFromPath(appBaseFun.getSDPath() + "/tpatt/CardInfo/Photo/" + strPhotoPath + "/parent3.jpg"));
//	        }
//	        catch (Exception e)
//		    {
//	        	Log.v("TPATT", "家长图片3" + ":无");
//		    	e.printStackTrace();
//		    }
//
//			//家长图片4
//			try
//	        {
//				//Log.v("TPATT", "家长图片4");
//				if ( imageV_parent4 != null )
//				{
//					imageV_parent4.setImageDrawable(null);//imageV_parent4.setImageResource(0);
//				}
//				imageV_parent4.setImageDrawable(Drawable.createFromPath(appBaseFun.getSDPath() + "/tpatt/CardInfo/Photo/" + strPhotoPath + "/parent4.jpg"));
//	        }
//	        catch (Exception e)
//		    {
//	        	Log.v("TPATT", "家长图片4" + ":无");
//		    	e.printStackTrace();
//		    }
//
//			//家长图片5
//			try
//	        {
//				//Log.v("TPATT", "家长图片5");
//				if ( imageV_parent5 != null )
//				{
//					imageV_parent5.setImageDrawable(null);//imageV_parent5.setImageResource(0);
//				}
//				imageV_parent5.setImageDrawable(Drawable.createFromPath(appBaseFun.getSDPath() + "/tpatt/CardInfo/Photo/" + strPhotoPath + "/parent5.jpg"));
//	        }
//	        catch (Exception e)
//		    {
//	        	Log.v("TPATT", "家长图片5" + ":无");
//		    	e.printStackTrace();
//		    }
//
//			//家长图片6
//			try
//	        {
//				//Log.v("TPATT", "家长图片6");
//				if ( imageV_parent6 != null )
//				{
//					imageV_parent6.setImageDrawable(null);//imageV_parent6.setImageResource(0);
//				}
//				imageV_parent6.setImageDrawable(Drawable.createFromPath(appBaseFun.getSDPath() + "/tpatt/CardInfo/Photo/" + strPhotoPath + "/parent6.jpg"));
//	        }
//	        catch (Exception e)
//		    {
//	        	Log.v("TPATT", "家长图片6" + ":无");
//		    	e.printStackTrace();
//		    }
//	    }
//        else
//        {
//        	Log.v("TPATT", "学生/家长图片路径不存在");
//        }
//        textV_studenttime.setText(new SimpleDateFormat("yyyy年MM月dd日    HH:mm").format(new Date(System.currentTimeMillis())));
//
//
//    }
//
//    /*
//	* 刷卡显示窗口超时退出
//	*
//	* @param
//	* @return
//	*/
//	Runnable redrawDidplayTimer = new Runnable()
//	{
//        public void run()
//        {
//            // handler自带方法实现定时器
//            try
//            {
//            	redrawDidplay();
//            }
//            catch (Exception e)
//            {
//
//            	Log.v("TPATT", "刷卡显示:异常 " + e.toString());
//            }  
//        }  
//    };
//    
//    
//    
//    
//    
//    
//  
//	public void onError(String arg0, SpeechError arg1) {
//		
//		
//	}
//
//
//	public void onSpeechFinish(String arg0) {
//		
//		
//	}
//
//
//	public void onSpeechProgressChanged(String arg0, int arg1) {
//		
//		
//	}
//
//
//	public void onSpeechStart(String arg0) {
//		
//		
//	}
//
//
//	public void onSynthesizeDataArrived(String arg0, byte[] arg1, int arg2) {
//		
//		
//	}
//
//
//	public void onSynthesizeFinish(String arg0) {
//		
//		
//	}
//
//
//	public void onSynthesizeStart(String arg0) {
//		
//		
//	}
//
//
//
//    
//    
//    
//}
