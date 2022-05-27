package com.l1fe1.springdatajpa;

import org.springframework.data.repository.Repository;

public interface ProductRepository extends Repository<Product, Long> {
    Products findAllByDescriptionContaining(String text);
}
