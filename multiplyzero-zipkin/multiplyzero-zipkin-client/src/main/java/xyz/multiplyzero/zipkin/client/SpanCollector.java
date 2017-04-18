package xyz.multiplyzero.zipkin.client;

import zipkin.Span;

public interface SpanCollector {
    public void collect(final Span span);
}
