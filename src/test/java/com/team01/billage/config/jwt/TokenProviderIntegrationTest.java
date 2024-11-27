package com.team01.billage.config.jwt;

import com.team01.billage.BillageApplication;
import com.team01.billage.config.jwt.impl.JwtProviderImpl;
import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.domain.Users;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = BillageApplication.class)
class TokenProviderIntegrationTest {

    private TokenProvider tokenProvider;
    private JwtProviderImpl jwtProviderImpl;
    private JwtProperties jwtProperties;

    @MockBean
    private UserDetailsService userDetailsService;

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
    @DisplayName("TokenProvider - 토큰 생성 및 검증 테스트")
    void tokenProviderLifecycleTest() {
        // given
        Duration duration = Duration.ofHours(1);

        // when
        String token = tokenProvider.generateToken(testUser, duration);
        boolean isValid = tokenProvider.validToken(token);
        Authentication authentication = tokenProvider.getAuthentication(token);
        Claims claims = tokenProvider.getClaims(token);

        // then
        assertNotNull(token);
        assertTrue(isValid);
        assertThat(authentication.getName()).isEqualTo(testUser.getEmail());
        assertThat(claims.getSubject()).isEqualTo(testUser.getEmail());
        assertThat(claims.get("id", Long.class)).isEqualTo(testUser.getId());
        assertThat(claims.get("nickname", String.class)).isEqualTo(testUser.getNickname());
        assertThat(claims.get("role", String.class)).isEqualTo(testUser.getRole().name());
    }

    @Test
    @DisplayName("잘못된 토큰 검증 실패 테스트")
    void invalidTokenTest() {
        // given
        String invalidToken = "invalid.token.string";

        // when & then
        assertFalse(tokenProvider.validToken(invalidToken));
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패 테스트")
    void expiredTokenTest() {
        // given
        Duration duration = Duration.ofMillis(1); // 즉시 만료되는 토큰
        String token = tokenProvider.generateToken(testUser, duration);

        // 토큰이 만료될 때까지 대기
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // when & then
        assertFalse(tokenProvider.validToken(token));
    }

    @Test
    @DisplayName("토큰의 클레임 값 검증 테스트")
    void tokenClaimsTest() {
        // given
        String token = tokenProvider.generateToken(testUser, Duration.ofHours(1));

        // when
        Claims claims = tokenProvider.getClaims(token);

        // then
        assertThat(claims.getSubject()).isEqualTo(testUser.getEmail());
        assertThat(claims.get("id", Long.class)).isEqualTo(testUser.getId());
        assertThat(claims.get("nickname", String.class)).isEqualTo(testUser.getNickname());
        assertThat(claims.get("role", String.class)).isEqualTo(testUser.getRole().name());
        assertThat(claims.get("provider", String.class)).isEqualTo(testUser.getProvider().name());
        assertThat(claims.getIssuer()).isEqualTo(jwtProperties.getIssuer());
    }

    @Test
    @DisplayName("인증 객체 생성 테스트")
    void authenticationGenerationTest() {
        // given
        String token = tokenProvider.generateToken(testUser, Duration.ofHours(1));

        // when
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then
        assertNotNull(authentication);
        assertThat(authentication.getName()).isEqualTo(testUser.getEmail());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }


}
