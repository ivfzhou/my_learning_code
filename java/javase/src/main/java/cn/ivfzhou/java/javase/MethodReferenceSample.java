package cn.ivfzhou.java.javase;

public final class MethodReferenceSample {

    // 静态方法引用。
    // Zoo:compareZoo

    // 实例方法引用。
    // String::compareTo

    // 构造方法引用。
    // Zoo::new

    // 数组构造方法引用。
    // Zoo[]::new
    // class Zoo{}
    // interface ZooCreate{
    //     Zoo[] create(int i);
    // }
    // ZooCreate zooCreate = Zoo[]::new;

}
