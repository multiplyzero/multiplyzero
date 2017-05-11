package xyz.multiplyzero.spring.feign.factory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import feign.Feign;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import xyz.multiplyzero.spring.feign.anno.FeignClient;

/**
 *
 * FeignFactory
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午4:52:07
 */
public class FeignFactory {
    public static final Feign.Builder getInstants(FeignClient feignClient, Decoder defaultDecoder,
            Encoder defaultEncoder) {
        Retryer retryer = new Retryer.Default(feignClient.retryPeriod(), feignClient.retryMaxPeriod(),
                feignClient.retryMaxAttempts());
        Request.Options options = new Request.Options(feignClient.connectTimeoutMillis(),
                feignClient.readTimeoutMillis());

        Feign.Builder builder = Feign.builder().retryer(retryer).options(options)
                .decoder(DecoderFactory.getInstants(defaultDecoder, feignClient.decoder()))
                .encoder(EncoderFactory.getInstants(defaultEncoder, feignClient.encoder()))
                /*
                 * .invocationHandlerFactory(new InvocationHandlerFactory() {
                 *
                 * @Override public InvocationHandler create(Target target,
                 * Map<Method, MethodHandler> dispatch) { return new
                 * InvocationHandler() {
                 *
                 * @Override public Object invoke(Object proxy, Method method,
                 * Object[] args) throws Throwable { return null; } }; } })
                 */
                .requestInterceptor(new RequestInterceptor() {
                    @Override
                    public void apply(RequestTemplate template) {
                        // template.
                    }
                });
        LoadingCache<Long, AtomicLong> counter = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS)
                .build(new CacheLoader<Long, AtomicLong>() {
                    @Override
                    public AtomicLong load(Long key) throws Exception {
                        return new AtomicLong(0);
                    }
                });
        return builder;
    }
}
