# spring 与 feign 集成


## use feign without spring cloud



```xml

<bean class="xyz.multiplyzero.spring.feign.bean.FeignClientPostProcessor">
    <property name="package" value="your.service.package" />
</bean>

```


```java

package your.service.package;

import java.util.List;

import com.yiwugou.ms.redis.api.bo.demo.Foo;
import com.yiwugou.product.bean.FeignClient;

import feign.Param;
import feign.RequestLine;

@FeignClient("ms-redis-service")
public interface FooRestService {
    @RequestLine("GET /foo1/{username}/{password}")
    Foo foo1(@Param("username") String username, @Param("password") String password);

    @RequestLine("GET /foo2?username={username}&password={password}")
    List<Foo> foo2(@Param("username") String username, @Param("password") String password);
}


```