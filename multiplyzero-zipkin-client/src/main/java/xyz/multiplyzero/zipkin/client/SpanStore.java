package xyz.multiplyzero.zipkin.client;

import zipkin.Span;

public interface SpanStore {
    public Span.Builder getSpan();

    public void setSpan(Span.Builder span);
    
    public void removeSpan();
}
