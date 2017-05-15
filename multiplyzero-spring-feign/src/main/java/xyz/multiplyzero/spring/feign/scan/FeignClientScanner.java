package xyz.multiplyzero.spring.feign.scan;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.util.StringUtils;

import com.netflix.discovery.EurekaClient;

import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.ribbon.RibbonClient;
import lombok.Setter;
import xyz.multiplyzero.spring.feign.anno.FeignClient;
import xyz.multiplyzero.spring.feign.bean.FeignClientFactoryBean;
import xyz.multiplyzero.spring.feign.factory.ConfigurationFactory;
import xyz.multiplyzero.spring.feign.factory.EurekaClientFactory;
import xyz.multiplyzero.spring.feign.factory.FeignFactory;
import xyz.multiplyzero.spring.feign.factory.ServerFactory;
import xyz.multiplyzero.spring.feign.factory.UrlFactory;
import xyz.multiplyzero.spring.feign.utils.ObjectUtils;

/**
 *
 * FeignClientScanner
 *
 * @author zhanxiaoyong
 *
 * @since 2017年5月11日 下午4:54:37
 */
public class FeignClientScanner extends ClassPathBeanDefinitionScanner {
    @Setter
    private String eurekaNamespace;
    @Setter
    private String eurekaConfigFile;
    @Setter
    private Decoder defaultDecoder;
    @Setter
    private Encoder defaultEncoder;
    @Setter
    private Properties properties;

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
                            this.eurekaNamespace);
                    String eurekaConfigFile = ObjectUtils.defaultIfHasText(feignClient.eurekaConfigFile(),
                            this.eurekaConfigFile);
                    EurekaClient eurekaClient = EurekaClientFactory.getInstants(eurekaNamespace, eurekaConfigFile);
                    List<String> serverList = ServerFactory.getUrlList(eurekaClient, feignClient);
                    ConfigurationFactory.setServices(eurekaNamespace, feignClient.eurekaServiceId(), serverList);
                    url = UrlFactory.getBibbonUrl(eurekaNamespace, feignClient.eurekaServiceId());
                    // Server server = ServerFactory.getInstants(eurekaClient,
                    // feignClient);
                    // url = UrlFactory.getInstants(server.getHost(),
                    // server.getPort());
                } else {
                    url = UrlFactory.replacePlaceholder(url, this.properties);
                    ConfigurationFactory.setServices("defaultUrl", beanClassName, url);
                    url = UrlFactory.getBibbonUrl("defaultUrl", beanClassName);
                }
                Feign.Builder builder = FeignFactory.getInstants(feignClient, this.defaultDecoder, this.defaultEncoder);

                Object obj = builder.client(RibbonClient.create()).target(clazz, url);
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