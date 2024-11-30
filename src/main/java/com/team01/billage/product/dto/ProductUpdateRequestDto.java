package com.team01.billage.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductUpdateRequestDto {

    private Long categoryId;
    private String title;
    private String description;
    private int dayPrice;
    private Integer weekPrice; // 선택값 (null 가능)
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<ProductImageRequestDto> productImages; // 추가하는 이미지
    private List<ExistProductImageRequestDto> existProductImages; // 기존 이미지

}
