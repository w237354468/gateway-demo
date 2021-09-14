package org.csits.demo.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class AddJwtHeaderFilter implements GlobalFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    Mono<WebSession> sessionMono = exchange.getSession();
    sessionMono.doOnNext(webSession -> {
      String jwt = (String) webSession.getAttributes().getOrDefault("JWT", "NULL");
      log.info("Web Session Jwt : {}", jwt);

      // Add header
      ServerHttpRequest mutableReq = exchange.getRequest().mutate().header("Authentication-JWT", jwt)
          .build();
      ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();

      chain.filter(mutableExchange);
    }).doOnError(throwable -> log.error(throwable.getMessage()));
    return Mono.empty();
  }

  @Override
  public int getOrder() {
    return -2;
  }
}
