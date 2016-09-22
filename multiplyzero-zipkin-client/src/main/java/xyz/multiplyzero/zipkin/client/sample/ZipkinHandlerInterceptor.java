package xyz.multiplyzero.zipkin.client.sample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import xyz.multiplyzero.zipkin.client.TraceKeys;
import xyz.multiplyzero.zipkin.client.ZeroZipkin;
import xyz.multiplyzero.zipkin.client.utils.ZipkinUtils;
import zipkin.Endpoint;

/**
 * 
 * ServletHandlerInterceptor
 * 
 * @author zhanxiaoyong
 *
 * @since 2016年9月19日 下午4:53:39
 */
public class ZipkinHandlerInterceptor extends HandlerInterceptorAdapter {
    private static final String HTTP_ATTRIBUTE_ENDPOINT = ZipkinHandlerInterceptor.class.getName() + ".endpoint";

    private ZeroZipkin zeroZipkin;

    private String serviceName;

    @Autowired
    public ZipkinHandlerInterceptor(ZeroZipkin zeroZipkin, String serviceName) {
        this.zeroZipkin = zeroZipkin;
        this.serviceName = serviceName;
    }

    @Autowired
    public ZipkinHandlerInterceptor(ZeroZipkin zeroZipkin) {
        this(zeroZipkin, "web");
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler) {
        Endpoint endpoint = Endpoint.create(serviceName, ZipkinUtils.ipToInt(request.getLocalAddr()),
                request.getLocalPort());
        zeroZipkin.startSpan(request.getMethod());
        zeroZipkin.sendAnnotation(TraceKeys.SERVER_RECV, endpoint);

        request.setAttribute(HTTP_ATTRIBUTE_ENDPOINT, endpoint);
        return true;
    }

    @Override
    public void afterConcurrentHandlingStarted(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler) {
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler, final Exception ex) {
        final Endpoint endpoint = (Endpoint) request.getAttribute(HTTP_ATTRIBUTE_ENDPOINT);
        zeroZipkin.sendAnnotation(TraceKeys.SERVER_SEND, endpoint);
        zeroZipkin.sendBinaryAnnotation(TraceKeys.HTTP_STATUS_CODE, response.getStatus() + "", endpoint);
        zeroZipkin.sendBinaryAnnotation(TraceKeys.HTTP_URL, request.getRequestURI(), endpoint);
        zeroZipkin.finishSpan();
    }

}
