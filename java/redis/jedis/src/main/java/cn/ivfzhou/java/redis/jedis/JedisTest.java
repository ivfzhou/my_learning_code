package cn.ivfzhou.java.redis.jedis;

import java.time.Duration;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class JedisTest {

    public static void main(String[] args) {
        System.out.println(operateTimesPerSecond());

        /*
        try (var jedisPool = newPool()) {
            var resource = jedisPool.getResource();
            resource.set("hello", "world");
            System.out.println(resource.get("hello"));
        }
        */
    }

    public static JedisPool newPool() {
        var poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1000);
        poolConfig.setMaxIdle(100);
        poolConfig.setMinIdle(1);
        poolConfig.setMaxWait(Duration.ofMillis(2000));
        return new JedisPool(poolConfig, "ivfzhoudockerredis", 6379);
    }

    public static int operateTimesPerSecond() {
        try (var jedis = new Jedis("ivfzhoudockerredis", 6379, 3000/*毫秒*/)) {
            int i = 0;
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 1000) {
                jedis.set("test" + i, i + "");
                i++;
            }
            return i;
        }
    }

}
