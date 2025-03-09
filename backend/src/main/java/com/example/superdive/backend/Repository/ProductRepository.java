package com.example.superdive.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.superdive.backend.Entity.Product;

@Repository
public interface ProductRepository extends JpaRepository< Product,Long> {

}
