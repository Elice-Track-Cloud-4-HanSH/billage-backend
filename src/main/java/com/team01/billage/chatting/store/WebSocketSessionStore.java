package com.team01.billage.chatting.store;

import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessionStore {
    private static final ConcurrentHashMap<String, Long> sessionRegistry = new ConcurrentHashMap<>();

    public static void save(String sessionId, Long userId) {
        sessionRegistry.put(sessionId, userId);
    }

    public static Long get(String sessionId) {
        return sessionRegistry.get(sessionId);
    }

    public static void remove(String sessionId) {
        sessionRegistry.remove(sessionId);
    }
}
