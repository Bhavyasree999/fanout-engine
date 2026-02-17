package com.fanout.sink;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class MessageQueueSink implements Sink {

    private final RateLimiter rateLimiter;
    private final Random random = new Random();

    public MessageQueueSink(int rateLimit) {
        this.rateLimiter = RateLimiter.create(rateLimit);
    }

    @Override
    public String name() {
        return "mq";
    }

    @Override
    public CompletableFuture<Boolean> send(Object data) {
        return CompletableFuture.supplyAsync(() -> {
            rateLimiter.acquire(); // throttle
            simulatePublishDelay();
            return simulateSuccess();
        });
    }

    private void simulatePublishDelay() {
        try {
            Thread.sleep(10); // simulate Kafka publish delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean simulateSuccess() {
        return random.nextInt(100) >= 5; // 5% failure rate
    }
}
