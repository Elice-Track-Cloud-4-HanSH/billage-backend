package com.team01.billage.product_review.service;

import static com.team01.billage.exception.ErrorCode.RENTAL_RECORD_NOT_FOUND;
import static com.team01.billage.exception.ErrorCode.REVIEW_ALREADY_EXISTS;
import static com.team01.billage.exception.ErrorCode.WRITE_ACCESS_FORBIDDEN;

import com.team01.billage.exception.CustomException;
import com.team01.billage.product.domain.Product;
import com.team01.billage.product.domain.ProductImage;
import com.team01.billage.product_review.domain.ProductReview;
import com.team01.billage.product_review.dto.ReviewSubjectResponseDto;
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

    public void createProductReview(WriteReviewRequestDto writeReviewRequestDto,
        long rentalRecordId, long userId) {

        RentalRecord rentalRecord = rentalRecordRepository.findById(rentalRecordId)
            .orElseThrow(() -> new CustomException(RENTAL_RECORD_NOT_FOUND));

        if (rentalRecord.getProductReview() != null) {
            throw new CustomException(REVIEW_ALREADY_EXISTS);
        }

        if (rentalRecord.getBuyer().getId() != userId) {
            throw new CustomException(WRITE_ACCESS_FORBIDDEN);
        }

        ProductReview productReview = ProductReview.builder()
            .score(writeReviewRequestDto.getScore())
            .content(writeReviewRequestDto.getContent())
            .author(rentalRecord.getBuyer())
            .rentalRecord(rentalRecord)
            .build();

        productReviewRepository.save(productReview);
    }

    public List<ShowReviewResponseDto> readMyProductReviews(long userId) {

        return productReviewRepository.findByAuthor(userId);
    }

    public List<ShowReviewResponseDto> readProductReviews(long productId) {

        return productReviewRepository.findByProduct(productId);
    }

    public ReviewSubjectResponseDto getReviewSubject(long rentalRecordId) {

        RentalRecord rentalRecord = rentalRecordRepository.findById(rentalRecordId)
            .orElseThrow(() -> new CustomException(RENTAL_RECORD_NOT_FOUND));

        Product product = rentalRecord.getProduct();
        String imageUrl = product.getProductImages().stream()
            .filter(pi -> pi.getThumbnail().equals("Y"))
            .map(ProductImage::getImageUrl)
            .findFirst()
            .orElse(null);

        return ReviewSubjectResponseDto.builder()
            .imageUrl(imageUrl)
            .subject(product.getTitle())
            .build();
    }
}
