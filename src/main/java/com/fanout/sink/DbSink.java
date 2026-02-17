package com.fanout.sink;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class DbSink implements Sink {

    private final RateLimiter rateLimiter;
    private final Random random = new Random();

    public DbSink(int rateLimit) {
        this.rateLimiter = RateLimiter.create(rateLimit);
    }

    @Override
    public String name() {
        return "db";
    }

    @Override
    public CompletableFuture<Boolean> send(Object data) {
        return CompletableFuture.supplyAsync(() -> {
            rateLimiter.acquire(); // throttle
            simulateDbWrite();
            return simulateSuccess();
        });
    }

    private void simulateDbWrite() {
        try {
            Thread.sleep(25); // simulate async DB upsert
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean simulateSuccess() {
        return random.nextInt(100) >= 8; // 8% failure rate
    }
}
