package org.apache.logging.log4j.core.handler;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.domain.ErrorLogInfo;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.logenum.LogType;

public class LevelErrorhandler extends EventHandler {

    @Override
    public void handleEvent(LogEvent event, StringBuilder toAppendTo) {

        ErrorLogInfo errorLogInfo = new ErrorLogInfo();

        fillGenericFields(errorLogInfo, event);

        ThrowableProxy thrownProxy = event.getThrownProxy();

        Throwable throwable = event.getThrown();

        if (throwable != null && thrownProxy != null) {

            errorLogInfo.setType(LogType.LOG_ERROR.getTypeName());

            errorLogInfo.setErrorName(thrownProxy.getName());
            errorLogInfo.setErrorMsg(thrownProxy.getMessage());
            errorLogInfo.setErrorStack(parseExceptionToString(throwable));
        }

        String message = event.getMessage().getFormattedMessage();
        errorLogInfo.setMsg(message);

        toAppendTo.append(JSON.toJSONString(errorLogInfo));
    }

    private String parseExceptionToString(Throwable e) {

        try {
            StringBuilder s = new StringBuilder(e.toString() + "/n");
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement element : stackTrace) {
                s.append("\tat ").append(element.toString());
            }

            for (Throwable throwable : e.getSuppressed()) {
                s.append(parseExceptionToString(throwable));
            }

            Throwable cause = e.getCause();

            if (cause != null) s.append(parseExceptionToString(cause));

            s.append("\n");

            return s.substring(0, 500);

        } catch (Exception exception) {
            exception.printStackTrace();
            return "ParseException转换异常" + exception;
        }
    }
}
