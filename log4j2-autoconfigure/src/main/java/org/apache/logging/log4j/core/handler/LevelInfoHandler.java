package org.apache.logging.log4j.core.handler;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.domain.LogInfo;

public class LevelInfoHandler extends EventHandler {
    @Override
    public void handleEvent(LogEvent event, StringBuilder toAppendTo) {
        LogInfo logInfo = new LogInfo();

        fillGenericFields(logInfo, event);

        String msg = event.getMessage().getFormattedMessage();

        logInfo.setMsg(msg);

        toAppendTo.append(JSON.toJSONString(logInfo));
    }
}
