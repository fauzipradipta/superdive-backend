package com.example.superdive.backend.Entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.example.superdive.backend.config.CustomDateDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private String name;
	
//	@Column(nullable=false)
//	private String email;
	
	@Column(nullable=false)
	private String phoneNum;
	
	@Column(nullable=false)
	
	@JsonDeserialize(using = CustomDateDeserializer.class)
	private Date dob;
	
	@Column(nullable=false)
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

//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}

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

	public List<DivingData> getDivingData() {
		return divingData;
	}

	public void setDivingData(List<DivingData> divingData) {
		this.divingData = divingData;
	}

	
	@JsonManagedReference
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DivingData> divingData = new ArrayList<>();
	
	@OneToMany
	private List<Reference> references = new ArrayList<>();
}
