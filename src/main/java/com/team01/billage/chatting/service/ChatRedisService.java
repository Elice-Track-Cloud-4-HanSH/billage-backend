package com.team01.billage.chatting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ChatRedisService {
    private final RedisTemplate<String, String> redisTemplate;

    private String createKey(Long chatroomId, Long senderId) {
        return chatroomId + "_" + senderId;
    }

    public void increaseUnreadChatCount(Long chatroomId, Long senderId) {
        String key = createKey(chatroomId, senderId);
        increaseUnreadChatCount(key);
    }

    public void increaseUnreadChatCount(String key) {
        String val = redisTemplate.opsForValue().get(key);
        int count = Integer.parseInt(val != null ? val : "0");

        redisTemplate.opsForValue().set(key, String.format("%d", ++count));
    }

    public void resetUnreadChatCount(Long chatroomId, Long senderId) {
        String key = createKey(chatroomId, senderId);
        resetUnreadChatCount(key);
    }

    public void resetUnreadChatCount(String key) {
        redisTemplate.opsForValue().set(key, "0");
    }

    public Long getUnreadChatCount(String key) {
        String val = redisTemplate.opsForValue().get(key);
        return Long.parseLong(val != null ? val : "0");
    }

    public void setUnreadChatCount(String key) {
        redisTemplate.opsForValue().set(key, "0");
    }

    public void setUnreadChatCount(String key, Long count) {
        setUnreadChatCount(key, count.toString());
    }

    public void setUnreadChatCount(String key, String count) {
        redisTemplate.opsForValue().set(key, count);
    }

    public Set<String> getKeysByPattern(String pattern, int maxKeysSize) {
        Set<String> results = new HashSet<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern + "*").build();
        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(scanOptions)){
            while (cursor.hasNext() && results.size() < maxKeysSize) {
                byte[] keyBytes = cursor.next();
                String key = new String(keyBytes, StandardCharsets.UTF_8);
                if (!key.isEmpty()) {
                    results.add(key);
                }
            }
        }
        catch (Exception e) {
            System.out.println("redis key scan error: " + e);
        }
        return results;
    }
}
