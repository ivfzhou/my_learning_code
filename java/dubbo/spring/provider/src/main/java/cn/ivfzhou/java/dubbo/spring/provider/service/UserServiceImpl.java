package cn.ivfzhou.java.dubbo.spring.provider.service;

import cn.ivfzhou.java.dubbo.spring.protocol.IUserService;

/**
 * 服务提供方实现：实现公共接口 {@link IUserService}，作为最终对外暴露的远程服务。
 */
public class UserServiceImpl implements IUserService {

    @Override
    public String ping() {
        return "pong";
    }

}
