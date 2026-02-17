package com.fanout.transform;

public class XmlTransformer implements Transformer {
    public Object transform(String input) {
        return "<record>" + input + "</record>";
    }
}
