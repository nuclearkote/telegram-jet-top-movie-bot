package ru.poseidonnet.jet_movie_top_bot.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class WaitArgsService {

    private final Map<Long, CompletableFuture<String>> futureMap = new ConcurrentHashMap<>();

    public String waitForArgs(Long userId, int timeout) throws Exception {
        try {
            return futureMap.computeIfAbsent(userId, u -> new CompletableFuture<>()).get(timeout, TimeUnit.SECONDS);
        } finally {
            futureMap.remove(userId);
        }
    }

    public boolean isWaiting(Long userId) {
        return futureMap.containsKey(userId);
    }

    public void completeFuture(Long userId, String args) {
        CompletableFuture<String> future = futureMap.get(userId);
        if (future != null) {
            future.complete(args);
        }
    }

}
