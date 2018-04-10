package com.att.act;

public class JniCurl {
	static{
		System.loadLibrary("jni_curl");
	}
	
	native public int curlInit();
}
