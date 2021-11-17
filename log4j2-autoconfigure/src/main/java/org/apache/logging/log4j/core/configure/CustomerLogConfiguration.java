package org.apache.logging.log4j.core.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "csits.log")
public class CustomerLogConfiguration {

    private String mode = "default";

    private String jsonDirName = "json";

    private String defaultDirName = "default";

    private String jsonLogCharset = "utf-8";
}
