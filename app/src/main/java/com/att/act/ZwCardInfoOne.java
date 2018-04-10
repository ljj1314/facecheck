package com.att.act;

import java.util.List;

public class ZwCardInfoOne {

	private String babyUuid;
	private String babyName;
	private String babyPic;
	private String cardNo;
	private String className;
	private List<ParentArray> parentArray;

	public String getBabyUuid() {
		return babyUuid;
	}

	public void setBabyUuid(String babyUuid) {
		this.babyUuid = babyUuid;
	}

	public String getBabyName() {
		return babyName;
	}

	public void setBabyName(String babyName) {
		this.babyName = babyName;
	}

	public String getBabyPic() {
		return babyPic;
	}

	public void setBabyPic(String babyPic) {
		this.babyPic = babyPic;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<ParentArray> getParentArray() {
		return parentArray;
	}

	public void setParentArray(List<ParentArray> parentArray) {
		this.parentArray = parentArray;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

}
