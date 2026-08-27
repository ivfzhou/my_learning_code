package cn.ivfzhou.java.redis.springboot.serializer;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import tools.jackson.core.JacksonException;
import tools.jackson.dataformat.xml.XmlMapper;

public class XmlRedisSerializer implements RedisSerializer<Object> {

    private final XmlMapper xmlMapper;

    public XmlRedisSerializer(XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    @Override
    public byte[] serialize(Object value) throws SerializationException {
        if (value == null) {
            return new byte[0];
        }
        try {
            return xmlMapper.writeValueAsBytes(value);
        } catch (JacksonException ex) {
            throw new SerializationException("XML 序列化失败: " + value.getClass().getName(), ex);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            // 启用 default typing 后, 会依据 XML 中的 xsi:type 属性还原具体类型。
            return xmlMapper.readValue(bytes, Object.class);
        } catch (JacksonException ex) {
            throw new SerializationException("XML 反序列化失败", ex);
        }
    }
}
