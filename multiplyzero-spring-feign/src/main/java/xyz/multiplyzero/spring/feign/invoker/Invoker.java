package xyz.multiplyzero.spring.feign.invoker;

import java.lang.reflect.Method;

public interface Invoker {
    Object invoke() throws Throwable;

    Method getMethod();

    Object[] getArgs();
}
