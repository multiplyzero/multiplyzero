package xyz.multiplyzero.spring.feign.factory;

/**
 *
 * UrlFactory
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午4:54:15
 */
public class UrlFactory {
    public static String getInstants(String host, int port) {
        return "http://" + host + ":" + port;
    }
}
