package com.team01.billage.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt") //application.yml 파일에 jwt 파일이 주입됨.
public class JwtProperties {

    private String issuer;
    private String secretKey;
}
