package com.team01.billage.user.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "token", timeToLive = 60 * 60 * 24 * 7)  //리프레시토큰과 expiretime 일치
//@RedisHash(value = "token", timeToLive = 30)  //ttl 테스트
// token:@Id 형태로 db에 저장
public class TokenRedis {
    @Id
    private Long id;    //long 해도 되나

    private String refreshToken;
}
