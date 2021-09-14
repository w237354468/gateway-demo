package org.csits.demo;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

@EnableAdminServer
@SpringCloudApplication
public class GatewayAdminApplication {

  public static void main(String[] args) {

    SpringApplication.run(GatewayAdminApplication.class, args);
  }
}
