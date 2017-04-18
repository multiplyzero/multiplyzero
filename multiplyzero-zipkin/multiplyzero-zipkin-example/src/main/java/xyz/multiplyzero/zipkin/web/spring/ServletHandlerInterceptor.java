package xyz.multiplyzero.zipkin.web.spring;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.ServerRequestInterceptor;
import com.github.kristofa.brave.ServerResponseInterceptor;
import com.github.kristofa.brave.ServerSpan;
import com.github.kristofa.brave.ServerSpanThreadBinder;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.HttpResponse;
import com.github.kristofa.brave.http.HttpServerRequest;
import com.github.kristofa.brave.http.HttpServerRequestAdapter;
import com.github.kristofa.brave.http.HttpServerResponseAdapter;
import com.github.kristofa.brave.http.SpanNameProvider;

/**
 * 
 * ServletHandlerInterceptor
 * 
 * @author zhanxiaoyong
 *
 * @since 2016年9月19日 下午4:53:39
 */
public class ServletHandlerInterceptor extends HandlerInterceptorAdapter {
    static final String HTTP_SERVER_SPAN_ATTRIBUTE = ServletHandlerInterceptor.class.getName() + ".server-span";

    private final ServerRequestInterceptor serverRequestInterceptor;
    private final ServerResponseInterceptor serverResponseInterceptor;
    private final ServerSpanThreadBinder serverSpanThreadBinder;
    private final SpanNameProvider spanNameProvider;

    private Brave brave;

    public ServletHandlerInterceptor(Brave brave) {
        this.brave = brave;
        this.serverRequestInterceptor = this.brave.serverRequestInterceptor();
        this.serverResponseInterceptor = this.brave.serverResponseInterceptor();
        this.serverSpanThreadBinder = this.brave.serverSpanThreadBinder();
        this.spanNameProvider = new DefaultSpanNameProvider();
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler) {
        serverRequestInterceptor.handle(new HttpServerRequestAdapter(new HttpServerRequest() {
            @Override
            public String getHttpHeaderValue(String headerName) {
                return request.getHeader(headerName);
            }

            @Override
            public URI getUri() {
                try {
                    return new URI(request.getRequestURI());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String getHttpMethod() {
                return request.getMethod();
            }
        }, spanNameProvider));

        return true;
    }

    @Override
    public void afterConcurrentHandlingStarted(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler) {
        request.setAttribute(HTTP_SERVER_SPAN_ATTRIBUTE, serverSpanThreadBinder.getCurrentServerSpan());
        serverSpanThreadBinder.setCurrentSpan(null);
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
            final Object handler, final Exception ex) {

        final ServerSpan span = (ServerSpan) request.getAttribute(HTTP_SERVER_SPAN_ATTRIBUTE);

        if (span != null) {
            serverSpanThreadBinder.setCurrentSpan(span);
        }

        serverResponseInterceptor.handle(new HttpServerResponseAdapter(new HttpResponse() {
            @Override
            public int getHttpStatusCode() {
                return response.getStatus();
            }
        }));
    }

}
