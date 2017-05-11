package xyz.multiplyzero.spring.feign.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import feign.codec.Encoder;

/**
 *
 * EncoderFactory
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午4:53:48
 */
public class EncoderFactory {
    private static final Map<String, Encoder> DATA_MAP = new ConcurrentHashMap<>();

    public static Encoder getInstants(Encoder defaultEncoder, Class<? extends Encoder> clazz) {
        try {
            if (defaultEncoder == null) {
                String className = clazz.getName();
                Encoder encoder = DATA_MAP.get(className);
                if (encoder == null) {
                    encoder = clazz.newInstance();
                    DATA_MAP.put(className, encoder);
                }
                return encoder;
            }
            return defaultEncoder;
        } catch (Exception e) {
            e.printStackTrace();
            return new Encoder.Default();
        }
    }

}
