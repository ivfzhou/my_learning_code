package cn.ivfzhou.java.dubbo.spring.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 服务提供方启动入口。
 *
 * <p>通过加载 classpath 下的 Spring XML 配置，完成 Dubbo 服务的注册与暴露。
 * 启动后阻塞主线程，使服务保持运行，供消费方调用。</p>
 */
public class ProviderApplication {

    static void main(String[] args) throws Exception {
        // 加载并启动 Spring 容器，XML 中的 <dubbo:service> 会在此时向注册中心暴露服务。
        var context = new ClassPathXmlApplicationContext("applicationContext-dubbo.xml");
        context.start();

        System.out.println("Dubbo 服务提供方已启动，按回车键退出...");
        // 阻塞主线程，保持服务持续运行
        System.in.read();

        context.close();
    }

}
