package com.team01.billage.product.service;

import com.team01.billage.exception.CustomException;
import com.team01.billage.product.domain.FavoriteProduct;
import com.team01.billage.product.domain.Product;
import com.team01.billage.product.dto.CheckFavoriteResponseDto;
import com.team01.billage.product.dto.FavoriteResponseDto;
import com.team01.billage.product.dto.ProductResponseDto;
import com.team01.billage.product.repository.FavoriteRepository;
import com.team01.billage.product.repository.ProductRepository;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.team01.billage.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // 해당 상품이 회원의 관심 상품인지 확인
    public CheckFavoriteResponseDto checkFavorite(CustomUserDetails userDetails, Long productId) {

        boolean isFavorite = false;

        // 로그인 한 경우에만 좋아요 상태 조회
        if(userDetails != null) {
            checkUser(userDetails.getId());
            isFavorite = favoriteRepository.existsByUserIdAndProductId(userDetails.getId(), productId);
        }

        return CheckFavoriteResponseDto.builder()
                .favorite(isFavorite)
                .build();
    }

    // 회원의 관심 상품 목록 조회
    public List<ProductResponseDto> findAllFavorite(Long userId, int page, int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);

        return favoriteRepository.findAllByUserId(userId, pageable);
    }

    @Transactional
    public FavoriteResponseDto createFavorite(Users user, Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if(favoriteRepository.existsByUserIdAndProductId(user.getId(), productId)){
            throw new CustomException(LIKE_ALREADY_EXISTS);
        }

        FavoriteProduct favoriteProduct = FavoriteProduct.builder()
                .user(user)
                .product(product)
                .build();

        FavoriteProduct favorite = favoriteRepository.save(favoriteProduct);

        return FavoriteResponseDto.builder()
                .favoriteProductId(favorite.getId())
                .userId(user.getId())
                .productId(favorite.getProduct().getId())
                .build();

    }

    @Transactional
    public void deleteFavorite(Long userId, Long productId) {

        productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public Users checkUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

}
