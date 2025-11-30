package cn.ivfzhou.java.spring.bean;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("user0")
// @Scope("prototype")
// @Repository
// @Service
// @Controller
public class User {

    @Value("18")
    private int age;

    private String name;

    @Autowired
    @Qualifier("user")
    private User friend;

    private Properties pros;

    private Set<String> set;

    private Map<String, String> map;

    private Object ref;

    public User() {}

    public User(int age, String name, User friend) {
        this.age = age;
        this.name = name;
        this.friend = friend;
    }

    public int getAge() {return age;}

    public void setAge(int age) {this.age = age;}

    public String getName() {return name;}

    @Value("ivfzhou")
    public void setName(String name) {this.name = name;}

    public User getFriend() {return friend;}

    public void setFriend(User friend) {this.friend = friend;}

    public Properties getPros() {return pros;}

    public void setPros(Properties pros) {this.pros = pros;}

    public Set<String> getSet() {return set;}

    public void setSet(Set<String> set) {this.set = set;}

    public Map<String, String> getMap() {return map;}

    public void setMap(Map<String, String> map) {this.map = map;}

    public Object getRef() {return ref;}

    public void setRef(Object ref) {this.ref = ref;}

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }

    @PostConstruct
    public void init() {System.out.println("user init");}

    @PreDestroy
    public void destroy() {System.out.println("user destroy");}

}
