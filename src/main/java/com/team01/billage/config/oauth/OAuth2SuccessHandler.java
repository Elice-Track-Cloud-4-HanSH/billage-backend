package com.team01.billage.config.oauth;


import com.team01.billage.common.CookieUtil;
import com.team01.billage.config.jwt.impl.AuthTokenImpl;
import com.team01.billage.config.jwt.impl.JwtProviderImpl;
import com.team01.billage.user.domain.RefreshToken;
import com.team01.billage.user.domain.TokenRedis;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.RefreshTokenRepository;
import com.team01.billage.user.repository.TokenRedisRepository;
import com.team01.billage.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static com.team01.billage.config.jwt.UserConstants.*;


/**
 * Oauth 로그인 성공시 호출되는 핸들러 클래스
 * */
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // 의존성 주입
    private final JwtProviderImpl provider;  // JWT 토큰 제공자
    private final RefreshTokenRepository refreshTokenRepository; // 리프레시 토큰 저장소
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository; // OAuth2 인증 요청 쿠키 저장소
    private final UserService userService; // 사용자 서비스
    private final TokenRedisRepository tokenRedisRepository;

    // OAuth2 로그인 성공 시 호출되는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); // 인증된 사용자의 정보를 가져옴
        Users user = userService.findByEmail((String) oAuth2User.getAttributes().get("email")); // 사용자 정보 조회

        Map<String, Object> claims = Map.of(
                "accountId", user.getId(),  //JWT 클래임에 accountId
                "role", user.getRole(),  //JWT 클래임에 role
                "provider",user.getProvider(),
                "email", user.getEmail()   //JWT 클래임에 email 추가
        );

        // 리프레시 토큰 생성 후 DB에 저장
        AuthTokenImpl createRefreshToken = provider.createRefreshToken(user.getId(), user.getRole(), claims); // 리프레시 토큰 생성
        String refreshToken = createRefreshToken.getToken();

        saveRefreshTokenFromRedis(user.getId(), refreshToken); // 리프레시 토큰 저장

        // 액세스 토큰 생성 후 쿠키에 저장
        AuthTokenImpl createAccessToken = provider.createAccessToken(user.getId(), user.getRole(), claims); // 리프레시 토큰 생성
        String accessToken = createAccessToken.getToken();

        addAccessTokenToCookie(request, response, accessToken); // 리프레시 토큰을 쿠키에 추가

        // 인증 관련 쿠키와 정보를 정리하고 리다이렉트 처리
        clearAuthenticationAttributes(request, response); // 인증 속성 정리(oauth2_auth_request 삭제)
        getRedirectStrategy().sendRedirect(request, response, "/"); // 지정된 URL로 리다이렉트
    }

    // 엑세스 토큰을 쿠키에 추가하는 메서드
    private void addAccessTokenToCookie(HttpServletRequest request, HttpServletResponse response, String accessToken) {
        int cookieMaxAge = (int) ACCESS_TOKEN_DURATION.toSeconds(); // 쿠키 유효 기간 설정

        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_TYPE_VALUE); // 기존 쿠키 삭제
        CookieUtil.addCookie(response, ACCESS_TOKEN_TYPE_VALUE, accessToken, cookieMaxAge); // 새 쿠키 추가
    }


    // 리프레시 토큰을 redis DB에 저장하는 메서드
    private void saveRefreshTokenFromRedis(Long Id, String newRefreshToken) {
        //새로운 리프레시 토큰 생성
        TokenRedis refreshToken = new TokenRedis(Id, newRefreshToken);
        tokenRedisRepository.save(refreshToken); // 저장소에 리프레시 토큰 저장
    }


    // 인증 관련 속성을 정리하는 메서드
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request); // 부모 클래스 메서드 호출하여 속성 정리
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response); // 인증 요청 쿠키 삭제
    }

}
