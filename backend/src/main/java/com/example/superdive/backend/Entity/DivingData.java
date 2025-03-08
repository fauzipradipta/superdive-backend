package com.example.superdive.backend.Entity;

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
	
	@ManyToOne
	@JoinColumn(name ="customer_id", nullable = false)
	private Customer customer;	
}
