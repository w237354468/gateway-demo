package org.csits.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.csits.demo.service.App2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @Autowired
    App2Service app2Service;

    @GetMapping("/echo/{str}")
    public String echo(@PathVariable String str) {
        log.info("This is provider 1 service, echo : {}", str);
        return app2Service.echo(str);
    }

    @GetMapping("/divide")
    public String divide(@RequestParam Integer a, @RequestParam Integer b) {
        return String.valueOf(a / b);
    }

}
