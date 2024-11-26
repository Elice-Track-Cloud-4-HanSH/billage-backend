package com.team01.billage.product_review.controller;

import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.product_review.dto.WriteReviewRequestDto;
import com.team01.billage.product_review.service.ProductReviewService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product-review")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @PostMapping("/{rentalRecordId}")
    public ResponseEntity<Void> writeProductReview(
        @Valid @RequestBody WriteReviewRequestDto writeReviewRequestDto,
        @PathVariable("rentalRecordId") long rentalRecordId,
        @AuthenticationPrincipal UserDetails userDetails) {

        productReviewService.createProductReview(writeReviewRequestDto, rentalRecordId,
            userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<ShowReviewResponseDto>> showProductReview(
        @AuthenticationPrincipal UserDetails userDetails) {

        List<ShowReviewResponseDto> response = productReviewService.readProductReviews(
            userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
