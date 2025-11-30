package cn.ivfzhou.java.springcloud.bus.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RefreshScope
public class RefreshController {

    @Value("${business.config.key}")
    private String name;

    @RequestMapping("/get")
    @ResponseBody
    public String get() {
        return name;
    }

}
