package org.csits.demo.config;

import java.util.function.Consumer;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory.Config;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("provider",
            predicateSpec -> predicateSpec.path("/echo/**")
                .filters(f -> f.circuitBreaker(new Consumer<Config>() {
                  @Override
                  public void accept(Config config) {

                  }
                }))
                .uri("lb://provider-service"))
        .build();
  }
}
