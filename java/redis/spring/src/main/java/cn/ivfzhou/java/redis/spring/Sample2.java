package cn.ivfzhou.java.redis.spring;

import cn.ivfzhou.java.redis.spring.bean.User;
import cn.ivfzhou.java.redis.spring.configuration.RedisConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

public final class Sample2 {

    static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(RedisConfig.class)) {
            RedisTemplate<String, String> redisStringTemplate = ctx.getBean("redisStringTemplate", RedisTemplate.class);
            redisStringTemplate.opsForValue().set("test", "value");
            System.out.println("redisStringTemplate get value " + redisStringTemplate.opsForValue().get("test"));


            var user = new User(1L, "ivfzhou", 18, "ivfzhou@126.com", new ArrayList<>(List.of("dev", "java")));
            RedisTemplate<String, Object> redisJsonTemplate = ctx.getBean("redisJsonTemplate", RedisTemplate.class);
            redisJsonTemplate.opsForValue().set("test", user);
            var userFromJson = (User) redisJsonTemplate.opsForValue().get("test");
            System.out.println("redisJsonTemplate get value " + userFromJson);
            redisJsonTemplate.delete("test");

            RedisTemplate<String, Object> redisXmlTemplate = ctx.getBean("redisXmlTemplate", RedisTemplate.class);
            redisXmlTemplate.opsForValue().set("test", user);
            var userFromXml = (User) redisXmlTemplate.opsForValue().get("test");
            System.out.println("redisXmlTemplate get value " + userFromXml);
            redisXmlTemplate.delete("test");

        }
    }

}
