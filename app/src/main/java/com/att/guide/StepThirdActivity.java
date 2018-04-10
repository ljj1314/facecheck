package com.att.guide;

import com.att.R;
import com.att.SettingPara;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class StepThirdActivity extends Activity {

	private ImageView back_button;
	boolean isSet = false;
	private Button next_step;

	private EditText et_device_id;
	private EditText et_school_id;
	private LinearLayout ll_province_id;
	private EditText et_province_id;

	private SettingPara sp = new SettingPara();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.step_third_seven);
		init();
	}

	@Override
	protected void onResume() {
		sp = new SettingPara();
		init();
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(StepThirdActivity.this, StepSecondActivity.class);
		intent.putExtra("isSet", isSet);
		startActivity(intent);
		overridePendingTransition(R.anim.back_left_in, R.anim.back_right_out);
		super.onBackPressed();
	}

	private void init() {
		isSet = getIntent().getBooleanExtra("isSet", false);
		back_button = (ImageView) findViewById(R.id.back_button);
		back_button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		next_step = (Button) findViewById(R.id.next_step);
		next_step.setEnabled(false);
		next_step.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(save_param()){
					Intent intent = new Intent();
					intent.setClass(StepThirdActivity.this, StepFourthActivity.class);
					intent.putExtra("isSet", isSet);
					startActivity(intent);
					overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
					StepThirdActivity.this.finish();
				}
			}
		});
		et_device_id = (EditText) findViewById(R.id.device_id);
		et_device_id.setText(sp.getDevice_id());
		et_device_id.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				updateNextStep();
			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void afterTextChanged(Editable arg0) {
			}
		});

		et_school_id = (EditText) findViewById(R.id.school_id);
		et_school_id.setText(sp.getSchool_id());
		et_school_id.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				updateNextStep();
			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void afterTextChanged(Editable arg0) {
			}
		});

		ll_province_id = (LinearLayout) findViewById(R.id.ll_province_id);
		et_province_id = (EditText) findViewById(R.id.province_id);
		if(sp.getAtt_pic_platform()==0&&
				(sp.getAtt_url().equals(SettingPara.ATT_URL_CTXY_ALL)
						||sp.getAtt_url().equals(SettingPara.ATT_URL_CTXY_GANSU))){
			ll_province_id.setVisibility(View.VISIBLE);
			et_province_id.setText(sp.getProvince_id());
			et_province_id.addTextChangedListener(new TextWatcher() {
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					updateNextStep();
				}

				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				}

				public void afterTextChanged(Editable arg0) {
				}
			});
		}else{
			ll_province_id.setVisibility(View.GONE);
			et_province_id.setText("62000000");
		}
	}

	private void updateNextStep() {
		if (et_device_id.getEditableText().toString().length() > 0
				&& et_school_id.getEditableText().toString().length() > 0
				&& et_province_id.getEditableText().toString().length() > 0) {
			next_step.setEnabled(true);
		} else {
			next_step.setEnabled(false);
		}
	}

	public boolean save_param() {
		try {
			sp.setDevice_id(et_device_id.getText().toString().trim());
			sp.setSchool_id(et_school_id.getText().toString().trim());
			sp.setProvince_id(et_province_id.getText().toString().trim());

			if (!sp.save_settingpara()) {
				Toast.makeText(this, "保存失败！", 0).show();
				return false;
			}

			//Toast.makeText(this, "保存成功！", 0).show();
			return true;
		} catch (Exception e) {
			Toast.makeText(this, "保存失败，不能为空！", 0).show();
			return false;
		}
	}
}
