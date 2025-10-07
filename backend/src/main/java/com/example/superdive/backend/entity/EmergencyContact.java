package com.example.superdive.backend.entity;


import jakarta.persistence.Column;
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
@Table(name = "emergency_contact")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContact {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="EmergencyContact_id")
	private Long Id;
	
	private String ecName; 
	private String ecPhone;
	
	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	Customer customer;
}
