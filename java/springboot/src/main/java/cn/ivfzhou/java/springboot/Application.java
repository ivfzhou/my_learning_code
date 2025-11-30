package cn.ivfzhou.java.springboot;

import java.io.IOException;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import cn.ivfzhou.java.springboot.filter.MyFilter;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@MapperScan("cn.ivfzhou.java.springboot.mapper")
public class Application {

    public static void main(String[] args) {
        /*
        var app = new SpringApplication(Application.class);
        var properties = new Properties();
        properties.load(Application.class.getClassLoader().getResourceAsStream("application.properties"));
        app.setDefaultProperties(properties);
        app.run(args);
        */

        SpringApplication.run(Application.class, args);
    }

    @Bean
    public FilterRegistrationBean<MyFilter> init() {
        var filter = new FilterRegistrationBean<MyFilter>();
        filter.setFilter(new MyFilter());
        filter.addUrlPatterns("/*");
        filter.setName("MyFilter");
        return filter;
    }
}
