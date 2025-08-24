package com.example.superdive.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DivingDataDTO {
	
	
	private String agencyName;
	private String level;

	@JsonProperty("isReference")
	private boolean reference;
	
	
	public String getAgencyName() {
		return agencyName;
	}
	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public boolean getReference() {
		return reference;
	}
	public void setReference(boolean reference) {
		this.reference = reference;
	}
	
	
	
}
