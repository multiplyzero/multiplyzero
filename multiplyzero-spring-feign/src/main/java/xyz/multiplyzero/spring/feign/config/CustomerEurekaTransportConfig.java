package xyz.multiplyzero.spring.feign.config;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.shared.transport.EurekaTransportConfig;

import xyz.multiplyzero.spring.feign.constants.TransportConfigConstants;

public class CustomerEurekaTransportConfig implements EurekaTransportConfig {

    private static final String SUB_NAMESPACE = TransportConfigConstants.TRANSPORT_CONFIG_SUB_NAMESPACE + ".";

    private final String namespace;
    private final DynamicPropertyFactory configInstance;

    public CustomerEurekaTransportConfig(String parentNamespace, DynamicPropertyFactory configInstance) {
        this.namespace = parentNamespace == null ? SUB_NAMESPACE
                : (parentNamespace.endsWith(".") ? parentNamespace + SUB_NAMESPACE
                        : parentNamespace + "." + SUB_NAMESPACE);
        this.configInstance = configInstance;
    }

    @Override
    public int getSessionedClientReconnectIntervalSeconds() {
        return this.configInstance
                .getIntProperty(this.namespace + TransportConfigConstants.SESSION_RECONNECT_INTERVAL_KEY,
                        TransportConfigConstants.Values.SESSION_RECONNECT_INTERVAL)
                .get();
    }

    @Override
    public double getRetryableClientQuarantineRefreshPercentage() {
        return this.configInstance
                .getDoubleProperty(this.namespace + TransportConfigConstants.QUARANTINE_REFRESH_PERCENTAGE_KEY,
                        TransportConfigConstants.Values.QUARANTINE_REFRESH_PERCENTAGE)
                .get();
    }

    @Override
    public int getApplicationsResolverDataStalenessThresholdSeconds() {
        return this.configInstance
                .getIntProperty(this.namespace + TransportConfigConstants.DATA_STALENESS_THRESHOLD_KEY,
                        TransportConfigConstants.Values.DATA_STALENESS_TRHESHOLD)
                .get();
    }

    @Override
    public boolean applicationsResolverUseIp() {
        return this.configInstance
                .getBooleanProperty(this.namespace + TransportConfigConstants.APPLICATION_RESOLVER_USE_IP_KEY, false)
                .get();
    }

    @Override
    public int getAsyncResolverRefreshIntervalMs() {
        return this.configInstance
                .getIntProperty(this.namespace + TransportConfigConstants.ASYNC_RESOLVER_REFRESH_INTERVAL_KEY,
                        TransportConfigConstants.Values.ASYNC_RESOLVER_REFRESH_INTERVAL)
                .get();
    }

    @Override
    public int getAsyncResolverWarmUpTimeoutMs() {
        return this.configInstance
                .getIntProperty(this.namespace + TransportConfigConstants.ASYNC_RESOLVER_WARMUP_TIMEOUT_KEY,
                        TransportConfigConstants.Values.ASYNC_RESOLVER_WARMUP_TIMEOUT)
                .get();
    }

    @Override
    public int getAsyncExecutorThreadPoolSize() {
        return this.configInstance
                .getIntProperty(this.namespace + TransportConfigConstants.ASYNC_EXECUTOR_THREADPOOL_SIZE_KEY,
                        TransportConfigConstants.Values.ASYNC_EXECUTOR_THREADPOOL_SIZE)
                .get();
    }

    @Override
    public String getWriteClusterVip() {
        return this.configInstance
                .getStringProperty(this.namespace + TransportConfigConstants.WRITE_CLUSTER_VIP_KEY, null).get();
    }

    @Override
    public String getReadClusterVip() {
        return this.configInstance
                .getStringProperty(this.namespace + TransportConfigConstants.READ_CLUSTER_VIP_KEY, null).get();
    }

    @Override
    public String getBootstrapResolverStrategy() {
        return this.configInstance
                .getStringProperty(this.namespace + TransportConfigConstants.BOOTSTRAP_RESOLVER_STRATEGY_KEY, null)
                .get();
    }

    @Override
    public boolean useBootstrapResolverForQuery() {
        return this.configInstance
                .getBooleanProperty(this.namespace + TransportConfigConstants.USE_BOOTSTRAP_RESOLVER_FOR_QUERY, true)
                .get();
    }
}
