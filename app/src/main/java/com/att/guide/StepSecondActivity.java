package com.att.guide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.att.R;
import com.att.SettingPara;

import java.util.Timer;
import java.util.TimerTask;

public class StepSecondActivity extends Activity {

	private ImageView back_button;
	boolean isSet = false;
	private Spinner sp_att_platform; // 考勤平台协议
	private GuideSpinnerAdapter adapterPlatform;
	private TextView state_1;

	private FrameLayout fl_region;
	private Spinner sp_att_platform_region; // 考勤平台区域
	private GuideSpinnerAdapter adapterRegion;
	private TextView state_2;
	private LinearLayout ll_region_url;
	private String[] region;

	private EditText url_att;
	private EditText url_att_pic;
	private EditText url_parents_pic;
	private EditText url_card_info;
	private EditText url_temp;

	private Button next_step;

	private SettingPara sp = new SettingPara();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.step_second_seven);
		init();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(StepSecondActivity.this, StepFirstActivity.class);
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
		sp_att_platform = (Spinner) findViewById(R.id.sp_att_platform);
		state_1 = (TextView) findViewById(R.id.state_1);
		state_1.setText("请选择");
		state_1.setTextColor(R.color.gray_c4c4c4);
		final String[] platform = getResources().getStringArray(R.array.platform);
		adapterPlatform = new GuideSpinnerAdapter(this, android.R.layout.simple_spinner_item, platform);
		sp_att_platform.setAdapter(adapterPlatform);
		sp_att_platform.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (adapterPlatform.getCount() == 0) {
					adapterPlatform.addAll(platform);
				}
				return false;
			}
		});
		sp_att_platform.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				state_1.setText(platform[arg2]);
				state_1.setTextColor(getResources().getColor(R.color.blue_45b1ea));
				if (arg2 == 0) {
					fl_region.setVisibility(View.VISIBLE);
					ll_region_url.setVisibility(View.VISIBLE);
					if (adapterRegion.getCount() == 0) {
						adapterRegion.addAll(region);
					}
					//sp_att_platform_region.setSelection(0);
					//state_2.setText("请选择");
					//state_2.setTextColor(R.color.gray_c4c4c4);
					//next_step.setEnabled(false);
				} else {
					fl_region.setVisibility(View.GONE);
					adapterRegion.clear();
					ll_region_url.setVisibility(View.GONE);
					//url_att.setText("");
					//url_att_pic.setText("");
					//url_parents_pic.setText("");
					//url_card_info.setText("");
					//url_temp.setText("");
					next_step.setEnabled(true);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		if (sp.getAtt_platform() >= 0) {
			if (adapterPlatform.getCount() == 0) {
				adapterPlatform.addAll(platform);
			}
			sp_att_platform.setSelection(sp.getAtt_platform());
		}

		fl_region = (FrameLayout) findViewById(R.id.fl_region);
		state_2 = (TextView) findViewById(R.id.state_2);
		state_2.setText("请选择");
		state_2.setTextColor(R.color.gray_c4c4c4);
		sp_att_platform_region = (Spinner) findViewById(R.id.sp_att_platform_region);
		region = getResources().getStringArray(R.array.region);
		adapterRegion = new GuideSpinnerAdapter(this, android.R.layout.simple_spinner_item, region);
		if (adapterRegion.getCount() == 0) {
			adapterRegion.addAll(region);
		}
		sp_att_platform_region.setAdapter(adapterRegion);
		sp_att_platform_region.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (adapterRegion.getCount() == 0) {
					adapterRegion.addAll(region);
				}
				return false;
			}
		});
		sp_att_platform_region.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				state_2.setText(region[arg2]);
				state_2.setTextColor(getResources().getColor(R.color.blue_45b1ea));
				ll_region_url.setVisibility(View.VISIBLE);
				url_att.setText(SettingPara.ATT_URLS[arg2]);
				url_att_pic.setText(SettingPara.ATT_PIC_URLS[arg2]);
				url_parents_pic.setText(SettingPara.PARENTS_PIC_URLS[arg2]);
				url_card_info.setText(SettingPara.CARD_INFO_URLS[arg2]);
				url_temp.setText(SettingPara.TEMP_URLS[arg2]);
				next_step.setEnabled(true);
			}
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		if (sp.getAtt_pic_platform() == 0){
			if (sp.getAtt_url()!=null&&sp.getAtt_url().length()>0) {
				int index = -1;
				for(int i=0;i<SettingPara.ATT_URLS.length;i++){
					if(SettingPara.ATT_URLS[i].equals(sp.getAtt_url())){
						index = i;
					}
				}
				if(index>=0){
					if (adapterRegion.getCount() == 0) {
						adapterRegion.addAll(region);
					}
					sp_att_platform_region.setSelection(index);
				}
			}
		}

		ll_region_url = (LinearLayout) findViewById(R.id.ll_region_url);
		url_att = (EditText) findViewById(R.id.url_1);
		url_att.clearComposingText();
		url_att.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		url_att.setHorizontallyScrolling(true);
		url_att_pic = (EditText) findViewById(R.id.url_2);
		url_att_pic.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		url_parents_pic = (EditText) findViewById(R.id.url_3);
		url_parents_pic.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		url_card_info = (EditText) findViewById(R.id.url_4);
		url_card_info.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		url_temp = (EditText) findViewById(R.id.url_5);
		url_temp.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

		next_step = (Button) findViewById(R.id.next_step);
		next_step.setEnabled(false);
		next_step.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (save_param()) {
					Intent intent = new Intent();
					if (sp_att_platform.getSelectedItemPosition() == 2) {// 中维无须填写终端ID
						intent.setClass(StepSecondActivity.this, StepFourthActivity.class);
					} else {
						intent.setClass(StepSecondActivity.this, StepThirdActivity.class);
					}
					intent.putExtra("isSet", isSet);
					startActivity(intent);
					overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
					StepSecondActivity.this.finish();// 结束本Activity
				}
			}
		});
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(url_att.getWindowToken(), 0);
			}
		}, 2000);
	}

	public boolean save_param() {
		try {
			sp.setAtt_platform(sp_att_platform.getSelectedItemPosition());
			sp.setAtt_pic_platform(sp_att_platform.getSelectedItemPosition());
			sp.setAtt_url(url_att.getText().toString().trim());
			sp.setAtt_pic_url(url_att_pic.getText().toString().trim());
			sp.setParents_pic_url(url_parents_pic.getText().toString().trim());
			sp.setCard_info_url(url_card_info.getText().toString().trim());
			if (url_temp.getText() != null && !"".equals(url_temp.getText())) {
				sp.setTempurl(url_temp.getText().toString());
			}
			// sp.setDevice_id(et_device_id.getText().toString().trim());
			// sp.setSchool_id(et_school_id.getText().toString().trim());

			if (!sp.save_settingpara()) {
				Toast.makeText(this, "保存失败！", 0).show();
				return false;
			}

			// Toast.makeText(this, "保存成功！", 0).show();
			return true;
		} catch (Exception e) {
			Toast.makeText(this, "保存失败，不能为空！", 0).show();
			return false;
		}
	}
}
