package com.team01.billage.product.service;

import static com.team01.billage.exception.ErrorCode.*;

import com.team01.billage.category.domain.Category;
import com.team01.billage.category.dto.CategoryProductResponseDto;
import com.team01.billage.category.repository.CategoryRepository;
import com.team01.billage.common.CustomSlice;
import com.team01.billage.exception.CustomException;
import com.team01.billage.product.domain.Product;
import com.team01.billage.product.domain.ProductImage;
import com.team01.billage.product.dto.*;
import com.team01.billage.product.enums.RentalStatus;
import com.team01.billage.product.repository.ProductImageRepository;
import com.team01.billage.product.repository.ProductRepository;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageService productImageService;
    private final UserRepository userRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Transactional
    public ProductDetailWrapperResponseDto findProduct(CustomUserDetails userDetails, Long productId) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        ProductDetailResponseDto productDetail = productRepository.findProductDetail(productId);

        increaseViewCount(product); // 조회수 단순 증가

        productDetail.setLongitude(product.getLocation().getX());
        productDetail.setLatitude(product.getLocation().getY());

        boolean checkAuthor = false;
        if (userDetails != null) {
            Users user = checkUser(userDetails.getId());
            if (product.getSeller().getId().equals(user.getId())) {
                checkAuthor = true;
            }
        }

        return ProductDetailWrapperResponseDto.builder()
                .productDetail(productDetail)
                .checkAuthor(checkAuthor)
                .build();
    }

    public List<ProductResponseDto> findAllProducts(CustomUserDetails userDetails, String categoryId, String rentalStatus) {

        Long userId = null;

        if (userDetails != null) {
            checkUser(userDetails.getId());
            userId = userDetails.getId();
        }

        return productRepository.findAllProducts(
                userId,
                Long.parseLong(categoryId),
                rentalStatus);
    }

    public Slice<OnSaleResponseDto> findAllOnSale(String email, LocalDateTime lastTime,
                                                  Pageable pageable) {

        List<OnSaleResponseDto> results = productRepository.findAllOnSale(email, lastTime,
                pageable);

        boolean hasNext = results.size() > pageable.getPageSize();

        if (hasNext) {
            results.remove(results.size() - 1);
        }

        LocalDateTime nextLastTime = results.isEmpty() ? null
                : results.get(results.size() - 1).getTime();

        return new CustomSlice<>(results, pageable, hasNext, nextLastTime);
    }

    @Transactional
    public ProductDetailResponseDto createProduct(Users user, ProductRequestDto productRequestDto) {

        Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        // 상품 생성
        Product product = Product.builder()
                .seller(user)
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
    public ProductDetailResponseDto updateProduct(
            Long userId,
            Long productId,
            ProductUpdateRequestDto productUpdateRequestDto) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (!product.getSeller().getId().equals(userId)) {
            throw new CustomException(ACCESS_DENIED);
        }

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
    public ProductDeleteCheckDto deleteProduct(Long userId, Long productId) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (!product.getSeller().getId().equals(userId)) {
            throw new CustomException(ACCESS_DENIED);
        }

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
                .productImages(imageDtos)
                .build();

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
    private Point toPoint(double longitude, double latitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    public Users checkUser(Long userId) {
        System.out.println("회원 확인: " + userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    // 조회수 단순 증가
    @Transactional
    public void increaseViewCount(Product product) {
        product.increaseViewCount();
        productRepository.save(product);
    }


}
