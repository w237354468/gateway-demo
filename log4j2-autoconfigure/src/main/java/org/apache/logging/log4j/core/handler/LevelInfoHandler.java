package org.apache.logging.log4j.core.handler;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.domain.LogInfo;
import org.apache.logging.log4j.core.logenum.LogType;

public class LevelInfoHandler extends EventHandler {
    @Override
    public void handleEvent(LogEvent event, StringBuilder toAppendTo) {
        LogInfo logInfo = new LogInfo();

        fillGenericFields(logInfo, event);

        String type = LogType.LOG_RECORD.getTypeName();
        String msg = event.getMessage().getFormattedMessage();

        logInfo.setType(type);
        logInfo.setMsg(msg);

        toAppendTo.append(JSON.toJSONString(logInfo));
    }
}
