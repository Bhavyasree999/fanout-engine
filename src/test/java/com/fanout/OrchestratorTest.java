package com.fanout;

import com.fanout.metrics.Metrics;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrchestratorTest {

    @Test
    void testMetricsIncrement() {
        Metrics metrics = new Metrics();
        metrics.record("rest", true);

        assertEquals(1, metrics.total.get());
        assertEquals(1, metrics.success.get("rest").get());
    }
}
