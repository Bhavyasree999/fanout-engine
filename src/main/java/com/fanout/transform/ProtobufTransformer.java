package com.fanout.transform;

public class ProtobufTransformer implements Transformer {
    public Object transform(String input) {
        return "PROTOBUF(" + input + ")";
    }
}
