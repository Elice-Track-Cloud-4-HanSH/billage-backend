package com.team01.billage.rental_record.controller;

import com.team01.billage.product.service.ProductService;
import com.team01.billage.rental_record.dto.PurchasersResponseDto;
import com.team01.billage.rental_record.dto.ShowRecordResponseDto;
import com.team01.billage.rental_record.dto.StartRentalRequestDto;
import com.team01.billage.rental_record.service.RentalRecordService;
import com.team01.billage.user.domain.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    CustomUserDetails userDetails) {

        rentalRecordService.createRentalRecord(startRentalRequestDto, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<ShowRecordResponseDto>> showRentalRecord(
        @RequestParam(name = "type") String type,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<ShowRecordResponseDto> responseDtos = rentalRecordService.readRentalRecords(type,
            userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @GetMapping("/set-to-rented/{productId}")
    public ResponseEntity<List<PurchasersResponseDto>> showPurchasers(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable("productId") long productId) {

        List<PurchasersResponseDto> responseDtos = rentalRecordService.readPurchasers(
            userDetails.getId(), productId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @PatchMapping("/{rentalRecordId}")
    public ResponseEntity<Void> returnCompleted(@PathVariable("rentalRecordId") long rentalRecordId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        rentalRecordService.updateRentalRecord(rentalRecordId, userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
