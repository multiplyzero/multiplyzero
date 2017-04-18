package xyz.multiplyzero.zipkin.client;

import java.util.Stack;

import zipkin.Span;

public class ThreadLocalSpanStore implements SpanStore {

    public static ThreadLocal<Stack<Span.Builder>> LOCAL_SPAN = new ThreadLocal<Stack<Span.Builder>>() {
        @Override
        protected Stack<Span.Builder> initialValue() {
            return new Stack<>();
        }
    };

    @Override
    public Span.Builder getSpan() {
        return LOCAL_SPAN.get().empty() ? null : LOCAL_SPAN.get().peek();
    }

    @Override
    public void setSpan(Span.Builder span) {
        if (span == null) {
            if (!LOCAL_SPAN.get().empty()) {
                LOCAL_SPAN.get().pop();
            }
        } else {
            LOCAL_SPAN.get().push(span);
        }
    }

    @Override
    public void removeSpan() {
        if (!LOCAL_SPAN.get().empty()) {
            LOCAL_SPAN.get().pop();
        }
    }
}
