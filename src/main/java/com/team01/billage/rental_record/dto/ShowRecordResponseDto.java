package com.team01.billage.rental_record.dto;

import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShowRecordResponseDto {

    @Parameter(description = "대여 기록 ID", example = "1")
    private long rentalRecordId;

    @Parameter(description = "대여 시작일", example = "2024-12-01")
    private LocalDate startDate;

    @Parameter(description = "반납 예정일", example = "2024-12-19")
    private LocalDate expectedReturnDate;

    @Parameter(description = "반납일", example = "2024-12-21")
    private LocalDate returnDate;

    @Parameter(description = "상품 ID", example = "1")
    private long productId;

    @Parameter(description = "상품 썸네일 이미지", example = "http://example.com/image.jpg")
    private String productImageUrl;

    @Parameter(description = "상품 제목", example = "디지털 카메라")
    private String title;

    @Parameter(description = "유저 프로필 이미지", example = "http://example.com/image.jpg")
    private String userImageUrl;

    @Parameter(description = "유저 닉네임", example = "김땡땡")
    private String nickname;
}
