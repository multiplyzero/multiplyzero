package xyz.multiplyzero.zipkin.client;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import zipkin.Codec;
import zipkin.Span;
import zipkin.internal.JsonCodec;

public abstract class SpanCollector implements Flushable, Closeable {
    private Codec codec = new JsonCodec();
    private final BlockingQueue<Span> PENDING = new LinkedBlockingQueue<>(1000);
    private final Flusher flusher;

    private final SpanMetrics metrics;

    protected SpanCollector() {
        this(1, new DefaultSpanMetrics());
    }

    protected SpanCollector(SpanMetrics metrics) {
        this(1, metrics);
    }

    protected SpanCollector(int flushInterval, SpanMetrics metrics) {
        this.metrics = metrics;
        this.flusher = flushInterval > 0 ? new Flusher(this, flushInterval) : new Flusher(this, 1);
    }

    public void collect(Span span) {
        metrics.incrementAcceptedSpans(1);
        if (!PENDING.offer(span)) {
            metrics.incrementDroppedSpans(1);
        }
    }

    public abstract void sendSpans(byte[] json) throws IOException;

    protected void reportSpans(List<Span> spans) throws IOException {
        byte[] encoded = codec.writeSpans(spans);
        sendSpans(encoded);
    }

    @Override
    public void flush() {
        if (PENDING.isEmpty()) {
            return;
        }
        List<Span> drained = new ArrayList<>(PENDING.size());
        PENDING.drainTo(drained);
        if (drained.isEmpty()) {
            return;
        }

        int spanCount = drained.size();
        try {
            reportSpans(drained);
        } catch (Exception e) {
            metrics.incrementDroppedSpans(spanCount);
        }
    }

    public List<Span> bytesToList(byte[] bs) {
        return codec.readSpans(bs);
    }

    @Override
    public void close() {
        if (flusher != null) {
            flusher.scheduler.shutdown();
        }
        int dropped = PENDING.drainTo(new LinkedList<Span>());
        metrics.incrementDroppedSpans(dropped);
    }

    static final class Flusher implements Runnable {
        final Flushable flushable;
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Flusher(Flushable flushable, int flushInterval) {
            this.flushable = flushable;
            this.scheduler.scheduleWithFixedDelay(this, 0, flushInterval, SECONDS);
        }

        @Override
        public void run() {
            try {
                flushable.flush();
            } catch (IOException ignored) {
            }
        }
    }
}
