package com.yiwugou.product.bean;

import org.springframework.beans.factory.FactoryBean;

import lombok.Setter;

public class FeignClientFactoryBean<T> implements FactoryBean<T> {

    @Setter
    private Class<T> mapperInterface;

    @Setter
    private T object;

    @Override
    public T getObject() throws Exception {
        return object;
    }

    @Override
    public Class<T> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
