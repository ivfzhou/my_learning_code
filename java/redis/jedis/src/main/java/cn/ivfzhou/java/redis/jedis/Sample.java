package cn.ivfzhou.java.redis.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Connection;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.RedisClusterClient;

import java.time.Duration;
import java.util.HashSet;

public final class Sample {

    static void main(String[] args) {
        connectionWithPool();
    }

    private static void basicConnection() {
        var config = DefaultJedisClientConfig.builder().user("ivfzhou").password("123456").database(0).build();
        var client = RedisClient.builder().hostAndPort("127.0.0.1", 6379).clientConfig(config).build();
        try (client) {
            System.out.println("ping " + client.ping());
        }
    }

    private static void connectionWithPool() {
        var poolConfig = new GenericObjectPoolConfig<Connection>();
        poolConfig.setMaxTotal(64);                     // 池中最大连接总数。
        poolConfig.setMaxIdle(8);                       // 最大空闲连接数。
        poolConfig.setMinIdle(1);                       // 最小空闲连接数。
        poolConfig.setMaxWait(Duration.ofSeconds(3));   // 获取连接的最大等待时间。
        var config = DefaultJedisClientConfig.builder().user("ivfzhou").password("123456").database(0).build();
        var client = RedisClient.builder().hostAndPort("127.0.0.1", 6379).poolConfig(poolConfig).clientConfig(config).build();
        try (client) {
            System.out.println("ping " + client.ping());
        }
    }

    private static void connectionWithCluster() {
        var nodes = new HashSet<HostAndPort>();
        nodes.add(new HostAndPort("127.0.0.1", 6379));
        var client = RedisClusterClient.builder()
                .nodes(nodes)
                .clientConfig(DefaultJedisClientConfig.builder().user("ivfzhou").password("123456").database(0).build())
                .maxAttempts(5)                                  // 命令最大重试次数。
                .topologyRefreshPeriod(Duration.ofSeconds(30))   // 集群拓扑刷新周期。
                .build();
        try (client) {
            System.out.println("ping " + client.ping());
        }
    }

}
