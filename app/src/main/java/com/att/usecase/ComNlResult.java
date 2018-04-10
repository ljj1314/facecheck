package com.att.usecase;

public class ComNlResult {

	
	private String Code;
	private String Msg;
	
	private Result Result;
	
	
	
	
	
	public class Result{
		
		private String Location;
		private String InOutType;
		private String SwipeTime;
		private String ScreenShot;
		
		public String getLocation() {
			return Location;
		}
		public void setLocation(String location) {
			Location = location;
		}
		public String getInOutType() {
			return InOutType;
		}
		public void setInOutType(String inOutType) {
			InOutType = inOutType;
		}
		public String getSwipeTime() {
			return SwipeTime;
		}
		public void setSwipeTime(String swipeTime) {
			SwipeTime = swipeTime;
		}
		public String getScreenShot() {
			return ScreenShot;
		}
		public void setScreenShot(String screenShot) {
			ScreenShot = screenShot;
		}
		
		
		
		
	}







	public String getCode() {
		return Code;
	}







	public void setCode(String code) {
		Code = code;
	}







	public String getMsg() {
		return Msg;
	}







	public void setMsg(String msg) {
		Msg = msg;
	}







	public Result getResult() {
		return Result;
	}







	public void setResult(Result result) {
		Result = result;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
