package xyz.multiplyzero.spring.feign.filter;

import java.util.concurrent.atomic.AtomicInteger;

import xyz.multiplyzero.spring.feign.invoker.Invoker;

public class LimitFilter implements Filter {

    private int limit;

    private AtomicInteger data;

    public LimitFilter(int limit) {
        this.limit = limit;
        this.data = new AtomicInteger(0);
    }

    @Override
    public Object invoke(Invoker invoker) throws Throwable {
        Object obj = null;
        try {
            if (this.data.incrementAndGet() > this.limit) {
                throw new Exception("limit is " + this.limit);
            }
            obj = invoker.invoke();
        } finally {
            this.data.decrementAndGet();
        }

        return obj;
    }

}
