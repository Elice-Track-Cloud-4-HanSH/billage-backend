package com.team01.billage.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductUpdateRequestDto {

    @NotNull(message = "카테고리를 선택해 주세요.")
    private Long categoryId;

    @NotBlank(message = "제목을 입력해 주세요.")
    private String title;

    @NotBlank(message = "설명을 입력해 주세요.")
    private String description;

    @NotBlank(message = "일 단위 가격은 필수입니다.")
    @Pattern(regexp = "^[0-9]+$", message = "가격은 숫자만 입력 가능합니다.")
    private String dayPrice;

    @Pattern(regexp = "^[0-9]+$", message = "가격은 숫자만 입력 가능합니다.")
    private String weekPrice; // 선택값 (null 가능)

    private double latitude;
    private double longitude;
    private List<ProductImageRequestDto> productImages; // 추가하는 이미지
    private List<ExistProductImageRequestDto> existProductImages; // 기존 이미지

}
