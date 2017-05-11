package xyz.multiplyzero.spring.feign.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.multiplyzero.spring.feign.config.CustomerEurekaClientConfig;
import xyz.multiplyzero.spring.feign.config.CustomerEurekaInstanceConfig;

public class EurekaClientFactory {

    private static final Map<EurekaConfig, EurekaClient> DATA_MAP = new ConcurrentHashMap<>();

    // private Interner<String> interner = Interners.<String>newWeakInterner();

    public static EurekaClient getInstants(String namespace, String configFile) {
        EurekaConfig eurekaConfig = new EurekaConfig(namespace, configFile);
        return EurekaClientFactory.getInstants(eurekaConfig);
    }

    public static EurekaClient getInstants(EurekaConfig eurekaConfig) {
        EurekaClient client = DATA_MAP.get(eurekaConfig);
        if (client != null) {
            return client;
        }
        return initClient(eurekaConfig);
    }

    private static EurekaClient initClient(EurekaConfig eurekaConfig) {
        ApplicationInfoManager.OptionalArgs options = null;
        CustomerEurekaInstanceConfig instanceConfig = new CustomerEurekaInstanceConfig(eurekaConfig.getNamespace(),
                eurekaConfig.getConfigFile());
        ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(instanceConfig, options);
        EurekaClientConfig clientConfig = new CustomerEurekaClientConfig(eurekaConfig.getNamespace(),
                eurekaConfig.getConfigFile());
        EurekaClient client = new DiscoveryClient(applicationInfoManager, clientConfig);
        DATA_MAP.put(eurekaConfig, client);
        return client;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EurekaConfig {
        private String namespace;
        private String configFile;
    }
}
