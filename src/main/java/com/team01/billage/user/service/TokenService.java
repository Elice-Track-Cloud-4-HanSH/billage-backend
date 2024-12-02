package com.team01.billage.user.service;

import com.team01.billage.config.jwt.impl.AuthTokenImpl;
import com.team01.billage.config.jwt.impl.JwtProviderImpl;
import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.domain.TokenRedis;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.dto.JwtTokenDto;
import com.team01.billage.user.dto.Request.JwtTokenLoginRequest;
import com.team01.billage.user.repository.TokenRedisRepository;
import com.team01.billage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProviderImpl jwtProvider;
    private final TokenRedisRepository tokenRedisRepository;

    @Value("${REDIS_TOKEN_TTL}")
    private Long redisTokenTtl;

    public JwtTokenDto login(JwtTokenLoginRequest request) {
        // 이메일로 사용자 찾기
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.USER_ALREADY_DELETED);
        }

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // claims에는 모든 필요한 정보를 담습니다
        Map<String, Object> claims = Map.of(
                "accountId", user.getId(),  // PK
                "email", user.getEmail(),   // email
                "role", user.getRole(),     // role
                "provider", user.getProvider() // provider
        );

        // userId(PK)를 기본 식별자로 사용
        AuthTokenImpl accessToken = jwtProvider.createAccessToken(
                user.getId(),    // email 대신 PK 사용
                user.getRole(),
                claims
        );

        AuthTokenImpl refreshToken = jwtProvider.createRefreshToken(
                user.getId(),    // email 대신 PK 사용
                user.getRole(),
                claims
        );

        // Redis에 저장
        tokenRedisRepository.save(
                new TokenRedis(user.getId(), refreshToken.getToken(),redisTokenTtl )
        );

        return JwtTokenDto.builder()
                .accessToken(accessToken.getToken())
                .role(user.getRole())
                .build();
    }

    public boolean validateToken(String accessToken) {
        return jwtProvider.validateToken(accessToken);
    }
}