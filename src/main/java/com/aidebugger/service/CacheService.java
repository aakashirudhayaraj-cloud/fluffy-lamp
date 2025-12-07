package com.aidebugger.service;

import com.aidebugger.util.HashUtil;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    
    public String get(String key) {
        return cache.get(key);
    }
    
    public void put(String key, String value) {
        cache.put(key, value);
    }
    
    public boolean contains(String key) {
        return cache.containsKey(key);
    }
    
    public String generateKey(String language, String code, String context) {
        return HashUtil.sha256(language + "|" + code + "|" + (context == null ? "" : context));
    }
}