package com.example.superdive.backend.dto;

import java.time.LocalDate;

public class CustomerDTO {
	
	private Long id;
	private String name; 
	private String phoneNum; 
	private String dob; 
	private boolean isDiver;
	public Long getId() {
		return id;
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
	public LocalDate getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public boolean isDiver() {
		return isDiver;
	}
	public void setDiver(boolean isDiver) {
		this.isDiver = isDiver;
	}
	public CustomerDTO(Long id, String name, String phoneNum, String dob, boolean isDiver) {
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
