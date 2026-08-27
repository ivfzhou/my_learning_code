package cn.ivfzhou.java.redis.spring.configuration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisClusterConfig {

    @Bean
    public JedisConnectionFactory jedisClusterConnectionFactory() {
        var clusterConfig = new RedisClusterConfiguration()
                .clusterNode("127.0.0.1", 7000)
                .clusterNode("127.0.0.1", 7001)
                .clusterNode("127.0.0.1", 7002);
        clusterConfig.setMaxRedirects(5);   // MOVED/ASK 重定向次数上限。
        clusterConfig.setUsername("ivfzhou");
        clusterConfig.setPassword(RedisPassword.of("123456"));

        var poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(16);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(2);
        poolConfig.setMaxWait(Duration.ofSeconds(3));

        var clientConfig = JedisClientConfiguration.builder()
                .usePooling()
                .poolConfig(poolConfig)
                .and()
                .clientName("redis-spring")
                .connectTimeout(Duration.ofSeconds(2))
                .readTimeout(Duration.ofSeconds(2))
                .build();

        return new JedisConnectionFactory(clusterConfig, clientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplateCluster(RedisConnectionFactory jedisClusterConnectionFactory) {
        var template = new RedisTemplate<String, String>();
        template.setConnectionFactory(jedisClusterConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

}
