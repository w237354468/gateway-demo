package org.apache.logging.log4j.core.logenum;

public enum LogType {

    LOG_REQUEST("LOG_REQUEST"),

    LOG_RECORD("LOG_RECORD"),

    LOG_ERROR("LOG_ERROR"),

    LOG_SCHEDULER_TASK("LOG_SCHEDULER_TASK"),

    LOG_MESSAGE_CONSUMER("LOG_MESSAGE_CONSUME");

    private final String typeName;

    LogType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
