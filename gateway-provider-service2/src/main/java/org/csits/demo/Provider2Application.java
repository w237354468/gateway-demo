package org.csits.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.SpringCloudApplication;

@SpringCloudApplication
public class Provider2Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Provider2Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }
}