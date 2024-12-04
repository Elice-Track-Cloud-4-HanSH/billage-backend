package com.team01.billage.product.repository;

import com.team01.billage.product.dto.OnSaleResponseDto;
import com.team01.billage.product.dto.ProductDetailResponseDto;
import com.team01.billage.product.dto.ProductResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomProductRepository {

    List<OnSaleResponseDto> findAllOnSale(String email, LocalDateTime lastTime,
        Pageable pageable);

    List<ProductResponseDto> findAllProducts(Long userId, Long categoryId, String rentalStatus);

    ProductDetailResponseDto findProductDetail(Long productId);

}
