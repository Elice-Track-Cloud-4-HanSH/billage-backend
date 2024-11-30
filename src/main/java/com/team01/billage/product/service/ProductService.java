package com.team01.billage.product.service;

import com.team01.billage.category.domain.Category;
import com.team01.billage.category.dto.CategoryProductResponseDto;
import com.team01.billage.category.repository.CategoryRepository;
import com.team01.billage.exception.CustomException;
import com.team01.billage.product.domain.Product;
import com.team01.billage.product.domain.ProductImage;
import com.team01.billage.product.dto.*;
import com.team01.billage.product.enums.RentalStatus;
import com.team01.billage.product.repository.ProductImageRepository;
import com.team01.billage.product.repository.ProductRepository;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.product_review.repository.ProductReviewRepository;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.team01.billage.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductReviewRepository productReviewRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageService productImageService;
    private final UserRepository userRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Transactional
    public ProductDetailResponseDto findProduct(Long productId) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        product.increaseViewCount(); // 조회수 단순 증가

        return toDetailDto(product);
    }

    public List<ProductResponseDto> findAllProducts() {
        List<Product> products = productRepository.findAllByDeletedAtIsNull();
        return products.stream()
                .map(product -> ProductResponseDto.builder()
                        .productId(product.getId())
                        .title(product.getTitle())
                        .updatedAt(product.getUpdatedAt())
                        .dayPrice(product.getDayPrice())
                        .weekPrice(product.getWeekPrice())
                        .viewCount(product.getViewCount())
                        .thumbnail(
                                productImageRepository.findThumbnailByProductId(product.getId())
                                        .orElseThrow(() -> new CustomException(THUMBNAIL_NOT_FOUND))
                        )
                        .build())
                .collect(Collectors.toList());
    }

    public List<OnSaleResponseDto> findAllOnSale(String email) {

        List<Product> products = productRepository.findAllOnSale(email);

        return products.stream()
                .map(product -> OnSaleResponseDto.builder()
                        .productId(product.getId())
                        //.productImageUrl(product.getImageUrl())
                        .title(product.getTitle())
                        .build())
                .toList();
    }

    @Transactional
    public ProductDetailResponseDto createProduct(ProductRequestDto productRequestDto) {

        Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        // 상품 생성
        Product product = Product.builder()
                .seller(testUser())
                .category(category)
                .title(productRequestDto.getTitle())
                .description(productRequestDto.getDescription())
                .dayPrice(productRequestDto.getDayPrice())
                .weekPrice(productRequestDto.getWeekPrice())
                .location(toPoint(productRequestDto.getLongitude(), productRequestDto.getLatitude()))
                .build();

        // 상품 이미지 생성
        if (productRequestDto.getProductImages() != null) {
            for (ProductImageRequestDto imageDto : productRequestDto.getProductImages()) {
                productImageService.createProductImage(product, imageDto);
            }
        }

        // 상품(+상품이미지) 저장
        Product createProduct = productRepository.save(product);

        return toDetailDto(createProduct);

    }

    @Transactional
    public ProductDetailResponseDto updateProduct(Long productId, ProductUpdateRequestDto productUpdateRequestDto) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (product.getRentalStatus() != RentalStatus.AVAILABLE) {
            throw new CustomException(PRODUCT_MODIFICATION_NOT_ALLOWED);
        }

        // 카테고리 새로 저장
        Category category = categoryRepository.findById(productUpdateRequestDto.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        product.updateProductCategory(category);
        product.updateProduct(productUpdateRequestDto);
        product.updateProductLocation(
                toPoint(productUpdateRequestDto.getLongitude(), productUpdateRequestDto.getLatitude())
        );

        // 새로 추가한 상품 이미지 저장 (상품 이미지 생성)
        if (productUpdateRequestDto.getProductImages() != null) {
            for (ProductImageRequestDto imageDto : productUpdateRequestDto.getProductImages()) {
                productImageService.createProductImage(product, imageDto);
            }
        }

        // 기존 이미지 썸네일 변경 여부 체크 및 저장
        if (productUpdateRequestDto.getExistProductImages() != null) {
            updateThumbnail(productUpdateRequestDto.getExistProductImages());
        }

        return toDetailDto(product);

    }

    @Transactional
    public ProductDeleteCheckDto deleteProduct(Long productId) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (product.getRentalStatus() != RentalStatus.AVAILABLE) {
            throw new CustomException(PRODUCT_MODIFICATION_NOT_ALLOWED);
        }

        // s3 이미지 삭제
        productImageService.deleteAll(productId);

        // 상품(soft delete: 삭제일 업데이트, 상품이미지 hard delete)
        product.deleteProduct();

        return ProductDeleteCheckDto.builder()
                .productId(productId)
                .deletedAt(product.getDeletedAt())
                .build();

    }

    private ProductDetailResponseDto toDetailDto(Product product) {

        List<ShowReviewResponseDto> reviews = productReviewRepository.findByProduct_id(
                product.getId());

        Users seller = product.getSeller();
        ProductSellerResponseDto sellerDto = ProductSellerResponseDto.builder()
                .sellerId(seller.getId())
                .sellerNickname(seller.getNickname())
                .sellerImageUrl(seller.getImageUrl())
                .build();

        List<ProductImage> images = productImageRepository.findByProductId(product.getId());
        List<ProductImageResponseDto> imageDtos = images.stream()
                .map(image -> ProductImageResponseDto.builder()
                        .imageId(image.getId())
                        .imageUrl(image.getImageUrl())
                        .thumbnail(image.getThumbnail())
                        .build()).toList();

        CategoryProductResponseDto category = CategoryProductResponseDto.builder()
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .build();

        return ProductDetailResponseDto.builder()
                .productId(product.getId())
                .category(category)
                .title(product.getTitle())
                .description(product.getDescription())
                .rentalStatus(product.getRentalStatus().getDisplayName())
                .dayPrice(product.getDayPrice())
                .weekPrice(product.getWeekPrice())
                .latitude(product.getLocation().getY())
                .longitude(product.getLocation().getX())
                .viewCount(product.getViewCount())
                .updatedAt(product.getUpdatedAt())
                .seller(sellerDto)
                .reviews(reviews)
                .productImages(imageDtos)
                .build();

    }

    // 테스트용 user
    private Users testUser() {
        return userRepository.findById(1L)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    // 기존 이미지의 썸네일 변경 여부 확인 및 업데이트
    private void updateThumbnail(List<ExistProductImageRequestDto> imageDtos) {
        List<ProductImage> dbImages = productImageRepository.findAllById(
                imageDtos.stream()
                        .map(ExistProductImageRequestDto::getImageId)
                        .collect(Collectors.toList())
        );

        for (ProductImage dbImg : dbImages) {
            ExistProductImageRequestDto updateDto = imageDtos.stream()
                    .filter(img -> img.getImageId().equals(dbImg.getId()))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(PRODUCT_IMAGE_NOT_FOUND));

            // 썸네일 값이 다른 경우에만 업데이트
            if (!dbImg.getThumbnail().equals(updateDto.getThumbnail())) {
                dbImg.updateThumbnail(updateDto.getThumbnail());
            }

        }
    }

    // Point(경도, 위도) 변환
    private Point toPoint(double longitude, double latitude){
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

}
