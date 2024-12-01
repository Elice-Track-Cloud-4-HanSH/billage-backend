package com.team01.billage.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageDeleteRequestDto {

    private Long imageId;
    private String imageUrl;

}
