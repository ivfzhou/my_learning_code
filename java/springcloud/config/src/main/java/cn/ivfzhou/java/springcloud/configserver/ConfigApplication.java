package cn.ivfzhou.java.springcloud.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigApplication {

    static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class);
    }

}
