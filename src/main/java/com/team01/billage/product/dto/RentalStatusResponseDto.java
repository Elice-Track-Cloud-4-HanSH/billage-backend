package com.team01.billage.product.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalStatusResponseDto {

    private String rentalStatus; // 대여 판매 중, 대여 중

}
