package xyz.multiplyzero.spring.feign.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.netflix.loadbalancer.AbstractLoadBalancer;
import com.netflix.loadbalancer.BaseLoadBalancer;

/**
 *
 * LoadBalancerFactory
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月12日 下午2:41:27
 */
public class LoadBalancerFactory {
    private static final Map<String, AbstractLoadBalancer> DATA_MAP = new ConcurrentHashMap<>();

    public static AbstractLoadBalancer getInstants(Class<? extends AbstractLoadBalancer> clazz) {
        try {
            String className = clazz.getName();
            AbstractLoadBalancer lb = DATA_MAP.get(className);
            if (lb == null) {
                lb = clazz.newInstance();
                DATA_MAP.put(className, lb);
            }
            return lb;
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseLoadBalancer();
        }
    }

}
