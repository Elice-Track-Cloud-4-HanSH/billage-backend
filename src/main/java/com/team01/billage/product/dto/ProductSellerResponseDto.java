package com.team01.billage.product.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSellerResponseDto {

    private Long sellerId;
    private String sellerNickname;
    private String sellerImageUrl;

}
