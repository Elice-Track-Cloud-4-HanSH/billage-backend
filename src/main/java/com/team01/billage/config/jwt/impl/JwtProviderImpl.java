package com.team01.billage.config.jwt.impl;

import com.team01.billage.config.jwt.JwtProperties;
import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.TokenRedis;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.repository.TokenRedisRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.team01.billage.config.jwt.UserConstants.*;


@Component
@RequiredArgsConstructor
public class JwtProviderImpl {
    private final TokenRedisRepository tokenRedisRepository;
    private final JwtProperties jwtProperties;
    private final ErrorMvcAutoConfiguration errorMvcAutoConfiguration;
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.token.access-expires}")
    private long accessExpires;

    @Value("${jwt.token.refresh-expires}")
    private long refreshExpires;

    private Key key;

    private final UserDetailsService userDetailsService;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Authentication getAuthentication(String authToken) {
        Claims claims = extractAllClaims(authToken);
        String email = claims.get("email", String.class);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public AuthTokenImpl createAccessToken(
            Long userId,                // email -> userId(PK)로 변경
            UserRole role,
            Map<String, Object> claimsMap
    ) {
        Claims claims = new DefaultClaims(claimsMap);
        claims.put("type", ACCESS_TOKEN_TYPE_VALUE);
        return new AuthTokenImpl(
                userId.toString(),      // toString() 추가
                role,
                key,
                claims,
                new Date(System.currentTimeMillis() + accessExpires)
        );
    }

    public AuthTokenImpl createRefreshToken(
            Long userId,                // email -> userId(PK)로 변경
            UserRole role,
            Map<String, Object> claimsMap
    ) {
        Claims claims = new DefaultClaims(claimsMap);
        claims.put("type", REFRESH_TOKEN_TYPE_VALUE);
        return new AuthTokenImpl(
                userId.toString(),      // toString() 추가
                role,
                key,
                claims,
                new Date(System.currentTimeMillis() + refreshExpires)
        );
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Claims claims = extractAllClaims(refreshToken);
        Long userId = claims.get("accountId", Long.class);
        String email = claims.get("email", String.class);
        UserRole role = UserRole.valueOf(claims.get("role", String.class));

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("accountId", userId)
                .claim("email", email)
                .claim("role", role.name())
                .claim("type", "access_token")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpires))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }


    public boolean validateToken(String jwtToken) {
        return !isTokenExpired(jwtToken);
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractAllClaims(jwtToken).getExpiration();
    }

    public boolean validateAdminToken(String accessToken) {
        try {
            Claims claims = extractAllClaims(accessToken);
            String roleStr = claims.get("role", String.class);
            UserRole role = UserRole.valueOf(roleStr);
            return UserRole.ADMIN == role;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    //redis 에 리프레시토큰 정보를 가져와 엑세스 토큰 재발급
    public String generateAccessTokenFromRefreshTokenByRedis(String jwtAccessToken) {
        Claims claims = extractAllClaims(jwtAccessToken);
        Long userId = claims.get("accountId", Long.class);  // email 대신 accountId(PK) 사용

        // Redis에서 PK로 조회
        TokenRedis tokenInfo = tokenRedisRepository.findById(userId.toString())
                .orElse(null);

        if (tokenInfo == null) {
            return NOT_FOUND_REFRESH_TOKEN;
        }

        Claims claimsByRedis = extractAllClaims(tokenInfo.getRefreshToken());

        return Jwts.builder()
                .setSubject(userId.toString())  // userId를 subject로
                .claim("accountId", userId)
                .claim("email", claimsByRedis.get("email", String.class))
                .claim("role", claimsByRedis.get("role", String.class))
                .claim("type", "access_token")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessExpires()))
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}