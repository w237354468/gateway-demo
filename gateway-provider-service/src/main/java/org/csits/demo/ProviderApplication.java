package org.csits.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author weizhiqiang
 */
@SpringCloudApplication
@EnableFeignClients
@EnableScheduling
public class ProviderApplication{
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
