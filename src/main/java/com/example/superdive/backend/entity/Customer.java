package com.example.superdive.backend.entity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.superdive.backend.config.CustomDateDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "customers")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="customer_id")
	private Long id;
	
	@Column(nullable=false,unique = true)
	private String name;
	
	@Column(nullable=false)
	private String phoneNum;
	
	@Column(nullable=false)
	@JsonDeserialize(using = CustomDateDeserializer.class)
	private Date dob;
	
	@Column(nullable=false)
	@JsonProperty("isDiver")
	private boolean diver;

	
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

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public boolean diver() {
		return diver;
	}

	public void setDiver(boolean diver) {
		this.diver = diver;
	}

	public List<DivingData> getDivingData() {
		return divingData;
	}

	public void setDivingData(List<DivingData> divingData) {
		this.divingData = divingData;
	}

	
	
	public List<Reference> getReferences() {
		return references;
	}

	public void setReferences(List<Reference> references) {
		this.references = references;
	}

	


	public Customer(Long id, String name, String phoneNum, Date dob, boolean diver, List<DivingData> divingData,
			List<Reference> references) {
		super();
		this.id = id;
		this.name = name;
		this.phoneNum = phoneNum;
		this.dob = dob;
		this.diver = diver;
		this.divingData = divingData;
		this.references = references;
	}
	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}

	@JsonManagedReference
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DivingData> divingData = new ArrayList<>();
	
	@JsonManagedReference
	@OneToMany(mappedBy = "customer", cascade=CascadeType.ALL,orphanRemoval = true)
	private List<Reference> references = new ArrayList<>();

}
