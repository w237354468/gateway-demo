package org.apache.logging.log4j.core.handler;

import cn.hutool.core.net.NetUtil;
import org.apache.logging.log4j.core.LogAutoConfiguration;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.domain.LogInfo;
import org.apache.logging.log4j.core.domain.TraceInfo;
import org.apache.logging.log4j.core.domain.UserInfo;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public abstract class EventHandler {

    private final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final String USER_AGENT = "User-Agent";

    private final String serverName;
    private final String instanceName;
    private final String serverIp;

    public EventHandler() {
        this.serverName = System.getProperty("skywalking.agent.service_name", "N/A");
        this.instanceName = System.getProperty("skywalking.agent.instance", "N/A");
        InetAddress address = NetUtil.getLocalhost();
        serverIp = (null == address) ? "N/A" : address.getHostAddress();
    }

    public String handle(LogEvent event) {
        StringBuilder builder = new StringBuilder();
        handleEvent(event, builder);
        return builder.append("\n").toString();
    }

    abstract public void handleEvent(LogEvent event, StringBuilder toAppendTo);

    void fillGenericFields(LogInfo logInfo, LogEvent event) {

        logInfo.setLevel(event.getLevel().name());
        logInfo.setServerIp(serverIp);
        logInfo.setOperationTime(formatMills(event.getTimeMillis()));

        logInfo.setAppName(LogAutoConfiguration.appName);
        logInfo.setInstanceName(instanceName);
        logInfo.setClassName(event.getLoggerName());
        logInfo.setTraceInfo(getTraceInfoFromSW());
        logInfo.setUserInfo(getRequestInfo());
    }

    private TraceInfo getTraceInfoFromSW() {
        String traceId = TraceContext.traceId();
        int spanId = TraceContext.spanId();
        String segmentId = TraceContext.segmentId();

        return new TraceInfo(traceId, spanId, segmentId);
    }

    private UserInfo getRequestInfo() {
        UserInfo userInfo = new UserInfo();

        try {
            RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) attributes;
            HttpServletRequest request = requestAttributes.getRequest();
            userInfo.setRemoteIp(request.getRemoteAddr());
            userInfo.setRequestUri(request.getRequestURI());
            userInfo.setUserAgent(request.getHeader(USER_AGENT));
            userInfo.setUser(request.getUserPrincipal().getName());
        } catch (Exception ignored) {
        }
        return userInfo;
    }

    private String formatMills(long timeMills) {

        return Instant.ofEpochMilli(timeMills)
                .atZone(ZoneOffset.ofHours(8))
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }
}
