package com.team01.billage.user_review.controller;

import com.team01.billage.product_review.dto.ReviewSubjectResponseDto;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.product_review.dto.WriteReviewRequestDto;
import com.team01.billage.user_review.service.UserReviewService;
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
@RequestMapping("/api/user-review")
public class UserReviewController {

    private final UserReviewService userReviewService;

    @PostMapping("/{rentalRecordId}")
    public ResponseEntity<Void> writeUserReview(
        @Valid @RequestBody WriteReviewRequestDto writeReviewRequestDto,
        @PathVariable("rentalRecordId") long rentalRecordId,
        @AuthenticationPrincipal UserDetails userDetails) {

        userReviewService.createUserReview(writeReviewRequestDto, rentalRecordId,
            userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<ShowReviewResponseDto>> showUserReview(
        @AuthenticationPrincipal UserDetails userDetails) {

        List<ShowReviewResponseDto> responseDtos = userReviewService.readUserReviews(
            userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(responseDtos);
    }

    @GetMapping("/{rentalRecordId}")
    public ResponseEntity<ReviewSubjectResponseDto> reviewSubject(
        @PathVariable("rentalRecordId") long id, @AuthenticationPrincipal UserDetails userDetails) {

        ReviewSubjectResponseDto responseDto = userReviewService.getReviewSubject(id,
            userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
