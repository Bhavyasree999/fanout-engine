package com.fanout.metrics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Metrics {

    public AtomicLong total = new AtomicLong();
    public ConcurrentHashMap<String, AtomicLong> success = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, AtomicLong> failure = new ConcurrentHashMap<>();

    private final long startTime = System.currentTimeMillis();

    public void record(String sink, boolean ok) {
        total.incrementAndGet();

        success.putIfAbsent(sink, new AtomicLong());
        failure.putIfAbsent(sink, new AtomicLong());

        if (ok) success.get(sink).incrementAndGet();
        else failure.get(sink).incrementAndGet();
    }

    public double getThroughputPerSecond() {
        long elapsedMillis = System.currentTimeMillis() - startTime;
        if (elapsedMillis == 0) return 0;
        return (total.get() * 1000.0) / elapsedMillis;
    }
}
