package com.team01.billage.product.service;

import static com.team01.billage.exception.ErrorCode.CATEGORY_NOT_FOUND;
import static com.team01.billage.exception.ErrorCode.PRODUCT_MODIFICATION_NOT_ALLOWED;
import static com.team01.billage.exception.ErrorCode.PRODUCT_NOT_FOUND;
import static com.team01.billage.exception.ErrorCode.THUMBNAIL_NOT_FOUND;

import com.team01.billage.category.domain.Category;
import com.team01.billage.category.repository.CategoryRepository;
import com.team01.billage.exception.CustomException;
import com.team01.billage.product.domain.Product;
import com.team01.billage.product.domain.ProductImage;
import com.team01.billage.product.dto.OnSaleResponseDto;
import com.team01.billage.product.dto.ProductDeleteCheckDto;
import com.team01.billage.product.dto.ProductDetailResponseDto;
import com.team01.billage.product.dto.ProductImageRequestDto;
import com.team01.billage.product.dto.ProductImageResponseDto;
import com.team01.billage.product.dto.ProductRequestDto;
import com.team01.billage.product.dto.ProductResponseDto;
import com.team01.billage.product.dto.ProductSellerResponseDto;
import com.team01.billage.product.enums.RentalStatus;
import com.team01.billage.product.repository.ProductImageRepository;
import com.team01.billage.product.repository.ProductRepository;
import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import com.team01.billage.utils.s3.S3BucketService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final S3BucketService s3BucketService;
    private final ProductImageService productImageService;
    private final UserRepository userRepository;

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

        return productRepository.findAllOnSale(email);
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
            .latitude(productRequestDto.getLatitude())
            .longitude(productRequestDto.getLongitude())
            .build();

        System.out.println("받아온 이미지 개수: " + productRequestDto.getProductImages().size());

        // 상품 이미지 생성
        for (ProductImageRequestDto imageDto : productRequestDto.getProductImages()) {
            System.out.println("image: " + imageDto.getImageUrl());
            ProductImage productImage = ProductImage.builder()
                .product(product)
                .imageUrl(s3BucketService.upload(imageDto.getImageUrl()))
                .thumbnail(imageDto.getThumbnail())
                .build();

            System.out.println("이미지 공용 url: " + productImage.getImageUrl());
            System.out.println("썸네일 여부: " + productImage.getThumbnail());

            // 상품에 상품 이미지 추가
            product.addProductImage(productImage);
        }

        // 상품(+상품이미지) 저장
        Product createProduct = productRepository.save(product);

        return toDetailDto(createProduct);

    }

    @Transactional
    public ProductDetailResponseDto updateProduct(Long productId,
        ProductRequestDto productRequestDto) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
            .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (product.getRentalStatus() != RentalStatus.AVAILABLE) {
            throw new CustomException(PRODUCT_MODIFICATION_NOT_ALLOWED);
        }

        Category category = categoryRepository.findById(productRequestDto.getCategoryId())
            .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        product.updateProductCategory(category);
        product.updateProduct(productRequestDto);

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

        return ProductDetailResponseDto.builder()
            .categoryName(product.getCategory().getName())
            .title(product.getTitle())
            .description(product.getDescription())
            .rentalStatus(product.getRentalStatus().getDisplayName())
            .dayPrice(product.getDayPrice())
            .weekPrice(product.getWeekPrice())
            .latitude(product.getLatitude())
            .longitude(product.getLongitude())
            .viewCount(product.getViewCount())
            .updatedAt(product.getUpdatedAt())
            .seller(sellerDto)
            .productImages(imageDtos)
            .build();

    }

    // 테스트용 user (email unique 해제 후 진행)
    private Users testUser() {
        Users testUser = Users.builder()
            .nickname("elice")
            .email("abc@gmail.com")
            .role(UserRole.USER)
            .provider(Provider.LOCAL)
            .build();

        return userRepository.save(testUser);
    }

}
