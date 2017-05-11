package xyz.multiplyzero.spring.feign.factory;

public class UrlFactory {
    public static String getInstants(String host, int port) {
        return "http://" + host + ":" + port;
    }
}
