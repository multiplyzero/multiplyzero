package xyz.multiplyzero.spring.feign.bean;

import org.springframework.beans.factory.FactoryBean;

import lombok.Setter;

/**
 *
 * FeignClientFactoryBean
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午4:52:41
 */
public class FeignClientFactoryBean<T> implements FactoryBean<T> {

    @Setter
    private Class<T> mapperInterface;

    @Setter
    private T object;

    @Override
    public T getObject() throws Exception {
        return this.object;
    }

    @Override
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
