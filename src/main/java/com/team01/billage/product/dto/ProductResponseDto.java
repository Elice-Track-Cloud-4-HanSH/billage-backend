package com.team01.billage.product.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto { // 상품 목록 DTO

    private Long productId;
    private String title;
    private LocalDateTime updatedAt;
    private int dayPrice;
    private Integer weekPrice;
    private int viewCount;
    private String thumbnailUrl;
    // 지역
    // 좋아요

}
