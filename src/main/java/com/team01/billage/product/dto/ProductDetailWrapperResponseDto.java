package com.team01.billage.product.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailWrapperResponseDto {

    private ProductDetailResponseDto productDetail;
    private boolean checkAuthor;

}
