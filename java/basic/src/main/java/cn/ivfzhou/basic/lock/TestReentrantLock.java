package cn.ivfzhou.basic.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class TestReentrantLock {

    public static void test() {
        var lock = new ReentrantLock();
        new Thread(() -> {
            try {
                lock.lock();
                System.out.println("lock1");
            } finally {
                lock.unlock();
            }
        }).start();
        new Thread(() -> {
            try {
                lock.lock();
                System.out.println("lock2");
            } finally {
                lock.unlock();
            }
        }).start();
    }

    public static void test2() {
        var lock = new ReentrantLock();
        new Thread(() -> {
            try {
                if (lock.tryLock())
                    System.out.println("wait timeout success");
                else
                    System.out.println("wait timeout failure");
            } finally {
                lock.unlock();
            }
        }).start();
        new Thread(() -> {
            try {
                if (!lock.tryLock(100L, TimeUnit.MICROSECONDS))
                    System.out.println("wait timeout success");
                else
                    System.out.println("wait timeout failure");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();
    }

    public static void test3() {
        var lock = new ReentrantLock();
        var thread = new Thread(() -> {
            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                System.out.println("interrupt");
                return;
            }
            lock.unlock();
        });
        thread.start();
        thread.interrupt();
    }

    public static void test4() {
        var lock = new ReentrantLock(true);
        new Thread(() -> {
            System.out.println("2 wait");
            lock.lock();
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("2 out");
            lock.unlock();
        }).start();
        new Thread(() -> {
            System.out.println("1 wait");
            lock.lock();
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("1 out");
            lock.unlock();
        }).start();
        new Thread(() -> {
            System.out.println("3 wait");
            lock.lock();
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("3 out");
            lock.unlock();
        }).start();
    }

    public static void main(String[] args) throws InterruptedException {
        TestReentrantLock.test();
        TestReentrantLock.test2();
        TestReentrantLock.test3();
        TestReentrantLock.test4();
        Thread.sleep(3000L);
    }

}
