package com.team01.billage.rental_record.controller;

import com.team01.billage.product.service.ProductService;
import com.team01.billage.rental_record.dto.StartRentalRequestDto;
import com.team01.billage.rental_record.service.RentalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rental-record")
public class RentalRecordController {

    private final RentalRecordService rentalRecordService;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Void> startRental(
        @Valid @RequestBody StartRentalRequestDto startRentalRequestDto, @AuthenticationPrincipal
    UserDetails userDetails) {

        rentalRecordService.createRentalRecord(startRentalRequestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
