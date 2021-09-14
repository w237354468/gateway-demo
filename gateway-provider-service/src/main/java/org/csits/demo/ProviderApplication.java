package org.csits.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author weizhiqiang
 */
@SpringCloudApplication
public class ProviderApplication{
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
