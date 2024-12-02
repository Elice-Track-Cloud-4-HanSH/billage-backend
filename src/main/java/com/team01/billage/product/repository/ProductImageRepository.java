package com.team01.billage.product.repository;

import com.team01.billage.product.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(Long productId);

    @Query("SELECT p.imageUrl FROM ProductImage p " +
            "WHERE p.product.id = :productId " +
            "AND p.thumbnail = 'Y'")
    Optional<String> findThumbnailByProductId(@Param("productId") Long productId);

}
