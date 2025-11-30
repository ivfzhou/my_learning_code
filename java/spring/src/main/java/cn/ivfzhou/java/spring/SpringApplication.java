package cn.ivfzhou.java.spring;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.ivfzhou.java.spring.aop.Target;
import cn.ivfzhou.java.spring.bean.User;
import cn.ivfzhou.java.spring.tx.MyTX;

@EnableAspectJAutoProxy
public final class SpringApplication {

    public static void main(String[] args) {
        var ac = new ClassPathXmlApplicationContext("application.xml");

        var user = ac.getBean("user", User.class);
        var user0 = (User) ac.getBean("user0");
        System.out.println(user0 == user);

        var target = ac.getBean(Target.class);
        target.get();

        var tx = (MyTX) ac.getBean("myTX");
        try {
            tx.insert();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tx.query();
    }

}
