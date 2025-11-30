package cn.ivfzhou.java.nacos.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NacosSpringCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosSpringCloudApplication.class);
    }

}
