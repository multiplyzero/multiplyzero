package xyz.multiplyzero.zipkin.web.filter;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import com.github.kristofa.brave.http.HttpServerRequest;

public class ServletHttpServerRequest implements HttpServerRequest {

    private final HttpServletRequest request;

    public ServletHttpServerRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getHttpHeaderValue(String headerName) {
        return request.getHeader(headerName);
    }

    @Override
    public URI getUri() {
        try {
            return new URI(request.getRequestURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getHttpMethod() {
        return request.getMethod();
    }
}