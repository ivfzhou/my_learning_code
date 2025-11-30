package cn.ivfzhou.java.redis.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ivfzhou.java.redis.springboot.bean.User;

@Controller
public class RedisController {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisController(@Autowired RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @RequestMapping("/get/{key}")
    @ResponseBody
    public String get(@PathVariable("key") String key) {
        System.out.println("get:" + key);
        return redisTemplate.opsForValue().get(key).toString();
    }

    @RequestMapping("/set/{key}/{value}")
    @ResponseBody
    public void set(@PathVariable("key") String key, @PathVariable("value") String value) {
        System.out.println("set key:" + key + ",value:" + value);
        redisTemplate.opsForValue().set(key, value);
    }

    @RequestMapping(value = "/hset/{key}/{hkey}", consumes = {"application/json"})
    @ResponseBody
    public Boolean hset(@PathVariable("key") String key, @PathVariable("hkey") String hkey, @RequestBody User value) {
        System.out.println("hset key:" + key + ",hkey:" + hkey + ",value:" + value);
        return redisTemplate.opsForHash().putIfAbsent(key, hkey, value);
    }

    @RequestMapping("/hget/{key}/{hkey}")
    @ResponseBody
    public User hget(@PathVariable("key") String key, @PathVariable("hkey") String hkey) {
        return (User) redisTemplate.opsForHash().get(key, hkey);
    }

}
