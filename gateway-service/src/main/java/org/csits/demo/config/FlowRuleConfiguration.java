package org.csits.demo.config;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

@Configuration
public class FlowRuleConfiguration {

  private final List<ViewResolver> viewResolvers;
  private final ServerCodecConfigurer serverCodecConfigurer;

  public FlowRuleConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
      ServerCodecConfigurer serverCodecConfigurer) {
    this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
    this.serverCodecConfigurer = serverCodecConfigurer;
  }

  /**
   * 配置SentinelGatewayBlockExceptionHandler，限流后异常处理
   */
  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
    return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
  }

  /**
   * 配置SentinelGatewayFilter
   */
  @Bean
  @Order(-1)
  public GlobalFilter sentinelGatewayFilter() {
    return new SentinelGatewayFilter();
  }

  @PostConstruct
  public void doInit() {
    initGatewayRules();
    initBlockHandler();
  }

  private void initBlockHandler() {
    BlockRequestHandler blockRequestHandler = new BlockRequestHandler() {
      @Override
      public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", "128");
        map.put("msg", "接口限流");

        // JSON result by default.
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(BodyInserters.fromObject(map));

      }
    };
    GatewayCallbackManager.setBlockHandler(blockRequestHandler);
  }

    private void initGatewayRules () {
      Set<GatewayFlowRule> rules = new HashSet<>();
      rules.add(new GatewayFlowRule("provider")
              // 限流阈值
              .setCount(1)
              // 统计时间窗口，单位是秒，默认是 1 秒
              .setIntervalSec(1)
//            .setGrade(RuleConstant.FLOW_GRADE_QPS)
              .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT)
//        应对突发请求时额外允许的请求数目（目前仅对参数限流生效）。
//            .setBurst(1)
          // 匀速排队模式下的最长排队时间，单位是毫秒，仅在匀速排队模式下生效。
//            .setMaxQueueingTimeoutMs(1)
          // 还可以对参数进行限流，可配置对应测类
//        .setParamItem()

      );

      GatewayRuleManager.loadRules(rules);
    }

    private static class ErrorResult {

      private final int code;
      private final String message;

      ErrorResult(int code, String message) {
        this.code = code;
        this.message = message;
      }

      public int getCode() {
        return code;
      }

      public String getMessage() {
        return message;
      }
    }
  }
