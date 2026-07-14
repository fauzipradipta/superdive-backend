package com.example.superdive.backend.dto;

public class ReferenceDTO {
	
	private String referenceName;
	private String referencePhoneNum;
	
	public String getReferenceName() {
		return referenceName;
	}
	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}
	public String getReferencePhoneNum() {
		return referencePhoneNum;
	}
	public void setReferencePhoneNum(String referencePhoneNum) {
		this.referencePhoneNum = referencePhoneNum;
	}
	public ReferenceDTO(String referenceName, String referencePhoneNum) {
		super();
		this.referenceName = referenceName;
		this.referencePhoneNum = referencePhoneNum;
	}
	public ReferenceDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
