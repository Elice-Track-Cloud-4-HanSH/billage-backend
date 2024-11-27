package com.team01.billage.config.jwt;

import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.domain.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TokenProviderTest {
    private TokenProvider tokenProvider;
    private JwtProperties jwtProperties;
    private Users testUser;

    @BeforeEach
    void setUp() {
        // JWT 프로퍼티 설정
        jwtProperties = new JwtProperties(
                "testsecretkeytestsecretkeytestsecretkeytestsecretkey", // secret
                3600000L,  // accessExpires (1시간)
                86400000L  // refreshExpires (24시간)
        );

        tokenProvider = new TokenProvider(jwtProperties);

        // 테스트용 사용자 생성
        testUser = Users.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testUser")
                .role(UserRole.USER)
                .provider(Provider.LOCAL)
                .build();
    }

    @Test
    @DisplayName("토큰 생성 테스트")
    void generateToken_ShouldCreateValidToken() {
        // given
        Duration expiredAt = Duration.ofDays(1);

        // when
        String token = tokenProvider.generateToken(testUser, expiredAt);

        // then
        assertNotNull(token);
        assertTrue(tokenProvider.validToken(token));
    }

    @Test
    @DisplayName("만료된 토큰 검증 테스트")
    void validToken_WithExpiredToken_ShouldReturnFalse() {
        // given
        Duration expiredAt = Duration.ofMillis(1);
        String token = tokenProvider.generateToken(testUser, expiredAt);

        // when
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // then
        assertFalse(tokenProvider.validToken(token));
    }

    @Test
    @DisplayName("올바른 토큰에서 Authentication 객체 생성 테스트")
    void getAuthentication_ShouldReturnValidAuthentication() {
        // given
        String token = tokenProvider.generateToken(testUser, Duration.ofHours(1));

        // when
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then
        assertNotNull(authentication);
        assertEquals(testUser.getId().toString(), authentication.getName());
        assertTrue(authentication.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_" + testUser.getRole().name()))
        );
    }

    @Test
    @DisplayName("토큰에서 클레임 추출 테스트")
    void getClaims_ShouldExtractCorrectClaims() {
        // given
        String token = tokenProvider.generateToken(testUser, Duration.ofHours(1));

        // when
        var claims = tokenProvider.getClaims(token);

        // then
        assertNotNull(claims);
        assertEquals(testUser.getId().toString(), claims.getSubject());
        assertEquals(testUser.getId(), claims.get("accountId", Long.class));
        assertEquals(testUser.getEmail(), claims.get("email", String.class));
        assertEquals(testUser.getRole().name(), claims.get("role", String.class));
        assertEquals(testUser.getProvider().name(), claims.get("provider", String.class));
    }

    @Test
    @DisplayName("잘못된 서명의 토큰 검증 테스트")
    void validToken_WithInvalidSignature_ShouldReturnFalse() {
        // given
        String token = tokenProvider.generateToken(testUser, Duration.ofHours(1));
        String invalidToken = token + "invalid";

        // then
        assertFalse(tokenProvider.validToken(invalidToken));
    }

    // issuer 테스트 제거
}