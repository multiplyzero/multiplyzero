package xyz.multiplyzero.spring.feign.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.netflix.appinfo.EurekaAccept;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.internal.util.Archaius1Utils;
import com.netflix.discovery.shared.transport.EurekaTransportConfig;

import xyz.multiplyzero.spring.feign.constants.Constants;
import xyz.multiplyzero.spring.feign.constants.EurekaConfigConstants;
import xyz.multiplyzero.spring.feign.constants.EurekaConfigConstants.Values;

/**
 *
 * CustomerEurekaClientConfig
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午4:53:06
 */
// @Singleton
// @ProvidedBy(CustomerEurekaClientConfigProvider.class)
public class CustomerEurekaClientConfig implements EurekaClientConfig {

    public static final String DEFAULT_ZONE = "defaultZone";

    private final String namespace;
    private final DynamicPropertyFactory configInstance;
    private final EurekaTransportConfig transportConfig;

    public CustomerEurekaClientConfig() {
        this(Constants.DEFAULT_CONFIG_NAMESPACE, Constants.DEFAULT_CONFIG_FILE);
    }

    public CustomerEurekaClientConfig(String namespace, String configFile) {
        this.namespace = namespace.endsWith(".") ? namespace : namespace + ".";
        this.configInstance = Archaius1Utils.initConfig(configFile);
        this.transportConfig = new CustomerEurekaTransportConfig(namespace, this.configInstance);
    }

