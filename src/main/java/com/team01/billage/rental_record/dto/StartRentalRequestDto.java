package com.team01.billage.rental_record.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class StartRentalRequestDto {

    @NotNull
    private long id;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate expectedReturnDate;
}
