package com.team01.billage.rental_record.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class StartRentalRequestDto {

    @NotBlank(message = "거래 상대방을 선택해주세요.")
    private long id;

    @NotBlank(message = "대여 시작일을 선택해주세요.")
    private LocalDate startDate;

    @NotBlank(message = "반납 예정일을 선택해주세요.")
    private LocalDate expectedRentalDate;
}
