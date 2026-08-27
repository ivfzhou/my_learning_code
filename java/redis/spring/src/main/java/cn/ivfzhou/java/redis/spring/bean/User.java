package cn.ivfzhou.java.redis.spring.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "user")   // 指定根元素名称。
@XmlAccessorType(XmlAccessType.FIELD) // 指定按字段映射（避免必须写 getter/setter）。
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    @XmlElement
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("email")
    private String email;

    @JsonProperty("tags")
    private List<String> tags;

    public User() {
    }

    public User(Long id, String name, Integer age, String email, List<String> tags) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.tags = tags;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if (tags != null) {
            sb.append("[ ");
            tags.forEach(tag -> {
                sb.append(tag);
                sb.append(", ");
            });
            sb.delete(sb.length() - 2, sb.length());
            sb.append(" ]");
        } else {
            sb.append("null");
        }
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", tags=" + sb +
                '}';
    }
}
