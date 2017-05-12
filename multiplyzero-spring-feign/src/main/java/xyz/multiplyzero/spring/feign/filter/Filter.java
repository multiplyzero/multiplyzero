package xyz.multiplyzero.spring.feign.filter;

import xyz.multiplyzero.spring.feign.invoker.Invoker;

/**
 *
 * Filter
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月12日 下午4:01:48
 */
public interface Filter {
    Object invoke(Invoker invoker) throws Throwable;
}
