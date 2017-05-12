package xyz.multiplyzero.spring.feign.filter;

import xyz.multiplyzero.spring.feign.invoker.Invoker;

/**
 *
 * MockFilter
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月12日 下午4:02:00
 */
public class MockFilter implements Filter {
    @Override
    public Object invoke(Invoker invoker) throws Throwable {
        Object obj = null;
        try {
            obj = invoker.invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
