package com.fanout.transform;

public class JsonTransformer implements Transformer {
    public Object transform(String input) {
        return "{\"data\":\"" + input + "\"}";
    }
}
