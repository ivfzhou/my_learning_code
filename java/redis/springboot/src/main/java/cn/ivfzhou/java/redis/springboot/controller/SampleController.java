package cn.ivfzhou.java.redis.springboot.controller;

import cn.ivfzhou.java.redis.springboot.bean.User;
import cn.ivfzhou.java.redis.springboot.service.SampleService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/redis")
public class SampleController {

    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("result", sampleService.ping());
    }

    @PostMapping("/json/{key}")
    public Map<String, Object> saveJson(
            @PathVariable(name = "key") String key,
            @RequestParam(defaultValue = "1", required = false, name = "id") Long id,
            @RequestParam(defaultValue = "ivfzhou", required = false, name = "name") String name,
            @RequestParam(defaultValue = "1800", required = false, name = "ttlSeconds") long ttlSeconds
    ) {
        var user = new User(id, name, 18, name + "@126.com", List.of("java", "redis", "spring"));
        sampleService.setJson(key, user, Duration.ofSeconds(ttlSeconds));
        return Map.of("key", key, "saved", user, "readBack", sampleService.getJson(key));
    }

    @GetMapping("/json/{key}")
    public Object getJson(@PathVariable(name = "key") String key) {
        return sampleService.getJson(key);
    }

    @PostMapping("/xml/{key}")
    public Map<String, Object> saveXml(
            @PathVariable(name = "key") String key,
            @RequestParam(name = "ttlSeconds", defaultValue = "1800") long ttlSeconds
    ) {
        var user = new User(2L, "ivfzhou", 20, "ivfzhou@126.com", List.of("xml", "redis"));
        sampleService.setXml(key, user, Duration.ofSeconds(ttlSeconds));
        return Map.of("key", key, "saved", user, "readBack", sampleService.getXml(key));
    }

    @GetMapping("/xml/{key}")
    public Object getXml(@PathVariable(name = "key") String key) {
        return sampleService.getXml(key);
    }

    @GetMapping("/raw/{key}")
    public Map<String, Object> raw(@PathVariable(name = "key") String key) {
        return Map.of("key", key, "rawValue", sampleService.getString(key));
    }

    @PostMapping("/string/{key}")
    public Map<String, Object> saveString(
            @PathVariable(name = "key") String key,
            @RequestParam(name = "value") String value
    ) {
        sampleService.setString(key, value, Duration.ofMinutes(10));
        return Map.of("key", key, "readBack", sampleService.getString(key));
    }

    @PostMapping("/hash/{key}/{field}")
    public Map<String, Object> putHash(
            @PathVariable(name = "key") String key,
            @PathVariable(name = "field") String field,
            @RequestParam(name = "value") String value
    ) {
        sampleService.putHash(key, field, value);
        return Map.of("key", key, "entries", sampleService.getHash(key));
    }

    @DeleteMapping("/{key}")
    public Map<String, Object> delete(@PathVariable(name = "key") String key) {
        return Map.of("key", key, "deleted", sampleService.delete(key));
    }

}
