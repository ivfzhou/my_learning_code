package cn.ivfzhou.java.nacos.springboot;

import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.discovery.EnableNacosDiscovery;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableNacosDiscovery
@EnableNacosConfig
@SpringBootApplication
public class NacosSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosSpringBootApplication.class, args);
    }

}
