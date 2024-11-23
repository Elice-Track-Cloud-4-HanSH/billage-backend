package com.team01.billage.product.dto;

import com.team01.billage.category.dto.CategoryProductResponseDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponseDto { // 상품 상세 확인 DTO

    private CategoryProductResponseDto categoryDto;
    private String title;
    private String description;
    private String rentalStatus;
    private int dayPrice;
    private Integer weekPrice;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private int viewCount;
    private LocalDateTime updatedAt;
    // 이미지

}
