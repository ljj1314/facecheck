package com.att;

import android.app.Application;

import cn.wch.ch9326driver.CH9326UARTDriver;

public class MyApp extends Application {
	public static CH9326UARTDriver driver;// 需要将CH9326的对象创建在Application下，已确保程序在手机锁屏解锁后仍然可以进行操作
//	ArcFaceHelper arcFaceHelper = new ArcFaceHelper(this);
}
