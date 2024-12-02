package com.team01.billage.product.repository;

import com.team01.billage.product.dto.ProductResponseDto;

import java.util.List;

public interface CustomFavoriteRepository {

    List<ProductResponseDto> findAllByUserId(Long userId);

}
