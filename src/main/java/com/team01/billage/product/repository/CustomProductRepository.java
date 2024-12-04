package com.team01.billage.product.repository;

import com.team01.billage.product.dto.OnSaleResponseDto;
import com.team01.billage.product.dto.ProductResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomProductRepository {

    List<OnSaleResponseDto> findAllOnSale(String email, LocalDateTime lastTime,
        Pageable pageable);

    List<ProductResponseDto> findAllProductsByCategoryId(Long categoryId, Long userId);
}
