package cn.ivfzhou.java.aop.cglib;


public class Target {

    public String run() {
        System.out.println("I'm running");
        return "OK";
    }

}
