package com.team01.billage.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class JwtProperties {

    private final String secret;

    private final long accessExpires;

    private final long refreshExpires;

    public JwtProperties(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.token.access-expires}") long accessExpires,
                         @Value("${jwt.token.refresh-expires}") long refreshExpires) {
        this.secret = secret;
        this.accessExpires = accessExpires;
        this.refreshExpires = refreshExpires;
    }
}