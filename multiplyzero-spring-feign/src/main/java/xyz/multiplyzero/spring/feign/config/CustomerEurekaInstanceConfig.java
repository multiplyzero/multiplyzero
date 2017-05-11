package xyz.multiplyzero.spring.feign.config;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.netflix.appinfo.AbstractInstanceConfig;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.internal.util.Archaius1Utils;

import xyz.multiplyzero.spring.feign.constants.Constants;
import xyz.multiplyzero.spring.feign.constants.EurekaInstanceConfigConstants;

/**
 *
 * CustomerEurekaInstanceConfig
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午4:53:11
 */
public class CustomerEurekaInstanceConfig extends AbstractInstanceConfig implements EurekaInstanceConfig {

    protected String namespace;

    protected DynamicPropertyFactory configInstance;

    private String appGrpNameFromEnv;

    public CustomerEurekaInstanceConfig() {
        this(Constants.DEFAULT_CONFIG_NAMESPACE, Constants.DEFAULT_CONFIG_FILE);
    }

    public CustomerEurekaInstanceConfig(String namespace, String configFile) {
        this(namespace, configFile, new DataCenterInfo() {
            @Override
            public Name getName() {
                return Name.MyOwn;
            }
        });
    }

    public CustomerEurekaInstanceConfig(String namespace, String configFile, DataCenterInfo info) {
        super(info);

        this.namespace = namespace.endsWith(".") ? namespace : namespace + ".";

        this.appGrpNameFromEnv = ConfigurationManager.getConfigInstance().getString(
                EurekaInstanceConfigConstants.FALLBACK_APP_GROUP_KEY,
                EurekaInstanceConfigConstants.Values.UNKNOWN_APPLICATION);

        this.configInstance = Archaius1Utils.initConfig(configFile);
    }

    @Override
    public boolean isInstanceEnabledOnit() {
        return this.configInstance
                .getBooleanProperty(this.namespace + EurekaInstanceConfigConstants.TRAFFIC_ENABLED_ON_INIT_KEY,
                        super.isInstanceEnabledOnit())
                .get();
    }

