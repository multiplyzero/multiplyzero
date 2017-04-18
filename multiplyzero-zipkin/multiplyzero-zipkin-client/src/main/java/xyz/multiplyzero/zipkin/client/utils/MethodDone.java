package xyz.multiplyzero.zipkin.client.utils;

public interface MethodDone<T> {
    T done() throws Throwable;
}
