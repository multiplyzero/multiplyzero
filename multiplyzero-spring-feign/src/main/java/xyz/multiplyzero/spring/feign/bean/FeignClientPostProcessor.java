package xyz.multiplyzero.spring.feign.bean;

import java.lang.reflect.Method;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import feign.codec.Decoder;
import feign.codec.Encoder;
import lombok.Setter;
import xyz.multiplyzero.spring.feign.anno.FeignClient;
import xyz.multiplyzero.spring.feign.constants.Constants;
import xyz.multiplyzero.spring.feign.scan.FeignClientScanner;

/**
 *
 * FeignClientPostProcessor
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午4:52:50
 */
public class FeignClientPostProcessor implements ApplicationContextAware, BeanDefinitionRegistryPostProcessor {

    @Setter
    private String eurekaNamespace = Constants.DEFAULT_CONFIG_NAMESPACE;

    private String eurekaConfigFile = Constants.DEFAULT_CONFIG_FILE;

    @Setter
    private Decoder defaultDecoder;
    @Setter
    private Encoder defaultEncoder;

    private Properties properties;

    private ApplicationContext applicationContext;

    private String[] packages;

    public void setPropertiesLoaderSupport(PropertiesLoaderSupport propertiesLoaderSupport) {
        Method method = ReflectionUtils.findMethod(PropertyPlaceholderConfigurer.class, "mergeProperties");
        ReflectionUtils.makeAccessible(method);
        this.properties = (Properties) ReflectionUtils.invokeMethod(method, propertiesLoaderSupport);
    }

    public void setPackage(String annotationPackage) {
        this.packages = StringUtils.hasText(annotationPackage) ? Constants.COMMA_SPLIT_PATTERN.split(annotationPackage)
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
        scanner.setEurekaConfigFile(this.eurekaConfigFile);
        scanner.setEurekaNamespace(this.eurekaNamespace);
        scanner.setDefaultDecoder(this.defaultDecoder);
        scanner.setDefaultEncoder(this.defaultEncoder);
        scanner.setResourceLoader(this.applicationContext);
        scanner.setProperties(this.properties);
        scanner.addIncludeFilter(new AnnotationTypeFilter(FeignClient.class));
        scanner.scan(this.packages);
    }

    public void setEurekaConfigFile(String eurekaConfigFile) {
        int index = eurekaConfigFile.indexOf(".properties");
        if (index > -1) {
            this.eurekaConfigFile = eurekaConfigFile.substring(0, index);
        } else {
            this.eurekaConfigFile = eurekaConfigFile;
        }
        // System.setProperty("eureka.client.props", defaultConfigFile);
    }

}
