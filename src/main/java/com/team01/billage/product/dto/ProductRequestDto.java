package com.team01.billage.product.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    private Long categoryId;
    private String title;
    private String description;
    private int dayPrice;
    private Integer weekPrice; // 선택값 (null 가능)
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<ProductImageRequestDto> productImages;

}
