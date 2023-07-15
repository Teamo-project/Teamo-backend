package com.teamo.teamo.controller;

import com.teamo.teamo.api.DemoApi;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController implements DemoApi {

    @Override
    public String test() {
        return "test 성공";
    }
}
