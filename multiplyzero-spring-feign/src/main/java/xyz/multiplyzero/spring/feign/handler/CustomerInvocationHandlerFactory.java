package xyz.multiplyzero.spring.feign.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import feign.InvocationHandlerFactory;
import feign.Target;
import xyz.multiplyzero.spring.feign.filter.Filter;

/**
 *
 * CustomerInvocationHandlerFactory
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午5:24:25
 */
public class CustomerInvocationHandlerFactory implements InvocationHandlerFactory {
    private CustomerInvocationHandlerFactory() {

    }

    @Override
    public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
        return new CustomerInvocationHandler(target, dispatch, this.filters);
    }

    public static CustomerInvocationHandlerFactory create() {
        return new CustomerInvocationHandlerFactory();
    }

    private List<Filter> filters = new LinkedList<>();

    public CustomerInvocationHandlerFactory addFilter(Filter filter) {
        this.filters.add(filter);
        return this;
    }
}
