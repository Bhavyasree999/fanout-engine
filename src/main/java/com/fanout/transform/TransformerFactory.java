package com.fanout.transform;

public class TransformerFactory {

    public static Transformer get(String type) {
        return switch (type) {
            case "rest" -> new JsonTransformer();
            case "grpc" -> new ProtobufTransformer();
            case "mq" -> new XmlTransformer();
            case "db" -> new AvroTransformer();
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }
}
