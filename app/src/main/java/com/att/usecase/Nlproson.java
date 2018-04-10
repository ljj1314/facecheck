package com.att.usecase;

import java.util.List;

public class Nlproson {

	private String Code;
	private String Msg;
	private Result Result;

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

	public class Result {

		private String Name;
		private String UserType;
		private String ClassName;
		private String Logo;
		private String Temperature;
		private List<Relations> Relations;

		public String getName() {
			return Name;
		}

		public void setName(String name) {
			Name = name;
		}

		public String getUserType() {
			return UserType;
		}

		public void setUserType(String userType) {
			UserType = userType;
		}

		public String getClassName() {
			return ClassName;
		}

		public void setClassName(String className) {
			ClassName = className;
		}

		public String getTemperature() {
			return Temperature;
		}

		public void setTemperature(String temperature) {
			Temperature = temperature;
		}

		public String getLogo() {
			return Logo;
		}

		public void setLogo(String logo) {
			Logo = logo;
		}

		public List<Relations> getRelations() {
			return Relations;
		}

		public void setRelations(List<Relations> relations) {
			Relations = relations;
		}

	}

	public class Relations {

		private String Name;
		private String Logo;

		public String getName() {
			return Name;
		}

		public void setName(String name) {
			Name = name;
		}

		public String getLogo() {
			return Logo;
		}

		public void setLogo(String logo) {
			Logo = logo;
		}

	}

}
