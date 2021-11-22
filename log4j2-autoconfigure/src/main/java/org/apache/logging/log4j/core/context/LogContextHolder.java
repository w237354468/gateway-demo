package org.apache.logging.log4j.core.context;

import cn.hutool.core.util.StrUtil;
import org.apache.logging.log4j.ThreadContext;

import static org.apache.logging.log4j.core.constant.ConfigConstant.DEFAULT_EMPTY_VALUE;

public class LogContextHolder {

    public static void putLogValue(String key, String value) {
        ThreadContext.put(key, value);
    }

    public static String getLogValue(String key) {
        String result = ThreadContext.get(key);
        return StrUtil.isBlank(result) ? DEFAULT_EMPTY_VALUE : result;
    }

    public static void remove(String key) {
        ThreadContext.remove(key);
    }

    public static void removeAll() {
        ThreadContext.clearAll();
    }
}
