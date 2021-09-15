package org.csits.demo.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class AddJwtHeaderFilter implements GlobalFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    String jwt = exchange.getSession().map(
        webSession -> webSession.getAttributeOrDefault("JWT", "NULL")).share().block();

    ServerHttpRequest mutableReq = exchange.getRequest().mutate()
          .header("Authentication-JWT", jwt)
          .build();
    ServerWebExchange webExchange = exchange.mutate().request(mutableReq).build();

    return chain.filter(webExchange);
  }

  @Override
  public int getOrder() {
    return -2;
  }
}
