package org.csits.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController {

  @GetMapping("/cache/broken")
  public String broken(){
    return "circuit breaker !";
  }
}
