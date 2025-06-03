package com.hoangloc.homilux.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestLoginController {

    @GetMapping("/login")
    public String login() {
        return "Wellcome";
    }

}
