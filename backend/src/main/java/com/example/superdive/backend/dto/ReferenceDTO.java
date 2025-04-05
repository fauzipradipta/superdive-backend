package com.example.superdive.backend.dto;

public class ReferenceDTO {
	
	private String referenceName;
	private String phoneNum;
	
	public String getReferenceName() {
		return referenceName;
	}
	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public ReferenceDTO(String referenceName, String phoneNum) {
		super();
		this.referenceName = referenceName;
		this.phoneNum = phoneNum;
	}
	public ReferenceDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
