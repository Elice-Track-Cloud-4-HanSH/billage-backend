package com.team01.billage.rental_record.dto;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchasersResponseDto {

    @Parameter(description = "채팅방 ID", example = "1")
    private long id;

    @Parameter(description = "거래 후보자 이미지", example = "http://example.com/image.jpg")
    private String imageUrl;

    @Parameter(description = "거래 후보자 닉네임", example = "김땡땡")
    private String nickname;
}
