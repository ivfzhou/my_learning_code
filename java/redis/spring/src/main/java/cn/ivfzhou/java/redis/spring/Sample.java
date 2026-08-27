package cn.ivfzhou.java.redis.spring;

import cn.ivfzhou.java.redis.spring.bean.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class Sample {

    static void main(String[] args) {
        var ctx = new ClassPathXmlApplicationContext("application.xml");
        str(ctx);
        json(ctx);
        jdk(ctx);
        xml(ctx);
        ctx.close();
    }

    private static void str(ApplicationContext ctx) {
        RedisTemplate<String, String> redisStringTemplate = ctx.getBean("redisStringTemplate", RedisTemplate.class);
        redisStringTemplate.opsForValue().set("test", "value", Duration.ofSeconds(120));
        System.out.println("redisStringTemplate get value " + redisStringTemplate.opsForValue().get("test"));
        redisStringTemplate.delete("test");
    }

    private static void json(ApplicationContext ctx) {
        RedisTemplate<String, User> redisJsonTemplate = ctx.getBean("redisJsonTemplate", RedisTemplate.class);
        redisJsonTemplate.opsForValue().set("test", new User(1L, "ivfzhou", 18, "ivfzhou@126.com", new ArrayList<>(List.of("dev", "java"))));
        var user = redisJsonTemplate.opsForValue().get("test");
        System.out.println("redisJsonTemplate get user " + user);
        redisJsonTemplate.delete("test");
    }

    private static void jdk(ApplicationContext ctx) {
        RedisTemplate<String, User> redisJdkTemplate = ctx.getBean("redisJdkTemplate", RedisTemplate.class);
        redisJdkTemplate.opsForValue().set("test", new User(1L, "ivfzhou", 18, "ivfzhou@126.com", new ArrayList<>(List.of("dev", "java"))));
        System.out.println("redisJdkTemplate get user " + redisJdkTemplate.opsForValue().get("test"));
        redisJdkTemplate.delete("test");
    }

    private static void xml(ApplicationContext ctx) {
        RedisTemplate<String, User> redisXmlTemplate = ctx.getBean("redisXmlTemplate", RedisTemplate.class);
        redisXmlTemplate.opsForValue().set("test", new User(1L, "ivfzhou", 18, "ivfzhou@126.com", new ArrayList<>(List.of("dev", "java"))));
        System.out.println("redisXmlTemplate get user " + redisXmlTemplate.opsForValue().get("test"));
        redisXmlTemplate.delete("test");
    }

}
