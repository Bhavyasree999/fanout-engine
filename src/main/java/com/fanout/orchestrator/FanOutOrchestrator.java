package com.fanout.orchestrator;

import com.fanout.metrics.Metrics;
import com.fanout.model.Record;
import com.fanout.sink.Sink;
import com.fanout.transform.Transformer;
import com.fanout.transform.TransformerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class FanOutOrchestrator {

    private final BlockingQueue<Record> queue;
    private final List<Sink> sinks;
    private final Metrics metrics = new Metrics();

    // Dead Letter Queue
    private final List<Record> deadLetterQueue = new CopyOnWriteArrayList<>();

    public FanOutOrchestrator(BlockingQueue<Record> queue, List<Sink> sinks) {
        this.queue = queue;
        this.sinks = sinks;
    }

    public void start() {

        ExecutorService executor =
                Executors.newFixedThreadPool(
                        Runtime.getRuntime().availableProcessors()
                );

        boolean running = true;

        while (running) {
            try {
                Record record = queue.take();

                // Stop signal
                if ("EOF".equals(record.raw())) {
                    running = false;
                    break;
                }

                for (Sink sink : sinks) {
                    executor.submit(() -> process(record, sink));
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void process(Record record, Sink sink) {

        Transformer transformer =
                TransformerFactory.get(sink.name());

        Object transformed = transformer.transform(record.raw());

        boolean success = false;

        for (int i = 0; i < 3; i++) {
            try {
                boolean ok = sink.send(transformed).join();
                metrics.record(sink.name(), ok);

                if (ok) {
                    success = true;
                    break;
                }

            } catch (Exception ignored) {
            }
        }

        // If failed after 3 retries → add to DLQ
        if (!success) {
            deadLetterQueue.add(record);
        }
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public int getDeadLetterCount() {
        return deadLetterQueue.size();
    }
}
