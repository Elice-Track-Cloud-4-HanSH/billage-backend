package com.team01.billage.product.repository;

import com.team01.billage.product.dto.OnSaleResponseDto;
import com.team01.billage.product.dto.ProductDetailResponseDto;
import com.team01.billage.product.dto.ProductResponseDto;
import java.util.List;

public interface CustomProductRepository {

    List<OnSaleResponseDto> findAllOnSale(long userId);

    List<ProductResponseDto> findAllProducts(Long userId, Long categoryId, String rentalStatus);

    ProductDetailResponseDto findProductDetail(Long productId);

}
