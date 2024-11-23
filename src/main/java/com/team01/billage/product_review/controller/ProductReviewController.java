package com.team01.billage.product_review.controller;

import com.team01.billage.product_review.dto.WriteReviewRequestDto;
import com.team01.billage.product_review.service.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping("/{id}")
    public ResponseEntity<Void> writeProductReview(
        @Valid @RequestBody WriteReviewRequestDto writeReviewRequestDto,
        @PathVariable("id") long id, @AuthenticationPrincipal UserDetails userDetails) {

        productReviewService.createProductReview(writeReviewRequestDto, id,
            userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