    @Override
    public int getRegistryFetchIntervalSeconds() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaConfigConstants.REGISTRY_REFRESH_INTERVAL_KEY, 30).get();
    }

    @Override
    public int getInstanceInfoReplicationIntervalSeconds() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaConfigConstants.REGISTRATION_REPLICATION_INTERVAL_KEY, 30).get();
    }

    @Override
    public int getInitialInstanceInfoReplicationIntervalSeconds() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaConfigConstants.INITIAL_REGISTRATION_REPLICATION_DELAY_KEY, 40)
                .get();
    }

    @Override
    public int getEurekaServiceUrlPollIntervalSeconds() {
        return this.configInstance.getIntProperty(
                this.namespace + EurekaConfigConstants.EUREKA_SERVER_URL_POLL_INTERVAL_KEY, 5 * 60 * 1000).get() / 1000;
    }

    @Override
    public String getProxyHost() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_PROXY_HOST_KEY, null).get();
    }

    @Override
    public String getProxyPort() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_PROXY_PORT_KEY, null).get();
    }

    @Override
    public String getProxyUserName() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_PROXY_USERNAME_KEY, null).get();
    }

    @Override
    public String getProxyPassword() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_PROXY_PASSWORD_KEY, null).get();
    }

    @Override
    public boolean shouldGZipContent() {
        return this.configInstance
                .getBooleanProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_GZIP_CONTENT_KEY, true).get();
    }

    @Override
    public int getEurekaServerReadTimeoutSeconds() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_READ_TIMEOUT_KEY, 8).get();
    }

    @Override
    public int getEurekaServerConnectTimeoutSeconds() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_CONNECT_TIMEOUT_KEY, 5).get();
    }

    @Override
    public String getBackupRegistryImpl() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.BACKUP_REGISTRY_CLASSNAME_KEY, null).get();
    }

    @Override
    public int getEurekaServerTotalConnections() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_MAX_CONNECTIONS_KEY, 200).get();
    }

    @Override
    public int getEurekaServerTotalConnectionsPerHost() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_MAX_CONNECTIONS_PER_HOST_KEY, 50)
                .get();
    }

    @Override
    public String getEurekaServerURLContext() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_URL_CONTEXT_KEY,
                        this.configInstance.getStringProperty(
                                this.namespace + EurekaConfigConstants.EUREKA_SERVER_FALLBACK_URL_CONTEXT_KEY, null)
                                .get())
                .get();
    }

    @Override
    public String getEurekaServerPort() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_PORT_KEY,
                        this.configInstance
                                .getStringProperty(
                                        this.namespace + EurekaConfigConstants.EUREKA_SERVER_FALLBACK_PORT_KEY, null)
                                .get())
                .get();
    }

    @Override
    public String getEurekaServerDNSName() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_DNS_NAME_KEY,
                        this.configInstance.getStringProperty(
                                this.namespace + EurekaConfigConstants.EUREKA_SERVER_FALLBACK_DNS_NAME_KEY, null).get())
                .get();
    }

    @Override
    public boolean shouldUseDnsForFetchingServiceUrls() {
        return this.configInstance.getBooleanProperty(this.namespace + EurekaConfigConstants.SHOULD_USE_DNS_KEY, false)
                .get();
    }

    @Override
    public boolean shouldRegisterWithEureka() {
        return this.configInstance
                .getBooleanProperty(this.namespace + EurekaConfigConstants.REGISTRATION_ENABLED_KEY, true).get();
    }

    @Override
    public boolean shouldPreferSameZoneEureka() {
        return this.configInstance
                .getBooleanProperty(this.namespace + EurekaConfigConstants.SHOULD_PREFER_SAME_ZONE_SERVER_KEY, true)
                .get();
    }

    @Override
    public boolean allowRedirects() {
        return this.configInstance
                .getBooleanProperty(this.namespace + EurekaConfigConstants.SHOULD_ALLOW_REDIRECTS_KEY, false).get();
    }

    @Override
    public boolean shouldLogDeltaDiff() {
        return this.configInstance
                .getBooleanProperty(this.namespace + EurekaConfigConstants.SHOULD_LOG_DELTA_DIFF_KEY, false).get();
    }

    @Override
    public boolean shouldDisableDelta() {
        return this.configInstance
                .getBooleanProperty(this.namespace + EurekaConfigConstants.SHOULD_DISABLE_DELTA_KEY, false).get();
    }

    @Override
    public String fetchRegistryForRemoteRegions() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.SHOULD_FETCH_REMOTE_REGION_KEY, null).get();
    }

    @Override
    public String getRegion() {
        DynamicStringProperty defaultEurekaRegion = this.configInstance
                .getStringProperty(EurekaConfigConstants.CLIENT_REGION_FALLBACK_KEY, Values.DEFAULT_CLIENT_REGION);
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.CLIENT_REGION_KEY, defaultEurekaRegion.get())
                .get();
    }

    @Override
    public String[] getAvailabilityZones(String region) {
        return this.configInstance.getStringProperty(
                this.namespace + region + "." + EurekaConfigConstants.CONFIG_AVAILABILITY_ZONE_PREFIX, DEFAULT_ZONE)
                .get().split(",");
    }

    @Override
    public List<String> getEurekaServerServiceUrls(String myZone) {
        String serviceUrls = this.configInstance.getStringProperty(
                this.namespace + EurekaConfigConstants.CONFIG_EUREKA_SERVER_SERVICE_URL_PREFIX + "." + myZone, null)
                .get();
        if (serviceUrls == null || serviceUrls.isEmpty()) {
            serviceUrls = this.configInstance.getStringProperty(
                    this.namespace + EurekaConfigConstants.CONFIG_EUREKA_SERVER_SERVICE_URL_PREFIX + ".default", null)
                    .get();

        }
        if (serviceUrls != null) {
            return Arrays.asList(serviceUrls.split(","));
        }

        return new ArrayList<>();
    }

    @Override
    public boolean shouldFilterOnlyUpInstances() {
        return this.configInstance
                .getBooleanProperty(this.namespace + EurekaConfigConstants.SHOULD_FILTER_ONLY_UP_INSTANCES_KEY, true)
                .get();
    }

    @Override
    public int getEurekaConnectionIdleTimeoutSeconds() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaConfigConstants.EUREKA_SERVER_CONNECTION_IDLE_TIMEOUT_KEY, 30)
                .get();
    }

    @Override
    public boolean shouldFetchRegistry() {
        return this.configInstance
                .getBooleanProperty(this.namespace + EurekaConfigConstants.FETCH_REGISTRY_ENABLED_KEY, true).get();
    }

    @Override
    public String getRegistryRefreshSingleVipAddress() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.FETCH_SINGLE_VIP_ONLY_KEY, null).get();
    }

    @Override
    public int getHeartbeatExecutorThreadPoolSize() {
        return this.configInstance.getIntProperty(this.namespace + EurekaConfigConstants.HEARTBEAT_THREADPOOL_SIZE_KEY,
                EurekaConfigConstants.Values.DEFAULT_EXECUTOR_THREAD_POOL_SIZE).get();
    }

    @Override
    public int getHeartbeatExecutorExponentialBackOffBound() {
        return this.configInstance.getIntProperty(this.namespace + EurekaConfigConstants.HEARTBEAT_BACKOFF_BOUND_KEY,
                EurekaConfigConstants.Values.DEFAULT_EXECUTOR_THREAD_POOL_BACKOFF_BOUND).get();
    }

    @Override
    public int getCacheRefreshExecutorThreadPoolSize() {
        return this.configInstance
                .getIntProperty(this.namespace + EurekaConfigConstants.CACHEREFRESH_THREADPOOL_SIZE_KEY,
                        EurekaConfigConstants.Values.DEFAULT_EXECUTOR_THREAD_POOL_SIZE)
                .get();
    }

    @Override
    public int getCacheRefreshExecutorExponentialBackOffBound() {
        return this.configInstance.getIntProperty(this.namespace + EurekaConfigConstants.CACHEREFRESH_BACKOFF_BOUND_KEY,
                EurekaConfigConstants.Values.DEFAULT_EXECUTOR_THREAD_POOL_BACKOFF_BOUND).get();
    }

    @Override
    public String getDollarReplacement() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.CONFIG_DOLLAR_REPLACEMENT_KEY,
                        Values.CONFIG_DOLLAR_REPLACEMENT)
                .get();
    }

    @Override
    public String getEscapeCharReplacement() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.CONFIG_ESCAPE_CHAR_REPLACEMENT_KEY,
                        Values.CONFIG_ESCAPE_CHAR_REPLACEMENT)
                .get();
    }

    @Override
    public boolean shouldOnDemandUpdateStatusChange() {
        return this.configInstance
                .getBooleanProperty(this.namespace + EurekaConfigConstants.SHOULD_ONDEMAND_UPDATE_STATUS_KEY, true)
                .get();
    }

    @Override
    public String getEncoderName() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.CLIENT_ENCODER_NAME_KEY, null).get();
    }

    @Override
    public String getDecoderName() {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.CLIENT_DECODER_NAME_KEY, null).get();
    }

    @Override
    public String getClientDataAccept() {
        return this.configInstance.getStringProperty(this.namespace + EurekaConfigConstants.CLIENT_DATA_ACCEPT_KEY,
                EurekaAccept.full.name()).get();
    }

    @Override
    public String getExperimental(String name) {
        return this.configInstance
                .getStringProperty(this.namespace + EurekaConfigConstants.CONFIG_EXPERIMENTAL_PREFIX + "." + name, null)
                .get();
    }

    @Override
    public EurekaTransportConfig getTransportConfig() {
        return this.transportConfig;
    }
}
