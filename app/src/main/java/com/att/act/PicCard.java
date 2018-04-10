package com.att.act;

import java.util.ArrayList;

public class PicCard {

	
	private String userId;
	private String userName;
	private String photoUrl;
	private String schoolName;
	private String addTime;
	private ArrayList<ParentsPhotos> parentsPhotos;
	private boolean issucc;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
	public ArrayList<ParentsPhotos> getParentsPhotos() {
		return parentsPhotos;
	}
	public void setParentsPhotos(ArrayList<ParentsPhotos> parentsPhotos) {
		this.parentsPhotos = parentsPhotos;
	}
	
	
	
	public boolean isIssucc() {
		return issucc;
	}
	public void setIssucc(boolean issucc) {
		this.issucc = issucc;
	}



	public static class ParentsPhotos {

		
		private String studentId;
		private String userName;
		private String photoUrl;
		private boolean issucc;
		
		private String addTime;

		public String getStudentId() {
			return studentId;
		}

		public void setStudentId(String studentId) {
			this.studentId = studentId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getPhotoUrl() {
			return photoUrl;
		}

		public void setPhotoUrl(String photoUrl) {
			this.photoUrl = photoUrl;
		}

		public String getAddTime() {
			return addTime;
		}

		public void setAddTime(String addTime) {
			this.addTime = addTime;
		}

		public boolean isIssucc() {
			return issucc;
		}

		public void setIssucc(boolean issucc) {
			this.issucc = issucc;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
