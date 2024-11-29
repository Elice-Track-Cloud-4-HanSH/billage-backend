package com.team01.billage.user.repository;

import com.team01.billage.user.domain.TokenRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRedisRepository extends CrudRepository<TokenRedis,String> {
    Optional<TokenRedis> findByRefreshToken(String accessToken);
}
