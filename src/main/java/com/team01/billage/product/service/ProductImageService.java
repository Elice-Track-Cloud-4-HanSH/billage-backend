package com.team01.billage.product.service;

import com.team01.billage.product.domain.Product;
import com.team01.billage.product.domain.ProductImage;
import com.team01.billage.product.dto.ProductImageDeleteRequestDto;
import com.team01.billage.product.dto.ProductImageRequestDto;
import com.team01.billage.product.repository.ProductImageRepository;
import com.team01.billage.utils.s3.S3BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final S3BucketService s3BucketService;

    // 상품 이미지 저장
    @Transactional
    public void createProductImage(Product product, ProductImageRequestDto imageDto){
        System.out.println("image: " + imageDto.getImageUrl());
        ProductImage productImage = ProductImage.builder()
                .product(product)
                .imageUrl(s3BucketService.upload(imageDto.getImageUrl()))
                .thumbnail(imageDto.getThumbnail())
                .build();

        product.addProductImage(productImage);
    }

    // 해당 상품의 상품 이미지 s3 일괄 삭제
    @Transactional
    public void deleteAll(Long productId) {

        List<ProductImage> images = productImageRepository.findByProductId(productId);

        for (ProductImage image : images) {
            s3BucketService.delete(image.getImageUrl());
        }

    }

    // 특정 이미지 삭제
    @Transactional
    public void deleteProductImages(List<ProductImageDeleteRequestDto> productImageDeleteRequestDtos){
        // s3에서 삭제
        for (ProductImageDeleteRequestDto image : productImageDeleteRequestDtos) {
            s3BucketService.delete(image.getImageUrl());
        }

        // 상품 이미지 테이블에서 삭제
        List<Long> imageIds = productImageDeleteRequestDtos.stream()
                .map(ProductImageDeleteRequestDto::getImageId)
                .collect(Collectors.toList());

        productImageRepository.deleteAllById(imageIds);

    }

}
