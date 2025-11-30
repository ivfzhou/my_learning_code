package cn.ivfzhou.java.redis.spring.bean;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")   // 指定根元素名称。
@XmlAccessorType(XmlAccessType.FIELD) // 指定按字段映射（避免必须写 getter/setter）。
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("name")
    @XmlElement
    private String name;

    @JsonProperty("age")
    @XmlElement
    private Integer age;

    public String name() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public Integer age() {
        return age;
    }

    public User setAge(Integer age) {
        this.age = age;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
