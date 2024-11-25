package com.team01.billage.product.service;

import com.team01.billage.category.domain.Category;
import com.team01.billage.category.dto.CategoryProductResponseDto;
import com.team01.billage.category.repository.CategoryRepository;
import com.team01.billage.exception.CustomException;
import com.team01.billage.product.domain.Product;
import com.team01.billage.product.enums.RentalStatus;
import com.team01.billage.product.dto.*;
import com.team01.billage.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public ProductDetailResponseDto findProduct(Long productId) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        product.increaseViewCount(); // 조회수 단순 증가

        Category category = product.getCategory();

        return toDetailDto(product, category);

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
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDetailResponseDto createProduct(ProductRequestDto productRequestDto) {

        Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .category(category)
                .title(productRequestDto.getTitle())
                .description(productRequestDto.getDescription())
                .dayPrice(productRequestDto.getDayPrice())
                .weekPrice(productRequestDto.getWeekPrice())
                .latitude(productRequestDto.getLatitude())
                .longitude(productRequestDto.getLongitude())
                .build();

        Product createProduct = productRepository.save(product);

        return toDetailDto(createProduct, category);

    }

    @Transactional
    public ProductDetailResponseDto updateProduct(Long productId, ProductRequestDto productRequestDto) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (product.getRentalStatus() != RentalStatus.AVAILABLE) {
            throw new CustomException(PRODUCT_MODIFICATION_NOT_ALLOWED);
        }

        Category category = categoryRepository.findById(productRequestDto.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        product.updateProductCategory(category);
        product.updateProduct(productRequestDto);

        return toDetailDto(product, category);

    }

    @Transactional
    public ProductDeleteCheckDto deleteProduct(Long productId) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (product.getRentalStatus() != RentalStatus.AVAILABLE) {
            throw new CustomException(PRODUCT_MODIFICATION_NOT_ALLOWED);
        }

        product.deleteProduct();

        return ProductDeleteCheckDto.builder()
                .productId(productId)
                .deletedAt(product.getDeletedAt())
                .build();

    }

    private ProductDetailResponseDto toDetailDto(Product product, Category category) {

        CategoryProductResponseDto categoryDto = CategoryProductResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();

        return ProductDetailResponseDto.builder()
                .categoryDto(categoryDto)
                .productId(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .rentalStatus(product.getRentalStatus().getDisplayName())
                .dayPrice(product.getDayPrice())
                .weekPrice(product.getWeekPrice())
                .latitude(product.getLatitude())
                .longitude(product.getLongitude())
                .viewCount(product.getViewCount())
                .updatedAt(product.getUpdatedAt())
                .build();

    }

}
