package org.csits.demo.config;

import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.Data;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Data
public class JsonSentinelGatewayBlockExceptionHandler implements WebExceptionHandler {

  private final List<ViewResolver> viewResolvers;
  private final ServerCodecConfigurer serverCodecConfigurer;

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    ServerHttpResponse exchangeResponse = exchange.getResponse();
    exchangeResponse.getHeaders().add("Content-Type", "application/json;charset=utf8");

    byte[] content = "{\"code\":403,\"msg\":\"限流了\"}".getBytes(StandardCharsets.UTF_8);
    DataBuffer wrap = exchangeResponse.bufferFactory().wrap(content);
    return exchangeResponse.writeWith(Mono.just(wrap));
  }
}
