package com.att.guide;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.att.R;

public class WifiPswDialog extends AlertDialog {
	Context context;
	private LinearLayout ll_cancel;
	private TextView tv_psw;
	private Button cancelButton;
	private Button okButton;
	private EditText pswEdit;
	private OnCustomDialogListener customDialogListener;
	String text;
	String textOK;

	String pw = null;

	boolean noCancel = false;

	Handler handler = new Handler();

	public WifiPswDialog(Context context, OnCustomDialogListener customListener, String text, String textOK) {
		// OnCancelListener cancelListener) {
		super(context);
		customDialogListener = customListener;
		this.context = context;
		this.text = text;
		this.textOK = textOK;
	}

	// 定义dialog的回调事件
	public interface OnCustomDialogListener {
		void back(String str);
	}

	public void setNoCancel() {
		noCancel = true;
		if (ll_cancel != null) {
			ll_cancel.setVisibility(View.GONE);
		}
	}

	public void setPW(String pw) {
		this.pw = pw;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_config_dialog);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		// setTitle("请输入密码");
		tv_psw = (TextView) findViewById(R.id.tv_psw);
		tv_psw.setText(text);
		pswEdit = (EditText) findViewById(R.id.wifiDialogPsw);
		pswEdit.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void afterTextChanged(final Editable arg0) {
				if (pw != null && arg0.toString().equals(pw)) {
					InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(pswEdit.getWindowToken(), 0);
					okButton.setFocusable(true);
					//okButton.setFocusableInTouchMode(true);
					okButton.requestFocus();
					//okButton.requestFocusFromTouch();
					handler.postDelayed(new Runnable() {
						public void run() {
							customDialogListener.back(arg0.toString());
							dismiss();
						}
					}, 8);
				}
			}
		});
		ll_cancel = (LinearLayout) findViewById(R.id.ll_cancel);
		if (noCancel) {
			ll_cancel.setVisibility(View.GONE);
		}
		cancelButton = (Button) findViewById(R.id.wifiDialogCancel);
		okButton = (Button) findViewById(R.id.wifiDialogCertain);
		okButton.setText(textOK);
		cancelButton.setOnClickListener(buttonDialogListener);
		okButton.setOnClickListener(buttonDialogListener);
		setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(pswEdit, InputMethodManager.SHOW_IMPLICIT);
			}
		});
	}

	private View.OnClickListener buttonDialogListener = new View.OnClickListener() {
		public void onClick(View view) {
			if (view.getId() == R.id.wifiDialogCancel) {
				pswEdit = null;
				customDialogListener.back(null);
				cancel();// 自动调用dismiss();
			} else {
				if (pswEdit.getText().toString() != null && pswEdit.getText().toString().length() > 0) {
					customDialogListener.back(pswEdit.getText().toString());
					dismiss();
				}
			}
		}
	};

}