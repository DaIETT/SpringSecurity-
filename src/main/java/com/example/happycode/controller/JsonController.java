package com.example.happycode.controller;


import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsonController {

    @Secured({"ROLE_vip","admin"})  //必需以ROLE_开头   只能角色控制  数组类型可以加多个(表示any)
    @RequestMapping("/test")
    public String test() {
        return "{\"msg\":\"你好\"}";
    }

    @RequestMapping("/test2")
    @PreAuthorize("hasAuthority('admin')")  //这里还只能用单引号哈  多种控制哈
    public String test2() {
        return "{\"msg\":\"你好2\"}";
    }
}
