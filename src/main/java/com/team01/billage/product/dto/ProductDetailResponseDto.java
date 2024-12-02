package com.team01.billage.product.dto;

import com.team01.billage.category.dto.CategoryProductResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponseDto { // 상품 상세 확인 DTO

    private Long productId;
    private CategoryProductResponseDto category;
    private String title;
    private String description;
    private String rentalStatus;
    private int dayPrice;
    private Integer weekPrice;
    private double latitude;
    private double longitude;
    private int viewCount;
    private LocalDateTime updatedAt;
    private ProductSellerResponseDto seller;
    private List<ProductImageResponseDto> productImages;

}
