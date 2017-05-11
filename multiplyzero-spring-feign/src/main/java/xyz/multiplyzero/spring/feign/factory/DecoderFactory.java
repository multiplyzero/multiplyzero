package xyz.multiplyzero.spring.feign.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import feign.codec.Decoder;

/**
 *
 * DecoderFactory
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午4:53:44
 */
public class DecoderFactory {
    private static final Map<String, Decoder> DATA_MAP = new ConcurrentHashMap<>();

    public static Decoder getInstants(Decoder defaultDecoder, Class<? extends Decoder> clazz) {
        try {
            if (defaultDecoder == null) {
                String className = clazz.getName();
                Decoder decoder = DATA_MAP.get(className);
                if (decoder == null) {
                    decoder = clazz.newInstance();
                    DATA_MAP.put(className, decoder);
                }
                return decoder;
            }
            return defaultDecoder;
        } catch (Exception e) {
            e.printStackTrace();
            return new Decoder.Default();
        }
    }
}
