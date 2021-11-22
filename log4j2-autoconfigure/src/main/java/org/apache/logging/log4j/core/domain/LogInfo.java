package org.apache.logging.log4j.core.domain;

import lombok.Data;

@Data
public class LogInfo extends DTO {

    private String type;

    private String className;

    private String level;

    private String appName;

    private String serverIp;

    private String instanceName;

    private TraceInfo traceInfo;

    private UserInfo userInfo;

    private String operationTime;

    private Long threadId;

    private String threadName;

    private Object msg;
}
