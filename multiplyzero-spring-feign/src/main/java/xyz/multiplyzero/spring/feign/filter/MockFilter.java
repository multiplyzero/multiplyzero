package xyz.multiplyzero.spring.feign.filter;

import xyz.multiplyzero.spring.feign.invoker.Invoker;

public class MockFilter implements Filter {
    @Override
    public Object invoke(Invoker invoker) throws Throwable {
        Object obj = null;
        try {
            obj = invoker.invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
