package com.l1fe1.elasticsearch.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l1fe1.elasticsearch.entity.Product;
import com.l1fe1.elasticsearch.mapper.ProductMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {
}
