package com.example.superdive.backend.Entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table( name = "customers")
@Data
public class Customer {
	
	private Long id;
	private String name; 
	private String email;
	private Date dob;
	private String phoneNum; 
	boolean isDiver;
	
//	@OneToMany(mappedBy = "customer", cascade= CascadeType.All,orphanRemoval = true )
	
}
