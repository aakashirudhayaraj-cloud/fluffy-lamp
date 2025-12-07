package com.aidebugger.security;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RedisTokenBlacklistService {
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public void blacklistToken(String token, long ttlMillis) {
        blacklist.add(token);
        // Simple cleanup after TTL (approximate)
        if (ttlMillis > 0) {
            new Thread(() -> {
                try {
                    Thread.sleep(ttlMillis);
                    blacklist.remove(token);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}