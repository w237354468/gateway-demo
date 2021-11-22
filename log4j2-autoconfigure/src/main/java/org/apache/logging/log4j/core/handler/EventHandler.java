package org.apache.logging.log4j.core.handler;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.constant.ConfigConstant;
import org.apache.logging.log4j.core.domain.LogInfo;
import org.apache.logging.log4j.core.domain.TraceInfo;
import org.apache.logging.log4j.core.domain.UserInfo;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import java.net.InetAddress;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.apache.logging.log4j.core.constant.ConfigConstant.*;

public abstract class EventHandler {

    private static final String SERVER_NAME = System.getProperty(SW_AGENT_SERVICE_NAME, DEFAULT_EMPTY_VALUE);
    private static final String INSTANCE_NAME = System.getProperty(SW_AGENT_INSTANCE, DEFAULT_EMPTY_VALUE);

    private final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final String serverIp;

    public EventHandler() {

        InetAddress address = NetUtil.getLocalhost();
        serverIp = (null == address) ? DEFAULT_EMPTY_VALUE : address.getHostAddress();
    }

    // json行末换行符
    public String handle(LogEvent event) {
        StringBuilder builder = new StringBuilder();
        handleEvent(event, builder);
        return builder.append("\n").toString();
    }

    abstract public void handleEvent(LogEvent event, StringBuilder toAppendTo);

    void fillGenericFields(LogInfo logInfo, LogEvent event) {

        ReadOnlyStringMap contextData = event.getContextData();
        logInfo.setType(contextData.getValue(ConfigConstant.LOG_TYPE));

        logInfo.setLevel(event.getLevel().name());
        logInfo.setServerIp(serverIp);
        logInfo.setOperationTime(formatMills(event.getTimeMillis()));
        logInfo.setThreadId(event.getThreadId());
        logInfo.setThreadName(event.getThreadName());

        logInfo.setAppName(SERVER_NAME);
        logInfo.setInstanceName(INSTANCE_NAME);
        logInfo.setClassName(event.getLoggerName());

        TraceInfo traceInfo = new TraceInfo();
        traceInfo.setTraceId(contextData.getValue(ConfigConstant.TRACE_ID));
        traceInfo.setSpanId(contextData.getValue(ConfigConstant.SPAN_ID));
        traceInfo.setSegmentId(contextData.getValue(ConfigConstant.SEGMENT_ID));
        logInfo.setTraceInfo(traceInfo);

        UserInfo userInfo = new UserInfo();
        userInfo.setRemoteIp(getStrOrDefault(contextData, ConfigConstant.REMOTE_IP));
        userInfo.setRequestUri(getStrOrDefault(contextData, ConfigConstant.REQUEST_URI));
        userInfo.setUserAgent(getStrOrDefault(contextData, ConfigConstant.USER_AGENT));
        userInfo.setUsername(getStrOrDefault(contextData, ConfigConstant.USER_NAME));
        logInfo.setUserInfo(userInfo);

    }

    private String getStrOrDefault(ReadOnlyStringMap map, String key) {
        String value = map.getValue(key);
        return StrUtil.isEmpty(value) ? DEFAULT_EMPTY_VALUE : value;
    }

    private String formatMills(long timeMills) {

        return Instant.ofEpochMilli(timeMills)
                .atZone(ZoneOffset.ofHours(8))
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }
}
