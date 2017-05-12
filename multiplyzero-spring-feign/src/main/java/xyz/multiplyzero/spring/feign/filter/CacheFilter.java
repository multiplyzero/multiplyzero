package xyz.multiplyzero.spring.feign.filter;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import xyz.multiplyzero.spring.feign.invoker.Invoker;

public class CacheFilter implements Filter {

    private LoadingCache<String, Object> data;

    public CacheFilter(long cache) {
        this.data = CacheBuilder.newBuilder().expireAfterWrite(cache, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public Object load(String key) throws Exception {
                        return null;
                    }
                });
    }

    @Override
    public Object invoke(Invoker invoker) throws Throwable {
        String methodName = invoker.getMethod().getName();
        Object obj = this.data.get(methodName);
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
