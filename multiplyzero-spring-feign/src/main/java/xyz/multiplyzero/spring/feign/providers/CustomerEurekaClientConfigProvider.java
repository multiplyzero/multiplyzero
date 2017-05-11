// package xyz.multiplyzero.spring.feign.providers;
//
// import javax.inject.Provider;
//
// import com.google.inject.Inject;
// import com.netflix.discovery.EurekaClientConfig;
// import com.netflix.discovery.EurekaNamespace;
//
// import xyz.multiplyzero.spring.feign.config.CustomerEurekaClientConfig;
//
// public class CustomerEurekaClientConfigProvider implements
// Provider<EurekaClientConfig> {
//
// @Inject(optional = true)
// @EurekaNamespace
// private String namespace;
//
// private CustomerEurekaClientConfig config;
//
// @Override
// public synchronized EurekaClientConfig get() {
// // if (this.config == null) {
// // this.config = (this.namespace == null) ? new
// // CustomerEurekaClientConfig()
// // : new CustomerEurekaClientConfig(this.namespace);
// // }
//
// return this.config;
// }
// }
