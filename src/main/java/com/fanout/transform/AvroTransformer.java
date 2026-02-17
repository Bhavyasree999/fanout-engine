package com.fanout.transform;

public class AvroTransformer implements Transformer {
    public Object transform(String input) {
        return "AVRO(" + input + ")";
    }
}
