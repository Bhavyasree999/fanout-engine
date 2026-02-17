package com.fanout;

import com.fanout.config.AppConfig;
import com.fanout.ingestion.FileProducer;
import com.fanout.orchestrator.FanOutOrchestrator;
import com.fanout.sink.*;

import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {

        AppConfig config = AppConfig.load();

        BlockingQueue<com.fanout.model.Record> queue =
                new ArrayBlockingQueue<>(config.queueCapacity);

        var sinks = List.of(
                new RestSink(config.restRate),
                new GrpcSink(config.grpcRate),
                new MessageQueueSink(config.mqRate),
                new DbSink(config.dbRate)
        );

        FanOutOrchestrator orchestrator =
                new FanOutOrchestrator(queue, sinks);

        Thread producerThread =
                new Thread(new FileProducer(config.sourcePath, queue));
        producerThread.start();

        // Start processing
        orchestrator.start();

        // Wait for producer to finish
        try {
            producerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Small delay to allow async tasks to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Print final metrics manually
        var m = orchestrator.getMetrics();
        System.out.println("Total: " + m.total.get());
        System.out.println("Throughput (records/sec): " + 
        String.format("%.2f", m.getThroughputPerSecond()));
        m.success.forEach((k,v) ->
                System.out.println(k + " Success: " + v.get()));
        m.failure.forEach((k,v) ->
                System.out.println(k + " Failure: " + v.get()));
        System.out.println("----");

        System.out.println("Processing Completed. Application Shutting Down.");
    }
}
