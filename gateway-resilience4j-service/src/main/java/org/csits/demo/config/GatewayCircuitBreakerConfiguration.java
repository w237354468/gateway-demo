package org.csits.demo.config;

import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnErrorEvent;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnSuccessEvent;
import io.github.resilience4j.core.EventConsumer;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4jBulkheadConfigurationBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4jBulkheadProvider;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory.Config;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec.RequestRateLimiterSpec;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author weizhiqiang
 */
@Configuration
public class GatewayCircuitBreakerConfiguration {

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {

    return builder.routes()
        .route(new Function<PredicateSpec, Buildable<Route>>() {
          @Override
          public Buildable<Route> apply(PredicateSpec predicateSpec) {
            return predicateSpec.path("/echo/**")
                .filters(filter -> filter.requestRateLimiter()
                    .rateLimiter(RedisRateLimiter.class,
                        rl -> rl.setBurstCapacity(1).setReplenishRate(1)).and())
                .uri("lb://provider-service");
          }
        })
        .build();
  }

  @Bean
  public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {

    CircuitBreakerConfig circuitBreakerConfig = new CircuitBreakerConfig.Builder()
        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
        .slidingWindowSize(5)
        // 最小几次开始统计
        .minimumNumberOfCalls(3)
        .slowCallDurationThreshold(Duration.ofMillis(50))
        .slowCallRateThreshold(50.0f)
        .failureRateThreshold(50.0f)
//                .maxWaitDurationInHalfOpenState(Duration.ofSeconds(20))
        // 开到半开的时间
        .waitDurationInOpenState(Duration.ofSeconds(10))
        .enableAutomaticTransitionFromOpenToHalfOpen()
        .recordExceptions(Throwable.class)
        .build();

    return factory -> {
      factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
          .circuitBreakerConfig(circuitBreakerConfig)
          .timeLimiterConfig(
              TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()).build());
      // 细粒度控制每个路由的回调
      factory.addCircuitBreakerCustomizer(circuitBreaker -> circuitBreaker.getEventPublisher()
              .onError(new NormalFluxErrorConsumer()).onSuccess(new NormalFluxSuccessConsumer()),
          "provider");
    };
  }

  @Bean
  public Customizer<Resilience4jBulkheadProvider> defaultBulkheadCustomizer() {
    return provider -> provider.configureDefault(
        id -> new Resilience4jBulkheadConfigurationBuilder()
            .bulkheadConfig(BulkheadConfig.custom().maxConcurrentCalls(4).build())
            .threadPoolBulkheadConfig(
                ThreadPoolBulkheadConfig.custom().coreThreadPoolSize(1).maxThreadPoolSize(1)
                    .build())
            .build()
    );
  }

  @Log4j2
  static class NormalFluxErrorConsumer implements EventConsumer<CircuitBreakerOnErrorEvent> {

    @Override
    public void consumeEvent(CircuitBreakerOnErrorEvent event) {
      log.error("CircuitBreaker find an error: {}", event.getEventType().toString());
    }
  }

  @Log4j2
  static class NormalFluxSuccessConsumer implements EventConsumer<CircuitBreakerOnSuccessEvent> {

    @Override
    public void consumeEvent(CircuitBreakerOnSuccessEvent event) {
      log.info("CircuitBreaker find an success: {}", event.getEventType().toString());
    }
  }
}
