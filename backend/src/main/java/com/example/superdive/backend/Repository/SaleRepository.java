package com.example.superdive.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.superdive.backend.Entity.Sale;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

}
