package com.team01.billage.product.service;

import com.team01.billage.exception.CustomException;
import com.team01.billage.product.domain.FavoriteProduct;
import com.team01.billage.product.domain.Product;
import com.team01.billage.product.dto.CheckFavoriteResponseDto;
import com.team01.billage.product.dto.FavoriteResponseDto;
import com.team01.billage.product.dto.ProductResponseDto;
import com.team01.billage.product.repository.FavoriteRepository;
import com.team01.billage.product.repository.ProductRepository;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.team01.billage.exception.ErrorCode.PRODUCT_NOT_FOUND;
import static com.team01.billage.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 해당 상품이 회원의 관심 상품인지 확인
    public CheckFavoriteResponseDto checkFavorite(Long productId) {

        boolean isFavorite = favoriteRepository.existsByUserIdAndProductId(testUser().getId(), productId);

        return CheckFavoriteResponseDto.builder()
                .isFavorite(isFavorite)
                .build();
    }

    // 회원의 관심 상품 목록 조회
    public List<ProductResponseDto> findAllFavorite() {

        return favoriteRepository.findAllByUserId(testUser().getId());
    }

    @Transactional
    public FavoriteResponseDto createFavorite(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        FavoriteProduct favoriteProduct = FavoriteProduct.builder()
                .user(testUser())
                .product(product)
                .build();

        FavoriteProduct favorite = favoriteRepository.save(favoriteProduct);

        return FavoriteResponseDto.builder()
                .favoriteProductId(favorite.getId())
                .userId(favorite.getUser().getId())
                .productId(favorite.getProduct().getId())
                .build();

    }

    @Transactional
    public void deleteFavorite(Long productId) {

        productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        favoriteRepository.deleteByUserIdAndProductId(testUser().getId(), productId);
    }

    // 테스트용 user
    private Users testUser() {
        return userRepository.findById(1L)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

}
