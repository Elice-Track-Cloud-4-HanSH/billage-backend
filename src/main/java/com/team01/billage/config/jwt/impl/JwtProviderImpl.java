package com.team01.billage.config.jwt.impl;

import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

import static com.team01.billage.config.jwt.UserConstants.ACCESS_TOKEN_TYPE_VALUE;
import static com.team01.billage.config.jwt.UserConstants.REFRESH_TOKEN_TYPE_VALUE;



@Component
@RequiredArgsConstructor
public class JwtProviderImpl {
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
        String email = extractEmail(authToken); // username 대신 email 사용
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public AuthTokenImpl createAccessToken(
            String email,
            UserRole role,
            Map<String, Object> claimsMap
    ) {
        Claims claims = new DefaultClaims(claimsMap);
        claims.put("type", ACCESS_TOKEN_TYPE_VALUE);
        return new AuthTokenImpl(
                email,
                role,
                key,
                claims,
                new Date(System.currentTimeMillis() + accessExpires)
        );
    }

    public AuthTokenImpl createRefreshToken(
            String email,
            UserRole role,
            Map<String, Object> claimsMap
    ) {
        Claims claims = new DefaultClaims(claimsMap);
        claims.put("type", REFRESH_TOKEN_TYPE_VALUE);
        return new AuthTokenImpl(
                email,
                role,
                key,
                claims,
                new Date(System.currentTimeMillis() + refreshExpires)
        );
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
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

    public boolean validateToken(String jwtToken) {
        return !isTokenExpired(jwtToken);
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractAllClaims(jwtToken).getExpiration();
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        Claims claims = extractAllClaims(refreshToken);
        String email = claims.getSubject();
        String nickname = claims.get("nickname", String.class);
        Long id = claims.get("id", Long.class);
        UserRole role = UserRole.valueOf(claims.get("role", String.class));
        Provider provider = Provider.valueOf(claims.get("provider", String.class));

        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .claim("nickname", nickname)
                .claim("role", role.name())
                .claim("provider", provider.name())
                .claim("type", "access_token")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessExpires))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean validateAdminToken(String accessToken) {
        try {
            Claims claims = extractAllClaims(accessToken);
            String roleStr = claims.get("role", String.class);
            UserRole role = UserRole.valueOf(roleStr);
            return UserRole.ADMIN == role;
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT signature");
        } catch (Exception e) {
            throw new RuntimeException("Token validation error: " + e.getMessage());
        }
    }
}