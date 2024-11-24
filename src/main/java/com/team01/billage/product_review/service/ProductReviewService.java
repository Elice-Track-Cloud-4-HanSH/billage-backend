package com.team01.billage.product_review.service;

import static com.team01.billage.exception.ErrorCode.RENTAL_REVIEW_NOT_FOUND;
import static com.team01.billage.exception.ErrorCode.WRITE_ACCESS_FORBIDDEN;

import com.team01.billage.exception.CustomException;
import com.team01.billage.product_review.domain.ProductReview;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.product_review.dto.WriteReviewRequestDto;
import com.team01.billage.product_review.repository.ProductReviewRepository;
import com.team01.billage.rental_record.domain.RentalRecord;
import com.team01.billage.rental_record.repository.RentalRecordRepository;
import com.team01.billage.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final UserRepository userRepository;
    private final RentalRecordRepository rentalRecordRepository;

    public void createProductReview(WriteReviewRequestDto writeReviewRequestDto, long id,
        String email) {

        RentalRecord rentalRecord = rentalRecordRepository.findById(id)
            .orElseThrow(() -> new CustomException(RENTAL_REVIEW_NOT_FOUND));

        if (!rentalRecord.getBuyer().getEmail().equals(email)) {
            throw new CustomException(WRITE_ACCESS_FORBIDDEN);
        }

        ProductReview productReview = ProductReview.builder()
            .score(writeReviewRequestDto.getScore())
            .content(writeReviewRequestDto.getContent())
            .author(rentalRecord.getBuyer())
            .product(rentalRecord.getProduct())
            .build();

        productReviewRepository.save(productReview);
    }

    public List<ShowReviewResponseDto> readProductReviews(String email) {

        return productReviewRepository.findByAuthor_email(email);
    }
}
