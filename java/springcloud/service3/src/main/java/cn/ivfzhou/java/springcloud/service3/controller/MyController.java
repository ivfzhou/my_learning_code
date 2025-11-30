package cn.ivfzhou.java.springcloud.service3.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

    @Value("${server.port}")
    private int port;

    @RequestMapping("/get/{param}")
    @ResponseBody
    public String run(@PathVariable("param") String param, @RequestBody String body) {
        System.out.println("MyController.run " + body + " " + param);
        System.out.println("MyController exception");
        int x = 1 / 0;
        return port + "";
    }

}
