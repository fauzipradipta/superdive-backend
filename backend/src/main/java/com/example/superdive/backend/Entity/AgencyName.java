package com.example.superdive.backend.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AgencyName {
    
    //This is for agency name dropdown
    @Id
    private Long id;
    private String name;
    private String level;
}
