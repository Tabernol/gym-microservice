package com.krasnopolskyi.security.http.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fit-coach/auth")
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "test in security";
    }
}
