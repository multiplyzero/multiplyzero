package xyz.multiplyzero.spring.feign.factory;

import org.springframework.util.StringUtils;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import xyz.multiplyzero.spring.feign.anno.FeignClient;
import xyz.multiplyzero.spring.feign.filter.CacheFilter;
import xyz.multiplyzero.spring.feign.filter.ExecuteFilter;
import xyz.multiplyzero.spring.feign.filter.LimitFilter;
import xyz.multiplyzero.spring.feign.filter.MockFilter;
import xyz.multiplyzero.spring.feign.handler.CustomerInvocationHandlerFactory;

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

        CustomerInvocationHandlerFactory invocationFactory = CustomerInvocationHandlerFactory.create();
        FeignFactory.initFilter(invocationFactory, feignClient);
        Feign.Builder builder = Feign.builder().retryer(retryer).options(options)
                .decoder(DecoderFactory.getInstants(defaultDecoder, feignClient.decoder()))
                .encoder(EncoderFactory.getInstants(defaultEncoder, feignClient.encoder()))
                .invocationHandlerFactory(invocationFactory);

        if (StringUtils.hasText(feignClient.basicAuth())) {
            String[] str = StringUtils.split(feignClient.basicAuth(), ":");
            builder.requestInterceptor(new BasicAuthRequestInterceptor(str[0], str[1]));
        }

        return builder;
    }

    private static final void initFilter(CustomerInvocationHandlerFactory invocationFactory, FeignClient feignClient) {
        if (feignClient.mock()) {
            invocationFactory.addFilter(new MockFilter());
        }
        if (feignClient.cache() > 0) {
            invocationFactory.addFilter(new CacheFilter(feignClient.cache()));
        }
        if (feignClient.limit() > 0) {
            invocationFactory.addFilter(new LimitFilter(feignClient.limit()));
        }
        if (feignClient.execute() > 0) {
            invocationFactory.addFilter(new ExecuteFilter(feignClient.execute()));
        }
    }
}
