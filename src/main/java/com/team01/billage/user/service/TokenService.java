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

    public JwtTokenDto login(JwtTokenLoginRequest request) {
        Users user = userRepository.findByEmail(request.getUserRealId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.USER_ALREADY_DELETED);
        }

        if (!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        Map<String, Object> claims = Map.of(
                "accountId", user.getId(),  //JWT 클래임에 accountId
                "role", user.getRole(),  //JWT 클래임에 role
                "provider", user.getProvider(),
                "email", user.getEmail()   //JWT 클래임에 실제 email 추가
        );

        AuthTokenImpl accessToken = jwtProvider.createAccessToken(
                user.getEmail(),   //토큰에 실제 ID 정보 입력
                user.getRole(),
                claims
        );

        AuthTokenImpl refreshToken = jwtProvider.createRefreshToken(
                user.getEmail(),   //토큰에 실제 email 정보 입력
                user.getRole(),
                claims
        );

        //리프레시 토큰은 redis 에 저장
        tokenRedisRepository.save(
                new TokenRedis(user.getId(), refreshToken.getToken()));

        return JwtTokenDto.builder()
                .accessToken(accessToken.getToken())
                .role(user.getRole())
                .build();
    }

    public boolean validateToken(String accessToken) {
        return jwtProvider.validateToken(accessToken);
    }
}
