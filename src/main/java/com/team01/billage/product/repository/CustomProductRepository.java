package com.team01.billage.product.repository;

import com.team01.billage.product.dto.OnSaleResponseDto;
import java.util.List;

public interface CustomProductRepository {

    List<OnSaleResponseDto> findAllOnSale(String email);
}
