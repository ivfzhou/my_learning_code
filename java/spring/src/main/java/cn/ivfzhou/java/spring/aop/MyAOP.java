package cn.ivfzhou.java.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyAOP {

    // 第一个 .* 表示包下所有类，第二个 .* 表示所有方法，(..) 表示任意数量和类型的参数。
    // a.b.c..：表示包下任意数量的子包。
    // a.b.*.c：其中 * 匹配单个包。
    // @annotation(a.b.c)：匹配注解类。
    @Pointcut("execution(* cn.ivfzhou.java.spring.aop.*.*(..))")
    public void cut() {}

    @Before("cut()")
    public void before() {System.out.println("before");}

    @After("cut()")
    public void after() {System.out.println("after");}

    @Around("cut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("around begin");
        var proceed = pjp.proceed();
        System.out.println("around end");
        return proceed;
    }

    @AfterReturning(value = "cut()", returning = "obj")
    public void afterReturning(Object obj) {System.out.println("afterReturning：" + obj);}

    @AfterThrowing(value = "cut()", throwing = "e")
    public void afterThrowing(Throwable e) {System.out.println("afterThrowing：" + e);}

}
