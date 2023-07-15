package com.teamo.teamo.controller;

import com.teamo.teamo.api.DemoApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements DemoApi {

    @Override
    public String test() {
        return "test 성공";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin 전용";
    }

    @GetMapping("/guest")
    public String guest() {
        return "guest 전용";
    }
}
