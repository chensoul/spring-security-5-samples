package com.chensoul.security.service;

import com.chensoul.security.model.Product;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @PreFilter("filterObject.owner == authentication.name")
    public List<Product> sellProducts(List<Product> products) {
        // order products and return the ordered products list
        return products;
    }
}
