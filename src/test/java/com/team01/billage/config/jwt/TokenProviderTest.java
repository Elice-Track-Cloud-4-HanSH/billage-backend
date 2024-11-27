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
        jwtProperties = new JwtProperties();
        jwtProperties.setIssuer("test@example.com");
        jwtProperties.setSecretKey("testsecretkeytestsecretkeytestsecretkeytestsecretkey"); // 최소 32바이트

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
        System.out.println(token);
    }

    @Test
    @DisplayName("만료된 토큰 검증 테스트")
    void validToken_WithExpiredToken_ShouldReturnFalse() {
        // given
        Duration expiredAt = Duration.ofMillis(1); // 1밀리초 후 만료
        String token = tokenProvider.generateToken(testUser, expiredAt);

        // when
        try {
            Thread.sleep(100); // 토큰 만료 대기
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
        assertEquals(testUser.getEmail(), authentication.getName());
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
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then
        assertNotNull(authentication);
        assertEquals(testUser.getEmail(), authentication.getName());
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

    @Test
    @DisplayName("토큰 발급자 검증 테스트")
    void generateToken_ShouldHaveCorrectIssuer() {
        // given
        String token = tokenProvider.generateToken(testUser, Duration.ofHours(1));

        // when
        String issuer = tokenProvider.getClaims(token).getIssuer();

        // then
        assertEquals(jwtProperties.getIssuer(), issuer);
    }
}
