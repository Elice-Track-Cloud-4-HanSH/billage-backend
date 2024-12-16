package com.team01.billage.product.repository;

import com.team01.billage.product.domain.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository {

    Optional<Product> findByIdAndDeletedAtIsNull(Long id);

    Optional<Product> findByIdAndSellerId(Long id, Long sellerId);

    @Query(value = """
        SELECT p FROM Product p
        JOIN NeighborArea n ON ST_Contains(n.geom, p.location) = true
        JOIN ActivityArea a ON n.emdArea.id = a.emdArea.id
        WHERE a.users.id = :userId
    """)
    List<Product> findProductsInNeighborArea(@Param("userId") Long userId);
}
