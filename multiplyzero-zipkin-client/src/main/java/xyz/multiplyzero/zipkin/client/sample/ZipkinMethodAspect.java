package xyz.multiplyzero.zipkin.client.sample;

import java.util.Map;

import lombok.Setter;
import xyz.multiplyzero.zipkin.client.TraceKeys;
import xyz.multiplyzero.zipkin.client.ZeroZipkin;
import xyz.multiplyzero.zipkin.client.utils.InetAddressUtils;
import xyz.multiplyzero.zipkin.client.utils.MethodDone;
import xyz.multiplyzero.zipkin.client.utils.ZipkinUtils;
import zipkin.Endpoint;

public class ZipkinMethodAspect {
    @Setter
    private ZeroZipkin zeroZipkin;
    @Setter
    private String serviceName;

    public <R> R methodAspect(MethodDone<R> method, String spanName, Map<String, String> keyValues) throws Throwable {
        int ipv4 = InetAddressUtils.localIpv4();
        Endpoint endpoint = Endpoint.create(serviceName, ipv4, 0);
        zeroZipkin.startSpan(spanName);
        zeroZipkin.sendAnnotation(TraceKeys.CLIENT_SEND, endpoint);
        if (keyValues != null) {
            for (Map.Entry<String, String> keyValue : keyValues.entrySet()) {
                zeroZipkin.sendBinaryAnnotation(keyValue.getKey(), keyValue.getValue(), endpoint);
            }
        }
        try {
            R r = method.done();
            methodFinally(null, endpoint);
            return r;
        } catch (Throwable e) {
            methodFinally(e, endpoint);
            throw e;
        }
    }

    public <R> R methodAspect(MethodDone<R> method, String spanName) throws Throwable {
        return this.methodAspect(method, spanName, null);
    }

    private void methodFinally(Throwable e, Endpoint endpoint) {
        zeroZipkin.sendAnnotation(TraceKeys.CLIENT_RECV, endpoint);
        if (e != null) {
            zeroZipkin.sendBinaryAnnotation(serviceName + TraceKeys.SUFFIX_ERROR, ZipkinUtils.errorToString(e),
                    endpoint);
            zeroZipkin.sendBinaryAnnotation(serviceName + TraceKeys.SUFFIX_BACK, TraceKeys.RESULT_FAILED, endpoint);
        } else {
            zeroZipkin.sendBinaryAnnotation(serviceName + TraceKeys.SUFFIX_BACK, TraceKeys.RESULT_SUCCESS, endpoint);
        }
        zeroZipkin.finishSpan();
    }
}
