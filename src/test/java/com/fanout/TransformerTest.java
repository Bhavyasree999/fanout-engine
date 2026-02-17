package com.fanout;

import com.fanout.transform.JsonTransformer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransformerTest {

    @Test
    void testJsonTransformation() {
        JsonTransformer transformer = new JsonTransformer();
        Object result = transformer.transform("test");

        assertTrue(result.toString().contains("test"));
    }
}
