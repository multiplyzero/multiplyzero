package xyz.multiplyzero.spring.feign.factory;

import java.util.Properties;

import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;

import xyz.multiplyzero.spring.feign.constants.Constants;

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
        return Constants.HTTP + host + ":" + port;
    }

    public static String getBibbonUrl(String namespace, String ribbonId) {
        return Constants.HTTP + namespace + Constants.NAMESPACE_RIBBON_ID_SPLIT + ribbonId;
    }

    public static String replacePlaceholder(String url, Properties properties) {
        if (url.startsWith(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX)
                && url.endsWith(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX)) {
            String key = url.substring(2, url.length() - 1);
            url = properties.getProperty(key);
        }
        return url;
    }
}
