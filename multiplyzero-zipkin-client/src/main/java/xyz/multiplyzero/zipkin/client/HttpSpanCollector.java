package xyz.multiplyzero.zipkin.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPOutputStream;

import lombok.Getter;

public class HttpSpanCollector extends SpanCollector {
    @Getter
    private String zipkinHost;
    private String url;
    private boolean compressionEnabled;

    public HttpSpanCollector(String zipkinHost) {
        this(zipkinHost, new DefaultSpanMetrics());
    }

    public HttpSpanCollector(String zipkinHost, SpanMetrics metrics) {
        super(metrics);
        this.url = zipkinHost + (zipkinHost.endsWith("/") ? "" : "/") + "api/v1/spans";
    }

    public void sendSpans(byte[] json) throws IOException {
        // intentionally not closing the connection, so as to use keep-alives
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(10 * 1000);
        connection.setReadTimeout(60 * 1000);
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Content-Type", "application/json");
        if (compressionEnabled) {
            connection.addRequestProperty("Content-Encoding", "gzip");
            ByteArrayOutputStream gzipped = new ByteArrayOutputStream();
            try (GZIPOutputStream compressor = new GZIPOutputStream(gzipped)) {
                compressor.write(json);
            }
            json = gzipped.toByteArray();
        }
        connection.setDoOutput(true);
        connection.setFixedLengthStreamingMode(json.length);
        connection.getOutputStream().write(json);

        try (InputStream in = connection.getInputStream()) {
            while (in.read() != -1)
                ; // skip
        } catch (IOException e) {
            try (InputStream err = connection.getErrorStream()) {
                if (err != null) { // possible, if the connection was dropped
                    while (err.read() != -1)
                        ; // skip
                }
            }
            throw e;
        }
    }
}
