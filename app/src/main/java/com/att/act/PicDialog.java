package com.att.act;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.att.R;

public class PicDialog {
	Context context;
	Builder builder;
	android.app.AlertDialog ad;
	//TextView titleView;
	ImageView messageView;
	LinearLayout buttonLayout;
	private WebView insimp;
//	private WebView wv;

	public PicDialog(Context context) {
		this.context = context;
		builder = new android.app.AlertDialog.Builder(context);
		ad = builder.create();
		ad.show();
		// 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
		Window window = ad.getWindow();
		window.setContentView(R.layout.picdlg);
		//	titleView = (TextView) window.findViewById(R.id.title);
		insimp=(WebView) window.findViewById(R.id.imp);

//		insimp.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				ad.dismiss();
//				Message mg=SwingCardAttActivity.sh.obtainMessage();
//				mg.what=0;
//				SwingCardAttActivity.sh.sendMessage(mg);
//			}
//		});
		WebSettings webSettings = insimp.getSettings();
		webSettings.setJavaScriptEnabled(true);

		insimp.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
		//messageView = (ImageView) window.findViewById(R.id.imp);
		buttonLayout = (LinearLayout) window.findViewById(R.id.buttonLayout);
	}

	public void setCancelable(Boolean cancelable) {
		if(builder!=null){
			builder.setCancelable(cancelable);
		}
	}

//	public void setTitle(int resId) {
//		titleView.setText(resId);
//	}
//
//	public void setTitle(String title) {
//		titleView.setText(title);
//	}

	public void setMessage(int resId) {
		messageView.setImageResource(resId);
	}

//	public void setMessage(String message) {
//		messageView.setImageResource(message);
//	}


	public boolean isshowing(){

		return ad.isShowing();
	}

	public void setimpvisible(){
		insimp.setVisibility(View.VISIBLE);
	}

	public void setimpinvisible(){
		insimp.setVisibility(View.INVISIBLE);
	}

	public void setUrl(String url){

		insimp.loadUrl(url);
	}


	/**
	 * 设置按钮
	 *
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(String text, final View.OnClickListener listener) {
		TextView button = new TextView(context);
		LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.weight = 1.0f;
		button.setLayoutParams(params);
		button.setBackgroundResource(R.drawable.selector_transparent_gray);
		//button.setPadding(0, 30, 0, 30);
		button.setText(text);
		button.setTextColor(context.getResources().getColor(R.color.blue_3173e1));
		button.setGravity(Gravity.CENTER);
		button.setTextSize(33);
		button.setOnClickListener(listener);
		buttonLayout.addView(button);
		button.setFocusable(true);
		//button.setFocusableInTouchMode(true);
		button.requestFocus();
		//button.requestFocusFromTouch();
	}

	/**
	 * 设置按钮
	 *
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton(String text, final View.OnClickListener listener) {
		TextView button = new TextView(context);
		LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.weight = 1.0f;
		button.setLayoutParams(params);
		button.setBackgroundResource(R.drawable.selector_transparent_gray);
		button.setPadding(0, 30, 0, 30);
		button.setText(text);
		button.setTextColor(context.getResources().getColor(R.color.blue_3173e1));
		button.setGravity(Gravity.CENTER);
		button.setTextSize(33);
		button.setOnClickListener(listener);
		if (buttonLayout.getChildCount() > 0) {
			params.setMargins(0, 0, 0, 0);
			button.setLayoutParams(params);
			buttonLayout.addView(button, 0);
		} else {
			button.setLayoutParams(params);
			buttonLayout.addView(button);
		}
		button.setFocusable(true);
		button.setFocusableInTouchMode(true);
		button.requestFocus();
		//button.requestFocusFromTouch();
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
	}

}
