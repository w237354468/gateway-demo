package org.apache.logging.log4j.core.aspect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.logging.log4j.core.annotation.LogAspect;
import org.apache.logging.log4j.core.constant.ConfigConstant;
import org.apache.logging.log4j.core.context.LogContextHolder;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.apache.logging.log4j.core.constant.ConfigConstant.DEFAULT_EMPTY_VALUE;

@Aspect
public class LogRecordAspect {

    private final String USER_AGENT = "User-Agent";

    @Around("@annotation(org.apache.logging.log4j.core.annotation.LogAspect) " +
            "|| within(@org.apache.logging.log4j.core.annotation.LogAspect *)")
    public Object logRecord(ProceedingJoinPoint pjp) throws Throwable {

        try {
            // 先从方法上拿
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            LogAspect logAspect = signature.getMethod().getAnnotation(LogAspect.class);

            // 方法上没有再取类上
            if (ObjectUtil.isNull(logAspect)) {
                logAspect = pjp.getTarget().getClass().getAnnotation(LogAspect.class);
            }

            // 获取日志类型 添加到Log4j上下文
            LogContextHolder.putLogValue(ConfigConstant.LOG_TYPE, logAspect.value().getTypeName());

            addTraceInfoIntoContext();

            addRequestInfoIntoContext();

            return pjp.proceed();

        } finally {
            LogContextHolder.removeAll();
        }
    }

    private void addTraceInfoIntoContext() {

        // 获取Trace信息
        LogContextHolder.putLogValue(ConfigConstant.TRACE_ID, TraceContext.traceId());
        LogContextHolder.putLogValue(ConfigConstant.SPAN_ID, String.valueOf(TraceContext.spanId()));
        LogContextHolder.putLogValue(ConfigConstant.SEGMENT_ID, TraceContext.segmentId());
    }

    private void addRequestInfoIntoContext() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes != null) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();

            LogContextHolder.putLogValue(ConfigConstant.USER_NAME, formatString(request.getRemoteUser()));
            LogContextHolder.putLogValue(ConfigConstant.USER_AGENT, formatString(request.getHeader(USER_AGENT)));
            LogContextHolder.putLogValue(ConfigConstant.REMOTE_IP, formatString(request.getRemoteAddr()));
            LogContextHolder.putLogValue(ConfigConstant.REQUEST_URI, formatString(request.getRemoteAddr()));
        }
    }

    private String formatString(@Nullable String str) {
        return StrUtil.isEmpty(str) ? DEFAULT_EMPTY_VALUE : str;
    }
}
