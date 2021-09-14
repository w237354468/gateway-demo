package org.csits.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @GetMapping("/echo/{str}")
  public String echo(@PathVariable String str, @RequestHeader("Authentication-JWT") String jwt)
      throws InterruptedException {
    Thread.sleep(100);
    System.out.println("收到JWT:" + jwt);
    return "This is provider 1 service, echo : " + str;
  }

  @GetMapping("/divide")
  public String divide(@RequestParam Integer a, @RequestParam Integer b) {
    return String.valueOf(a / b);
  }

}
