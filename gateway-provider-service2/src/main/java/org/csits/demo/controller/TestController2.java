package org.csits.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.annotation.LogAspect;
import org.apache.logging.log4j.core.logenum.LogType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@LogAspect(LogType.LOG_REQUEST)
@RestController
public class TestController2 {

    @GetMapping("/echo/{str}")
    public String echo(@PathVariable String str) {
        log.info("This is provider 2 service, echo : {}", str);
        return "This is provider 2 service, echo : " + str;
    }

    @GetMapping("/divide")
    public String divide(@RequestParam Integer a, @RequestParam Integer b) {
        return String.valueOf(a / b);
    }

}