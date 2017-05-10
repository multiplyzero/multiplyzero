package com.yiwugou.product.bean;

import java.util.regex.Pattern;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

public class FeignClientPostProcessor implements ApplicationContextAware, BeanDefinitionRegistryPostProcessor {

    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    private ApplicationContext applicationContext;

    private String[] annotationPackages;

    public void setPackage(String annotationPackage) {
        this.annotationPackages = StringUtils.hasText(annotationPackage) ? COMMA_SPLIT_PATTERN.split(annotationPackage)
                : null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        FeignClientScanner scanner = new FeignClientScanner(registry);
        scanner.setResourceLoader(this.applicationContext);
        scanner.addIncludeFilter(new AnnotationTypeFilter(FeignClient.class));
        scanner.scan(this.annotationPackages);

    }

}
