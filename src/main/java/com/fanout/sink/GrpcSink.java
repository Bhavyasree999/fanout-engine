package com.fanout.sink;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class GrpcSink implements Sink {

    private final RateLimiter rateLimiter;
    private final Random random = new Random();

    public GrpcSink(int rateLimit) {
        this.rateLimiter = RateLimiter.create(rateLimit);
    }

    @Override
    public String name() {
        return "grpc";
    }

    @Override
    public CompletableFuture<Boolean> send(Object data) {
        return CompletableFuture.supplyAsync(() -> {
            rateLimiter.acquire();  // throttle
            simulateNetworkDelay();
            return simulateSuccess();
        });
    }

    private void simulateNetworkDelay() {
        try {
            Thread.sleep(15); // simulate gRPC streaming delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean simulateSuccess() {
        return random.nextInt(100) >= 10; // 10% failure rate
    }
}
