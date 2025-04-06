package com.example.superdive.backend.dto;

import java.time.LocalDate;
import java.util.List;

public class DivingDataDTO {
	
	
	private String agencyName;
	private String level;
	private boolean isReference;
	
	
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
	public boolean isReference() {
		return isReference;
	}
	public void setReference(boolean isReference) {
		this.isReference = isReference;
	}
	
	
}
