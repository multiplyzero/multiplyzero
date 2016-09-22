package xyz.multiplyzero.zipkin.client;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import zipkin.Span;

public class LogSpanCollector extends AbstractSpanCollector {
    private static final Logger logger = Logger.getLogger(LogSpanCollector.class);

    @Override
    public void sendSpans(byte[] json) throws IOException {
        List<Span> spans = super.bytesToList(json);
        for (Span span : spans) {
            logger.info(span);
        }
    }

}
