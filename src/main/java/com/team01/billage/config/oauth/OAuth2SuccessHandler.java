package com.team01.billage.config.oauth;

import com.team01.billage.common.CookieUtil;
import com.team01.billage.config.jwt.UserConstants;
import com.team01.billage.config.jwt.impl.AuthTokenImpl;
import com.team01.billage.config.jwt.impl.JwtProviderImpl;
import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.domain.TokenRedis;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
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
import java.util.HashMap;
import java.util.Map;

import static com.team01.billage.config.jwt.UserConstants.ACCESS_TOKEN_DURATION;
import static com.team01.billage.config.jwt.UserConstants.ACCESS_TOKEN_TYPE_VALUE;


@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProviderImpl tokenProvider;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;
    private final TokenRedisRepository tokenRedisRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = (String) oAuth2User.getAttributes().get("email");

        Users user = userService.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Cannot find user with email: " + email);
        }

        // 삭제된 회원 체크
        if (user.isDeleted()) {
            throw new IllegalArgumentException("This user account has been deleted");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("accountId", user.getId());

        AuthTokenImpl accessToken = tokenProvider.createAccessToken(
                user.getId(),
                user.getRole(),
                claims
        );

        AuthTokenImpl refreshToken = tokenProvider.createRefreshToken(
                user.getId(),
                user.getRole(),
                claims
        );

        CookieUtil.addCookie(response, ACCESS_TOKEN_TYPE_VALUE,
                accessToken.getToken(), (int) ACCESS_TOKEN_DURATION.toSeconds());

        TokenRedis tokenRedis = new TokenRedis(user.getId(), refreshToken.getToken());
        tokenRedisRepository.save(tokenRedis);

        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/chat");        //TODO: 환경변수화
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}