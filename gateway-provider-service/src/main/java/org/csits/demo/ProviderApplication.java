package org.csits.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author weizhiqiang
 */
@SpringCloudApplication
@EnableFeignClients
public class ProviderApplication{
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
