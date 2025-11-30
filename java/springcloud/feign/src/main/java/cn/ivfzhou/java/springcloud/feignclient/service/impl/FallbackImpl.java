package cn.ivfzhou.java.springcloud.feignclient.service.impl;

import org.springframework.stereotype.Component;

import cn.ivfzhou.java.springcloud.feignclient.service.IFeignService;

@Component
public class FallbackImpl implements IFeignService {

    @Override
    public String run(String params, String body) {
        System.out.println("FallbackImpl#run " + params + " " + body);
        return "降级了";
    }

}
