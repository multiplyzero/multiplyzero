package xyz.multiplyzero.spring.feign.scan;

import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.util.StringUtils;

import com.netflix.discovery.EurekaClient;
import com.netflix.loadbalancer.Server;

import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import lombok.Setter;
import xyz.multiplyzero.spring.feign.anno.FeignClient;
import xyz.multiplyzero.spring.feign.bean.FeignClientFactoryBean;
import xyz.multiplyzero.spring.feign.factory.EurekaClientFactory;
import xyz.multiplyzero.spring.feign.factory.FeignFactory;
import xyz.multiplyzero.spring.feign.factory.ServerFactory;
import xyz.multiplyzero.spring.feign.factory.UrlFactory;
import xyz.multiplyzero.spring.feign.utils.ObjectUtils;

public class FeignClientScanner extends ClassPathBeanDefinitionScanner {
    @Setter
    private String defaultNamespace;
    @Setter
    private String defaultConfigFile;
    @Setter
    private Decoder defaultDecoder;
    @Setter
    private Encoder defaultEncoder;

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
                String url = feignClient.value();
                if (!StringUtils.hasText(url)) {
                    String eurekaNamespace = ObjectUtils.defaultIfHasText(feignClient.eurekaNamespace(),
                            this.defaultNamespace);
                    String eurekaConfigFile = ObjectUtils.defaultIfHasText(feignClient.eurekaConfigFile(),
                            this.defaultConfigFile);
                    EurekaClient eurekaClient = EurekaClientFactory.getInstants(eurekaNamespace, eurekaConfigFile);
                    Server server = ServerFactory.getInstants(eurekaClient, feignClient);
                    url = UrlFactory.getInstants(server.getHost(), server.getPort());
                }
                Feign.Builder builder = FeignFactory.getInstants(feignClient, this.defaultDecoder, this.defaultEncoder);
                Object obj = builder.target(clazz, url);
                definition.setBeanClass(FeignClientFactoryBean.class);
                definition.getPropertyValues().addPropertyValue("mapperInterface", clazz);
                definition.getPropertyValues().addPropertyValue("object", obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return holders;
    }

}