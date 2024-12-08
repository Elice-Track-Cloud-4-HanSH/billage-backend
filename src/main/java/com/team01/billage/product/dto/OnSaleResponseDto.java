package com.team01.billage.product.dto;

import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OnSaleResponseDto {

    @Parameter(description = "상품 ID", example = "1")
    private long productId;

    @Parameter(description = "상품 썸네일 이미지", example = "http://example.com/image.jpg")
    private String productImageUrl;

    @Parameter(description = "상품 제목", example = "디지털 카메라")
    private String title;

    @Parameter(description = "상품 최신 수정일", example = "2024-12-10")
    private LocalDateTime time;
}
