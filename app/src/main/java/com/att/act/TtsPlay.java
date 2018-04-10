package com.att.act;

import android.content.Context;
import android.util.Log;

import com.baidu.tts.answer.auth.AuthInfo;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;

public class TtsPlay {
	private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
	@SuppressWarnings("unused")
	private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
	private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
	private static SpeechSynthesizer mSpeechSynthesizer=null;

	public static void init(Context context,String path){
		if (mSpeechSynthesizer!=null) {
			Log.i("TAPP", "语音已初始化");
			return;
		}
		//  获取 tts 实例
		mSpeechSynthesizer = SpeechSynthesizer.getInstance();
		//  设置 app 上下文（必需参数）
		mSpeechSynthesizer.setContext(context);
		//  设置 tts 监听器
		//	mSpeechSynthesizer.setSpeechSynthesizerListener((SpeechSynthesizerListener) context);
		//  文本模型文件路径  (离线引擎使用)
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE,
				path+"/data/"+TEXT_MODEL_NAME);
		//  声学模型文件路径  (离线引擎使用)
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
				path+"/data/"+SPEECH_FEMALE_MODEL_NAME);
		//  本 地 授 权 文 件 路 径 , 如 未 设 置 将 使 用 默 认 路 径 . 设 置 临 时 授 权 文 件 路 径 ，
		//	LICENCE_FILE_NAME 请替换成临时授权文件的实际路径，仅在使用临时 license 文件时需要进行
		//	设置，如果在[应用管理]中开通了离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
//    	speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE,
//    	LICENSE_FILE_FULL_PATH_NAME);

		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "8");
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");

		//  请替换为语音开发者平台上注册应用得到的 App ID (离线授权)
		mSpeechSynthesizer.setAppId("7421180");
		//  请替换为语音开发者平台注册应用得到的 apikey 和 secretkey (在线授权)
		//	mSpeechSynthesizer.setApiKey("go37Fsw9TeFaE9M7PrQHXixl", "301694cd4f41309142fab3ebdb4c5c34");
		//  授权检测接口
		AuthInfo authInfo = mSpeechSynthesizer.auth(TtsMode.MIX);
		//  引擎初始化接口
		if (authInfo.isSuccess()) {
			Log.i("........","isSuccess"+authInfo.getTtsError().getDetailMessage() );
			mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, SpeechSynthesizer.SPEAKER_FEMALE);
			mSpeechSynthesizer.initTts(TtsMode.MIX);
		} else {
			String errorMsg = authInfo.getTtsError().getDetailMessage();
			Log.i("........","auth failed errorMsg=" + errorMsg);
		}


	}



	public static void speaktts(String text){
		if (mSpeechSynthesizer==null) {
			Log.i("TAPP", "语音未初始化");
			return;
		}
		Log.i("TAPP", "语音播放:"+text);
		mSpeechSynthesizer.speak(text);

	}


	public static boolean istts(){

		if (mSpeechSynthesizer==null) {
			return false;
		}else {
			return true;
		}


	}

	public static void ttsstop(){

		if (mSpeechSynthesizer==null) {
			Log.i("TAPP", "语音未初始化");
			return;
		}

		mSpeechSynthesizer.release();
		mSpeechSynthesizer=null;


	}


	public static void play(){

		if (mSpeechSynthesizer==null) {
			Log.i("TAPP", "语音未初始化");
			return;
		}
		mSpeechSynthesizer.stop();
	}



}
