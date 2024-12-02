package com.team01.billage.product.repository;

import com.team01.billage.product.domain.FavoriteProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<FavoriteProduct, Long>, CustomFavoriteRepository {

    // 관심상품 여부 확인
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);


}
