package com.serport;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

/**接收数据处理*/
public class DataUtils {

	//2字节长度数组转化为整形
	public static int bytesToint2(byte high,byte low){
		int res = 0;
		res+=((high&0xff)<<8);
		res+=((low&0xff));
		return res;
	}

	//整数转化为2字节数组
	public static byte[] intTobyte2(int i){
		byte[] a = new byte[2];
		a[0]= (byte)(i >> 8 & 0xFF);
		a[1]= (byte)(i & 0xFF);
		return a;
	}

	//整数转化为4字节数组
	public static byte[] intTobyte4(int i){
		byte[] a = new byte[4];
		a[0]= (byte)(i >> 24 & 0xFF);
		a[1]= (byte)(i >> 16 & 0xFF);
		a[2]= (byte)(i >> 8 & 0xFF);
		a[3]= (byte)(i & 0xFF);
		return a;
	}

	//发送文件字节数组次数   每次只能发送20字节
	public static int sendDataTimes(byte[] dataByte){
		int byteSize = dataByte.length;
		int times =0; //需要发送数据的次数
		if(byteSize<20){
			times =1;
		}else{
			if(byteSize%20 == 0){ //剛好是20的倍數
				times = byteSize/20;
			}else if(byteSize%20 != 0){ //不能整除
				times =byteSize/20+1;
			}
		}
		return times;
	}

	/**实际发送字节数不足，需补齐0
	 * @param src 源字节数组
	 * @return 补齐后的字节数组
	 * */
	public static byte[] toComplete(byte[] src){
		int times = sendDataTimes(src);
		byte[] dst = new byte[times*20];
		int Comp_len = times*20-src.length;
		byte[] tem = new byte[Comp_len];
		for(int i =0;i<Comp_len;i++){
			tem[i] = 0x00;
		}
		System.arraycopy(src, 0, dst, 0, src.length);
		System.arraycopy(tem, 0, dst, src.length, tem.length);
		return dst;
	}

	/** *//**
	 * 把字节数组转换成16进制字符串
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}


	public static final byte[] HexStringTobytes(String Hexstr)
	{

		byte[] values = Hexstr.getBytes();
		int length = values.length /2;
		byte[] ret = new byte[length];
		int index = 0;
		byte value = 0;

		while(index < length)
		{
			value = 0;

			if (values[index*2] >= 0x30 && values[index * 2] <= 0x39)
			{
				value |= values[index*2] - 0x30;

			}
			else if (values[index*2] >= 'a' && values[index * 2] <= 'f')
			{

				value |= values[index*2] - 'a' + 0x0A;
			}
			else if (values[index*2] >= 'A' && values[index * 2] <= 'F')
			{

				value |= values[index*2] - 'A' + 0x0A;
			}
			else {
				return null;
			}

			value <<= 4;
			if (values[index*2 +1] >= 0x30 && values[index * 2+1] <= 0x39)
			{
				value |= values[index*2+1] - 0x30;

			}
			else if (values[index*2+1] >= 'a' && values[index * 2+1] <= 'f')
			{

				value |= values[index*2+1] - 'a' + 0x0A;
			}
			else if (values[index*2+1] >= 'A' && values[index * 2+1] <= 'F')
			{

				value |= values[index*2+1] - 'A' + 0x0A;
			}
			else {
				return null;
			}
			ret[index] = value;
			index++;
		}

		return ret;

	}

	public static byte getByteTime(int time) {
		byte bt = (byte) ((0xff & time) + (0xff & time) / 10 * 6);
		return bt;
	}

	//数组倒置
	public static void swap(byte[] a){
		for(int i=0;i<a.length/2;i++){
			byte temp;
			temp = a[i];
			a[i] = a[a.length-i-1];
			a[a.length-i-1] = temp;
		}
	}

	/**
	 * 两字节时间转化为时间字符串。
	 * @param  highTime 两字节时间的高位
	 * @param  lowTime 两字节时间的地位
	 * 年：通过日比较得出年
	 * 月：通过年得出月
	 * 日：高字节的高五位
	 * 时：高字节的低3位+低字节的高2位
	 * 分：低字节的低6位
	 * @return 字符串数组 uploadDataTime[0]为年月日，uploadDataTime[1]为时分
	 * */
	public static final String[] bytesToTimeStringArray(byte highTime,byte lowTime){
		String[] uploadDataTime = new String[2]; //uploadDataTime[0]为年月日  ，uploadDataTime[1]为日时分

		StringBuffer time = new StringBuffer("");
		//分
		byte fen_byte =(byte)( lowTime & 0x3f);
		//时
		byte shi_low2 = (byte)((lowTime & 0xc0) >> 6);
		byte shi_high3 = (byte)((highTime & 0x07) << 2);
		byte shi_byte = (byte)(shi_high3 | shi_low2);
		//日
		byte ri_byte = (byte)((highTime & 0xf8) >> 3);
		if((fen_byte & 0xff)>=60){
			time.append(shi_byte & 0xff).append(":").append(59);
		}else{
			String fen_str = ""+(fen_byte &0xff);
			if(fen_str.length()==1){
				fen_str = "0"+fen_str;
			}
			time.append(shi_byte & 0xff).append(":").append(fen_str);
		}
//			 byte[] times = new byte[]{ri_byte,shi_byte,fen_byte,shi_high3,shi_low2};
		Calendar c = Calendar.getInstance();//获取当前时间
		int year = (c.get(Calendar.YEAR));   			//年
		int month = (c.get(Calendar.MONTH)+1);  		//月
		int date = c.get(Calendar.DATE); 			//日
		StringBuffer year_month_day = new StringBuffer("");
		if((ri_byte & 0xff)>date){  //判断上传的日是否大于当前的日，如果大于则属于上个月
//				 //年月日
			if(month == 1){
				month = 12;
				year = year -1;
				year_month_day.append(year).append("-").append(month).append("-").append(ri_byte & 0xff);
			}else{
				month = month - 1;
				year_month_day.append(year).append("-").append(month).append("-").append(ri_byte & 0xff);
			}
		}else{  //当月数据
			if((ri_byte & 0xff)<=0){
				year_month_day.append(year).append("-").append(month).append("-").append(01);
			}else{
				year_month_day.append(year).append("-").append(month).append("-").append(ri_byte & 0xff);
			}
		}
		System.out.println("year"+year+" month"+month);
		uploadDataTime[0] = year_month_day.toString();
		uploadDataTime[1] = time.toString();
		return uploadDataTime;
	}

	/**
	 * 字符串转换成十六进制字符串
	 * @param String str 待转换的ASCII字符串
	 * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] str2HexStr(String str) throws UnsupportedEncodingException
	{
//			 byte[] bs1 = str.getBytes();
//		   String ai=new String(bs1, "ANSI");
		@SuppressWarnings("unused")
		char[] chars = "0123456789ABCDEF".toCharArray();
		@SuppressWarnings("unused")
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes("GBK");
//		     int bit;
//
//		     for (int i = 0; i < bs.length; i++)
//		     {
//		         bit = (bs[i] & 0x0f0) >> 4;
//		         sb.append(chars[bit]);
//		         bit = bs[i] & 0x0f;
//		         sb.append(chars[bit]);
//		         sb.append(' ');
//		     }
		return bs;
	}

	/**
	 * 将byte数组转换为int数据
	 * @param b 字节数组
	 * @return 生成的int数据
	 */
	public static int byteToInt2(byte[] b){
		return (((int)b[0]) << 24) + (((int)b[1]) << 16) + (((int)b[2]) << 8) + b[3];
	}


}
