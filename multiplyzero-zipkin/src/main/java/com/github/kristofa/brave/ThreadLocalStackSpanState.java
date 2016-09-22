package com.github.kristofa.brave;

import java.net.UnknownHostException;
import java.util.Stack;

import com.github.kristofa.brave.internal.Util;
import com.twitter.zipkin.gen.Endpoint;
import com.twitter.zipkin.gen.Span;

public class ThreadLocalStackSpanState implements ServerClientAndLocalSpanState {
    private final static ThreadLocal<ServerSpan> currentServerSpan = new ThreadLocal<ServerSpan>() {
        @Override
        protected ServerSpan initialValue() {
            return ServerSpan.create(null);
        }
    };
    private final static ThreadLocal<Span> currentClientSpan = new ThreadLocal<Span>();

    private final static ThreadLocal<Stack<Span>> currentLocalSpan = new ThreadLocal<Stack<Span>>() {
        @Override
        protected Stack<Span> initialValue() {
            return new Stack<>();
        }
    };;

    private final Endpoint endpoint;

    public ThreadLocalStackSpanState(int ip, int port, String serviceName) {
        Util.checkNotBlank(serviceName, "Service name must be specified.");
        endpoint = Endpoint.create(serviceName, ip, port);
    }

    public ThreadLocalStackSpanState(String serviceName) throws UnknownHostException {
        int ip = InetAddressUtilities.toInt(InetAddressUtilities.getLocalHostLANAddress());
        Util.checkNotBlank(serviceName, "Service name must be specified.");
        endpoint = Endpoint.create(serviceName, ip, 0);
    }

    @Override
    public ServerSpan getCurrentServerSpan() {
        return currentServerSpan.get();
    }

    @Override
    public void setCurrentServerSpan(final ServerSpan span) {
        if (span == null) {
            currentServerSpan.remove();
        } else {
            currentServerSpan.set(span);
        }
    }

    @Override
    public Endpoint endpoint() {
        return endpoint;
    }

    @Override
    public Span getCurrentClientSpan() {
        return currentClientSpan.get();
    }

    @Override
    public void setCurrentClientSpan(final Span span) {
        currentClientSpan.set(span);
    }

    @Override
    public Boolean sample() {
        return currentServerSpan.get().getSample();
    }

    @Override
    public Span getCurrentLocalSpan() {
        return currentLocalSpan.get().empty() ? null : currentLocalSpan.get().peek();
    }

    @Override
    public void setCurrentLocalSpan(Span span) {
        if (span == null) {
            if (!currentLocalSpan.get().empty()) {
                currentLocalSpan.get().pop();
            }
        } else {
            currentLocalSpan.get().push(span);
        }
    }

}
