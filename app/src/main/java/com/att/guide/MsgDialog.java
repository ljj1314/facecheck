package com.att.guide;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.att.R;

public class MsgDialog {
	Context context;
	Builder builder;
	android.app.AlertDialog ad;
	TextView titleView;
	TextView messageView;
	LinearLayout buttonLayout;

	public MsgDialog(Context context) {
		this.context = context;
		builder = new android.app.AlertDialog.Builder(context);
		ad = builder.create();
		ad.show();
		// 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
		Window window = ad.getWindow();
		window.setContentView(R.layout.msg_dialog);
		titleView = (TextView) window.findViewById(R.id.title);
		messageView = (TextView) window.findViewById(R.id.message);
		buttonLayout = (LinearLayout) window.findViewById(R.id.buttonLayout);
	}

	public void setCancelable(Boolean cancelable) {
		if(builder!=null){
			builder.setCancelable(cancelable);
		}
	}

	public void setTitle(int resId) {
		titleView.setText(resId);
	}

	public void setTitle(String title) {
		titleView.setText(title);
	}

	public void setMessage(int resId) {
		messageView.setText(resId);
	}

	public void setMessage(String message) {
		messageView.setText(message);
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
		button.setPadding(0, 30, 0, 30);
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