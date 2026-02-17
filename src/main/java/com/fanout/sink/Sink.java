package com.fanout.sink;

import java.util.concurrent.CompletableFuture;

public interface Sink {
    CompletableFuture<Boolean> send(Object data);
    String name();
}
