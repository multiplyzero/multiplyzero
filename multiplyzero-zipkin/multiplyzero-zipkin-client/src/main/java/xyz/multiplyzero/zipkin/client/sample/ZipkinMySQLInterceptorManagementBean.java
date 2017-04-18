package xyz.multiplyzero.zipkin.client.sample;

import java.io.Closeable;
import java.io.IOException;

import xyz.multiplyzero.zipkin.client.ZeroZipkin;

public class ZipkinMySQLInterceptorManagementBean implements Closeable {

    public ZipkinMySQLInterceptorManagementBean(final ZeroZipkin zeroZipkin) {
        this(zeroZipkin, null);
    }

    public ZipkinMySQLInterceptorManagementBean(final ZeroZipkin zeroZipkin, String serviceName) {
        ZipkinMySQLInterceptor.setZeroZipkin(zeroZipkin);
        ZipkinMySQLInterceptor.setServiceName(serviceName);
    }

    @Override
    public void close() throws IOException {
        ZipkinMySQLInterceptor.setZeroZipkin(null);
    }
}
