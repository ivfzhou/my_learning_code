package cn.ivfzhou.java.dubbo.spring.consumer;

import cn.ivfzhou.java.dubbo.spring.protocol.IUserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 服务消费方启动入口。
 *
 * <p>通过加载 Spring XML 配置，从注册中心订阅远程服务，并调用其方法。</p>
 */
public class ConsumerApplication {

    static void main(String[] args) {
        // 加载并启动 Spring 容器，XML 中的 <dubbo:reference> 会生成远程服务代理 Bean。
        var context = new ClassPathXmlApplicationContext("applicationContext-dubbo.xml");
        context.start();

        // 从 Spring 容器中取出远程服务代理并调用。
        IUserService userService = context.getBean(IUserService.class);
        String result = userService.ping();
        System.out.println("调用远程服务结果：" + result);

        context.close();
    }

}
