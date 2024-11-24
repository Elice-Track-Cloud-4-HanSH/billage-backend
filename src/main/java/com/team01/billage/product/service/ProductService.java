package com.team01.billage.product.service;

import com.team01.billage.category.domain.Category;
import com.team01.billage.category.dto.CategoryProductResponseDto;
import com.team01.billage.category.repository.CategoryRepository;
import com.team01.billage.exception.CustomException;
import com.team01.billage.product.domain.Product;
import com.team01.billage.product.domain.RentalStatus;
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

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        product.increaseViewCount(); // 조회수 단순 증가

        Category category = product.getCategory();

        return toDetailDto(product, category);

    }

    public List<ProductResponseDto> findAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> ProductResponseDto.builder()
                        .title(product.getTitle())
                        .updatedAt(product.getUpdatedAt())
                        .dayPrice(product.getDayPrice())
                        .weekPrice(product.getWeekPrice())
                        .viewCount(product.getViewCount())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDetailResponseDto createProduct(ProductRequestDto request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .dayPrice(request.getDayPrice())
                .weekPrice(request.getWeekPrice())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        Product createProduct = productRepository.save(product);

        return toDetailDto(createProduct, category);

    }

    @Transactional
    public ProductDetailResponseDto updateProduct(Long productId, ProductRequestDto request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (product.getRentalStatus() != RentalStatus.AVAILABLE) {
            throw new CustomException(PRODUCT_MODIFICATION_NOT_ALLOWED);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        product.updateProductCategory(category);
        product.updateProduct(request);

        return toDetailDto(product, category);

    }

    @Transactional
    public RentalStatusResponseDto updateProductRentalStatus(Long productId, RentalStatusUpdateRequestDto request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        RentalStatus rentalStatus = RentalStatus.valueOf(request.getRentalStatus().toUpperCase());
        product.updateRentalStatus(rentalStatus);

        return RentalStatusResponseDto.builder()
                .rentalStatus(product.getRentalStatus().getDisplayName())
                .build();

    }

    @Transactional
    public void deleteProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (product.getRentalStatus() != RentalStatus.AVAILABLE) {
            throw new CustomException(PRODUCT_MODIFICATION_NOT_ALLOWED);
        }

        product.deleteProduct();

    }

    private ProductDetailResponseDto toDetailDto(Product product, Category category) {

        CategoryProductResponseDto categoryDto = CategoryProductResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();

        return ProductDetailResponseDto.builder()
                .categoryDto(categoryDto)
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
