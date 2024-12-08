package com.team01.billage.product.repository;

import com.team01.billage.product.dto.ProductResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomFavoriteRepository {

    List<ProductResponseDto> findAllByUserId(Long userId, Pageable pageable);

}
