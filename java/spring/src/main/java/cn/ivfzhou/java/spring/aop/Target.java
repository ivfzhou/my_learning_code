package cn.ivfzhou.java.spring.aop;

import org.springframework.stereotype.Component;

@Component
public class Target {

    public String get() {
        System.out.println("target.get()");
        return "OK";
    }

}
