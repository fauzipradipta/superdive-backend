package com.example.superdive.backend.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "diving_data")	
@Data

public class DivingData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	@Column(nullable=false)
	private String agencyName; 
	
	@Column(nullable=false)
	private String level; 
	
	@Column(nullable=false)
	private boolean isReference; 
	
//	@Column(nullable=false)
//	private String referenceName;
//	
//	@Column(nullable=false)
//	private String referencePhoneNum;
//	
//	
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


//	public boolean isReference() {
//		return isReference;
//	}
//
//
//	public void setReference(boolean isReference) {
//		this.isReference = isReference;
//	}
//
//
//	public String getReferenceName() {
//		return referenceName;
//	}
//
//
//	public void setReferenceName(String referenceName) {
//		this.referenceName = referenceName;
//	}
//
//
//	public String getReferencePhoneNum() {
//		return referencePhoneNum;
//	}
//
//
//	public void setReferencePhoneNum(String referencePhoneNum) {
//		this.referencePhoneNum = referencePhoneNum;
//	}


	public Customer getCustomer() {
		return customer;
	}


	public void setCustomer(Customer customer) {
		this.customer = customer;
	}



	public DivingData(Long id, String agencyName, String level, Customer customer) {
		super();
		this.id = id;
		this.agencyName = agencyName;
		this.level = level;
		this.customer = customer;
	}


	public DivingData() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name ="customer_id", nullable = false)
	private Customer customer;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Reference reference;
}
