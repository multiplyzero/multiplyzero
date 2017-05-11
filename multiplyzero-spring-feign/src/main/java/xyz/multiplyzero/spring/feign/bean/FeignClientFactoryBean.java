package xyz.multiplyzero.spring.feign.bean;

import org.springframework.beans.factory.FactoryBean;

import lombok.Setter;

public class FeignClientFactoryBean<T> implements FactoryBean<T> {

    @Setter
    private Class<T> mapperInterface;

    @Setter
    private T object;

    @Override
    public T getObject() throws Exception {
        System.err.println("FeignClientFactoryBean get object");
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
