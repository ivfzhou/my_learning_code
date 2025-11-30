package cn.ivfzhou.java.redis.spring;

import java.time.Duration;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import cn.ivfzhou.java.redis.spring.bean.User;

public final class SpringRedisTest {

    public static void main(String[] args) {
        xml();
    }

    public static void json() {
        var ap = new ClassPathXmlApplicationContext("application.xml");
        RedisTemplate<String, User> redisTemplate = ap.getBean("redisTemplate", RedisTemplate.class);
        redisTemplate.opsForValue().set("hello", new User().setAge(18).setName("ivfzhou"), Duration.ofSeconds(120));
        User user = redisTemplate.opsForValue().get("hello");
        System.out.println(user);
    }

    public static void str() {
        var ap = new ClassPathXmlApplicationContext("application.xml");
        RedisTemplate<String, String> redisTemplate = ap.getBean("redisTemplate2", RedisTemplate.class);
        redisTemplate.opsForValue().set("hello2", "world", Duration.ofSeconds(120));
        System.out.println(redisTemplate.opsForValue().get("hello2"));
    }

    public static void jdk() {
        var ap = new ClassPathXmlApplicationContext("application.xml");
        RedisTemplate<String, User> redisTemplate = ap.getBean("redisTemplate3", RedisTemplate.class);
        redisTemplate.opsForValue().set("hello3", new User().setAge(18).setName("ivfzhou"), Duration.ofSeconds(120));
        System.out.println(redisTemplate.opsForValue().get("hello3"));
    }

    public static void generic() {
        var ap = new ClassPathXmlApplicationContext("application.xml");
        RedisTemplate<String, User> redisTemplate = ap.getBean("redisTemplate4", RedisTemplate.class);
        redisTemplate.opsForValue().set("hello4", new User().setAge(18).setName("ivfzhou"), Duration.ofSeconds(120));
        System.out.println(redisTemplate.opsForValue().get("hello4"));
    }

    public static void xml() {
        var ap = new ClassPathXmlApplicationContext("application.xml");
        RedisTemplate<String, User> redisTemplate = ap.getBean("redisTemplate5", RedisTemplate.class);
        redisTemplate.opsForValue().set("hello5", new User().setAge(18).setName("ivfzhou"), Duration.ofSeconds(120));
        System.out.println(redisTemplate.opsForValue().get("hello5"));
    }

}
