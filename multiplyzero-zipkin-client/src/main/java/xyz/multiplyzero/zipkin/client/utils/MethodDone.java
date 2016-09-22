package xyz.multiplyzero.zipkin.client.utils;

@FunctionalInterface
public interface MethodDone<T> {
    T done() throws Throwable;
}
