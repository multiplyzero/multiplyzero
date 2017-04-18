package xyz.multiplyzero.zipkin.mysql;

import java.io.Closeable;
import java.io.IOException;

import com.github.kristofa.brave.ClientTracer;

/**
 * A simple bean whose only purpose in life is to manage the lifecycle of the
 * {@linkplain ClientTracer} in the {@linkplain MySQLStatementInterceptor}.
 */
public class MySQLStatementInterceptorManagementBean implements Closeable {

    public MySQLStatementInterceptorManagementBean(final ClientTracer tracer) {
        MySQLStatementInterceptor.setClientTracer(tracer);
    }

    @Override
    public void close() throws IOException {
        MySQLStatementInterceptor.setClientTracer(null);
    }
}
