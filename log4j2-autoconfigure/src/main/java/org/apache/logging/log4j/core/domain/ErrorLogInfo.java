package org.apache.logging.log4j.core.domain;

import lombok.Data;

@Data
public class ErrorLogInfo extends LogInfo {

    private String errorMsg;

    private String errorStack;

    private String errorName;

    private Long ThreadId;

    private String threadName;

}
