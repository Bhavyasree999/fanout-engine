package com.fanout.config;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class AppConfig {

    public String sourcePath;
    public int queueCapacity;

    public int restRate;
    public int grpcRate;
    public int mqRate;
    public int dbRate;

    @SuppressWarnings("unchecked")
    public static AppConfig load() {
        Yaml yaml = new Yaml();
        InputStream in = AppConfig.class
                .getClassLoader()
                .getResourceAsStream("application.yaml");

        Map<String, Object> obj = yaml.load(in);

        AppConfig config = new AppConfig();

        Map<String, Object> source = (Map<String, Object>) obj.get("source");
        config.sourcePath = (String) source.get("path");

        Map<String, Object> queue = (Map<String, Object>) obj.get("queue");
        config.queueCapacity = (int) queue.get("capacity");

        Map<String, Object> sinks = (Map<String, Object>) obj.get("sinks");

        config.restRate = (int)((Map<String,Object>)sinks.get("rest")).get("rateLimit");
        config.grpcRate = (int)((Map<String,Object>)sinks.get("grpc")).get("rateLimit");
        config.mqRate = (int)((Map<String,Object>)sinks.get("mq")).get("rateLimit");
        config.dbRate = (int)((Map<String,Object>)sinks.get("db")).get("rateLimit");

        return config;
    }
}
