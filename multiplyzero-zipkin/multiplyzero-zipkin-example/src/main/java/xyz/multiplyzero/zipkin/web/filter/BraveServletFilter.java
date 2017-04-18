package xyz.multiplyzero.zipkin.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.github.kristofa.brave.ServerRequestInterceptor;
import com.github.kristofa.brave.ServerResponseInterceptor;
import com.github.kristofa.brave.http.HttpResponse;
import com.github.kristofa.brave.http.HttpServerRequestAdapter;
import com.github.kristofa.brave.http.HttpServerResponseAdapter;
import com.github.kristofa.brave.http.SpanNameProvider;

/**
 * 
 * web.xml
 * 
 * <pre>
 * {@code
<filter>
  <filter-name>braveFilter</filter-name> 
  <filter-class>
    org.springframework.web.filter.DelegatingFilterProxy
  </filter-class>
  <init-param>    
    <param-name>targetFilterLifecycle</param-name>  
    <param-value>true</param-value>
  </init-param>
</filter>
<filter-mapping>
  <filter-name>braveFilter</filter-name>
  <url-pattern>/*</url-pattern>
</filter-mapping>
}
 * </pre>
 * 
 * 
 * 
 * spring-mvc.xml
 * 
 * <pre>
 * {@code

< bean id="braveFilter" class="xyz.multiplyzero.zipkin.web.filter.BraveServletFilter">
  < constructor-arg value="#{brave.serverRequestInterceptor()}"/>
  < constructor-arg value="#{brave.serverResponseInterceptor()}"/>
  < constructor-arg>
    < bean class="com.github.kristofa.brave.http.DefaultSpanNameProvider"/>
  < /constructor-arg>
</bean>
}
 * </pre>
 * 
 * 
 */
public class BraveServletFilter implements Filter {

    private final ServerRequestInterceptor requestInterceptor;
    private final ServerResponseInterceptor responseInterceptor;
    private final SpanNameProvider spanNameProvider;

    private FilterConfig filterConfig;

    public BraveServletFilter(ServerRequestInterceptor requestInterceptor,
            ServerResponseInterceptor responseInterceptor, SpanNameProvider spanNameProvider) {
        this.requestInterceptor = requestInterceptor;
        this.responseInterceptor = responseInterceptor;
        this.spanNameProvider = spanNameProvider;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        String alreadyFilteredAttributeName = getAlreadyFilteredAttributeName();
        boolean hasAlreadyFilteredAttribute = request.getAttribute(alreadyFilteredAttributeName) != null;

        if (hasAlreadyFilteredAttribute) {
            // Proceed without invoking this filter...
            filterChain.doFilter(request, response);
        } else {

            final StatusExposingServletResponse statusExposingServletResponse = new StatusExposingServletResponse(
                    (HttpServletResponse) response);
            requestInterceptor.handle(new HttpServerRequestAdapter(
                    new ServletHttpServerRequest((HttpServletRequest) request), spanNameProvider));

            try {
                filterChain.doFilter(request, statusExposingServletResponse);
            } finally {
                responseInterceptor.handle(new HttpServerResponseAdapter(new HttpResponse() {
                    @Override
                    public int getHttpStatusCode() {
                        return statusExposingServletResponse.getStatus();
                    }
                }));
            }
        }
    }

    @Override
    public void destroy() {

    }

    private String getAlreadyFilteredAttributeName() {
        String name = getFilterName();
        if (name == null) {
            name = getClass().getName();
        }
        return name + ".FILTERED";
    }

    private final String getFilterName() {
        return (this.filterConfig != null ? this.filterConfig.getFilterName() : null);
    }

    private static class StatusExposingServletResponse extends HttpServletResponseWrapper {
        private int httpStatus = HttpServletResponse.SC_OK;

        public StatusExposingServletResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void sendError(int sc) throws IOException {
            httpStatus = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            httpStatus = sc;
            super.sendError(sc, msg);
        }

        @Override
        public void setStatus(int sc) {
            httpStatus = sc;
            super.setStatus(sc);
        }

        public int getStatus() {
            return httpStatus;
        }
    }
}
