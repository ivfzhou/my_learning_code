package cn.ivfzhou.java.redis.springboot.configuration;

import cn.ivfzhou.java.redis.springboot.serializer.XmlRedisSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.dataformat.xml.XmlMapper;

@Configuration
public class RedisConfig {

    @Bean
    public GenericJacksonJsonRedisSerializer jsonRedisSerializer() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();
        return GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(ptv) // 序列化时写入 "@class" 类型信息，反序列化时自动还原为原始类型(解决 Object 转 LinkedHashMap 的问题)。
                .build();
    }

    @Bean
    public RedisSerializer<Object> xmlRedisSerializer() {
        var xmlMapper = XmlMapper.builder()
                // 用 xsi:type 承载类型信息(合法的 XML 属性名)。
                // 注意: 不要用 "@class", XML 属性名不允许 "@" 字符。
                .activateDefaultTypingAsProperty(
                        BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build(),
                        DefaultTyping.NON_FINAL,
                        "xsi:type"
                )
                .build();
        return new XmlRedisSerializer(xmlMapper);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            GenericJacksonJsonRedisSerializer jsonRedisSerializer
    ) {
        return createTemplate(connectionFactory, jsonRedisSerializer);
    }

    @Bean
    public RedisTemplate<String, Object> xmlRedisTemplate(
            RedisConnectionFactory connectionFactory,
            @Qualifier("xmlRedisSerializer")
            RedisSerializer<Object> xmlRedisSerializer
    ) {
        return createTemplate(connectionFactory, xmlRedisSerializer);
    }

    private RedisTemplate<String, Object> createTemplate(
            RedisConnectionFactory connectionFactory,
            RedisSerializer<?> valueSerializer
    ) {
        var template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);

        var stringSerializer = RedisSerializer.string();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();
        return template;
    }

}
