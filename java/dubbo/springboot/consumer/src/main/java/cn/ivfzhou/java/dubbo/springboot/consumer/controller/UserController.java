package cn.ivfzhou.java.dubbo.springboot.consumer.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ivfzhou.java.dubbo.springboot.protocol.IUserService;

@Controller
public class UserController {

    @DubboReference
    private IUserService userService;

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return userService.ping();
    }

}
