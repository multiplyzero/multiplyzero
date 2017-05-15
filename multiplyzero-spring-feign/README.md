# spring 与 feign 集成


## use feign without spring cloud



use eureka (default eureka properties is eureka-client.properties)

```xml

<bean class="xyz.multiplyzero.spring.feign.bean.FeignClientPostProcessor">
    <property name="package" value="your.service.package" />
</bean>

```


```java

package your.service.package;

import feign.Param;
import feign.RequestLine;

@FeignClient(eurekaServiceId="ms-redis-service")
public interface FooRestService {
    @RequestLine("GET /foo1/{username}/{password}")
    Foo foo1(@Param("username") String username, @Param("password") String password);

    @RequestLine("GET /foo2?username={username}&password={password}")
    List<Foo> foo2(@Param("username") String username, @Param("password") String password);
}


```

without eureka

```xml

<bean class="xyz.multiplyzero.spring.feign.bean.FeignClientPostProcessor">
    <property name="package" value="your.service.package" />
    <property name="propertiesLoaderSupport" ref="propertiesLoaderSupport" />
</bean>

<bean id="" class="? extends org.springframework.core.io.support.PropertiesLoaderSupport">
    <property name="locations">
        <list>
            <value>classpath*:product-common.properties</value>
        </list>
    </property>
</bean>

```


```java

package your.service.package;

import feign.Param;
import feign.RequestLine;

@FeignClient("${your-key in properties file}")
public interface FooRestService {
    @RequestLine("GET /foo1/{username}/{password}")
    Foo foo1(@Param("username") String username, @Param("password") String password);

    @RequestLine("GET /foo2?username={username}&password={password}")
    List<Foo> foo2(@Param("username") String username, @Param("password") String password);
}