package com.example.superdive.backend.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Entity
public class OrderItem{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id; 

    @ManyToOne
    @JoinColumn(  name = "sale_id")
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private int qty;
    
    public Long getId() {
        return Id;
    }
    public void setId(Long id) {
        Id = id;
    }
    public Sale getSale() {
        return sale;
    }
    public void setSale(Sale sale) {
        this.sale = sale;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public int getQty() {
        return qty;
    }
    public void setQty(int qty) {
        this.qty = qty;
    }
    public OrderItem(Long id, Sale sale, Product product, int qty) {
        Id = id;
        this.sale = sale;
        this.product = product;
        this.qty = qty;
    }

    

    

}