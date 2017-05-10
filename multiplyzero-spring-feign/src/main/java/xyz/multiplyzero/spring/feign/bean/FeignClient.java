package xyz.multiplyzero.spring.feign.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import feign.codec.Decoder;
import feign.codec.Encoder;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClient {

    String value() default "";

    String url() default "";

    int connectTimeoutMillis() default 10 * 1000;

    int readTimeoutMillis() default 60 * 1000;

    Class<? extends Encoder> encoder() default Encoder.Default.class;

    Class<? extends Decoder> decoder() default Decoder.Default.class;

    int maxAttempts() default 100;

    long period() default 1000;

    long maxPeriod() default 5;

}
