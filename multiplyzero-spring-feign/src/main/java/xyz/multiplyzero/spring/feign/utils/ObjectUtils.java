package xyz.multiplyzero.spring.feign.utils;

public class ObjectUtils {
    public <T> T defaultIfNull(T t, T def) {
        if (t == null) {
            return def;
        }
        return t;
    }
}
