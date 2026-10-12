package cn.ivfzhou.java.springcloud.service1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Service1Application {

    static void main(String[] args) {
        SpringApplication.run(Service1Application.class);
    }

}
