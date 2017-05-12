package xyz.multiplyzero.spring.feign.invoker;

import java.lang.reflect.Method;
import java.util.Map;

import feign.InvocationHandlerFactory.MethodHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DefaultInvoker implements Invoker {
    @Getter
    Map<Method, MethodHandler> dispatch;

    @Getter
    private Method method;

    @Getter
    private Object[] args;

    @Override
    public Object invoke() throws Throwable {
        return this.dispatch.get(this.method).invoke(this.args);
    }

}
