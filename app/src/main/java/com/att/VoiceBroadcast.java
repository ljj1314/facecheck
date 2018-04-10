///**************************************************************************
//Copyright (C) 广东天波教育科技有限公司　版权所有
//文 件 名：语音播报
//创 建 人：吴伟聪
//创建时间：2015.11.04
//功能描述：
//**************************************************************************/
//package com.att;
//
//
//import android.content.Context;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//import com.iflytek.cloud.ErrorCode;
//import com.iflytek.cloud.InitListener;
//import com.iflytek.cloud.SpeechConstant;
//import com.iflytek.cloud.SpeechError;
//import com.iflytek.cloud.SpeechSynthesizer;
//import com.iflytek.cloud.SynthesizerListener;
//import com.iflytek.cloud.util.ResourceUtil;
//import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
//import com.att.R;
//
//public class VoiceBroadcast
//{
//    private static String            mEngineType ="cloud";//"local";//"cloud";
//	private static int               mPercentForBuffering = 0;
//	private static int               mPercentForPlaying = 0;
//	private static Toast             mToast = null;
//	private static SpeechSynthesizer mTts = null;// 语音合成对象
//	public static String             voicerCloud="xiaoyan";	// 默认云端发音人
//	public static String             voicerLocal="xiaoqian";//"nannan";//"xiaoyan"; // 默认本地发音人
//
//
//
//	private static AppBaseFun       appBaseFun = new AppBaseFun();
//
//	/**
//	* 初始化监听。
//	*/
//	private static InitListener mTtsInitListener = new InitListener()
//	{
//		public void onInit(int code)
//		{
//			//Log.d(TAG, "InitListener init() code = " + code);
//			if (code != ErrorCode.SUCCESS)
//			{
//				showTip("初始化失败,错误码："+code);
//			}
//			else
//			{
//				showTip("语音播报:初始化成功");
//			}
//		}
//	};
//
//	/**
//	* 合成回调监听。
//	*/
//	private static SynthesizerListener mTtsListener = new SynthesizerListener()
//	{
//		public void onSpeakBegin()
//		{
//			showTip("开始播放");
//		}
//
//		public void onSpeakPaused()
//		{
//			showTip("暂停播放");
//		}
//
//		public void onSpeakResumed()
//		{
//			showTip("继续播放");
//		}
//
//		public void onBufferProgress(int percent, int beginPos, int endPos,String info)
//		{
//			mPercentForBuffering = percent;
//			//mToast.setText(String.format(getString(R.string.tts_toast_format),mPercentForBuffering, mPercentForPlaying));
//
//			//mToast.show();
//		}
//
//		public void onSpeakProgress(int percent, int beginPos, int endPos)
//		{
//			mPercentForPlaying = percent;
//			showTip(String.format(getString(R.string.tts_toast_format),mPercentForBuffering, mPercentForPlaying));
//		}
//
//		public void onCompleted(SpeechError error)
//		{
//			if(error == null)
//			{
//				showTip("播放完成");
//			}
//			else if(error != null)
//			{
//				showTip(error.getPlainDescription(true));
//			}
//		}
//
//		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3)
//		{
//			// TODO Auto-generated method stub
//		}
//	};
//
//	private static void showTip(final String str)
//	{
//		Log.v("TPATT", "语音播报:" + str);
//	}
//
//	protected static String getString(int ttsToastFormat)
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/**
//	* 参数设置
//    * @param param
//	* @return
//	*/
//	private static void setParam(Context context)
//	{
//		// 清空参数
//		Log.v("TPATT", "语音播报:清空参数");
//
//		mTts.setParameter(SpeechConstant.PARAMS, null);
//
//		//设置合成
//		if( mEngineType.equals(SpeechConstant.TYPE_CLOUD) )
//		{
//			Log.v("TPATT", "语音播报:设置合成");
//
//			//设置使用云端引擎
//			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//			//设置发音人
//			mTts.setParameter(SpeechConstant.VOICE_NAME,voicerCloud);
//		}
//		else
//		{
//			Log.v("TPATT", "语音播报:本地音源");
//
//			//设置使用本地引擎
//			mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
//			//设置发音人资源路径
//			mTts.setParameter(ResourceUtil.TTS_RES_PATH,getResourcePath(context));
//			//设置发音人
//			mTts.setParameter(SpeechConstant.VOICE_NAME,voicerLocal);
//		}
//
//		Log.v("TPATT", "语音播报:设置参数2");
//
//		//设置语速
//		mTts.setParameter(SpeechConstant.SPEED,"50");
//
//		//设置音调
//		mTts.setParameter(SpeechConstant.PITCH,"50");
//
//		//设置音量
//		mTts.setParameter(SpeechConstant.VOLUME, "100");
//
//		//设置播放器音频流类型
//		mTts.setParameter(SpeechConstant.STREAM_TYPE,"3");
//	}
//
//	//获取发音人资源路径
//	private static String getResourcePath(Context context)
//	{
//		StringBuffer tempBuffer = new StringBuffer();
//
//		//合成通用资源
//		tempBuffer.append(ResourceUtil.generateResourcePath(context, RESOURCE_TYPE.path, appBaseFun.getPhoneCardPath()+"/speechcloud/data/asr/common.jet"));
//	//	tempBuffer.append(ResourceUtil.generateResourcePath(context, RESOURCE_TYPE.assets, "tts/common.jet"));
//		tempBuffer.append(";");
//
//		//发音人资源
//		tempBuffer.append(ResourceUtil.generateResourcePath(context, RESOURCE_TYPE.path, appBaseFun.getPhoneCardPath()+"/speechcloud/data/tts/"+voicerLocal+".irf"));
//		//tempBuffer.append(ResourceUtil.generateResourcePath(context, RESOURCE_TYPE.assets, "tts/"+voicerLocal+".irf"));
//
//		return tempBuffer.toString();
//	}
//
//	protected void onDestroy()
//	{
//		try
//		{
//		  //recLen_lunbo=0;
//		  //timer_goLunBo.cancel();
//		  //timer_heart_telpo.cancel();
//		  //timer_time.cancel();
//		  //timer_autoGallery.cancel();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//
//		mTts.stopSpeaking();
//		// 退出时释放连接
//	    mTts.destroy();
//	    mTts = null;
//	}
//
//	public static void ttsInit(Context context)
//	{
//		if (mTts == null)
//		{
//		    mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
//		}
//	}
//
//	public static void ttsPlay(Context context,String text)
//	{
//		// 设置参数
//		setParam(context);
//		mTts.stopSpeaking();
//		int code = mTts.startSpeaking(text, mTtsListener);
//
//		if ( code != ErrorCode.SUCCESS )
//		{
//			showTip("语音合成失败,错误码: " + code);
//		}
//		else
//		{
//			Log.v("TPATT", "语音播报:播放成功");
//		}
//
//
//	}
///************************************科大讯飞*********************************************/
//}
