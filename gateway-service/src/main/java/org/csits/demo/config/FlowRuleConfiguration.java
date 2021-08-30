package org.csits.demo.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

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
  }

  private void initGatewayRules() {
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
  }
}
