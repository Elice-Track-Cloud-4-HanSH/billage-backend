package com.team01.billage.product.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto { // 상품 목록 DTO

    private String title;
    private LocalDateTime updatedAt;
    private int dayPrice;
    private Integer weekPrice;
    private int viewCount;
    // 썸네일 이미지
    // 지역
    // 좋아요

}