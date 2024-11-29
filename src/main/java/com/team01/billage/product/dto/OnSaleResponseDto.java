package com.team01.billage.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnSaleResponseDto {

    private long productId;
    private String productImageUrl;
    private String title;
}
