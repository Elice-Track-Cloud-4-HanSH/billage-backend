package com.team01.billage.rental_record.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ShowRecordResponseDto {

    private long rentalRecordId;
    private LocalDate startDate;
    private LocalDate expectedReturnDate;
    private LocalDate returnDate;
    private long productId;
    private String productImageUrl;
    private String title;
    private long userId;
    private String userImageUrl;
    private String nickname;
}
