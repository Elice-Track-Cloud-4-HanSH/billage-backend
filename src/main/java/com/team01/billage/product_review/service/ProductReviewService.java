package com.team01.billage.product_review.service;

import static com.team01.billage.exception.ErrorCode.RENTAL_RECORD_NOT_FOUND;
import static com.team01.billage.exception.ErrorCode.REVIEW_ALREADY_EXISTS;
import static com.team01.billage.exception.ErrorCode.WRITE_ACCESS_FORBIDDEN;

import com.team01.billage.common.CustomSlice;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    public Slice<ShowReviewResponseDto> readMyProductReviews(long userId, Long lastStandard,
        Pageable pageable) {

        List<ShowReviewResponseDto> content = productReviewRepository.findByAuthor(userId,
            lastStandard, pageable);

        boolean hasNext = content.size() > pageable.getPageSize();

        if (hasNext) {
            content.remove(content.size() - 1);
        }

        Long nextLastStandard = null;

        if (!content.isEmpty()) {
            nextLastStandard = content.get(content.size() - 1).getReviewId();
        }

        return new CustomSlice<>(content, pageable, hasNext, nextLastStandard);
    }

    public Slice<ShowReviewResponseDto> readProductReviews(long productId, Long lastStandard,
        Pageable pageable) {

        List<ShowReviewResponseDto> content = productReviewRepository.findByProduct(productId,
            lastStandard, pageable);

        boolean hasNext = content.size() > pageable.getPageSize();

        if (hasNext) {
            content.remove(content.size() - 1);
        }

        Long nextLastStandard = null;

        if (!content.isEmpty()) {
            nextLastStandard = content.get(content.size() - 1).getReviewId();
        }

        return new CustomSlice<>(content, pageable, hasNext, nextLastStandard);
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
