package com.example.superdive.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.superdive.backend.Entity.Retail;

@Repository 
public interface RetailRepository extends JpaRepository<Retail,Long > {

}
