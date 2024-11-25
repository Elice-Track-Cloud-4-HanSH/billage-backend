package com.team01.billage.product.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeleteCheckDto {

    private Long productId;
    private LocalDateTime deletedAt;

}
