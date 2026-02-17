package com.fanout.sink;

import com.google.common.util.concurrent.RateLimiter;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class RestSink implements Sink {

    private final RateLimiter limiter;
    private final Random random = new Random();

    public RestSink(int rate) {
        this.limiter = RateLimiter.create(rate);
    }

    public String name() { return "REST"; }

    public CompletableFuture<Boolean> send(Object data) {
        return CompletableFuture.supplyAsync(() -> {
            limiter.acquire();
            simulateDelay();
            return random.nextInt(10) != 0; // 10% failure
        });
    }

    private void simulateDelay() {
        try { Thread.sleep(20); } catch (Exception ignored) {}
    }
}
