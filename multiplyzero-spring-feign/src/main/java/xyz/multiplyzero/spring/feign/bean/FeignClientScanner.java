package xyz.multiplyzero.spring.feign.bean;

import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import feign.Feign;
import feign.Request;
import feign.Retryer;

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

                String url = feignClient.url();

                FeignClientFactoryBean<Object> factoryBean = new FeignClientFactoryBean<>();

                Retryer retryer = new Retryer.Default(feignClient.period(), feignClient.maxPeriod(),
                        feignClient.maxAttempts());
                Request.Options options = new Request.Options(feignClient.connectTimeoutMillis(),
                        feignClient.readTimeoutMillis());

                Feign.Builder builder = Feign.builder().retryer(retryer).options(options);
                builder.decoder(feignClient.decoder().newInstance()).encoder(feignClient.encoder().newInstance());
                Object obj = builder.target(clazz, url);

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