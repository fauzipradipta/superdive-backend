package com.example.superdive.backend.Entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( name = "customers")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name; 
	private String email;
	
	@Temporal(TemporalType.DATE)
	private Date dob;
	private String phoneNum; 
	boolean isDiver;
	
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	 private List<DivingData> divingData = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmergencyContact> emergencyContacts = new ArrayList<>();

}
