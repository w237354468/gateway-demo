package org.csits.demo.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class GenerateJwtFilter implements GlobalFilter, Ordered {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    log.info("this is {}", this.getClass().getName());

    Mono<WebSession> sessionMono = exchange.getSession();
    sessionMono.doOnNext(webSession -> {

          log.info("Web Session Id : {}", webSession.getId());
          webSession.getAttributes()
              .put("JWT", exchange.getRequest().getRemoteAddress().getHostString());

        })
        .subscribe();
    return chain.filter(exchange);
  }

  @Override
  public int getOrder() {
    return -3;
  }
}
