package com.team01.billage.product.repository;

import com.team01.billage.product.domain.Product;
import java.util.List;

public interface CustomProductRepository {

    List<Product> findAllOnSale(String email);
}
