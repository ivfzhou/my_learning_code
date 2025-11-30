package cn.ivfzhou.java.spring;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import cn.ivfzhou.java.spring.bean.User;
import cn.ivfzhou.java.spring.tx.MyTX;

@SpringJUnitConfig(locations = "classpath:application.xml")
public class SpringApplicationTest {

    @Autowired
    @Qualifier("user")
    User user;

    @Autowired
    MyTX myTx;

    @BeforeAll
    public static void setup() {
        System.out.println("setup");
    }

    @AfterAll
    public static void teardown() {
        System.out.println("teardown");
    }

    @BeforeEach
    public void before() {
        System.out.println("before");
    }

    @AfterEach
    public void after() {
        System.out.println("after");
    }

    @Test
    public void test() {
        System.out.println(user);
    }

    @Test
    public void query() {
        myTx.query();
    }

    @Test
    public void insert() {
        try {
            myTx.insert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
