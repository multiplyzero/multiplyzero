package xyz.multiplyzero.spring.feign.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.IRule;

/**
 *
 * RuleFactory
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午4:54:04
 */
public class RuleFactory {
    private static final Map<String, IRule> DATA_MAP = new ConcurrentHashMap<>();

    public static IRule getInstants(Class<? extends IRule> clazz) {
        try {
            if (clazz != null && !clazz.equals(IRule.class)) {
                String className = clazz.getName();
                IRule rule = DATA_MAP.get(className);
                if (rule == null) {
                    rule = clazz.newInstance();
                    DATA_MAP.put(className, rule);
                }
                return rule;
            }
            return new AvailabilityFilteringRule();
        } catch (Exception e) {
            e.printStackTrace();
            return new AvailabilityFilteringRule();
        }
    }
}
