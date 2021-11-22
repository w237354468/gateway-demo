package org.apache.logging.log4j.core.annotation;

import org.apache.logging.log4j.core.logenum.LogType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface LogAspect {

    LogType value() default LogType.LOG_RECORD;

}
