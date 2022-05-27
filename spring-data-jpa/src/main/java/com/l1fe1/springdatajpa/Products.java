package com.l1fe1.springdatajpa;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;

import java.math.BigDecimal;
import java.util.Iterator;

@RequiredArgsConstructor(staticName = "of")
public class Products implements Streamable<Product> {
    private Streamable<Product> streamable;

    public BigDecimal getTotal() {
        return streamable.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.valueOf(0L), BigDecimal::add);
    }

    @Override
    public Iterator<Product> iterator() {
        return null;
    }
}
