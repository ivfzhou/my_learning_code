package cn.ivfzhou.java.redis.springboot.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class SampleService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisTemplate<String, Object> xmlRedisTemplate;

    private final StringRedisTemplate stringRedisTemplate;

    public SampleService(
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("xmlRedisTemplate")
            RedisTemplate<String, Object> xmlRedisTemplate,
            StringRedisTemplate stringRedisTemplate
    ) {
        this.redisTemplate = redisTemplate;
        this.xmlRedisTemplate = xmlRedisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void setJson(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public Object getJson(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setXml(String key, Object value, Duration ttl) {
        xmlRedisTemplate.opsForValue().set(key, value, ttl);
    }

    public Object getXml(String key) {
        return xmlRedisTemplate.opsForValue().get(key);
    }

    public void setString(String key, String value, Duration ttl) {
        stringRedisTemplate.opsForValue().set(key, value, ttl);
    }

    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void putHash(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public Map<Object, Object> getHash(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Boolean expire(String key, Duration ttl) {
        return redisTemplate.expire(key, ttl);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public String ping() {
        return stringRedisTemplate.getConnectionFactory().getConnection().ping();
    }
}
