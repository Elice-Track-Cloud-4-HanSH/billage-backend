package com.team01.billage.user.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "token")
public class TokenRedis {
    @Id
    private Long id;

    private String refreshToken;

    @TimeToLive
    private Long timeToLive;

}