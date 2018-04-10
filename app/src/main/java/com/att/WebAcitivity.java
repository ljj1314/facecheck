package com.att;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WebAcitivity extends Activity {

	private WebView webview;
	private ImageView btn;
	private TextView tv;
	private Calendar cad;
	private Handler th = new Handler();
	private SettingPara settingPara = new SettingPara();
	private byte[] midbytes;
	private Handler serialPortTimer = new Handler();
	private boolean isstop = true;
	public static Handler backhan=new Handler();
	public static Handler mm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.swwebview);

		webview = (WebView) findViewById(R.id.web);

		btn = (ImageView) findViewById(R.id.webback);
		tv = (TextView) findViewById(R.id.tvscid);

		cad = Calendar.getInstance();

		String card =getIntent().getStringExtra("card");


		webview.loadUrl("http://school.lovicoco.com/Monitor/RunRanking.aspx?deviceid="+card);
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		//webSettings.setBuiltInZoomControls(false);
		webview.addJavascriptInterface(new JsOperation(WebAcitivity.this), "android");

		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
		btn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(WebAcitivity.this, MainIdleActivity.class);
				startActivity(intent);
				finish();
			}
		});




		mm=new Handler(){
			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) {
					case 0:
						Intent intent = new Intent();
						intent.setClass(WebAcitivity.this, MainIdleActivity.class);
						startActivity(intent);
						finish();
						break;

					default:
						break;
				}


			}


		};


		th.postDelayed(runnable, 1000);
		thread.start();




		backhan.postDelayed(rn, 1000*10);
	}

	Runnable runnable = new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			tv.setText(new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss")
					.format(new Date(System.currentTimeMillis()))
					+ "        "
					+ MainIdleActivity.weeknum((cad.get(Calendar.DAY_OF_WEEK) - 1)));
			th.postDelayed(runnable, 1000);

		}
	};

	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent intent = new Intent();
			intent.setClass(WebAcitivity.this, MainIdleActivity.class);
			startActivity(intent);
			finish();
		}
		return false;
	};

	/*
	 * 刷卡检测线程
	 *
	 * @param
	 *
	 * @return
	 */
	Runnable mReadSerialPortTimer = new Runnable() {
		public void run() {
			try {
				String strTemp;
				String cardid = null;
				int i = 0;
				try {
					cardid = SwingCard.AttChkIdCardNormal(
							settingPara.getCard_upload(),
							settingPara.isCard_reversal());
				} catch (Exception e) {
					Log.i("TPATT", "AttChkIdCardNormal出错：" + e.toString());
				}
				//	System.out.println("cardid:" + cardid);
				if (cardid != null) {

					Log.i("TPATT", "待机页面 接收到刷卡卡号：" + cardid);

					// 处理串口数据,正常触发SwingCardAttActivity
					midbytes = cardid.getBytes();
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
						for (i = 0; i < cardid.length(); i++) {
							strTemp = new String(midbytes, i, 1);
							if (strTemp.equals("0")) {
							} else {
								cardid = new String(midbytes, i,
										cardid.length() - i);
								break;
							}
						}
					}

					// 从MainIdleActivity跳转到SwingCardAttActivity
					final Intent intent = new Intent(WebAcitivity.this,
							SwingCardAttActivity.class);
					// 如果之前启动过这个SwingCardAttActivity，并还没有被destroy的话，而是无论是否存在，都重新启动新的MainIdleActivity
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Bundle bundle = new Bundle();
					bundle.putString("swingcardid", cardid);
					intent.putExtras(bundle);
					// Log.i("TPATT", "start to intent");
					// 获取应用的上下文，生命周期是整个应用，应用结束才会结束
					getApplicationContext().startActivity(intent);// 跳转
					finish();// 结束本欢迎画面MainIdleActivity

				}

			} catch (Exception e) {

				Log.i("TPATT", "定时刷卡线程: Exception");
				serialPortTimer.postDelayed(mReadSerialPortTimer, 200); // 100ms
			}
		}
	};

	Thread thread = new Thread(new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
			System.out.println("线程启动" + isstop);
			while (isstop) {
				try {
					serialPortTimer.postDelayed(mReadSerialPortTimer, 100); // 100ms
					Thread.sleep(150);
				} catch (Exception e) {
					// TODO: handle exception
					serialPortTimer.postDelayed(mReadSerialPortTimer, 100); // 100ms
				}

			}

		}
	});

	protected void onDestroy() {
		super.onDestroy();
		isstop = false;

		backhan.removeCallbacks(rn);
	};

	public static Runnable rn=new Runnable() {
		public void run() {

			Message ms=mm.obtainMessage();
			ms.what=0;
			mm.sendMessage(ms);

		}
	};

	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
			//触摸屏幕时刻
			case MotionEvent.ACTION_DOWN:
				backhan.removeCallbacks(rn);
				backhan.postDelayed(rn, 1000*10);
				break;
			//触摸并移动时刻
			case MotionEvent.ACTION_MOVE:

				break;
			//终止触摸时刻
			case MotionEvent.ACTION_UP:
				break;

			default:
				break;
		}



		return false;


	}


	class JsOperation {

		Activity mActivity;

		public JsOperation(Activity activity) {
			mActivity = activity;
		}

		//    测试方法
		@JavascriptInterface
		public void reback() {
			Log.i("webs", "inso");
			backhan.removeCallbacks(rn);
			backhan.postDelayed(rn, 1000*10);
		}
	}




}
