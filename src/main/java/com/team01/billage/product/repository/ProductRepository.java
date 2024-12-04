package com.team01.billage.product.repository;

import com.team01.billage.product.domain.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository {

    Optional<Product> findByIdAndDeletedAtIsNull(Long id);

}
