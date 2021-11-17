package org.csits.demo.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("provider-service2")
public interface App2Service {

    @GetMapping("/echo/{str}")
    String echo(@PathVariable("str") String str);
}
