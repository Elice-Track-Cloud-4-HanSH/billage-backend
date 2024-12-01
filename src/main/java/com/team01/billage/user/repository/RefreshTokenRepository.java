package com.team01.billage.user.repository;

import com.nimbusds.oauth2.sdk.token.RefreshToken;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByUserId(Long userId);    // String -> Long으로 변경, 메서드명 카멜케이스로 수정
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}