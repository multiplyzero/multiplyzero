package xyz.multiplyzero.spring.feign.factory;

import feign.Feign;
import feign.Request;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import xyz.multiplyzero.spring.feign.anno.FeignClient;

public class FeignFactory {
    public static final Feign.Builder getInstants(FeignClient feignClient, Decoder defaultDecoder,
            Encoder defaultEncoder) {
        Retryer retryer = new Retryer.Default(feignClient.retryPeriod(), feignClient.retryMaxPeriod(),
                feignClient.retryMaxAttempts());
        Request.Options options = new Request.Options(feignClient.connectTimeoutMillis(),
                feignClient.readTimeoutMillis());

        Feign.Builder builder = Feign.builder().retryer(retryer).options(options)
                .decoder(DecoderFactory.getInstants(defaultDecoder, feignClient.decoder()))
                .encoder(EncoderFactory.getInstants(defaultEncoder, feignClient.encoder()));
        return builder;
    }
}
