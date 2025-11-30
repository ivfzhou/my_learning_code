package cn.ivfzhou.java.springmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

    @RequestMapping("/toRedirectPage")
    public String toRedirectPage() {
        System.out.println("toRedirectPage");
        return "redirect:redirect";
    }

    @RequestMapping("/redirect")
    public String redirect() {
        System.out.println("toRedirectPage");
        return "redirect";
    }

    @RequestMapping("/toForwardPage")
    public String toForwardPage() {
        return "forward:forward.html";
    }

    @CrossOrigin("http://otherhost:8080")
    @ResponseBody
    @RequestMapping("/allowCross")
    public void allowCross() {
        System.out.println("allowCross");
    }

    @ResponseBody
    @RequestMapping("/ping")
    public String ping(@RequestBody String body) {
        return "ping " + body;
    }

}
