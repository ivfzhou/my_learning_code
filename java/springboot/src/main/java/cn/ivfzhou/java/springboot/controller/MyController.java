package cn.ivfzhou.java.springboot.controller;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.ivfzhou.java.springboot.service.MyService;

@RestController
public class MyController {

    @Value("${spring.application.name}")
    private String msg;

    @Resource
    private MyService myService;

    @RequestMapping("/getMsg")
    public String get() {
        System.out.println(myService.pingRedis());
        return msg;
    }

}
