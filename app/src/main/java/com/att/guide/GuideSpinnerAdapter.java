package com.att.guide;

import com.att.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GuideSpinnerAdapter extends ArrayAdapter<String> {
	Context context;
	String[] items = new String[] {};
	int size = 28;

	public GuideSpinnerAdapter(final Context context, final int textViewResourceId, final String[] objects) {
		super(context, textViewResourceId);
		this.items = objects;
		this.context = context;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) 
	{

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
		}

		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		tv.setText(getItem(position));
		tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		tv.setTextColor(context.getResources().getColor(R.color.black_333333));
		tv.setPadding(20, 20, 88, 20);
		tv.setTextSize(size);
		return convertView;
	}

	@SuppressLint("RtlHardcoded")
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if (convertView == null) 
		{
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
		}

		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		tv.setText(items[position]);
		tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		tv.setTextColor(Color.TRANSPARENT);
		tv.setTextSize(size);
		return convertView;
	}
}