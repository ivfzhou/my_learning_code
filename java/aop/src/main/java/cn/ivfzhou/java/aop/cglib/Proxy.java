package cn.ivfzhou.java.aop.cglib;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class Proxy<T> implements MethodInterceptor {

    private final T proxied;

    public Proxy(T proxied) {
        this.proxied = proxied;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("begin " + method.getName());
        Object invoke = method.invoke(proxied, args);
        System.out.println("end");
        return new StringBuilder(((String) invoke)).reverse().toString();
    }

    public T proxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxied.getClass());
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }

}
