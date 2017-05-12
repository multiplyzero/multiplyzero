package xyz.multiplyzero.spring.feign.handler;

import static feign.Util.checkNotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

import feign.InvocationHandlerFactory.MethodHandler;
import feign.Target;
import xyz.multiplyzero.spring.feign.filter.Filter;
import xyz.multiplyzero.spring.feign.invoker.DefaultInvoker;
import xyz.multiplyzero.spring.feign.invoker.Invoker;

/**
 *
 * CustomerInvocationHandler
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午5:24:30
 */
public class CustomerInvocationHandler implements InvocationHandler {
    private final Target target;
    private final Map<Method, MethodHandler> dispatch;
    private List<Filter> filters;

    public CustomerInvocationHandler(Target target, Map<Method, MethodHandler> dispatch, List<Filter> filters) {
        this.target = checkNotNull(target, "target");
        this.dispatch = checkNotNull(dispatch, "dispatch for %s", target);
        this.filters = filters;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                return this.equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return this.hashCode();
        } else if ("toString".equals(method.getName())) {
            return this.toString();
        }

        Invoker invoker = this.buildFilterChain(new DefaultInvoker(this.dispatch, method, args));
        return invoker.invoke();

        // return this.dispatch.get(method).invoke(args);
    }

    private Invoker buildFilterChain(final Invoker invoker) {
        Invoker last = invoker;
        for (int i = this.filters.size() - 1; i >= 0; i--) {
            final Filter filter = this.filters.get(i);
            final Invoker next = last;
            last = new Invoker() {
                @Override
                public Object invoke() throws Throwable {
                    return filter.invoke(next);
                }

                @Override
                public Method getMethod() {
                    return invoker.getMethod();
                }

                @Override
                public Object[] getArgs() {
                    return invoker.getArgs();
                }
            };
        }
        return last;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CustomerInvocationHandler) {
            CustomerInvocationHandler other = (CustomerInvocationHandler) obj;
            return this.target.equals(other.target);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.target.hashCode();
    }

    @Override
    public String toString() {
        return this.target.toString();
    }
}
