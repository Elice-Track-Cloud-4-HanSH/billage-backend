package com.team01.billage.product.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWrapperResponseDto {

    List<ProductResponseDto> products;
    private boolean login;

}
