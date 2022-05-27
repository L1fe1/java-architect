package com.l1fe1.springdatajpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String description;
    private BigDecimal price;
    BigDecimal getPrice() {
        return price;
    }
    void setPrice(BigDecimal price) {
        this.price = price;
    }
}
