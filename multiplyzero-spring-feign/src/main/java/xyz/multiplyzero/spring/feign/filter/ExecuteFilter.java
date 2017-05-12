package xyz.multiplyzero.spring.feign.filter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import xyz.multiplyzero.spring.feign.invoker.Invoker;

public class ExecuteFilter implements Filter {

    private int execute;

    public ExecuteFilter(int execute) {
        this.execute = execute;
    }

    private LoadingCache<String, AtomicInteger> counter = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS).build(new CacheLoader<String, AtomicInteger>() {
                @Override
                public AtomicInteger load(String key) throws Exception {
                    return new AtomicInteger(0);
                }
            });

    @Override
    public Object invoke(Invoker invoker) throws Throwable {
        String methodName = invoker.getMethod().getName();
        if (this.counter.get(methodName).incrementAndGet() > this.execute) {
            throw new Exception("execute is " + this.execute);
        }
        return invoker.invoke();
    }

}
