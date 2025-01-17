package com.team01.billage.chatting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ChatRedisService {
    private final RedisTemplate<String, String> redisTemplate;

    private String createKey(Long chatroomId, Long senderId) {
        return chatroomId + "_" + senderId;
    }

    @Async
    public void increaseUnreadChatCount(Long chatroomId, Long senderId) {
        String key = createKey(chatroomId, senderId);
        increaseUnreadChatCount(key);
    }

    @Async
    public void increaseUnreadChatCount(String key) {
        String val = redisTemplate.opsForValue().get(key);
        int count = Integer.parseInt(val != null ? val : "0");

        redisTemplate.opsForValue().set(key, String.format("%d", ++count));
    }

    @Async
    public void resetUnreadChatCount(Long chatroomId, Long senderId) {
        String key = createKey(chatroomId, senderId);
        resetUnreadChatCount(key);
    }

    @Async
    public void resetUnreadChatCount(String key) {
        redisTemplate.opsForValue().set(key, "0");
    }

    public Long getUnreadChatCount(String key) {
        String val = redisTemplate.opsForValue().get(key);
        return Long.parseLong(val != null ? val : "0");
    }

    public Map<String, Long> getUnreadChatsCount(List<String> keys) {
        List<Long> chatsCount  = getMultipleValues(keys).stream()
                .map(value -> Long.parseLong(value != null ? value : "0"))
                .toList();
        Map<String, Long> resultMap = new HashMap<>();

        for (int i = 0; i < keys.size(); i++) {
            resultMap.put(keys.get(i), chatsCount.get(i));
        }
        return resultMap;
    }

    public List<String> getMultipleValues(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    @Async
    public void setUnreadChatCount(String key) {
        setUnreadChatCount(key, "0");
    }

    @Async
    public void setUnreadChatCount(String key, Long count) {
        setUnreadChatCount(key, count.toString());
    }

    @Async
    public void setUnreadChatCount(String key, String count) {
        redisTemplate.opsForValue().set(key, count);
    }

    public Set<String> getKeysByPattern(String pattern, int maxKeysSize) {
        Set<String> results = new HashSet<>();
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).build();
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

    public Long sumOfKeysValue(String pattern, Long maxGet) {
        Set<String> keys = getKeysByPattern(pattern, maxGet.intValue());
        return keys.stream().mapToLong(this::getUnreadChatCount).sum();
    }
}
