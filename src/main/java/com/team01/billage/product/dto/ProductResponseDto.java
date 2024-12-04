package com.team01.billage.product.dto;

import lombok.*;

import java.time.LocalDate;
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
    private boolean favorite;
    private Long favoriteCnt;
    private LocalDate expectedReturnDate;
    // 지역

}