    @Override
    public int getNonSecurePort() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaInstanceConfigConstants.PORT_KEY, super.getNonSecurePort())
                .get();
    }

    @Override
    public int getSecurePort() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaInstanceConfigConstants.SECURE_PORT_KEY, super.getSecurePort())
                .get();
    }

    @Override
    public boolean isNonSecurePortEnabled() {
        return this.configInstance.getBooleanProperty(this.namespace + EurekaInstanceConfigConstants.PORT_ENABLED_KEY,
                super.isNonSecurePortEnabled()).get();
    }

    @Override
    public boolean getSecurePortEnabled() {
        return this.configInstance
                .getBooleanProperty(this.namespace + EurekaInstanceConfigConstants.SECURE_PORT_ENABLED_KEY,
                        super.getSecurePortEnabled())
                .get();
    }

    @Override
    public int getLeaseRenewalIntervalInSeconds() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaInstanceConfigConstants.LEASE_RENEWAL_INTERVAL_KEY,
                        super.getLeaseRenewalIntervalInSeconds())
                .get();
    }

    @Override
    public int getLeaseExpirationDurationInSeconds() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaInstanceConfigConstants.LEASE_EXPIRATION_DURATION_KEY,
                        super.getLeaseExpirationDurationInSeconds())
                .get();
    }

    @Override
    public String getVirtualHostName() {
        if (this.isNonSecurePortEnabled()) {
            return this.configInstance
                    .getStringProperty(this.namespace + EurekaInstanceConfigConstants.VIRTUAL_HOSTNAME_KEY,
                            super.getVirtualHostName())
                    .get();
        } else {
            return null;
        }
    }

    @Override
    public String getSecureVirtualHostName() {
        if (this.getSecurePortEnabled()) {
            return this.configInstance
                    .getStringProperty(this.namespace + EurekaInstanceConfigConstants.SECURE_VIRTUAL_HOSTNAME_KEY,
                            super.getSecureVirtualHostName())
                    .get();
        } else {
            return null;
        }
    }

    @Override
    public String getASGName() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaInstanceConfigConstants.ASG_NAME_KEY, super.getASGName())
                .get();
    }

    /**
     * Gets the metadata map associated with the instance. The properties that
     * will be looked up for this will be <code>namespace + ".metadata"</code>.
     *
     * <p>
     * For instance, if the given namespace is <code>eureka.appinfo</code>, the
     * metadata keys are searched under the namespace
     * <code>eureka.appinfo.metadata</code>.
     * </p>
     */
    @Override
    public Map<String, String> getMetadataMap() {
        String metadataNamespace = this.namespace + EurekaInstanceConfigConstants.INSTANCE_METADATA_PREFIX + ".";
        Map<String, String> metadataMap = new LinkedHashMap<>();
        Configuration config = (Configuration) this.configInstance.getBackingConfigurationSource();
        String subsetPrefix = metadataNamespace.charAt(metadataNamespace.length() - 1) == '.'
                ? metadataNamespace.substring(0, metadataNamespace.length() - 1) : metadataNamespace;
        for (Iterator<String> iter = config.subset(subsetPrefix).getKeys(); iter.hasNext();) {
            String key = iter.next();
            String value = config.getString(subsetPrefix + "." + key);
            metadataMap.put(key, value);
        }
        return metadataMap;
    }

    @Override
    public String getInstanceId() {
        String result = this.configInstance
                .getStringProperty(this.namespace + EurekaInstanceConfigConstants.INSTANCE_ID_KEY, null).get();
        return result == null ? null : result.trim();
    }

    @Override
    public String getAppname() {
        return this.configInstance.getStringProperty(this.namespace + EurekaInstanceConfigConstants.APP_NAME_KEY,
                EurekaInstanceConfigConstants.Values.UNKNOWN_APPLICATION).get().trim();
    }

    @Override
    public String getAppGroupName() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaInstanceConfigConstants.APP_GROUP_KEY, this.appGrpNameFromEnv)
                .get().trim();
    }

    @Override
    public String getIpAddress() {
        return super.getIpAddress();
    }

    @Override
    public String getStatusPageUrlPath() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaInstanceConfigConstants.STATUS_PAGE_URL_PATH_KEY,
                        EurekaInstanceConfigConstants.Values.DEFAULT_STATUSPAGE_URLPATH)
                .get();
    }

    @Override
    public String getStatusPageUrl() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaInstanceConfigConstants.STATUS_PAGE_URL_KEY, null).get();
    }

    @Override
    public String getHomePageUrlPath() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaInstanceConfigConstants.HOME_PAGE_URL_PATH_KEY,
                        EurekaInstanceConfigConstants.Values.DEFAULT_HOMEPAGE_URLPATH)
                .get();
    }

    @Override
    public String getHomePageUrl() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaInstanceConfigConstants.HOME_PAGE_URL_KEY, null).get();
    }

    @Override
    public String getHealthCheckUrlPath() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaInstanceConfigConstants.HEALTHCHECK_URL_PATH_KEY,
                        EurekaInstanceConfigConstants.Values.DEFAULT_HEALTHCHECK_URLPATH)
                .get();
    }

    @Override
    public String getHealthCheckUrl() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaInstanceConfigConstants.HEALTHCHECK_URL_KEY, null).get();
    }

    @Override
    public String getSecureHealthCheckUrl() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaInstanceConfigConstants.SECURE_HEALTHCHECK_URL_KEY, null)
                .get();
    }

    @Override
    public String[] getDefaultAddressResolutionOrder() {
        String result = this.configInstance.getStringProperty(
                this.namespace + EurekaInstanceConfigConstants.DEFAULT_ADDRESS_RESOLUTION_ORDER_KEY, null).get();
        return result == null ? new String[0] : result.split(",");
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }
}
