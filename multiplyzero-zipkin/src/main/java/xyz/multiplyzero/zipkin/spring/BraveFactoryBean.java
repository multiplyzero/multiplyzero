package xyz.multiplyzero.zipkin.spring;

import java.net.UnknownHostException;

import org.springframework.beans.factory.FactoryBean;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.ThreadLocalStackSpanState;
import com.github.kristofa.brave.http.HttpSpanCollector;

import lombok.Setter;

/**
 * 
 * <pre>
{@code
< bean id="brave" class="xyz.multiplyzero.zipkin.spring.BraveFactoryBean">
    <property name="serviceName" value="web-client" />
    <property name="zipkinHost" value="http://127.0.0.1:9411" />
 < /bean>
}
 * </pre>
 * 
 * BraveFactoryBean
 * 
 * @author zhanxiaoyong
 *
 * @since 2016年9月19日 下午4:51:20
 */
public class BraveFactoryBean implements FactoryBean<Brave> {
    @Setter
    private String serviceName;

    @Setter
    private String zipkinHost;

    private Brave brave;

    private void createInstance() throws UnknownHostException {
        Brave.Builder builder = new Brave.Builder(new ThreadLocalStackSpanState(serviceName));
        if (this.zipkinHost != null && !"".equals(this.zipkinHost)) {
            builder.spanCollector(HttpSpanCollector.create(this.zipkinHost, new EmptySpanCollectorMetricsHandler()));
        }
        this.brave = builder.build();
    }

    @Override
    public Brave getObject() throws Exception {
        if (this.brave == null) {
            this.createInstance();
        }
        return this.brave;
    }

    @Override
    public Class<?> getObjectType() {
        return Brave.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
