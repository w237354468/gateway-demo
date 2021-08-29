package org.csits.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController2 {

    @GetMapping("/echo/{str}")
    public String echo(@PathVariable String str) throws InterruptedException {
        Thread.sleep(100);
        return "This is provider 2 service, echo : " + str;
    }

    @GetMapping("/divide")
    public String divide(@RequestParam Integer a, @RequestParam Integer b) {
        return String.valueOf(a / b);
    }

}
