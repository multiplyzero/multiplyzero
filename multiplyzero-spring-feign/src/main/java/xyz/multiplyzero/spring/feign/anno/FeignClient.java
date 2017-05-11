package xyz.multiplyzero.spring.feign.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.netflix.loadbalancer.IRule;

import feign.codec.Decoder;
import feign.codec.Encoder;
import xyz.multiplyzero.spring.feign.code.jackson.JacksonDecoder;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClient {
    /**
     * 直接通过url地址访问 优先级最高
     */
    String value() default "";

    /**
     * eureka配置文件地址 优先级小于value
     */
    String eurekaConfigFile() default "";

    /**
     * namespace命名空间 用于链接多个eureka
     */
    String eurekaNamespace() default "";

    /**
     * 设置这个值 必须设置eureka(或在xml中注入)
     */
    String eurekaServiceId() default "";

    int connectTimeoutMillis() default 10 * 1000;

    int readTimeoutMillis() default 60 * 1000;

    Class<? extends Encoder> encoder() default Encoder.Default.class;

    Class<? extends Decoder> decoder() default JacksonDecoder.class;

    Class<? extends IRule> rule() default IRule.class;

    int retryMaxAttempts() default 100;

    long retryPeriod() default 1000;

    long retryMaxPeriod() default 5;

}
