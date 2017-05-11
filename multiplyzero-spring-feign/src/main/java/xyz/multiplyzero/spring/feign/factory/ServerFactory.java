package xyz.multiplyzero.spring.feign.factory;

import com.google.inject.Provider;
import com.netflix.discovery.EurekaClient;
import com.netflix.loadbalancer.AbstractLoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import com.netflix.loadbalancer.ZoneAffinityServerListFilter;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import xyz.multiplyzero.spring.feign.anno.FeignClient;

public class ServerFactory {

    public static Server getInstants(EurekaClient eurekaClient, FeignClient feignClient) {
        ServerList<DiscoveryEnabledServer> serverList = new DiscoveryEnabledNIWSServerList(
                feignClient.eurekaServiceId(), new Provider<EurekaClient>() {
                    @Override
                    public EurekaClient get() {
                        return eurekaClient;
                    }
                });

        IRule rule = RuleFactory.getInstants(feignClient.rule());

        AbstractLoadBalancer lb = LoadBalancerBuilder.<DiscoveryEnabledServer>newBuilder()
                .withDynamicServerList(serverList).withRule(rule)
                .withServerListFilter(new ZoneAffinityServerListFilter<>()).buildDynamicServerListLoadBalancer();
        Server server = lb.chooseServer();
        return server;
    }
}