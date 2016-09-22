package xyz.multiplyzero.zipkin.client;

import org.apache.log4j.Logger;

import zipkin.Annotation;
import zipkin.BinaryAnnotation;
import zipkin.Endpoint;
import zipkin.Span;

public class ZeroZipkin {
    private static final Logger logger = Logger.getLogger(ZeroZipkin.class);
    private SpanCollector spanCollector;
    private SpanStore spanStore;

    public ZeroZipkin(String zipkinHost) {
        this.spanCollector = new HttpSpanCollector(zipkinHost);
        this.spanStore = new ThreadLocalSpanStore();
    }

    public Span startSpan(Long id, Long traceId, Long parentId, String name) {
        if (id != null && traceId != null && parentId != null) {
            Span.Builder builder = Span.builder().id(id).traceId(traceId).parentId(parentId).name(name)
                    .timestamp(nanoTime());
            spanStore.setSpan(builder);
            return builder.build();
        } else {
            return this.startSpan(name);
        }

    }

    public Span startSpan(String name) {
        long id = GenerateKey.generateKey();
        try {
            Span.Builder parentSpan = spanStore.getSpan();
            Span.Builder builder = Span.builder().id(id).traceId(id).name(name).timestamp(nanoTime());
            if (parentSpan != null) {
                Span span = parentSpan.build();
                builder.traceId(span.traceId).parentId(span.id);
            }
            spanStore.setSpan(builder);
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return Span.builder().id(id).traceId(id).timestamp(nanoTime()).build();
        }
    }

    public void sendAnnotation(String value, Endpoint endpoint) {
        try {
            Span.Builder span = spanStore.getSpan();
            span.addAnnotation(Annotation.create(nanoTime(), value, endpoint));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public void sendBinaryAnnotation(String key, String value, Endpoint endpoint) {
        try {
            Span.Builder span = spanStore.getSpan();
            span.addBinaryAnnotation(BinaryAnnotation.create(key, value, endpoint));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }

    }

    public void finishSpan() {
        try {
            Span.Builder span = spanStore.getSpan();
            if (span != null) {
                long duration = nanoTime() - span.build().timestamp;
                spanCollector.collect(span.duration(duration).build());
            } else {
                logger.error("you must use startSpan before finishSpan");
            }
            spanStore.removeSpan();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public static Long nanoTime() {
        return System.currentTimeMillis() * 1000;
    }

}
