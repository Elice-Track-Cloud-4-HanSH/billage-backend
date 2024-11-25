package com.team01.billage.rental_record.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ShowRecordResponseDto {

    private String productImageUrl;
    private String title;
    private String userImageUrl;
    private String nickname;
    private LocalDate startDate;
    private LocalDate expectedReturnDate;
    private LocalDate returnDate;
}
