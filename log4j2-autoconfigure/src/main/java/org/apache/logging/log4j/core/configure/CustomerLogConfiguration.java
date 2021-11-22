package org.apache.logging.log4j.core.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "csits.log")
public class CustomerLogConfiguration {

    private boolean jsonEnable = false;

    private String jsonInfoFilename = "appInfoJson";

    private String jsonWarnFilename = "appWarnJson";

    private String jsonErrorFilename = "appErrorJson";

    private String jsonDirName = "json";

    private String fileRollingSize = "200MB";

}
