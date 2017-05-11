package xyz.multiplyzero.spring.feign.utils;

import org.springframework.util.StringUtils;

/**
 *
 * ObjectUtils
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午4:54:41
 */
public class ObjectUtils {
    public static <T> T defaultIfNull(T t, T def) {
        if (t == null) {
            return def;
        }
        return t;
    }

    public static String defaultIfHasText(String str, String def) {
        if (StringUtils.hasText(str)) {
            return str;
        }
        return def;
    }
}
