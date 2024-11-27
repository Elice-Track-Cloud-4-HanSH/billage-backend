package com.team01.billage.product.service;

import com.team01.billage.product.domain.ProductImage;
import com.team01.billage.product.repository.ProductImageRepository;
import com.team01.billage.utils.s3.S3BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final S3BucketService s3BucketService;

    // 해당 상품의 상품 이미지 s3 일괄 삭제
    @Transactional
    public void deleteAll(Long productId) {

        List<ProductImage> images = productImageRepository.findByProductId(productId);

        for (ProductImage image : images) {
            s3BucketService.delete(image.getImageUrl());
        }

    }
}
