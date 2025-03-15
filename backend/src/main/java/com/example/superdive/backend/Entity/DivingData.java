package com.example.superdive.backend.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "diving_data")	
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DivingData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	private String agencyName; 
	private String level; 
	private boolean isReference; 
	private String referenceName;
	private String referencePhoneNum;
	
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


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


	public Customer getCustomer() {
		return customer;
	}


	public void setCustomer(Customer customer) {
		this.customer = customer;
	}


	@ManyToOne
	@JoinColumn(name ="customer_id", nullable = false)
	private Customer customer;	
}
