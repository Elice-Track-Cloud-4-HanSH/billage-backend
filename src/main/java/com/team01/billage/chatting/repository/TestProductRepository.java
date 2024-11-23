package com.team01.billage.chatting.repository;

import com.team01.billage.chatting.domain.TestProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestProductRepository extends JpaRepository<TestProduct, Long> {
}
