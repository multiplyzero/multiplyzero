package xyz.multiplyzero.spring.feign.filter;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import xyz.multiplyzero.spring.feign.invoker.Invoker;

public class CacheFilter implements Filter {

    private Cache<String, Object> data;

    public CacheFilter(long cache) {
        this.data = CacheBuilder.newBuilder().expireAfterWrite(cache, TimeUnit.MILLISECONDS).build();
    }

    @Override
    public Object invoke(Invoker invoker) throws Throwable {
        String methodName = invoker.getMethod().getName();
        Object obj = this.data.getIfPresent(methodName);
        if (obj != null) {
            return obj;
        }
        obj = invoker.invoke();
        if (obj != null) {
            this.data.put(methodName, obj);
        }
        return obj;
    }

}
