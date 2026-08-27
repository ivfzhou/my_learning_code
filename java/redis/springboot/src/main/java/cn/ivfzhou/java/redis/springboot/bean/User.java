package cn.ivfzhou.java.redis.springboot.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("email")
    private String email;

    @JsonProperty("tags")
    // 关闭该字段的类型信息: List<String> 类型已在字段上声明, 无需写入类型;
    // 否则 XML 序列化时 default typing 会为 List 套一层数组, 触发 "nested arrays/Collections" 异常。
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
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
