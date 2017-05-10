package com.yiwugou.product.bean;

import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import feign.Feign;

public class FeignClientScanner extends ClassPathBeanDefinitionScanner {
    public FeignClientScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> holders = super.doScan(basePackages);
        try {
            for (BeanDefinitionHolder holder : holders) {
                GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
                String beanClassName = definition.getBeanClassName();
                Class<?> clazz = Class.forName(beanClassName);

                FeignClient feignClient = clazz.getAnnotation(FeignClient.class);
                String value = feignClient.value();
                System.err.println(value);
                String url = feignClient.url();
                System.err.println(url);

                FeignClientFactoryBean<Object> factoryBean = new FeignClientFactoryBean<>();
                Object obj = Feign.builder().decoder(new JacksonDecoder()).target(clazz, "http://127.0.0.1:8763/");
                definition.setBeanClass(factoryBean.getClass());
                definition.getPropertyValues().addPropertyValue("mapperInterface", clazz);
                definition.getPropertyValues().addPropertyValue("object", obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return holders;
    }

}