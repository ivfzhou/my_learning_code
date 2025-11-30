package cn.ivfzhou.java.aop.cglib;

public final class TestMain {

    public static void main(String[] args) {
        var target = new Proxy<>(new Target()).proxy();
        System.out.println(target.run());
    }

}
