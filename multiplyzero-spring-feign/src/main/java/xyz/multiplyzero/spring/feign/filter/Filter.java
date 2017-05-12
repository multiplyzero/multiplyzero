package xyz.multiplyzero.spring.feign.filter;

import xyz.multiplyzero.spring.feign.invoker.Invoker;

public interface Filter {

    Object invoke(Invoker invoker) throws Throwable;
}
