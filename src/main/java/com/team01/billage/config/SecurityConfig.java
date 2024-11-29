package com.team01.billage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team01.billage.config.jwt.impl.AuthTokenImpl;
import com.team01.billage.config.jwt.impl.JwtProviderImpl;
import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.TokenRedis;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.TokenRedisRepository;
import com.team01.billage.user.repository.UserRepository;
import com.team01.billage.user.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserDetailsService userDetailsService;
    private final JwtProviderImpl jwtProvider;
    private final UserRepository userRepository;
    private final TokenRedisRepository tokenRedisRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**", "/", "/login/**", "/oauth2/**").permitAll()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
                            String email = (String) oauthUser.getAttributes().get("email");

                            // 사용자 조회 또는 생성
                            Users user = userRepository.findByEmail(email)
                                    .orElseGet(() -> userRepository.save(Users.builder()
                                            .email(email)
                                            .nickname((String) oauthUser.getAttributes().get("name"))
                                            .imageUrl((String) oauthUser.getAttributes().get("picture"))
                                            .provider(Provider.GOOGLE)
                                            .role(UserRole.USER)
                                            .build()));

                            // JWT 토큰 생성
                            Map<String, Object> claims = new HashMap<>();
                            claims.put("email", email);
                            claims.put("accountId", user.getId());

                            AuthTokenImpl accessToken = jwtProvider.createAccessToken(
                                    user.getId(),
                                    user.getRole(),
                                    claims
                            );

                            AuthTokenImpl refreshToken = jwtProvider.createRefreshToken(
                                    user.getId(),
                                    user.getRole(),
                                    claims
                            );

                            // Redis에 리프레시 토큰 저장 - 수정된 부분
                            TokenRedis tokenRedis = TokenRedis.builder()
                                    .id(user.getId())  // Long을 String으로 변환
                                    .refreshToken(refreshToken.getToken())
                                    .build();

                        // 기존 토큰이 있다면 제거
                            tokenRedisRepository.deleteById(user.getId().toString());
                            tokenRedisRepository.save(tokenRedis);

                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(
                                    objectMapper.writeValueAsString(
                                            new HashMap<String, String>() {{
                                                put("accessToken", accessToken.getToken());
                                                put("refreshToken", refreshToken.getToken());
                                                put("email", email);
                                            }}
                                    )
                            );
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(
                                    objectMapper.writeValueAsString(
                                            new HashMap<String, String>() {{
                                                put("error", "인증에 실패했습니다.");
                                                put("message", exception.getMessage());
                                            }}
                                    )
                            );
                        })
                )
                .userDetailsService(userDetailsService);

            return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
