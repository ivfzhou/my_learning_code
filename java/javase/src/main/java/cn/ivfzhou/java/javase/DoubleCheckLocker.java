package cn.ivfzhou.java.javase;

public final class DoubleCheckLocker {

    private static volatile DoubleCheckLocker instance;

    private DoubleCheckLocker() {
    }

    public static DoubleCheckLocker getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckLocker.class) {
                if (instance == null) {
                    instance = new DoubleCheckLocker();
                }
            }
        }

        return instance;
    }

}
