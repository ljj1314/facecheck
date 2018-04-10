package com.telpo.dataprocess;

public class DataProcess{

	
	
	
	
	
	
	public native  String Geticcard(byte[] buf, int buflen);
	
	public native  String GetRecord(byte[] buf, int buflen,int reversal);

	
	
	public  String load(byte[] buf, int buflen){
		
	
		return Geticcard( buf,  buflen);
	}
	
	
	public  String loadap(byte[] buf, int buflen,int reversal){
		
		
		return GetRecord( buf,  buflen, reversal);
	}
	
	static{
		
		System.loadLibrary("telpo-dataprocess");
		
	}
	
}
