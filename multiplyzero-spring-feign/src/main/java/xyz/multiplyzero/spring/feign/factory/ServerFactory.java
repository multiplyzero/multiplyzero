package xyz.multiplyzero.spring.feign.factory;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Provider;
import com.netflix.discovery.EurekaClient;
import com.netflix.loadbalancer.AbstractLoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import com.netflix.loadbalancer.ZoneAffinityServerListFilter;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import xyz.multiplyzero.spring.feign.anno.FeignClient;
import xyz.multiplyzero.spring.feign.constants.Constants;

/**
 *
 * ServerFactory
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午4:54:09
 */
public class ServerFactory {

    public static Server getInstants(final EurekaClient eurekaClient, FeignClient feignClient) {
        ServerList<DiscoveryEnabledServer> serverList = ServerFactory.getServerList(eurekaClient, feignClient);

        // IRule rule = RuleFactory.getInstants(feignClient.rule());
        IRule rule = new RoundRobinRule();
        AbstractLoadBalancer lb = LoadBalancerBuilder.<DiscoveryEnabledServer>newBuilder()
                .withDynamicServerList(serverList).withRule(rule)
                .withServerListFilter(new ZoneAffinityServerListFilter<DiscoveryEnabledServer>())
                .buildDynamicServerListLoadBalancer();
        Server server = lb.chooseServer();
        return server;
    }

    private static ServerList<DiscoveryEnabledServer> getServerList(final EurekaClient eurekaClient,
            FeignClient feignClient) {
        ServerList<DiscoveryEnabledServer> serverList = new DiscoveryEnabledNIWSServerList(
                feignClient.eurekaServiceId(), new Provider<EurekaClient>() {
                    @Override
                    public EurekaClient get() {
                        return eurekaClient;
                    }
                });
        return serverList;
    }

    public static List<String> getUrlList(final EurekaClient eurekaClient, FeignClient feignClient) {
        ServerList<DiscoveryEnabledServer> serverList = getServerList(eurekaClient, feignClient);
        List<DiscoveryEnabledServer> dnsList = serverList.getUpdatedListOfServers();
        List<String> urls = new ArrayList<>();
        for (DiscoveryEnabledServer dns : dnsList) {
            urls.add(Constants.HTTP + dns.getHostPort());
        }
        return urls;
    }
}
