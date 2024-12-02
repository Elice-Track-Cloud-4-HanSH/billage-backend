package com.team01.billage.product.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponseDto {

    private Long imageId;
    private String imageUrl;
    private String thumbnail; // Y/N

}
