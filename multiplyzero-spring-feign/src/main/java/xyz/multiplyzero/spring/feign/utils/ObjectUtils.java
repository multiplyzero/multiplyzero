package xyz.multiplyzero.spring.feign.utils;

import org.springframework.util.StringUtils;

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
