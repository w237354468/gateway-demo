package org.csits.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

@SpringCloudApplication
public class GatewaySentinelApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewaySentinelApplication.class, args);
  }
}
