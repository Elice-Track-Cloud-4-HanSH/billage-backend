package com.team01.billage.rental_record.dto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class StartRentalRequestDto {

    @Parameter(description = "거래 상대방과 채팅했던 채팅방의 ID", example = "1")
    @NotNull
    private long id;

    @Parameter(description = "대여 시작일", example = "2024-12-09")
    @NotNull
    private LocalDate startDate;

    @Parameter(description = "반납 예정일", example = "2024-12-19")
    @NotNull
    private LocalDate expectedReturnDate;
}
