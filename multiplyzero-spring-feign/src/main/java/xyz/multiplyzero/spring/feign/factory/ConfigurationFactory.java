package xyz.multiplyzero.spring.feign.factory;

import java.net.URI;
import java.util.List;

import org.springframework.util.StringUtils;

import com.netflix.config.ConfigurationManager;

import xyz.multiplyzero.spring.feign.constants.Constants;

/**
 *
 * ConfigurationFactory
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月12日 下午2:41:18
 */
public class ConfigurationFactory {
    public static void setServices(String namespace, String appId, List<String> serverList) {
        String str = StringUtils.collectionToDelimitedString(serverList, ",");
        ConfigurationManager.getConfigInstance()
                .setProperty(namespace + Constants.NAMESPACE_RIBBON_ID_SPLIT + appId + ".ribbon.listOfServers", str);
    }

    public static void setServices(String namespace, String ribbonId, String... serverList) {
        String str = StringUtils.arrayToDelimitedString(serverList, ",");
        ConfigurationManager.getConfigInstance()
                .setProperty(namespace + Constants.NAMESPACE_RIBBON_ID_SPLIT + ribbonId + ".ribbon.listOfServers", str);
    }

    public static void main(String[] args) {
        String url = "http://xxxxx-sddd-fff";
        URI asUri = URI.create(url);
        System.err.println(asUri.getScheme());
        System.err.println(asUri.getHost());
        System.err.println(asUri.getPath());
        System.err.println(asUri.getPort());
    }
}
