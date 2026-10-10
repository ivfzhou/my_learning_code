package cn.ivfzhou.java.dubbo.springboot.provider.service;

import cn.ivfzhou.java.dubbo.springboot.protocol.IUserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class UserService implements IUserService {

    public String ping() {
        System.out.println("OK");
        return "pong";
    }

}
