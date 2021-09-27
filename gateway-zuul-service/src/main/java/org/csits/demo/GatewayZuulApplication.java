package org.csits.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringCloudApplication
@EnableZuulProxy
//声明开启 Zuul 网关功能
public class GatewayZuulApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayZuulApplication.class);
  }
}
