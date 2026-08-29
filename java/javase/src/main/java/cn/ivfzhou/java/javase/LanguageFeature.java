package cn.ivfzhou.java.javase;

import module java.sql; // Java25 模块导入声明，导入 java.sql 模块导出的所有包。

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// 导入一个类中的静态成员（静态变量和静态方法），使其在当前类中可以直接使用，而无需再写类名前缀。
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;

public class LanguageFeature {

    static void main() {
        example6();
    }

    // Java9 允许在接口中编写 private 方法。
    interface MyInterface {
        default void printTwice(String msg) {
            String formatted = format(msg);
            System.out.println(formatted);
            System.out.println(formatted);
        }

        private String format(String s) {
            return "[ " + s + " ]";
        }
    }


    // Java9 允许在 try 块中使用有效终态（effectively final） 的变量。
    static void example1() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("file.txt"));
        // 直接使用 reader 变量，无需再声明 try (BufferedReader r = reader)
        try (reader) {
            System.out.println(reader.readLine());
        }
    }

    // Java9 允许在匿名内部类中使用钻石操作符 <>。
    static void example2() {
        // 合法：匿名内部类中省略泛型类型
        List<String> list = new ArrayList<>() {
            // 自定义内部逻辑
        };
    }

    // Java10 使用 var 让编译器根据右侧表达式推断类型。
    static void example3() {
        var list = new ArrayList<String>(); // 推断为 ArrayList<String>
        var map = new HashMap<String, Integer>(); // 推断为 HashMap<String, Integer>
        list.add("Hello");
    }

    // Java11 允许在 Lambda 表达式中用 var 声明参数。
    static void example4() {
        // 允许在 Lambda 参数上使用 var，结合注解
        @FunctionalInterface
        interface Matcher {
            boolean test(String s);
        }

        Matcher m = (var s) -> s.length() > 3;
    }

    // Java14 switch 表达式。
    static void example5() {
        var day = 1;
        int numLetters = switch (day) {
            case MONDAY, FRIDAY, SUNDAY -> 6;
            case TUESDAY -> 7;
            case THURSDAY, SATURDAY -> 8;
            default -> {
                // 多行逻辑使用 yield 返回
                int len = 9;
                yield len;
            }
        };
    }

    // Java15 文本块，使用 """ 定义多行字符串，自动处理缩进，免去大量转义。
    static void example6() {
        String json = """
                {
                    "name": "Java",
                    "version": 25
                }
                """;
        System.out.println(json);
    }

    // Java16 Record 类，透明数据载体，自动生成构造器、equals、hashCode 和 toString。
    static void example7() {
        record Point(int x, int y) {
        }

        // 使用
        Point p = new Point(3, 5);
        System.out.println(p.x()); // 自动生成访问器
    }

    // Java16 instanceof 模式匹配，在判断类型的同时声明变量，无需显式强制转换。
    static void example8() {
        Object obj = "Hello";
        if (obj instanceof String str) { // 直接声明 str
            System.out.println(str.length());
        }
    }

    // Java17 密封类，使用 sealed 限制哪些子类可以继承或实现当前类/接口。
    sealed interface Shape permits Circle, Rectangle, Triangle {
    }

    final class Circle implements Shape {
    }

    final class Rectangle implements Shape {
    }

    // 允许随意扩展。
    non-sealed class Triangle implements Shape {
    }

    // Java21 switch 的模式匹配，在 case 中使用类型模式，进行安全灵活的匹配。
    static void example9() {
        Object obj = "123";
        String result = switch (obj) {
            case Integer i -> "整数: " + i;
            case String s -> "字符串长度: " + s.length();
            case null -> "空值";
            default -> "其他类型";
        };
    }

    // Java21 记录模式，对 Record 进行解构，直接提取内部组件。
    static void example10() {
        record Point(int x, int y) {
        }

        Object obj = new Point(1, 1);
        // 直接提取 x 和 y
        if (obj instanceof Point(int x, int y)) {
            System.out.println(x + y);
        }
    }

    // Java22 未命名变量与模式，使用下划线 _ 表示未使用的变量或模式组件。
    static void example11() {
        try {
            int result = 10 / 0;
        } catch (Exception _) { // 未使用的异常对象
            System.out.println("发生异常，但不关心具体内容");
        }

        record Point(int x, int y) {
        }
        Object obj = new Point(1, 1);
        // 配合记录模式，忽略不用的组件
        if (obj instanceof Point(int x, _)) {
            System.out.println("x = " + x); // 忽略 y
        }
    }

    // Java25 灵活的构造函数体，允许在显式调用 super(...) 或 this(...) 之前，先初始化当前类的字段。
    class Parent {
        Parent(String msg) {
        }
    }

    class Child extends Parent {
        private String name;

        Child() {
            this.name = "默认值"; // 在 super 之前允许
            super("调用父类");
        }
    }

    // Java25 模块导入声明，一次性导入某个模块导出的所有包。
    static void example12() {
        // 可直接使用 java.sql.* 下的类
        java.sql.Date d = new java.sql.Date(0);
    }
}
