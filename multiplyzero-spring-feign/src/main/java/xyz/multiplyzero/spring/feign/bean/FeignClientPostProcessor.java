package xyz.multiplyzero.spring.feign.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.type.filter.AnnotationTypeFilter;
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
    private String defaultNamespace = Constants.DEFAULT_CONFIG_NAMESPACE;

    private String defaultConfigFile = Constants.DEFAULT_CONFIG_FILE;

    @Setter
    private Decoder defaultDecoder;
    @Setter
    private Encoder defaultEncoder;

    private ApplicationContext applicationContext;

    private String[] packages;

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
        scanner.setDefaultConfigFile(this.defaultConfigFile);
        scanner.setDefaultNamespace(this.defaultNamespace);
        scanner.setDefaultDecoder(this.defaultDecoder);
        scanner.setDefaultEncoder(this.defaultEncoder);
        scanner.setResourceLoader(this.applicationContext);
        scanner.addIncludeFilter(new AnnotationTypeFilter(FeignClient.class));
        scanner.scan(this.packages);
    }

    public void setDefaultConfigFile(String defaultConfigFile) {
        int index = defaultConfigFile.indexOf(".properties");
        if (index > -1) {
            this.defaultConfigFile = defaultConfigFile.substring(0, index);
        } else {
            this.defaultConfigFile = defaultConfigFile;
        }
        // System.setProperty("eureka.client.props", defaultConfigFile);
    }

}
