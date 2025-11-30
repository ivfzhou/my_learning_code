package cn.ivfzhou.java.aop.jdk;

public class Target implements Proxy {

    @Override
    public String run() {
        System.out.println("I'm running");
        return "OK";
    }

}
