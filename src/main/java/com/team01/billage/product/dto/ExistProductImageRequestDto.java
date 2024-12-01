package com.team01.billage.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExistProductImageRequestDto {

    private Long imageId;
    private String imageUrl;
    private String thumbnail; // Y/N

}
