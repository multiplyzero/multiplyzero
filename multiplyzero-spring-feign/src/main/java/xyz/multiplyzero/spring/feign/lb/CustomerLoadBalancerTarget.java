package xyz.multiplyzero.spring.feign.lb;

import java.net.URI;

import com.netflix.loadbalancer.AbstractLoadBalancer;
import com.netflix.loadbalancer.Server;

import feign.Request;
import feign.RequestTemplate;
import feign.Target;
import feign.Util;
import lombok.EqualsAndHashCode;

/**
 *
 * CustomerLoadBalancerTarget
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月12日 下午2:41:13
 */
@EqualsAndHashCode
public class CustomerLoadBalancerTarget<T> implements Target<T> {
    private String name;
    private String scheme;
    private String path;
    private Class<T> type;
    private AbstractLoadBalancer loadBalancer;

    protected CustomerLoadBalancerTarget(Class<T> type, String scheme, String name, String path,
            AbstractLoadBalancer loadBalancer) {
        this.type = Util.checkNotNull(type, "type");
        this.scheme = Util.checkNotNull(scheme, "scheme");
        this.name = Util.checkNotNull(name, "name");
        this.path = Util.checkNotNull(path, "path");
        this.loadBalancer = loadBalancer;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String url() {
        return String.format("%s://%s", this.scheme, this.path);
    }

    @Override
    public Request apply(RequestTemplate input) {
        Server currentServer = this.loadBalancer.chooseServer(null);
        String url = String.format("%s://%s%s", this.scheme, currentServer.getHostPort(), this.path);
        input.insert(0, url);
        try {
            return input.request();
        } finally {
            this.loadBalancer.getLoadBalancerStats().incrementNumRequests(currentServer);
        }
    }

    public static <T> CustomerLoadBalancerTarget<T> create(Class<T> type, String url,
            AbstractLoadBalancer loadBalancer) {
        URI asUri = URI.create(url);
        return new CustomerLoadBalancerTarget<>(type, asUri.getScheme(), asUri.getHost(), asUri.getPath(),
                loadBalancer);
    }
}
