package cn.ivfzhou.java.redis.spring.configuration;

import cn.ivfzhou.java.redis.spring.bean.User;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.OxmSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        var serverConfig = new RedisStandaloneConfiguration("127.0.0.1", 6379);
        serverConfig.setUsername("ivfzhou");
        serverConfig.setPassword("123456");
        serverConfig.setDatabase(0);

        var poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(16);                     // 池中最大连接总数。
        poolConfig.setMaxIdle(8);                       // 最大空闲连接数。
        poolConfig.setMinIdle(1);                       // 最小空闲连接数。
        poolConfig.setMaxWait(Duration.ofSeconds(3));   // 获取连接的最大等待时间。

        var clientConfig = JedisClientConfiguration.builder()
                .usePooling()
                .poolConfig(poolConfig)
                .and()
                .clientName("redis-spring")
                .connectTimeout(Duration.ofSeconds(2))
                .readTimeout(Duration.ofSeconds(2))
                .build();

        return new JedisConnectionFactory(serverConfig, clientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisStringTemplate(RedisConnectionFactory connectionFactory) {
        var template = new RedisTemplate<String, String>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisJsonTemplate(RedisConnectionFactory connectionFactory) {
        var ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("cn.ivfzhou.java.redis.spring.bean") // 将可反序列化类型限制在本项目包内，避免任意类型反序列化风险。
                .allowIfSubType("java.util.")   // default typing 会写入集合元素类型（如 ArrayList），需放行
                .build();

        var jsonSerializer = GenericJacksonJsonRedisSerializer
                .builder()
                .enableDefaultTyping(ptv) // 启用 default typing 后写入时在 JSON 中携带类型信息（@class）。
                .build();

        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonSerializer);
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisXmlTemplate(RedisConnectionFactory connectionFactory) {
        var marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(User.class); // 指定参与 JAXB 编组的类（contextPath / classesToBeBound / packagesToScan 三选一）。

        var oxmSerializer = new OxmSerializer(marshaller, marshaller);

        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(oxmSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(oxmSerializer);
        return template;
    }

}
