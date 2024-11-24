package com.team01.billage.chatting.repository;

import com.team01.billage.chatting.domain.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestUserRepository extends JpaRepository<TestUser, Long> {
}
