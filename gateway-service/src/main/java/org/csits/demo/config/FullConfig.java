package org.csits.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.client.TomcatWebSocketClient;
import org.springframework.web.reactive.socket.server.upgrade.TomcatRequestUpgradeStrategy;

//@Configuration
//public class FullConfig {
//
//    @Bean
//    public TomcatWebSocketClient tomcatWebSocketClient(){
//        return new TomcatWebSocketClient();
//    }
//
//    @Bean
//    public TomcatRequestUpgradeStrategy tomcatRequestUpgradeStrategy(){
//        return new TomcatRequestUpgradeStrategy();
//    }
//}
