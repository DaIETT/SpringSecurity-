package com.example.happycode.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @RequestMapping("/toMain")
    public String toMain() {
        return "redirect:main.html";
    }

    @RequestMapping("/toWrong")
    public String toWrong() {
        return "redirect:wrong.html";
    }


    /**
     * thymeleaf 必须通过走controller
     * @return
     */
    @RequestMapping("/toDemo")
    public String toDemo() {
        return "demo";
    }

}
