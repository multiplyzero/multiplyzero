package xyz.multiplyzero.zipkin.client;

import java.io.IOException;
import java.util.List;

import zipkin.Span;

public class SystemSpanCollector extends SpanCollector {

    @Override
    public void sendSpans(byte[] json) throws IOException {
        List<Span> spans = super.bytesToList(json);
        for (Span span : spans) {
            System.err.println(span);
        }
    }

}
