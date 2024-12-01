package com.team01.billage.config.jwt.impl;



import com.team01.billage.user.domain.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
@Slf4j
public class AuthTokenImpl {
    private final String token;
    private final Key key;

    public AuthTokenImpl(
            String userId,
            UserRole role,
            Key key,
            Claims claims,
            Date expiredDate
    ) {
        this.key = key;
        this.token = createJwtToken(userId, role, claims, expiredDate).get();
    }

    private Optional<String> createJwtToken(
            String userId,
            UserRole role,
            Map<String, Object> claimsMap,
            Date expiredDate
    ) {
        DefaultClaims claims = new DefaultClaims(claimsMap);
        claims.put("role", role);

        return Optional.ofNullable(Jwts.builder()
                .setSubject(userId)
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiredDate)
                .compact()
        );
    }
}
