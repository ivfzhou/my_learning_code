package cn.ivfzhou.java.redis.springboot.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    @JsonProperty("name")
    private String name;

    @JsonProperty("age")
    private Integer age;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

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
}
