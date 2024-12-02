package com.team01.billage.product.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponseDto {

    private Long favoriteProductId;
    private Long userId;
    private Long productId;

}
