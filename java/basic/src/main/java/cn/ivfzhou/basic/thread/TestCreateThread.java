package cn.ivfzhou.basic.thread;

public final class TestCreateThread {

    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("重写run方法");
        }
    }

    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("实现run方法");
        }
    }

    static void test() {
        var thread = new MyThread();
        thread.start();
    }

    static void test2() {
        var thread = new Thread(new MyRunnable());
        thread.start();
    }

    public static void main(String[] args) throws InterruptedException {
        test();
        test2();
        Thread.sleep(1000);
    }

}
