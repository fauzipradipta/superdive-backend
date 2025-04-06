package com.example.superdive.backend.dto;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class CustomerDTO {
	
	private Long id;
	private String name; 
	private String phoneNum; 
	private Date dob; 
	private boolean isDiver;
	
	private List<DivingDataDTO> divingData;
	private List<ReferenceDTO> reference;
	
	public Long getId() {
		return id;
	}
	public List<DivingDataDTO> getDivingData() {
		return divingData;
	}
	public void setDivingData(List<DivingDataDTO> divingData) {
		this.divingData = divingData;
	}
	public List<ReferenceDTO> getReference() {
		return reference;
	}
	public void setReference(List<ReferenceDTO> reference) {
		this.reference = reference;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public boolean isDiver() {
		return isDiver;
	}
	public void setDiver(boolean isDiver) {
		this.isDiver = isDiver;
	}
	public CustomerDTO(Long id, String name, String phoneNum, Date dob, boolean isDiver) {
		super();
		this.id = id;
		this.name = name;
		this.phoneNum = phoneNum;
		this.dob = dob;
		this.isDiver = isDiver;
	}
	public CustomerDTO() {
		super();
		// TODO Auto-generated constructor stub
	} 
	
	
}
