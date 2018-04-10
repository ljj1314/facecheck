package com.lztek.toolkit;

import java.io.IOException;

import android.util.Log;

public final class WatchDog {

	private static native boolean _enable();
	private static native boolean _disable();
	private static native boolean _feed(); 
	
	static {  
		java.io.File file = new java.io.File("/dev/watchdog");
		if (!file.exists() || !file.canRead() || !file.canWrite()) { 
			SU.exec("chmod 666 /dev/watchdog"); 
//			try {
//				Runtime.getRuntime().exec("chmod 666 /dev/watchdog");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} 
			long times = 0;
			while ( (!file.exists() || !file.canRead() || !file.canWrite()) && times < 2500 ) { 
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) { 
				}
				times += 50;
			} 
			if (!file.exists() || !file.canRead() || !file.canWrite()) {
				Log.e("#ERROR#", "!!! change watchdog mode failed !!!!");
			}
		}

		try {
			System.loadLibrary("watchdog"); 
		} catch (Throwable t) {
			Log.e("#ERROR#", "load library[libwatchdog.so] failed", t);
		} 
	} 

    /**
     * 鎵撳紑寮�闂ㄧ嫍,浣胯兘寮�闂ㄧ嫍鐨勫姛鑳�
     *
     * @param  
     *
     * @return {@code true} 鎴愬姛, {@code false} 澶辫触.
     */
	public static boolean enable() {
		try {
			return _enable();
		} catch (Throwable t) {
			Log.e("#ERROR#", "#WATCH_DOG# cannot find watchdog fuction[_enable]");
			return false;
		}
	}

    /**
     * 鍏抽棴寮�闂ㄧ嫍锛屽仠姝㈠紑闂ㄧ嫍鍔熻兘
     *
     * @param  
     *
     * @return {@code true} 鎴愬姛, {@code false} 澶辫触.
     */
	public static boolean disable() { 
		try {
			return _disable();
		} catch (Throwable t) {
			Log.e("#ERROR#", "#WATCH_DOG# cannot find watchdog fuction[_disable]");
			return false;
		}
	}

    /**
     * 鍠傜嫍锛屼娇鑳藉紑闂ㄧ嫍鍔熻兘鍚庯紝闇�瀹氭椂鍠傜嫍锛岃緭鍑轰竴瀹氭椂闂翠笉鍠傜嫍鍚庣郴缁熷皢鑷姩閲嶅惎
     *
     * @param  
     *
     * @return {@code true} 鎴愬姛, {@code false} 澶辫触.
     */
	public static boolean feed() { 
		try {
			return _feed();
		} catch (Throwable t) {
			Log.e("#ERROR#", "#WATCH_DOG# cannot find watchdog fuction[_feed]");
			return false;
		}
	} 
}
