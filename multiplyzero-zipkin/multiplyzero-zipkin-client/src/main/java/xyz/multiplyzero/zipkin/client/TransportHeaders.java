package xyz.multiplyzero.zipkin.client;

import lombok.Getter;

public enum TransportHeaders {
    /**
     * Trace id http header field name.
     */
    TraceId("X-B3-TraceId"),
    /**
     * Span id http header field name.
     */
    SpanId("X-B3-SpanId"),
    /**
     * Parent span id http header field name.
     */
    ParentSpanId("X-B3-ParentSpanId"),
    /**
     * Sampled http header field name. Indicates if this trace should be sampled
     * or not.
     */
    Sampled("X-B3-Sampled");

    @Getter
    private final String name;

    private TransportHeaders(final String name) {
        this.name = name;
    }

}
