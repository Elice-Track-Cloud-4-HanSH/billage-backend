package com.team01.billage.user.repository;

import com.team01.billage.user.domain.TokenRedis;  // 이렇게 변경
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<TokenRedis, String> {
    Optional<TokenRedis> findByUserId(Long userId);
    Optional<TokenRedis> findByRefreshToken(String refreshToken);
}