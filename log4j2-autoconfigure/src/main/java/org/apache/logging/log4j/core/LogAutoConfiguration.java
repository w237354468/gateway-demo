package org.apache.logging.log4j.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.configure.CustomerLogConfiguration;
import org.apache.logging.log4j.core.layout.CustomerJsonLayout;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.core.constant.ConfigConstant.*;

@Slf4j
@Configuration
@EnableConfigurationProperties(CustomerLogConfiguration.class)
public class LogAutoConfiguration implements ApplicationContextAware {

    public static final String appName = "";
    @Autowired
    CustomerLogConfiguration customerLogConfiguration;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Environment environment = applicationContext.getEnvironment();

        environment.getProperty("spring.application.name");
        String mode = customerLogConfiguration.getMode();
        String loggingFilePath = environment.getProperty(LOGGING_FILE_PATH, "");

        String logDir = checkAndFormatLogDir(loggingFilePath);

        // 如果开启json，那么就用复制一份维json格式
        if (mode.equals("all")) {
            org.apache.logging.log4j.spi.LoggerContext context = LogManager.getContext(false);
            final LoggerContext ctx = (LoggerContext) context;
            final org.apache.logging.log4j.core.config.Configuration configuration = ctx.getConfiguration();

            Map<String, Appender> rollingFileAppenders = configuration.getAppenders().values().stream()
                    .filter(appender -> appender.getClass().getName().equals(RollingFileAppender.class.getName()))
                    .map(appender -> {

                        RollingFileAppender fileAppender = (RollingFileAppender) appender;
                        String jsonFileAppenderName = JSON_PREFIX + fileAppender.getName();

                        // 此时已解析
                        return RollingFileAppender.newBuilder()
                                .setName(jsonFileAppenderName)
                                .setConfiguration(configuration)
                                .withFileName(fileAppender.getFileName())
                                .withFilePattern(fileAppender.getFilePattern())
//                                .setLayout(PatternLayout.newBuilder()
//                                        .withPattern(DEFAULT_PATTERN_LAYOUT)
//                                        .withConfiguration(configuration)
//                                        .withCharset(Charset.forName(csitsLogConfiguration.getJsonLogCharset()))
//                                        .build())
                                .setLayout(CustomerJsonLayout.newBuilder().build())
                                .withFilePattern(fileAppender.getFilePattern())
                                .withPolicy(fileAppender.getTriggeringPolicy())
                                .withFilter(fileAppender.getFilter())
                                .build();
                    }).collect(Collectors.toMap(AbstractAppender::getName, e -> e));

            for (Appender appender : rollingFileAppenders.values()) {
                appender.start();
                configuration.addAppender(appender);
            }

            for (LoggerConfig loggerConfig : configuration.getLoggers().values()) {

                for (String s : loggerConfig.getAppenders().keySet()) {
                    String jsonKey = JSON_PREFIX + s;
                    if (rollingFileAppenders.containsKey(jsonKey)) {
                        loggerConfig.removeAppender(s);
                        loggerConfig.addAppender(rollingFileAppenders.get(jsonKey), loggerConfig.getLevel(), loggerConfig.getFilter());
                    }
                }
            }
            ctx.updateLoggers(configuration);
        }
    }

    private String getLogStoragePath(String logDir) {
        if (logDir.endsWith("/")) {
            return logDir + customerLogConfiguration.getJsonDirName();
        } else {
            return logDir + "/" + customerLogConfiguration.getJsonDirName();
        }
    }

    private String checkAndFormatLogDir(String logDir) {

        if (StringUtils.isEmpty(logDir)) {
            log.info("property [{}] not set, will use default logging path: [./logs]", LOGGING_FILE_PATH);
            return DEFAULT_LOGGING_FILE_PATH;
        }
        return logDir;
    }
}
