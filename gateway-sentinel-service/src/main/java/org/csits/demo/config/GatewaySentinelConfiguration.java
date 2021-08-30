package org.csits.demo.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GatewaySentinelConfiguration {

  private final List<ViewResolver> viewResolvers;
  private final ServerCodecConfigurer serverCodecConfigurer;

  public GatewaySentinelConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
      ServerCodecConfigurer serverCodecConfigurer) {
    this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
    this.serverCodecConfigurer = serverCodecConfigurer;
  }

  /**
   * 配置SentinelGatewayBlockExceptionHandler，限流后异常处理
   */
  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public JsonSentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
    return new JsonSentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
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
  }

  // 自定义api
  private void initCustomizedApis() {
    Set<ApiDefinition> definitions = new HashSet<>();
    ApiDefinition api1 = new ApiDefinition("some_customized_api")
        .setPredicateItems(new HashSet<ApiPredicateItem>() {{
          add(new ApiPathPredicateItem().setPattern("/ahas"));
          add(new ApiPathPredicateItem().setPattern("/product/**")
              .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
        }});
    ApiDefinition api2 = new ApiDefinition("another_customized_api")
        .setPredicateItems(new HashSet<ApiPredicateItem>() {{
          add(new ApiPathPredicateItem().setPattern("/**")
              .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
        }});
    definitions.add(api1);
    definitions.add(api2);
    GatewayApiDefinitionManager.loadApiDefinitions(definitions);
  }

  // 添加限流规则
  private void initGatewayRules() {
    Set<GatewayFlowRule> rules = new HashSet<>();
    rules.add(new GatewayFlowRule("provider")
            // 限流阈值
            .setCount(1)
            // 统计时间窗口，单位是秒，默认是 1 秒
            .setIntervalSec(2)
//            .setGrade(RuleConstant.FLOW_GRADE_QPS)
            .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER)
//        应对突发请求时额外允许的请求数目（目前仅对参数限流生效）。
//            .setBurst(1)
        // 匀速排队模式下的最长排队时间，单位是毫秒，仅在匀速排队模式下生效。
//            .setMaxQueueingTimeoutMs(1)
        // 还可以对参数进行限流，可配置对应测类
//        .setParamItem()
    );
    rules.add(new GatewayFlowRule("aliyun_route")
        .setCount(2)
        .setIntervalSec(2)
        .setBurst(2)
        .setParamItem(new GatewayParamFlowItem()
            .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP)
        )
    );
    rules.add(new GatewayFlowRule("httpbin_route")
        .setCount(10)
        .setIntervalSec(1)
        .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER)
        .setMaxQueueingTimeoutMs(600)
        .setParamItem(new GatewayParamFlowItem()
            .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HEADER)
            .setFieldName("X-Sentinel-Flag")
        )
    );
    rules.add(new GatewayFlowRule("httpbin_route")
        .setCount(1)
        .setIntervalSec(1)
        .setParamItem(new GatewayParamFlowItem()
            .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
            .setFieldName("pa")
        )
    );
    rules.add(new GatewayFlowRule("httpbin_route")
        .setCount(2)
        .setIntervalSec(30)
        .setParamItem(new GatewayParamFlowItem()
            .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
            .setFieldName("type")
            .setPattern("warn")
            .setMatchStrategy(SentinelGatewayConstants.PARAM_MATCH_STRATEGY_CONTAINS)
        )
    );

    rules.add(new GatewayFlowRule("some_customized_api")
        .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
        .setCount(5)
        .setIntervalSec(1)
        .setParamItem(new GatewayParamFlowItem()
            .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
            .setFieldName("pn")
        )
    );
    GatewayRuleManager.loadRules(rules);
    GatewayCallbackManager.setBlockHandler((BlockRequestHandler) (exchange, t) -> {
      ServerHttpResponse exchangeResponse = exchange.getResponse();
      exchangeResponse.getHeaders().add("Content-Type", "application/json;charset=utf8");

      byte[] content = "{\"code\":404,\"msg\":\"限流了\"}".getBytes(StandardCharsets.UTF_8);
      DataBuffer wrap = exchangeResponse.bufferFactory().wrap(content);
      exchangeResponse.writeWith(Mono.just(wrap));
      return Mono.empty();
    });
  }

  // 熔断规则

}
