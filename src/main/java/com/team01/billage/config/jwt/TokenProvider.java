package com.team01.billage.config.jwt;

import com.team01.billage.user.domain.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

    @RequiredArgsConstructor
    @Service
    public class TokenProvider {

        private final JwtProperties jwtProperties;

        public String generateToken(Users user, Duration expiredAt) {
            Date now = new Date();
            return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
        }

        private String makeToken(Date expiry, Users user) {
            Date now = new Date();

            return Jwts.builder()
                    .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                    .setIssuer(jwtProperties.getIssuer())
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .setSubject(user.getEmail())  // email을 주요 식별자로 사용
                    .claim("id", user.getId())    // DB ID도 포함
                    .claim("nickname", user.getNickname())
                    .claim("role", user.getRole().name())
                    .claim("provider", user.getProvider().name())
                    .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                    .compact();
        }

        //토큰 검증
        public boolean validToken(String token) {
            try {
                // 요청된 토큰을 파싱하여 유효한지 검증
                Jwts.parser()
                        .setSigningKey(jwtProperties.getSecretKey())    //비밀키를 사용하여 서명 검증
                        .parseClaimsJws(token);

                return true;    //유효할 경우 true 반환
            } catch (Exception e) {
                return false;   //유효하지 않을 경우 false 반환
            }
        }

        //Spring Security 인증 객체 생성
        public Authentication getAuthentication(String token) {
            Claims claims = getClaims(token);   //토큰에서 클레임을 가져온다.

            // 사용자 권한 설정
            Set<SimpleGrantedAuthority> authorities
                    = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

            //Spring Security 에서 제공하는 User 인증정보를 사용 하여 Authentication 객체 생성
            return new UsernamePasswordAuthenticationToken(
                    new org.springframework.security.core.userdetails.User(
                            claims.getSubject(), "", authorities), token, authorities
            );
        }

        //토큰에서 클레임만 파싱
        public Claims getClaims(String token) {     //TODO: 나중에 Private로
            return Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())        //비밀키 사용해 토큰 파싱
                    .parseClaimsJws(token)      //토큰에서 JWT의 본문 가져옴
                    .getBody(); //클레임 반환
        }

    }
