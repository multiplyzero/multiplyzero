package xyz.multiplyzero.zipkin.client;

public interface SpanMetrics {
    void incrementAcceptedSpans(int quantity);

    void incrementDroppedSpans(int quantity);
}
