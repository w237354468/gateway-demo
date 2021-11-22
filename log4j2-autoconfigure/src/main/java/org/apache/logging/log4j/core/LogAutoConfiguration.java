package org.apache.logging.log4j.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.configure.CustomerLogConfiguration;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.CustomerJsonLayout;
import org.apache.logging.log4j.spi.StandardLevel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.stream.Stream;

import static org.apache.logging.log4j.core.constant.ConfigConstant.LOGGING_FILE_PATH;

@Slf4j
@Configuration
@EnableConfigurationProperties(CustomerLogConfiguration.class)
public class LogAutoConfiguration implements ApplicationContextAware {

    private String loggingFilePath;
    @Autowired
    CustomerLogConfiguration customerLogConfiguration;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Environment environment = applicationContext.getEnvironment();
        loggingFilePath = environment.getProperty(LOGGING_FILE_PATH, "./logs");
        // 如果开启json，那么就打开json
        if (customerLogConfiguration.isJsonEnable()) {
            org.apache.logging.log4j.spi.LoggerContext context = LogManager.getContext(false);
            final LoggerContext ctx = (LoggerContext) context;
            final org.apache.logging.log4j.core.config.Configuration configuration = ctx.getConfiguration();

            // 添加三个RollingFileAppender
            // InfoAppender
            RollingFileAppender jsonInfoRollingAppender = RollingFileAppender.newBuilder()
                    .setName("jsonInfoRollingAppender")
                    .setConfiguration(configuration)
                    .withFileName(getFileNamePattern(customerLogConfiguration.getJsonInfoFilename()))
                    .withFilePattern(getFilePattern())
                    .setLayout(CustomerJsonLayout.newBuilder().build())
                    .withPolicy(TimeBasedTriggeringPolicy.newBuilder().build())
                    .withPolicy(SizeBasedTriggeringPolicy.createPolicy(customerLogConfiguration.getFileRollingSize()))
                    .setFilter(ThresholdFilter.createFilter(Level.INFO, Filter.Result.ACCEPT, Filter.Result.DENY))
                    .build();

            // WarnAppender
            RollingFileAppender jsonWarnRollingAppender = RollingFileAppender.newBuilder()
                    .setName("jsonWarnRollingAppender")
                    .setConfiguration(configuration)
                    .withFileName(getFileNamePattern(customerLogConfiguration.getJsonInfoFilename()))
                    .withFilePattern(getFilePattern())
                    .setLayout(CustomerJsonLayout.newBuilder().build())
                    .withPolicy(TimeBasedTriggeringPolicy.newBuilder().build())
                    .withPolicy(SizeBasedTriggeringPolicy.createPolicy(customerLogConfiguration.getFileRollingSize()))
                    .setFilter(ThresholdFilter.createFilter(Level.WARN, Filter.Result.ACCEPT, Filter.Result.DENY))
                    .build();

            // ErrorAppender
            RollingFileAppender jsonErrorRollingAppender = RollingFileAppender.newBuilder()
                    .setName("jsonErrorRollingAppender")
                    .setConfiguration(configuration)
                    .withFileName(getFileNamePattern(customerLogConfiguration.getJsonInfoFilename()))
                    .withFilePattern(getFilePattern())
                    .setLayout(CustomerJsonLayout.newBuilder().build())
                    .withPolicy(TimeBasedTriggeringPolicy.newBuilder().build())
                    .withPolicy(SizeBasedTriggeringPolicy.createPolicy(customerLogConfiguration.getFileRollingSize()))
                    .setFilter(ThresholdFilter.createFilter(Level.ERROR, Filter.Result.ACCEPT, Filter.Result.DENY))
                    .build();

            Stream.of(jsonInfoRollingAppender, jsonWarnRollingAppender, jsonErrorRollingAppender)
                    .peek(configuration::addAppender)
                    .forEach(RollingFileAppender::start);

            // 所有的logger都接入
            for (LoggerConfig loggerConfig : configuration.getLoggers().values()) {

                if (loggerConfig.getLevel().intLevel() >= StandardLevel.ERROR.intLevel()) {
                    loggerConfig.addAppender(jsonErrorRollingAppender, loggerConfig.getLevel(), loggerConfig.getFilter());
                }
                if (loggerConfig.getLevel().intLevel() >= StandardLevel.WARN.intLevel()) {
                    loggerConfig.addAppender(jsonWarnRollingAppender, loggerConfig.getLevel(), loggerConfig.getFilter());
                }
                if (loggerConfig.getLevel().intLevel() >= StandardLevel.INFO.intLevel()) {
                    loggerConfig.addAppender(jsonInfoRollingAppender, loggerConfig.getLevel(), loggerConfig.getFilter());
                }
            }
            ctx.updateLoggers(configuration);
        }
    }

    private String getFileNamePattern(String filePrefix) {
        String dirName = customerLogConfiguration.getJsonDirName();
        return loggingFilePath + "/" + dirName + "/" + filePrefix + ".log";
    }

    private String getFilePattern() {
        return loggingFilePath + "/" + customerLogConfiguration.getJsonDirName()
                + "/" + "$${date:yyyy-MM}/app-%d{MM-dd-yyyy}-%i.log.gz";
    }

}
