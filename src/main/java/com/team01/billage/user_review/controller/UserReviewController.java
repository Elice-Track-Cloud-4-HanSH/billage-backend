package com.team01.billage.user_review.controller;

import com.team01.billage.product_review.dto.ReviewSubjectResponseDto;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.product_review.dto.WriteReviewRequestDto;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user_review.service.UserReviewService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-review")
public class UserReviewController {

    private final UserReviewService userReviewService;

    @PostMapping("/{rentalRecordId}")
    public ResponseEntity<Void> writeUserReview(
        @Valid @RequestBody WriteReviewRequestDto writeReviewRequestDto,
        @PathVariable("rentalRecordId") long rentalRecordId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        userReviewService.createUserReview(writeReviewRequestDto, rentalRecordId,
            userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<ShowReviewResponseDto>> showUserReview(
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<ShowReviewResponseDto> responseDtos = userReviewService.readUserReviews(
            userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @GetMapping("/{rentalRecordId}")
    public ResponseEntity<ReviewSubjectResponseDto> reviewSubject(
        @PathVariable("rentalRecordId") long rentalRecordId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        ReviewSubjectResponseDto responseDto = userReviewService.getReviewSubject(rentalRecordId,
            userDetails.getId());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/target/{userId}")
    public ResponseEntity<List<ShowReviewResponseDto>> targetReview(
        @PathVariable("userId") long userId) {

        List<ShowReviewResponseDto> responseDtos = userReviewService.readTargetReviews(userId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }
}
