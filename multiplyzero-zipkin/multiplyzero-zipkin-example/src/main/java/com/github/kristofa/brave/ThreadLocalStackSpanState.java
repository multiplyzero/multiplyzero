package com.github.kristofa.brave;

import java.net.UnknownHostException;
import java.util.Stack;

import com.github.kristofa.brave.internal.Util;
import com.twitter.zipkin.gen.Endpoint;
import com.twitter.zipkin.gen.Span;

public class ThreadLocalStackSpanState implements ServerClientAndLocalSpanState {
    private final static ThreadLocal<Stack<ServerSpan>> currentServerSpan = new ThreadLocal<Stack<ServerSpan>>() {
        @Override
        protected Stack<ServerSpan> initialValue() {
            return new Stack<>();
        }
    };
    private final static ThreadLocal<Stack<Span>> currentClientSpan = new ThreadLocal<Stack<Span>>() {
        @Override
        protected Stack<Span> initialValue() {
            return new Stack<>();
        }
    };

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
        return currentServerSpan.get().peek();
    }

    @Override
    public void setCurrentServerSpan(final ServerSpan span) {
        if (span == null) {
            currentServerSpan.get().pop();
        } else {
            currentServerSpan.get().push(span);
        }
    }

    @Override
    public Endpoint endpoint() {
        return endpoint;
    }

    @Override
    public Span getCurrentClientSpan() {
        return currentClientSpan.get().peek();
    }

    @Override
    public void setCurrentClientSpan(final Span span) {
        currentClientSpan.get().push(span);
    }

    @Override
    public Boolean sample() {
        return currentServerSpan.get().peek().getSample();
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
