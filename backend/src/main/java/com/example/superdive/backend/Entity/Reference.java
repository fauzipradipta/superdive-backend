package com.example.superdive.backend.Entity;

import java.security.PrivateKey;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Data
public class Reference {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String referenceName;
	private String referencePhoneNum;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public Reference(Long id, String referenceName, String referencePhoneNum) {
		super();
		this.id = id;
		this.referenceName = referenceName;
		this.referencePhoneNum = referencePhoneNum;
	}
	public Reference() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name ="customer_id", nullable = false)
	private Customer customer;
}
